package com.mimir.engine.tools

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap

data class ToolDef(
    val id: String,
    val capability: String,
    val riskClass: String,
    val requiresApproval: Boolean,
    val sideEffect: Boolean,
    val idempotent: Boolean,
    val timeoutMs: Long,
    val permissions: List<String>,
    val requiredInputs: List<String>,
)

object ToolCapabilities {
    const val REGISTER = "tool.register"
    const val RESOLVE = "tool.resolve"
    const val AUTHORIZE = "tool.authorize"
    const val LIST = "tool.list"
    const val DENY = "tool.deny"
    const val METRICS = "tool.metrics"
    val ALL = listOf(REGISTER, RESOLVE, AUTHORIZE, LIST, DENY, METRICS)
}

/**
 * Tool Registry — **enforcement** not just description.
 * Deny by default when unknown or missing approval/permission.
 */
class ToolRegistryEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val tools = ConcurrentHashMap<String, ToolDef>()
    private val denied = ConcurrentHashMap.newKeySet<String>()
    private var state = EngineState.Discovered

    init {
        // seed v2 defaults
        registerInternal(
            ToolDef("terminal.command.execute", "terminal.command.execute", "high", true, true, false, 60_000, listOf("terminal.execute"), listOf("command")),
        )
        registerInternal(
            ToolDef("forge.scaffold.run", "forge.scaffold.run", "medium", false, true, true, 120_000, listOf("forge.write"), listOf("templateId", "targetPath")),
        )
        registerInternal(
            ToolDef("browser.tab.open", "browser.tab.open", "low", false, false, true, 15_000, listOf("browser.navigate"), listOf()),
        )
        registerInternal(
            ToolDef("vault.put", "vault.put", "high", false, true, true, 10_000, listOf("vault.write"), listOf("key", "value", "passphrase")),
        )
    }

    private val manifest = EngineManifest(
        id = EngineId("tool-registry"),
        name = "Mimir Tool Registry",
        version = "0.4.0",
        provides = ToolCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("tool.admin"),
        dataNamespace = "engine.tools",
        priority = 96,
        platforms = listOf("android", "desktop"),
        runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x",
        publisher = "mimir",
    )

    private fun registerInternal(t: ToolDef) {
        tools[t.id] = t
    }

    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState {
        state = EngineState.Configured; return state
    }
    override suspend fun start(): EngineState {
        state = EngineState.Running; return state
    }
    override suspend fun stop(reason: String): EngineState {
        state = EngineState.Stopped; return state
    }
    override suspend fun health() =
        HealthReport(manifest.id, state == EngineState.Running, state, "tools=0.4.0 count=${tools.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            ToolCapabilities.LIST ->
                InvocationResult.Ok(request.requestId, mapOf("count" to tools.size.toString(), "ids" to tools.keys.joinToString(",")))
            ToolCapabilities.RESOLVE -> {
                val id = request.input["toolId"] ?: return InvocationResult.Err(request.requestId, "invalid", "toolId")
                if (id in denied) return InvocationResult.Err(request.requestId, "tool_denied", id)
        val t = tools[id] ?: return InvocationResult.Err(request.requestId, "unknown_tool", id)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "toolId" to t.id,
                        "riskClass" to t.riskClass,
                        "requiresApproval" to t.requiresApproval.toString(),
                        "idempotent" to t.idempotent.toString(),
                        "permissions" to t.permissions.joinToString(","),
                    ),
                )
            }
            ToolCapabilities.AUTHORIZE -> authorize(request)
            ToolCapabilities.REGISTER -> {
                val id = request.input["toolId"] ?: return InvocationResult.Err(request.requestId, "invalid", "toolId")
                val t = ToolDef(
                    id = id,
                    capability = request.input["capability"] ?: id,
                    riskClass = request.input["riskClass"] ?: "medium",
                    requiresApproval = request.input["requiresApproval"]?.equals("true", true) == true,
                    sideEffect = request.input["sideEffect"]?.equals("true", true) != false,
                    idempotent = request.input["idempotent"]?.equals("true", true) == true,
                    timeoutMs = request.input["timeoutMs"]?.toLongOrNull() ?: 30_000,
                    permissions = (request.input["permissions"] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    requiredInputs = (request.input["requiredInputs"] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() },
                )
                tools[id] = t
                InvocationResult.Ok(request.requestId, mapOf("toolId" to id, "registered" to "true"))
            }
            ToolCapabilities.DENY -> {
                val id = request.input["toolId"] ?: return InvocationResult.Err(request.requestId, "invalid", "toolId")
                denied.add(id)
                InvocationResult.Ok(request.requestId, mapOf("toolId" to id, "denied" to "true"))
            }
            ToolCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("tools" to tools.size.toString(), "denied" to denied.size.toString(), "state" to state.name),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun authorize(request: InvocationRequest): InvocationResult {
        val id = request.input["toolId"] ?: return InvocationResult.Err(request.requestId, "invalid", "toolId")
        if (id in denied) return InvocationResult.Err(request.requestId, "tool_denied", id)
        val t = tools[id] ?: return InvocationResult.Err(request.requestId, "unknown_tool", id) // deny unknown
        // required inputs
        for (req in t.requiredInputs) {
            if (request.input[req].isNullOrBlank()) {
                return InvocationResult.Err(request.requestId, "missing_input", req)
            }
        }
        // granted permissions (comma list)
        val granted = (request.input["grantedPermissions"] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        val missingPerm = t.permissions.filter { it !in granted }
        if (missingPerm.isNotEmpty()) {
            return InvocationResult.Err(request.requestId, "permission_denied", missingPerm.joinToString(","))
        }
        if (t.requiresApproval && request.input["approvalToken"].isNullOrBlank()) {
            return InvocationResult.Err(request.requestId, "approval_required", id)
        }
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "toolId" to id,
                "authorized" to "true",
                "timeoutMs" to t.timeoutMs.toString(),
                "idempotent" to t.idempotent.toString(),
            ),
        )
    }
}

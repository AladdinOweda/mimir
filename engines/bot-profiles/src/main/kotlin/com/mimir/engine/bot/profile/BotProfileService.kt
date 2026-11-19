package com.mimir.engine.bot.profile

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Multi-bot profiles + scoped memory isolation (AnyModelBot patterns, Mimir-owned).
 * Does not replace playbook BotEngine — complements it.
 */
class BotProfileService(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val profiles = ConcurrentHashMap<String, BotProfile>()
    /** memory key = botId + "::" + key */
    private val memory = ConcurrentHashMap<String, String>()
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("bot-profiles"),
        name = "Mimir Bot Profiles",
        version = "0.4.0",
        provides = BotProfileCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("bot.admin", "bot.read"),
        dataNamespace = "engine.bot.profiles",
        priority = 79,
        platforms = listOf("android", "desktop"),
        runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x",
        publisher = "mimir",
    )

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
        HealthReport(manifest.id, state == EngineState.Running, state, "profiles=${profiles.size} mem=${memory.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            BotProfileCapabilities.PROFILE_CREATE -> create(request)
            BotProfileCapabilities.PROFILE_GET -> get(request)
            BotProfileCapabilities.PROFILE_LIST ->
                InvocationResult.Ok(request.requestId, mapOf("count" to profiles.size.toString(), "ids" to profiles.keys.joinToString(",")))
            BotProfileCapabilities.PROFILE_UPDATE -> update(request)
            BotProfileCapabilities.PROFILE_DELETE -> {
                val id = request.input["id"] ?: return inv(request, "id")
                profiles.remove(id)
                memory.keys.filter { it.startsWith("$id::") }.forEach { memory.remove(it) }
                InvocationResult.Ok(request.requestId, mapOf("deleted" to id))
            }
            BotProfileCapabilities.MEMORY_PUT -> memPut(request)
            BotProfileCapabilities.MEMORY_GET -> memGet(request)
            BotProfileCapabilities.MEMORY_ISOLATION_CHECK -> isolation(request)
            BotProfileCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("profiles" to profiles.size.toString(), "memoryKeys" to memory.size.toString(), "state" to state.name),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun create(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return inv(request, "id")
        if (profiles.containsKey(id)) return InvocationResult.Err(request.requestId, "exists", id)
        val p = BotProfile(
            id = id,
            displayName = request.input["displayName"] ?: id,
            persona = request.input["persona"] ?: "",
            instructions = request.input["instructions"] ?: "",
            modelRef = request.input["modelRef"] ?: "",
            workspaceId = request.input["workspaceId"] ?: "ws-$id",
            memoryScope = request.input["memoryScope"] ?: "bot",
            permissionProfile = request.input["permissionProfile"] ?: "default",
            maxSteps = request.input["maxSteps"]?.toIntOrNull() ?: 50,
            computerUse = request.input["computerUse"] ?: "off",
        )
        profiles[id] = p
        return InvocationResult.Ok(request.requestId, mapOf("id" to id, "workspaceId" to p.workspaceId, "modelRef" to p.modelRef))
    }

    private fun get(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return inv(request, "id")
        val p = profiles[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "id" to p.id,
                "displayName" to p.displayName,
                "modelRef" to p.modelRef,
                "workspaceId" to p.workspaceId,
                "memoryScope" to p.memoryScope,
                "computerUse" to p.computerUse,
                "enabled" to p.enabled.toString(),
            ),
        )
    }

    private fun update(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return inv(request, "id")
        val cur = profiles[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        val next = cur.copy(
            displayName = request.input["displayName"] ?: cur.displayName,
            modelRef = request.input["modelRef"] ?: cur.modelRef,
            persona = request.input["persona"] ?: cur.persona,
            enabled = request.input["enabled"]?.toBooleanStrictOrNull() ?: cur.enabled,
        )
        profiles[id] = next
        return InvocationResult.Ok(request.requestId, mapOf("id" to id, "modelRef" to next.modelRef))
    }

    private fun memPut(request: InvocationRequest): InvocationResult {
        val botId = request.input["botId"] ?: return inv(request, "botId")
        if (!profiles.containsKey(botId)) return InvocationResult.Err(request.requestId, "not_found", botId)
        val key = request.input["key"] ?: return inv(request, "key")
        val value = request.input["value"] ?: return inv(request, "value")
        // crude secret filter
        if (value.contains("API_KEY", ignoreCase = true) || value.contains("SECRET=", ignoreCase = true)) {
            return InvocationResult.Err(request.requestId, "secret_rejected", key)
        }
        memory["$botId::$key"] = value
        return InvocationResult.Ok(request.requestId, mapOf("stored" to key, "botId" to botId))
    }

    private fun memGet(request: InvocationRequest): InvocationResult {
        val botId = request.input["botId"] ?: return inv(request, "botId")
        val requester = request.input["requesterBotId"] ?: botId
        val key = request.input["key"] ?: return inv(request, "key")
        if (requester != botId) {
            return InvocationResult.Err(request.requestId, "isolation_denied", "$requester!$botId")
        }
        val v = memory["$botId::$key"] ?: return InvocationResult.Err(request.requestId, "not_found", key)
        return InvocationResult.Ok(request.requestId, mapOf("value" to v))
    }

    private fun isolation(request: InvocationRequest): InvocationResult {
        val a = request.input["botA"] ?: return inv(request, "botA")
        val b = request.input["botB"] ?: return inv(request, "botB")
        val allowed = a == b
        return InvocationResult.Ok(request.requestId, mapOf("crossReadAllowed" to allowed.toString()))
    }

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)
}

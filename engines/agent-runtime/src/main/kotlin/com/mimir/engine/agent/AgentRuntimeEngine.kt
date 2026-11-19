package com.mimir.engine.agent

import com.mimir.contracts.agent.AgentPort
import com.mimir.contracts.agent.AgentId
import com.mimir.contracts.agent.AgentInfo
import com.mimir.contracts.agent.AgentSessionId
import com.mimir.contracts.agent.AgentSessionInfo

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object AgentRuntimeCapabilities {
    const val AGENT_CREATE = "agent.create"
    const val SESSION_START = "agent.session.start"
    const val SESSION_STOP = "agent.session.stop"
    const val TOOL_INVOKE = "agent.tool.invoke"
    const val SESSION_STATUS = "agent.session.status"
    const val AGENT_LIST = "agent.list"
    const val SESSION_LIST = "agent.session.list"
    const val METRICS = "agent.metrics"
    val ALL = listOf(AGENT_CREATE, SESSION_START, SESSION_STOP, TOOL_INVOKE, SESSION_STATUS, AGENT_LIST, SESSION_LIST, METRICS)
}

data class AgentRecord(
    val id: String,
    val displayName: String,
    val modelId: String,
    val toolAllowlist: Set<String>,
    val maxCostUsd: Double,
)

data class AgentSession(
    val id: String,
    val agentId: String,
    var status: String = "Running",
    var spentUsd: Double = 0.0,
)

/**
 * R10 Agent Runtime — isolation by allowlist + cost ceiling.
 * Tools only via Kernel capabilities (caller must pass through KernelHost in product).
 */
class AgentRuntimeEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine, AgentPort {
    private val agents = ConcurrentHashMap<String, AgentRecord>()
    private val sessions = ConcurrentHashMap<String, AgentSession>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("agent-runtime"),
        name = "Mimir Agent Runtime",
        version = "0.5.0",
        provides = AgentRuntimeCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("agent.admin", "agent.run"),
        dataNamespace = "engine.agent",
        priority = 82,
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
        HealthReport(manifest.id, state == EngineState.Running, state, "agents=${agents.size} sessions=${sessions.size}", clockMs())

    private fun charge(session: AgentSession, amount: Double): Boolean {
        if (session.spentUsd + amount > (agents[session.agentId]?.maxCostUsd ?: 0.0)) return false
        session.spentUsd += amount
        return true
    }

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            AgentRuntimeCapabilities.AGENT_CREATE -> {
                val id = request.input["id"] ?: "agent-${seq.incrementAndGet()}"
                val tools = (request.input["tools"] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                val maxCost = request.input["maxCostUsd"]?.toDoubleOrNull() ?: 1.0
                agents[id] = AgentRecord(
                    id = id,
                    displayName = request.input["displayName"] ?: id,
                    modelId = request.input["modelId"] ?: "noop",
                    toolAllowlist = tools,
                    maxCostUsd = maxCost,
                )
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "tools" to tools.joinToString(",")))
            }
            AgentRuntimeCapabilities.SESSION_START -> {
                val agentId = request.input["agentId"] ?: return inv(request, "agentId")
                if (!agents.containsKey(agentId)) return InvocationResult.Err(request.requestId, "not_found", agentId)
                val sid = "sess-${seq.incrementAndGet()}"
                sessions[sid] = AgentSession(sid, agentId)
                InvocationResult.Ok(request.requestId, mapOf("sessionId" to sid, "agentId" to agentId, "status" to "Running"))
            }
            AgentRuntimeCapabilities.SESSION_STOP -> {
                val sid = request.input["sessionId"] ?: return inv(request, "sessionId")
                val s = sessions[sid] ?: return InvocationResult.Err(request.requestId, "not_found", sid)
                s.status = "Stopped"
                InvocationResult.Ok(request.requestId, mapOf("sessionId" to sid, "status" to s.status))
            }
            AgentRuntimeCapabilities.TOOL_INVOKE -> {
                val sid = request.input["sessionId"] ?: return inv(request, "sessionId")
                val tool = request.input["tool"] ?: return inv(request, "tool")
                val s = sessions[sid] ?: return InvocationResult.Err(request.requestId, "not_found", sid)
                if (s.status != "Running") return InvocationResult.Err(request.requestId, "session_stopped", sid)
                val agent = agents[s.agentId] ?: return InvocationResult.Err(request.requestId, "agent_missing", s.agentId)
                if (tool !in agent.toolAllowlist && "*" !in agent.toolAllowlist) {
                    return InvocationResult.Err(request.requestId, "tool_not_allowed", tool)
                }
                val cost = request.input["estimatedUsd"]?.toDoubleOrNull()
                    ?: request.input["costUsd"]?.toDoubleOrNull()
                    ?: 0.01
                if (s.spentUsd + cost > agent.maxCostUsd) {
                    return InvocationResult.Err(request.requestId, "cost_limit", "spent=${s.spentUsd}")
                }
                s.spentUsd += cost
                // Does not execute tool itself — records authorization only (Kernel executes)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "sessionId" to sid,
                        "tool" to tool,
                        "authorized" to "true",
                        "spentUsd" to s.spentUsd.toString(),
                        "executed" to "false",
                        "message" to "authorized_for_kernel_dispatch",
                    ),
                )
            }
            AgentRuntimeCapabilities.AGENT_LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "count" to agents.size.toString(),
                        "ids" to agents.keys.joinToString(","),
                    ),
                )
            AgentRuntimeCapabilities.SESSION_LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "count" to sessions.size.toString(),
                        "ids" to sessions.keys.joinToString(","),
                        "running" to sessions.values.count { it.status == "Running" }.toString(),
                    ),
                )
            AgentRuntimeCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "agents" to agents.size.toString(),
                        "sessions" to sessions.size.toString(),
                        "running" to sessions.values.count { it.status == "Running" }.toString(),
                        "state" to state.name,
                    ),
                )
            AgentRuntimeCapabilities.SESSION_STATUS -> {
                val sid = request.input["sessionId"] ?: return inv(request, "sessionId")
                val s = sessions[sid] ?: return InvocationResult.Err(request.requestId, "not_found", sid)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("sessionId" to sid, "agentId" to s.agentId, "status" to s.status, "spentUsd" to s.spentUsd.toString()),
                )
            }
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)

    // --- AgentPort ---
    override fun createAgent(displayName: String, modelId: String, toolAllowlist: Set<String>): AgentInfo {
        val id = "agent-${seq.incrementAndGet()}"
        agents[id] = AgentRecord(id, displayName, modelId, toolAllowlist, maxCostUsd = 1.0)
        return AgentInfo(AgentId(id), displayName, modelId)
    }
    override fun startSession(agentId: AgentId): AgentSessionInfo {
        val id = "as-${seq.incrementAndGet()}"
        sessions[id] = AgentSession(id, agentId.value, "Running")
        return AgentSessionInfo(AgentSessionId(id), agentId, "Running")
    }
    override fun stopSession(sessionId: AgentSessionId): Boolean {
        val s = sessions[sessionId.value] ?: return false
        s.status = "Stopped"
        return true
    }
    override fun sessionStatus(sessionId: AgentSessionId): AgentSessionInfo? {
        val s = sessions[sessionId.value] ?: return null
        return AgentSessionInfo(AgentSessionId(s.id), AgentId(s.agentId), s.status)
    }


    fun agentMetrics(): Map<String, String> = mapOf(
        "agents" to agents.size.toString(),
        "sessions" to sessions.size.toString(),
        "runningSessions" to sessions.values.count { it.status == "Running" }.toString(),
        "state" to state.name,
    )

}

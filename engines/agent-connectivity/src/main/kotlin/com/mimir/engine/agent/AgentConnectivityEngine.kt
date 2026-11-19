package com.mimir.engine.agent

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object AgentConnectivityCapabilities {
    const val SESSION_START = "agent.conn.session.start"
    const val SESSION_PROMPT = "agent.conn.session.prompt"
    const val SESSION_END = "agent.conn.session.end"
    const val SESSION_LIST = "agent.conn.session.list"
    const val SESSION_STATUS = "agent.conn.session.status"
    val ALL = listOf(SESSION_START, SESSION_PROMPT, SESSION_END, SESSION_LIST, SESSION_STATUS)
}

private val ALLOWED_PROTOCOLS = setOf("mcp", "acp", "http", "noop")

/**
 * Agent connectivity — protocol sessions (MCP/ACP style) without embedding vendor SDKs.
 * Prompts are recorded; live remote IO is adapter responsibility.
 */
class AgentConnectivityEngine(
    private val defaultMaxTurns: Int = 32,
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private data class Session(
        val id: String,
        val protocol: String,
        val endpoint: String,
        val maxTurns: Int,
        var turns: Int = 0,
        var status: String = "open",
        val prompts: MutableList<String> = mutableListOf(),
    )

    private val sessions = ConcurrentHashMap<String, Session>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("agent-connectivity"),
        name = "Mimir Agent Connectivity",
        version = "0.5.0",
        provides = AgentConnectivityCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("agent.connect"),
        dataNamespace = "engine.agent.conn",
        priority = 81,
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
        HealthReport(manifest.id, state == EngineState.Running, state, "sessions=${sessions.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            AgentConnectivityCapabilities.SESSION_START -> {
                val proto = (request.input["protocol"] ?: "noop").lowercase()
                if (proto !in ALLOWED_PROTOCOLS) {
                    return InvocationResult.Err(request.requestId, "protocol_unsupported", proto)
                }
                val id = "as-${seq.incrementAndGet()}"
                val max = request.input["maxTurns"]?.toIntOrNull() ?: defaultMaxTurns
                sessions[id] = Session(
                    id, proto, request.input["endpoint"] ?: "noop://local", maxTurns = max.coerceIn(1, 10_000),
                )
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("sessionId" to id, "protocol" to proto, "maxTurns" to max.toString()),
                )
            }
            AgentConnectivityCapabilities.SESSION_PROMPT -> {
                val id = request.input["sessionId"] ?: return inv(request, "sessionId")
                val s = sessions[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                if (s.status != "open") return InvocationResult.Err(request.requestId, "session_closed", id)
                if (s.turns >= s.maxTurns) {
                    return InvocationResult.Err(request.requestId, "max_turns", "${s.turns}/${s.maxTurns}")
                }
                val text = request.input["prompt"] ?: return inv(request, "prompt")
                s.turns++
                s.prompts += text.take(2000)
                // Honest: no live model call here
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "sessionId" to id,
                        "turns" to s.turns.toString(),
                        "echo" to "ACK:${text.take(120)}",
                        "live" to "false",
                    ),
                )
            }
            AgentConnectivityCapabilities.SESSION_END -> {
                val id = request.input["sessionId"] ?: return inv(request, "sessionId")
                val s = sessions[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                s.status = "closed"
                InvocationResult.Ok(request.requestId, mapOf("sessionId" to id, "status" to "closed", "turns" to s.turns.toString()))
            }
            AgentConnectivityCapabilities.SESSION_LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "count" to sessions.size.toString(),
                        "ids" to sessions.keys.joinToString(","),
                    ),
                )
            AgentConnectivityCapabilities.SESSION_STATUS -> {
                val id = request.input["sessionId"] ?: return inv(request, "sessionId")
                val s = sessions[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "sessionId" to id,
                        "protocol" to s.protocol,
                        "endpoint" to s.endpoint,
                        "status" to s.status,
                        "turns" to s.turns.toString(),
                        "maxTurns" to s.maxTurns.toString(),
                    ),
                )
            }
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)
}

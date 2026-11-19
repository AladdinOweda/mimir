package com.mimir.engine.collaboration

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object CollaborationCapabilities {
    const val SESSION_OPEN = "collab.session.open"
    const val SESSION_CLOSE = "collab.session.close"
    const val MEMBER_ADD = "collab.member.add"
    const val MESSAGE = "collab.message"
    const val SESSION_STATUS = "collab.session.status"
    const val METRICS = "collab.session.metrics"
    val ALL = listOf(SESSION_OPEN, SESSION_CLOSE, MEMBER_ADD, MESSAGE, SESSION_STATUS, METRICS)
}

data class CollabSession(
    val id: String, val topic: String,
    val members: MutableSet<String> = mutableSetOf(),
    val messages: MutableList<String> = mutableListOf(),
    var status: String = "open",
)

class CollaborationEngine(private val clockMs: () -> Long = { System.currentTimeMillis() }) : MimirEngine {
    private val sessions = ConcurrentHashMap<String, CollabSession>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered
    private val manifest = EngineManifest(
        id = EngineId("collaboration"), name = "Mimir Collaboration", version = "0.1.0",
        provides = CollaborationCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("collab.session"), dataNamespace = "engine.collaboration", priority = 76,
        platforms = listOf("android", "desktop"), runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x", publisher = "mimir",
    )
    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState { state = EngineState.Configured; return state }
    override suspend fun start(): EngineState { state = EngineState.Running; return state }
    override suspend fun stop(reason: String): EngineState { state = EngineState.Stopped; return state }
    override suspend fun health() =
        HealthReport(manifest.id, state == EngineState.Running, state, "sessions=${sessions.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded)
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        return when (request.capability.id.value) {
            CollaborationCapabilities.SESSION_OPEN -> {
                val id = "cs-${seq.incrementAndGet()}"
                val topic = request.input["topic"] ?: "session"
                val members = (request.input["members"] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() }
                sessions[id] = CollabSession(id, topic, members.toMutableSet())
                InvocationResult.Ok(request.requestId, mapOf("sessionId" to id, "members" to members.size.toString()))
            }
            CollaborationCapabilities.SESSION_CLOSE -> {
                val s = session(request) ?: return notFound(request)
                s.status = "closed"
                InvocationResult.Ok(request.requestId, mapOf("sessionId" to s.id, "status" to "closed"))
            }
            CollaborationCapabilities.MEMBER_ADD -> {
                val s = session(request) ?: return notFound(request)
                if (s.status != "open") return InvocationResult.Err(request.requestId, "closed", s.id)
                val m = request.input["member"] ?: return inv(request, "member")
                s.members += m
                InvocationResult.Ok(request.requestId, mapOf("sessionId" to s.id, "members" to s.members.size.toString()))
            }
            CollaborationCapabilities.MESSAGE -> {
                val s = session(request) ?: return notFound(request)
                if (s.status != "open") return InvocationResult.Err(request.requestId, "closed", s.id)
                val from = request.input["from"] ?: return inv(request, "from")
                val text = request.input["text"] ?: return inv(request, "text")
                if (from !in s.members) return InvocationResult.Err(request.requestId, "not_member", from)
                s.messages += "$from:$text"
                InvocationResult.Ok(request.requestId, mapOf("sessionId" to s.id, "messageCount" to s.messages.size.toString()))
            }
            CollaborationCapabilities.SESSION_STATUS -> {
                val s = session(request) ?: return notFound(request)
                InvocationResult.Ok(request.requestId, mapOf(
                    "sessionId" to s.id, "topic" to s.topic, "status" to s.status,
                    "members" to s.members.joinToString(","), "messageCount" to s.messages.size.toString(),
                ))
            }
            CollaborationCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "sessions" to sessions.size.toString(),
                        "open" to sessions.values.count { it.status == "open" }.toString(),
                        "state" to state.name,
                    ),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }
    private fun session(request: InvocationRequest) = request.input["sessionId"]?.let { sessions[it] }
    private fun notFound(request: InvocationRequest) = InvocationResult.Err(request.requestId, "not_found", request.input["sessionId"] ?: "")
    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)
}

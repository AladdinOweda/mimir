package com.mimir.engine.session

import com.mimir.engine.session.steward.ContextBudget
import com.mimir.engine.session.steward.ContextSteward
import com.mimir.engine.session.steward.StewardAction
import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object SessionContinuityCapabilities {
    const val SESSION_OPEN = "session.open"
    const val SESSION_APPEND = "session.append"
    const val SESSION_EVALUATE = "session.evaluate"
    const val SESSION_HANDOFF = "session.handoff"
    const val SESSION_RECOVER = "session.recover"
    const val SESSION_GET = "session.get"
    const val SESSION_LIST = "session.list"
    val ALL = listOf(SESSION_OPEN, SESSION_APPEND, SESSION_EVALUATE, SESSION_HANDOFF, SESSION_RECOVER, SESSION_GET, SESSION_LIST)
}

data class AgentSession(
    val id: String,
    val botId: String,
    val modelRef: String,
    var history: String = "",
    var status: String = "open", // open | handed_off | closed
    var handoffSummary: String = "",
    val parentSessionId: String? = null,
    val createdAtEpochMs: Long,
    var updatedAtEpochMs: Long,
)

/**
 * Phase C — Agent continuity: append, steward evaluate, handoff, recover after crash (in-memory journal).
 */
class SessionContinuityEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val sessions = ConcurrentHashMap<String, AgentSession>()
    private val journal = ConcurrentHashMap<String, String>() // sessionId -> last snapshot
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("agent-session-continuity"),
        name = "Mimir Session Continuity",
        version = "0.4.0",
        provides = SessionContinuityCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("session.read", "session.write"),
        dataNamespace = "engine.session",
        priority = 88,
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
            SessionContinuityCapabilities.SESSION_OPEN -> open(request)
            SessionContinuityCapabilities.SESSION_APPEND -> append(request)
            SessionContinuityCapabilities.SESSION_EVALUATE -> evaluate(request)
            SessionContinuityCapabilities.SESSION_HANDOFF -> handoff(request)
            SessionContinuityCapabilities.SESSION_RECOVER -> recover(request)
            SessionContinuityCapabilities.SESSION_GET -> get(request)
            SessionContinuityCapabilities.SESSION_LIST ->
                InvocationResult.Ok(request.requestId, mapOf("count" to sessions.size.toString(), "ids" to sessions.keys.joinToString(",")))
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun open(request: InvocationRequest): InvocationResult {
        val id = "ses-${seq.incrementAndGet()}"
        val s = AgentSession(
            id = id,
            botId = request.input["botId"] ?: "bot-default",
            modelRef = request.input["modelRef"] ?: "local",
            createdAtEpochMs = clockMs(),
            updatedAtEpochMs = clockMs(),
        )
        sessions[id] = s
        journal[id] = s.history
        return InvocationResult.Ok(request.requestId, mapOf("sessionId" to id, "status" to s.status))
    }

    private fun append(request: InvocationRequest): InvocationResult {
        val id = request.input["sessionId"] ?: return inv(request, "sessionId")
        val s = sessions[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        if (s.status != "open") return InvocationResult.Err(request.requestId, "not_open", s.status)
        val text = request.input["text"] ?: return inv(request, "text")
        s.history = if (s.history.isEmpty()) text else s.history + "\n" + text
        s.updatedAtEpochMs = clockMs()
        journal[id] = s.history
        return InvocationResult.Ok(request.requestId, mapOf("sessionId" to id, "chars" to s.history.length.toString()))
    }

    private fun evaluate(request: InvocationRequest): InvocationResult {
        val id = request.input["sessionId"] ?: return inv(request, "sessionId")
        val s = sessions[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        val window = request.input["contextWindow"]?.toIntOrNull() ?: 8000
        val budget = ContextBudget(modelContextWindow = window)
        val d = ContextSteward.evaluate(s.history, budget)
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "action" to d.action.name,
                "estimatedTokens" to d.estimatedTokens.toString(),
                "softLimit" to d.softLimit.toString(),
                "hardLimit" to d.hardLimit.toString(),
                "summaryChars" to d.summary.length.toString(),
            ),
        )
    }

    private fun handoff(request: InvocationRequest): InvocationResult {
        val id = request.input["sessionId"] ?: return inv(request, "sessionId")
        val s = sessions[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        val window = request.input["contextWindow"]?.toIntOrNull() ?: 8000
        val d = ContextSteward.evaluate(s.history, ContextBudget(window))
        val summary = if (d.summary.isNotBlank()) d.summary else ContextSteward.compact(s.history, 3000)
        s.status = "handed_off"
        s.handoffSummary = summary
        s.updatedAtEpochMs = clockMs()
        val newId = "ses-${seq.incrementAndGet()}"
        val child = AgentSession(
            id = newId,
            botId = s.botId,
            modelRef = request.input["modelRef"] ?: s.modelRef,
            history = "HANDOFF:\n$summary",
            parentSessionId = id,
            createdAtEpochMs = clockMs(),
            updatedAtEpochMs = clockMs(),
        )
        sessions[newId] = child
        journal[newId] = child.history
        journal[id] = s.history
        return InvocationResult.Ok(
            request.requestId,
            mapOf("from" to id, "to" to newId, "status" to "handed_off", "summaryChars" to summary.length.toString()),
        )
    }

    private fun recover(request: InvocationRequest): InvocationResult {
        val id = request.input["sessionId"] ?: return inv(request, "sessionId")
        val snap = journal[id] ?: return InvocationResult.Err(request.requestId, "no_journal", id)
        val existing = sessions[id]
        if (existing != null) {
            existing.history = snap
            existing.updatedAtEpochMs = clockMs()
            return InvocationResult.Ok(request.requestId, mapOf("sessionId" to id, "recovered" to "true", "chars" to snap.length.toString()))
        }
        // recreate shell session from journal
        val s = AgentSession(
            id = id,
            botId = request.input["botId"] ?: "recovered",
            modelRef = request.input["modelRef"] ?: "local",
            history = snap,
            createdAtEpochMs = clockMs(),
            updatedAtEpochMs = clockMs(),
        )
        sessions[id] = s
        return InvocationResult.Ok(request.requestId, mapOf("sessionId" to id, "recovered" to "true", "chars" to snap.length.toString()))
    }

    private fun get(request: InvocationRequest): InvocationResult {
        val id = request.input["sessionId"] ?: return inv(request, "sessionId")
        val s = sessions[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "sessionId" to s.id,
                "botId" to s.botId,
                "status" to s.status,
                "modelRef" to s.modelRef,
                "chars" to s.history.length.toString(),
                "parent" to (s.parentSessionId ?: ""),
            ),
        )
    }

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)
}

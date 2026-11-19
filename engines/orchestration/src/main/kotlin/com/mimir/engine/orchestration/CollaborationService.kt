package com.mimir.engine.orchestration

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object CollabCapabilities {
    const val GROUP_OPEN = "collab.group.open"
    const val DELEGATE = "collab.delegate"
    const val HANDOVER = "collab.handover"
    const val STATUS = "collab.status"
    const val EVIDENCE_ADD = "collab.evidence.add"
    const val GROUP_CLOSE = "collab.group.close"
    const val METRICS = "collab.metrics"
    val ALL = listOf(GROUP_OPEN, DELEGATE, HANDOVER, STATUS, EVIDENCE_ADD, GROUP_CLOSE, METRICS)
}

data class GroupSession(
    val id: String,
    val goal: String,
    val memberBotIds: MutableList<String> = mutableListOf(),
    val assignments: MutableMap<String, String> = mutableMapOf(), // botId -> task
    val evidence: MutableList<String> = mutableListOf(),
    var status: String = "open",
)

/**
 * Phase E — multi-bot collaboration without a new Engine type.
 */
class CollaborationService(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val groups = ConcurrentHashMap<String, GroupSession>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("collab-orchestration"),
        name = "Mimir Multi-Bot Collaboration",
        version = "0.4.0",
        provides = CollabCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("orch.run"),
        dataNamespace = "engine.collab",
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
        HealthReport(manifest.id, state == EngineState.Running, state, "groups=${groups.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            CollabCapabilities.GROUP_OPEN -> {
                val id = "grp-${seq.incrementAndGet()}"
                val goal = request.input["goal"] ?: return InvocationResult.Err(request.requestId, "invalid", "goal")
                val members = (request.input["bots"] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() }
                groups[id] = GroupSession(id, goal, members.toMutableList())
                InvocationResult.Ok(request.requestId, mapOf("groupId" to id, "members" to members.size.toString()))
            }
            CollabCapabilities.DELEGATE -> {
                val g = group(request) ?: return InvocationResult.Err(request.requestId, "not_found", "groupId")
                val bot = request.input["botId"] ?: return InvocationResult.Err(request.requestId, "invalid", "botId")
                val task = request.input["task"] ?: return InvocationResult.Err(request.requestId, "invalid", "task")
                if (bot !in g.memberBotIds) return InvocationResult.Err(request.requestId, "not_member", bot)
                g.assignments[bot] = task
                InvocationResult.Ok(request.requestId, mapOf("groupId" to g.id, "botId" to bot, "task" to task))
            }
            CollabCapabilities.HANDOVER -> {
                val g = group(request) ?: return InvocationResult.Err(request.requestId, "not_found", "groupId")
                val from = request.input["fromBot"] ?: return InvocationResult.Err(request.requestId, "invalid", "fromBot")
                val to = request.input["toBot"] ?: return InvocationResult.Err(request.requestId, "invalid", "toBot")
                val task = g.assignments.remove(from) ?: return InvocationResult.Err(request.requestId, "no_assignment", from)
                if (to !in g.memberBotIds) return InvocationResult.Err(request.requestId, "not_member", to)
                g.assignments[to] = task
                g.evidence += "handover:$from->$to:$task"
                InvocationResult.Ok(request.requestId, mapOf("from" to from, "to" to to, "task" to task))
            }
            CollabCapabilities.EVIDENCE_ADD -> {
                val g = group(request) ?: return InvocationResult.Err(request.requestId, "not_found", "groupId")
                val e = request.input["evidence"] ?: return InvocationResult.Err(request.requestId, "invalid", "evidence")
                g.evidence += e
                InvocationResult.Ok(request.requestId, mapOf("count" to g.evidence.size.toString()))
            }
            CollabCapabilities.STATUS -> {
                val g = group(request) ?: return InvocationResult.Err(request.requestId, "not_found", "groupId")
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "groupId" to g.id,
                        "status" to g.status,
                        "members" to g.memberBotIds.joinToString(","),
                        "assignments" to g.assignments.entries.joinToString(";") { "${it.key}=${it.value}" },
                        "evidence" to g.evidence.size.toString(),
                    ),
                )
            }
            CollabCapabilities.METRICS ->
                InvocationResult.Ok(request.requestId, collabMetrics())
            CollabCapabilities.GROUP_CLOSE -> {
                val g = group(request) ?: return InvocationResult.Err(request.requestId, "not_found", "groupId")
                g.status = "closed"
                InvocationResult.Ok(request.requestId, mapOf("groupId" to g.id, "status" to g.status))
            }
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun group(request: InvocationRequest): GroupSession? {
        val id = request.input["groupId"] ?: return null
        return groups[id]
    }

    fun collabMetrics(): Map<String, String> = mapOf(
        "groups" to groups.size.toString(),
        "openGroups" to groups.values.count { it.status != "closed" }.toString(),
        "state" to state.name,
    )

}

package com.mimir.engine.process

import com.mimir.contracts.process.TaskBoardCapabilities
import com.mimir.contracts.process.TaskBoardPort
import com.mimir.contracts.process.TaskItem
import com.mimir.contracts.process.TaskItemStatus
import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Pullboard-inspired task board: atomic claims + leases. Not a new top-level product Engine.
 */
class TaskBoardEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
    private val defaultLeaseMs: Long = 60_000,
    private val store: JdbcTaskStore? = null,
) : MimirEngine, TaskBoardPort {
    private val items = ConcurrentHashMap<String, TaskItem>()
    private val seq = AtomicLong(0)
    private val claimLock = Any()
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("task-board"),
        name = "Mimir Task Board",
        version = "0.4.0",
        provides = TaskBoardCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("task.write", "task.claim"),
        dataNamespace = "engine.task",
        priority = 78,
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
        store?.loadAll()?.forEach { items[it.id] = it }
        state = EngineState.Running; return state
    }
    override suspend fun stop(reason: String): EngineState {
        state = EngineState.Stopped; return state
    }
    override suspend fun health() =
        HealthReport(manifest.id, state == EngineState.Running, state, "items=${items.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            TaskBoardCapabilities.ITEM_CREATE -> create(request)
            TaskBoardCapabilities.ITEM_LIST ->
                InvocationResult.Ok(request.requestId, mapOf("count" to items.size.toString(), "ids" to items.keys.joinToString(",")))
            TaskBoardCapabilities.CLAIM -> claim(request)
            TaskBoardCapabilities.RENEW -> renew(request)
            TaskBoardCapabilities.RELEASE -> release(request)
            TaskBoardCapabilities.EXPIRE -> expire(request)
            TaskBoardCapabilities.STATUS -> status(request)
            TaskBoardCapabilities.ITEM_SUBMIT -> submit(request)
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun create(request: InvocationRequest): InvocationResult {
        val reqs = (request.input["requirementIds"] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (reqs.isEmpty()) return InvocationResult.Err(request.requestId, "invalid", "requirementIds")
        val id = request.input["id"] ?: "T-${seq.incrementAndGet()}"
        if (items.containsKey(id)) return InvocationResult.Err(request.requestId, "exists", id)
        val item = TaskItem(
            id = id,
            title = request.input["title"] ?: id,
            criterion = request.input["criterion"] ?: "tests pass",
            requirementIds = reqs,
            lane = request.input["lane"] ?: "default",
            testCommand = request.input["testCommand"] ?: "",
            dependsOn = (request.input["dependsOn"] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() },
        )
        item.activity += "CREATED@${clockMs()}"
        items[id] = item
        persist(item)
        return InvocationResult.Ok(request.requestId, mapOf("id" to id, "status" to item.status.name))
    }

    private fun claim(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return InvocationResult.Err(request.requestId, "invalid", "id")
        val agent = request.input["agentId"] ?: return InvocationResult.Err(request.requestId, "invalid", "agentId")
        val leaseMs = request.input["leaseMs"]?.toLongOrNull() ?: defaultLeaseMs
        synchronized(claimLock) {
            expireInternal()
            val item = items[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
            if (item.status == TaskItemStatus.Claimed && item.claimAgentId != null && item.leaseUntilEpochMs > clockMs()) {
                if (item.claimAgentId != agent) {
                    return InvocationResult.Err(request.requestId, "already_claimed", item.claimAgentId ?: "")
                }
                // same agent renews
            } else if (item.status != TaskItemStatus.Open && item.status != TaskItemStatus.Rejected) {
                return InvocationResult.Err(request.requestId, "invalid_state", item.status.name)
            }
            // dependency gate
            for (dep in item.dependsOn) {
                val d = items[dep]
                if (d == null || d.status != TaskItemStatus.Accepted) {
                    return InvocationResult.Err(request.requestId, "dependency_blocked", dep)
                }
            }
            item.status = TaskItemStatus.Claimed
            item.claimAgentId = agent
            item.leaseUntilEpochMs = clockMs() + leaseMs
            item.frozenRequirementFingerprint = item.requirementIds.sorted().joinToString(",")
            item.activity += "CLAIMED:$agent@${clockMs()}"
            persist(item)
            return InvocationResult.Ok(
                request.requestId,
                mapOf("id" to id, "agentId" to agent, "leaseUntil" to item.leaseUntilEpochMs.toString()),
            )
        }
    }

    private fun renew(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return InvocationResult.Err(request.requestId, "invalid", "id")
        val agent = request.input["agentId"] ?: return InvocationResult.Err(request.requestId, "invalid", "agentId")
        synchronized(claimLock) {
            val item = items[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
            if (item.claimAgentId != agent) return InvocationResult.Err(request.requestId, "not_owner", agent)
            if (item.leaseUntilEpochMs < clockMs()) return InvocationResult.Err(request.requestId, "lease_expired", id)
            item.leaseUntilEpochMs = clockMs() + (request.input["leaseMs"]?.toLongOrNull() ?: defaultLeaseMs)
            item.activity += "RENEWED:$agent@${clockMs()}"
            persist(item)
            return InvocationResult.Ok(request.requestId, mapOf("id" to id, "leaseUntil" to item.leaseUntilEpochMs.toString()))
        }
    }

    private fun release(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return InvocationResult.Err(request.requestId, "invalid", "id")
        synchronized(claimLock) {
            val item = items[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
            item.status = TaskItemStatus.Open
            item.claimAgentId = null
            item.leaseUntilEpochMs = 0
            item.activity += "RELEASED@${clockMs()}"
            persist(item)
            return InvocationResult.Ok(request.requestId, mapOf("id" to id, "status" to item.status.name))
        }
    }

    private fun expire(request: InvocationRequest): InvocationResult {
        val n = synchronized(claimLock) { expireInternal() }
        return InvocationResult.Ok(request.requestId, mapOf("expired" to n.toString()))
    }

    private fun expireInternal(): Int {
        var n = 0
        val now = clockMs()
        for (item in items.values) {
            if (item.status == TaskItemStatus.Claimed && item.leaseUntilEpochMs in 1 until now) {
                item.status = TaskItemStatus.Open
                item.claimAgentId = null
                item.leaseUntilEpochMs = 0
                item.activity += "LEASE_EXPIRED@${now}"
                persist(item)
                n++
            }
        }
        return n
    }

    private fun status(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return InvocationResult.Err(request.requestId, "invalid", "id")
        val item = items[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "id" to id,
                "status" to item.status.name,
                "claimAgentId" to (item.claimAgentId ?: ""),
                "leaseUntil" to item.leaseUntilEpochMs.toString(),
                "reqs" to item.requirementIds.joinToString(","),
                "activity" to item.activity.size.toString(),
            ),
        )
    }

    /** Internal access for verification service in same process tests */
    private fun submit(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return InvocationResult.Err(request.requestId, "invalid", "id")
        val agent = request.input["agentId"] ?: return InvocationResult.Err(request.requestId, "invalid", "agentId")
        val commit = request.input["commitRef"] ?: return InvocationResult.Err(request.requestId, "invalid", "commitRef")
        if (!markSubmitted(id, commit, agent)) {
            return InvocationResult.Err(request.requestId, "submit_denied", id)
        }
        markReview(id)
        return InvocationResult.Ok(request.requestId, mapOf("id" to id, "status" to "InReview", "commitRef" to commit))
    }

    override fun getItem(id: String): TaskItem? = items[id]
    private fun persist(item: TaskItem) { store?.save(item) }
    fun recoverFromStore() {
        store?.loadAll()?.forEach { items[it.id] = it }
    }
    override fun markSubmitted(id: String, commitRef: String, agentId: String): Boolean {
        val item = items[id] ?: return false
        if (item.claimAgentId != agentId) return false
        item.status = TaskItemStatus.Submitted
        item.commitRef = commitRef
        item.activity += "SUBMITTED:$commitRef@$agentId"
        persist(item)
        return true
    }
    override fun markReview(id: String) {
        items[id]?.also { it.status = TaskItemStatus.InReview; persist(it) }
    }
    override fun markAccepted(id: String) {
        items[id]?.apply {
            status = TaskItemStatus.Accepted
            activity += "ACCEPTED@${clockMs()}"
            persist(this)
        }
    }
    override fun markRejected(id: String, reason: String) {
        items[id]?.apply {
            status = TaskItemStatus.Rejected
            claimAgentId = null
            leaseUntilEpochMs = 0
            activity += "REJECTED:$reason@${clockMs()}"
            persist(this)
        }
    }
}

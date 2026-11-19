package com.mimir.engine.workflow

import com.mimir.contracts.workflow.WorkflowPort
import com.mimir.contracts.workflow.WorkflowId
import com.mimir.contracts.workflow.WorkflowRunId
import com.mimir.contracts.workflow.WorkflowRunInfo
import com.mimir.contracts.workflow.WorkflowRunStatus

import com.mimir.kernel.contract.*
import com.mimir.engine.workflow.persist.InMemoryWorkflowRunStore
import com.mimir.engine.workflow.persist.WorkflowRunStore
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object WorkflowCapabilities {
    const val VALIDATE = "workflow.validate"
    const val RUN_START = "workflow.run.start"
    const val RUN_PAUSE = "workflow.run.pause"
    const val RUN_RESUME = "workflow.run.resume"
    const val RUN_CANCEL = "workflow.run.cancel"
    const val RUN_STATUS = "workflow.run.status"
    const val CHECKPOINT = "workflow.run.checkpoint"
    const val METRICS = "workflow.metrics"
    val ALL = listOf(VALIDATE, RUN_START, RUN_PAUSE, RUN_RESUME, RUN_CANCEL, RUN_STATUS, CHECKPOINT, METRICS)
}

enum class RunStatus {
    Pending, Running, Paused, WaitingApproval, Succeeded, Failed, Cancelled
}

data class WorkflowNode(val id: String, val type: String, val capability: String = "")
data class WorkflowEdge(val from: String, val to: String, val condition: String = "")
data class WorkflowDef(
    val id: String,
    val version: String,
    val nodes: List<WorkflowNode>,
    val edges: List<WorkflowEdge>,
)

data class Checkpoint(val id: String, val atEpochMs: Long, val nodeId: String, val stateRef: String)
data class WorkflowRun(
    val runId: String,
    val workflowId: String,
    val workflowVersion: String,
    var status: RunStatus,
    var currentNodeId: String?,
    val nodeOrder: List<String>,
    var index: Int = 0,
    val checkpoints: MutableList<Checkpoint> = mutableListOf(),
    val completedNodes: MutableSet<String> = mutableSetOf(),
    val idempotencyKey: String = "",
)

/**
 * Native Mimir Workflow runtime — no LangGraph dependency.
 * Supports validate, start, pause, resume, cancel, checkpoint.
 */
class WorkflowEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
    private val store: WorkflowRunStore = InMemoryWorkflowRunStore(),
) : MimirEngine, WorkflowPort {
    private val defs = ConcurrentHashMap<String, WorkflowDef>()
    private val runs = ConcurrentHashMap<String, WorkflowRun>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("workflow-core"),
        name = "Mimir Workflow",
        version = "0.3.0",
        provides = WorkflowCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("workflow.read", "workflow.run"),
        dataNamespace = "engine.workflow",
        priority = 87,
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
        HealthReport(manifest.id, state == EngineState.Running, state, "workflow=0.3.0 runs=${runs.size}", clockMs())

    fun registerDef(def: WorkflowDef) {
        defs[def.id] = def
    }

    fun validate(def: WorkflowDef): List<String> {
        val errors = mutableListOf<String>()
        if (def.nodes.isEmpty()) errors += "no_nodes"
        val ids = def.nodes.map { it.id }.toSet()
        if (ids.size != def.nodes.size) errors += "duplicate_node_ids"
        for (e in def.edges) {
            if (e.from !in ids) errors += "edge_from_missing:${e.from}"
            if (e.to !in ids) errors += "edge_to_missing:${e.to}"
        }
        return errors
    }

    /** Topological-ish order: nodes with no inbound first, then BFS. Fallback: declaration order. */
    fun nodeOrder(def: WorkflowDef): List<String> {
        val inbound = def.nodes.associate { it.id to 0 }.toMutableMap()
        for (e in def.edges) inbound[e.to] = (inbound[e.to] ?: 0) + 1
        val q = ArrayDeque(inbound.filter { it.value == 0 }.keys)
        val order = mutableListOf<String>()
        val seen = mutableSetOf<String>()
        while (q.isNotEmpty()) {
            val n = q.removeFirst()
            if (!seen.add(n)) continue
            order += n
            for (e in def.edges.filter { it.from == n }) {
                inbound[e.to] = (inbound[e.to] ?: 1) - 1
                if (inbound[e.to] == 0) q.add(e.to!!)
            }
        }
        if (order.size < def.nodes.size) {
            for (n in def.nodes) if (n.id !in seen) order += n.id
        }
        return order
    }

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            WorkflowCapabilities.VALIDATE -> validateInv(request)
            WorkflowCapabilities.RUN_START -> startRun(request)
            WorkflowCapabilities.RUN_PAUSE -> pause(request)
            WorkflowCapabilities.RUN_RESUME -> resume(request)
            WorkflowCapabilities.RUN_CANCEL -> cancel(request)
            WorkflowCapabilities.RUN_STATUS -> status(request)
            WorkflowCapabilities.CHECKPOINT -> checkpoint(request)
            WorkflowCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "defs" to defs.size.toString(),
                        "runs" to runs.size.toString(),
                        "running" to runs.values.count { it.status == RunStatus.Running }.toString(),
                        "state" to state.name,
                    ),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun validateInv(request: InvocationRequest): InvocationResult {
        val id = request.input["workflowId"] ?: return err(request, "workflowId")
        val def = defs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        val errors = validate(def)
        return InvocationResult.Ok(
            request.requestId,
            mapOf("valid" to (errors.isEmpty()).toString(), "errors" to errors.joinToString(";")),
        )
    }

    private fun startRun(request: InvocationRequest): InvocationResult {
        val wid = request.input["workflowId"] ?: return err(request, "workflowId")
        val def = defs[wid] ?: return InvocationResult.Err(request.requestId, "not_found", wid)
        val errors = validate(def)
        if (errors.isNotEmpty()) {
            return InvocationResult.Err(request.requestId, "invalid_workflow", errors.joinToString(";"))
        }
        val idem = request.input["idempotencyKey"] ?: ""
        if (idem.isNotEmpty()) {
            val existing = runs.values.find { it.idempotencyKey == idem && it.workflowId == wid }
            if (existing != null) {
                return InvocationResult.Ok(
                    request.requestId,
                    mapOf("runId" to existing.runId, "status" to existing.status.name, "deduped" to "true"),
                )
            }
        }
        val order = nodeOrder(def)
        val runId = "wr-${seq.incrementAndGet()}"
        val run = WorkflowRun(
            runId = runId,
            workflowId = def.id,
            workflowVersion = def.version,
            status = RunStatus.Running,
            currentNodeId = order.firstOrNull(),
            nodeOrder = order,
            index = 0,
            idempotencyKey = idem,
        )
        // auto-advance non-approval nodes as "completed" record for native demo
        while (run.index < order.size) {
            val nodeId = order[run.index]
            val node = def.nodes.first { it.id == nodeId }
            if (node.type == "human_approval") {
                run.status = RunStatus.WaitingApproval
                run.currentNodeId = nodeId
                break
            }
            run.completedNodes += nodeId
            run.checkpoints += Checkpoint("cp-${run.checkpoints.size + 1}", clockMs(), nodeId, "done")
            run.index++
            run.currentNodeId = order.getOrNull(run.index)
        }
        if (run.status == RunStatus.Running && run.index >= order.size) {
            run.status = RunStatus.Succeeded
            run.currentNodeId = null
        }
        runs[runId] = run
        store.save(run)
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "runId" to runId,
                "status" to run.status.name,
                "currentNodeId" to (run.currentNodeId ?: ""),
                "completed" to run.completedNodes.joinToString(","),
            ),
        )
    }

    private fun pause(request: InvocationRequest): InvocationResult {
        val run = runOf(request) ?: return InvocationResult.Err(request.requestId, "not_found", "runId")
        if (run.status != RunStatus.Running && run.status != RunStatus.WaitingApproval) {
            return InvocationResult.Err(request.requestId, "invalid_state", run.status.name)
        }
        run.status = RunStatus.Paused
        store.save(run)
        return InvocationResult.Ok(request.requestId, mapOf("runId" to run.runId, "status" to run.status.name))
    }

    private fun resume(request: InvocationRequest): InvocationResult {
        val run = runOf(request) ?: return InvocationResult.Err(request.requestId, "not_found", "runId")
        val def = defs[run.workflowId] ?: return InvocationResult.Err(request.requestId, "not_found", run.workflowId)
        if (run.status == RunStatus.WaitingApproval) {
            val granted = !request.input["approvalToken"].isNullOrBlank()
            if (!granted) return InvocationResult.Err(request.requestId, "approval_required", run.currentNodeId ?: "")
            run.completedNodes += run.currentNodeId!!
            run.checkpoints += Checkpoint("cp-${run.checkpoints.size + 1}", clockMs(), run.currentNodeId!!, "approved")
            run.index++
        } else if (run.status != RunStatus.Paused) {
            return InvocationResult.Err(request.requestId, "invalid_state", run.status.name)
        }
        run.status = RunStatus.Running
        while (run.index < run.nodeOrder.size) {
            val nodeId = run.nodeOrder[run.index]
            val node = def.nodes.first { it.id == nodeId }
            if (node.type == "human_approval" && nodeId !in run.completedNodes) {
                run.status = RunStatus.WaitingApproval
                run.currentNodeId = nodeId
                break
            }
            run.completedNodes += nodeId
            run.checkpoints += Checkpoint("cp-${run.checkpoints.size + 1}", clockMs(), nodeId, "done")
            run.index++
            run.currentNodeId = run.nodeOrder.getOrNull(run.index)
        }
        if (run.status == RunStatus.Running && run.index >= run.nodeOrder.size) {
            run.status = RunStatus.Succeeded
            run.currentNodeId = null
        }
        store.save(run)
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "runId" to run.runId,
                "status" to run.status.name,
                "currentNodeId" to (run.currentNodeId ?: ""),
                "checkpoints" to run.checkpoints.size.toString(),
            ),
        )
    }

    private fun cancel(request: InvocationRequest): InvocationResult {
        val run = runOf(request) ?: return InvocationResult.Err(request.requestId, "not_found", "runId")
        run.status = RunStatus.Cancelled
        store.save(run)
        return InvocationResult.Ok(request.requestId, mapOf("runId" to run.runId, "status" to run.status.name))
    }

    private fun status(request: InvocationRequest): InvocationResult {
        val run = runOf(request) ?: return InvocationResult.Err(request.requestId, "not_found", "runId")
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "runId" to run.runId,
                "status" to run.status.name,
                "currentNodeId" to (run.currentNodeId ?: ""),
                "completed" to run.completedNodes.joinToString(","),
                "checkpoints" to run.checkpoints.size.toString(),
            ),
        )
    }

    private fun checkpoint(request: InvocationRequest): InvocationResult {
        val run = runOf(request) ?: return InvocationResult.Err(request.requestId, "not_found", "runId")
        val nodeId = run.currentNodeId ?: "end"
        val cp = Checkpoint("cp-${run.checkpoints.size + 1}", clockMs(), nodeId, request.input["stateRef"] ?: "manual")
        run.checkpoints += cp
        return InvocationResult.Ok(request.requestId, mapOf("checkpointId" to cp.id, "nodeId" to nodeId, "count" to run.checkpoints.size.toString()))
    }

    private fun runOf(request: InvocationRequest): WorkflowRun? {
        val id = request.input["runId"] ?: return null
        runs[id]?.let { return it }
        val loaded = store.load(id) ?: return null
        runs[id] = loaded
        return loaded
    }

    private fun err(request: InvocationRequest, field: String) =
        InvocationResult.Err(request.requestId, "invalid", field)

    // --- WorkflowPort ---
    private fun mapStatus(s: RunStatus): WorkflowRunStatus = when (s) {
        RunStatus.Pending -> WorkflowRunStatus.Pending
        RunStatus.Running -> WorkflowRunStatus.Running
        RunStatus.Paused -> WorkflowRunStatus.Paused
        RunStatus.WaitingApproval -> WorkflowRunStatus.WaitingApproval
        RunStatus.Succeeded -> WorkflowRunStatus.Succeeded
        RunStatus.Failed -> WorkflowRunStatus.Failed
        RunStatus.Cancelled -> WorkflowRunStatus.Cancelled
    }
    override fun startRun(workflowId: WorkflowId, version: String): WorkflowRunInfo {
        val id = "wr-${seq.incrementAndGet()}"
        val run = WorkflowRun(id, workflowId.value, version, RunStatus.Running, null, emptyList())
        runs[id] = run
        return WorkflowRunInfo(WorkflowRunId(id), workflowId, WorkflowRunStatus.Running, null)
    }
    override fun pauseRun(runId: WorkflowRunId): WorkflowRunInfo? {
        val r = runs[runId.value] ?: return null
        r.status = RunStatus.Paused
        return WorkflowRunInfo(runId, WorkflowId(r.workflowId), mapStatus(r.status), r.currentNodeId)
    }
    override fun resumeRun(runId: WorkflowRunId): WorkflowRunInfo? {
        val r = runs[runId.value] ?: return null
        r.status = RunStatus.Running
        return WorkflowRunInfo(runId, WorkflowId(r.workflowId), mapStatus(r.status), r.currentNodeId)
    }
    override fun cancelRun(runId: WorkflowRunId): WorkflowRunInfo? {
        val r = runs[runId.value] ?: return null
        r.status = RunStatus.Cancelled
        return WorkflowRunInfo(runId, WorkflowId(r.workflowId), mapStatus(r.status), r.currentNodeId)
    }
    override fun status(runId: WorkflowRunId): WorkflowRunInfo? {
        val r = runs[runId.value] ?: return null
        return WorkflowRunInfo(runId, WorkflowId(r.workflowId), mapStatus(r.status), r.currentNodeId)
    }

}

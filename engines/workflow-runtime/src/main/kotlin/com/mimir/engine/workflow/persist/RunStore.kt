package com.mimir.engine.workflow.persist

import com.mimir.engine.workflow.Checkpoint
import com.mimir.engine.workflow.RunStatus
import com.mimir.engine.workflow.WorkflowRun
import java.util.concurrent.ConcurrentHashMap

/** Persistence boundary — SQLDelight adapter later; ADR-008 compliant. */
interface WorkflowRunStore {
    fun save(run: WorkflowRun)
    fun load(runId: String): WorkflowRun?
    fun listByWorkflow(workflowId: String): List<WorkflowRun>
}

class InMemoryWorkflowRunStore : WorkflowRunStore {
    private val runs = ConcurrentHashMap<String, WorkflowRun>()
    override fun save(run: WorkflowRun) {
        // defensive copy of mutable collections
        runs[run.runId] = run.copy(
            checkpoints = run.checkpoints.toMutableList(),
            completedNodes = run.completedNodes.toMutableSet(),
        )
    }
    override fun load(runId: String) = runs[runId]
    override fun listByWorkflow(workflowId: String) =
        runs.values.filter { it.workflowId == workflowId }
}

/** Snapshot DTO for export/audit without exposing engine internals. */
data class RunSnapshot(
    val runId: String,
    val workflowId: String,
    val status: String,
    val currentNodeId: String?,
    val checkpointCount: Int,
    val completed: List<String>,
)

fun WorkflowRun.toSnapshot() = RunSnapshot(
    runId, workflowId, status.name, currentNodeId, checkpoints.size, completedNodes.toList(),
)

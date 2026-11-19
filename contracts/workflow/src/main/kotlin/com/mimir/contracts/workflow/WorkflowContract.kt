package com.mimir.contracts.workflow

object WorkflowCapabilityIds {
    const val VALIDATE = "workflow.validate"
    const val RUN_START = "workflow.run.start"
    const val RUN_PAUSE = "workflow.run.pause"
    const val RUN_RESUME = "workflow.run.resume"
    const val RUN_CANCEL = "workflow.run.cancel"
    const val RUN_STATUS = "workflow.run.status"
    const val CHECKPOINT = "workflow.run.checkpoint"
    val ALL = listOf(VALIDATE, RUN_START, RUN_PAUSE, RUN_RESUME, RUN_CANCEL, RUN_STATUS, CHECKPOINT)
}

enum class WorkflowRunStatus {
    Pending, Running, Paused, WaitingApproval, Succeeded, Failed, Cancelled
}

data class WorkflowId(val value: String)
data class WorkflowRunId(val value: String)

data class WorkflowRunInfo(
    val runId: WorkflowRunId,
    val workflowId: WorkflowId,
    val status: WorkflowRunStatus,
    val currentNodeId: String? = null,
)

interface WorkflowPort {
    fun startRun(workflowId: WorkflowId, version: String = "1"): WorkflowRunInfo
    fun pauseRun(runId: WorkflowRunId): WorkflowRunInfo?
    fun resumeRun(runId: WorkflowRunId): WorkflowRunInfo?
    fun cancelRun(runId: WorkflowRunId): WorkflowRunInfo?
    fun status(runId: WorkflowRunId): WorkflowRunInfo?
}

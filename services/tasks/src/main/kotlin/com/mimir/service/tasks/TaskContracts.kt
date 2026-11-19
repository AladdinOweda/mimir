package com.mimir.service.tasks

/**
 * ADR-039 task execution lifecycle + independent verification verdict.
 */
data class TaskId(val value: String)

enum class TaskStatus {
    Draft,
    Queued,
    Ready,
    Running,
    Paused,
    Blocked,
    WaitingApproval,
    Completed,
    Failed,
    Cancelled,
    ;

    /** Legacy UI aliases — prefer explicit statuses. */
    companion object {
        fun fromLegacy(name: String): TaskStatus = when (name) {
            "Open" -> Draft
            "InProgress" -> Running
            "Done" -> Completed // verification separate
            else -> valueOf(name)
        }
    }
}

/** Independent of [TaskStatus] (ADR-036). */
enum class VerificationVerdict {
    Unverified,
    Verified,
    Inconclusive,
    Rejected,
}

data class TaskSummary(
    val id: TaskId,
    val title: String,
    val status: TaskStatus,
    val verification: VerificationVerdict = VerificationVerdict.Unverified,
)

/** Forge Kanban column is a view over lifecycle + verification. */
enum class ForgeColumn {
    Backlog,
    Ready,
    InProgress,
    Blocked,
    WaitingForApproval,
    InReview,
    Done,
}

/**
 * Forge Kanban view (ADR-039). G-04 accepted 2026-11-10:
 * Inconclusive → InReview; Rejected → Ready; Failed/Cancelled stay off Done (Backlog filter).
 */
fun TaskSummary.toForgeColumn(): ForgeColumn = when {
    status == TaskStatus.Draft || status == TaskStatus.Queued -> ForgeColumn.Backlog
    status == TaskStatus.Ready -> ForgeColumn.Ready
    status == TaskStatus.Running || status == TaskStatus.Paused -> ForgeColumn.InProgress
    status == TaskStatus.Blocked -> ForgeColumn.Blocked
    status == TaskStatus.WaitingApproval -> ForgeColumn.WaitingForApproval
    status == TaskStatus.Completed && verification == VerificationVerdict.Verified -> ForgeColumn.Done
    status == TaskStatus.Completed && verification == VerificationVerdict.Unverified -> ForgeColumn.InReview
    status == TaskStatus.Completed && verification == VerificationVerdict.Inconclusive -> ForgeColumn.InReview
    status == TaskStatus.Completed && verification == VerificationVerdict.Rejected -> ForgeColumn.Ready
    status == TaskStatus.Failed || status == TaskStatus.Cancelled -> ForgeColumn.Backlog
    else -> ForgeColumn.Backlog
}

interface TaskDirectory {
    fun list(): List<TaskSummary>
    fun get(id: TaskId): TaskSummary?
}

class InMemoryTaskDirectory : TaskDirectory {
    private val items = linkedMapOf<String, TaskSummary>()
    override fun list() = items.values.toList()
    override fun get(id: TaskId) = items[id.value]
    fun put(summary: TaskSummary) {
        items[summary.id.value] = summary
    }
}

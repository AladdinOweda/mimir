package com.mimir.contracts.process

/**
 * Process runtime + task-board + ownership contracts.
 * argv-first; shell is separate high-risk capability.
 */
object ProcessCapabilityIds {
    const val EXEC_ARGV = "process.exec.argv"
    const val EXEC_SHELL = "process.exec.shell"
    const val METRICS = "process.metrics"
    val ALL = listOf(EXEC_ARGV, EXEC_SHELL, METRICS)
}

object TaskBoardCapabilityIds {
    const val ITEM_CREATE = "task.item.create"
    const val ITEM_LIST = "task.item.list"
    const val CLAIM = "task.claim"
    const val RENEW = "task.claim.renew"
    const val RELEASE = "task.claim.release"
    const val EXPIRE = "task.claim.expire"
    const val STATUS = "task.item.status"
    const val SUBMIT = "task.item.submit"
    val ALL = listOf(ITEM_CREATE, ITEM_LIST, CLAIM, RENEW, RELEASE, EXPIRE, STATUS, SUBMIT)
}

/** Alias used by TaskBoardEngine — same IDs as TaskBoardCapabilityIds. */
object TaskBoardCapabilities {
    const val ITEM_CREATE = TaskBoardCapabilityIds.ITEM_CREATE
    const val ITEM_LIST = TaskBoardCapabilityIds.ITEM_LIST
    const val CLAIM = TaskBoardCapabilityIds.CLAIM
    const val RENEW = TaskBoardCapabilityIds.RENEW
    const val RELEASE = TaskBoardCapabilityIds.RELEASE
    const val EXPIRE = TaskBoardCapabilityIds.EXPIRE
    const val STATUS = TaskBoardCapabilityIds.STATUS
    const val ITEM_SUBMIT = TaskBoardCapabilityIds.SUBMIT
    val ALL = TaskBoardCapabilityIds.ALL
}

object OwnershipCapabilityIds {
    const val CLAIM_LANE = "ownership.lane.claim"
    const val RELEASE_LANE = "ownership.lane.release"
    const val CHECK = "ownership.lane.check"
    val ALL = listOf(CLAIM_LANE, RELEASE_LANE, CHECK)
}

object ProcessPermissionIds {
    const val EXEC = "process.exec"
    const val TASK_WRITE = "task.write"
    const val OWNERSHIP = "ownership.write"
}

enum class TaskItemStatus { Open, Claimed, Submitted, InReview, Accepted, Rejected, Blocked }

data class TaskItem(
    val id: String,
    val title: String,
    val criterion: String,
    val requirementIds: List<String>,
    val lane: String = "default",
    val testCommand: String = "",
    val dependsOn: List<String> = emptyList(),
    var status: TaskItemStatus = TaskItemStatus.Open,
    var claimAgentId: String? = null,
    var leaseUntilEpochMs: Long = 0L,
    var commitRef: String? = null,
    var frozenRequirementFingerprint: String? = null,
    val activity: MutableList<String> = mutableListOf(),
)

data class ProcessArgvRequest(
    val argv: List<String>,
    val cwd: String? = null,
    val workspaceRoot: String? = null,
    val timeoutMs: Long = 15_000,
)

data class ProcessShellRequest(
    val command: String,
    val cwd: String? = null,
    val workspaceRoot: String? = null,
    val timeoutMs: Long = 15_000,
    val approvalToken: String? = null,
)

data class ProcessExecOutcome(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val timedOut: Boolean = false,
    val errorCode: String? = null,
)

interface ProcessPort {
    fun execArgv(request: ProcessArgvRequest): ProcessExecOutcome
    fun execShell(request: ProcessShellRequest): ProcessExecOutcome
}

/**
 * Full task-board port: claim/list + verification lifecycle (no engine import needed).
 */
interface TaskBoardPort {
    fun getItem(id: String): TaskItem?
    fun markSubmitted(id: String, commitRef: String, agentId: String): Boolean
    fun markReview(id: String)
    fun markAccepted(id: String)
    fun markRejected(id: String, reason: String)
}

interface OwnershipPort {
    fun claimLane(pathPrefix: String, agentId: String): Boolean
    fun releaseLane(pathPrefix: String, agentId: String): Boolean
}

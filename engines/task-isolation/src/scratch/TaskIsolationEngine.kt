package com.mimir.engine.task

import com.mimir.engine.task.workspace.GitWorktreeBackendActual
import com.mimir.engine.task.workspace.GitWorktreeIsolationBackend
import com.mimir.engine.task.workspace.IsolationBackend
import com.mimir.engine.task.workspace.NoneIsolationBackend
import com.mimir.engine.task.workspace.TempDirIsolationBackend
import com.mimir.engine.task.workspace.WorkspaceHandle
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.EngineId
import com.mimir.kernel.contract.EngineManifest
import com.mimir.kernel.contract.EngineState
import com.mimir.kernel.contract.HealthReport
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import com.mimir.kernel.contract.KernelErrorCodes
import com.mimir.kernel.contract.MimirEngine
import com.mimir.kernel.contract.RuntimeMode

/**
 * Task isolation v0.2 — prefers real Git worktree on Desktop when available.
 */
class TaskIsolationEngine(
    private val useRealGit: Boolean = true,
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {

    private val gitBackend: IsolationBackend =
        if (useRealGit) GitWorktreeBackendActual() else GitWorktreeIsolationBackend()

    private val backends: Map<IsolationKind, IsolationBackend> = mapOf(
        IsolationKind.None to NoneIsolationBackend(),
        IsolationKind.TempDir to TempDirIsolationBackend(),
        IsolationKind.GitWorktree to gitBackend,
    )

    private val tasks = linkedMapOf<String, WorkTask>()
    private val workspaces = mutableMapOf<String, WorkspaceHandle>()
    private val repoRoots = mutableMapOf<String, String>()
    private var counter = 0L
    private var state: EngineState = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("task-isolation"),
        name = "Mimir Task Execution & Isolation",
        version = "0.2.0",
        apiContract = "mimir.engine/1.x",
        publisher = "mimir",
        runtimeMode = RuntimeMode.Embedded,
        platforms = listOf("desktop"),
        provides = TaskCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf(TaskPermissions.READ, TaskPermissions.RUN, TaskPermissions.MERGE),
        dataNamespace = "engine.task-isolation",
        priority = 80,
    )

    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState {
        state = EngineState.Configured
        return state
    }
    override suspend fun start(): EngineState {
        state = EngineState.Running
        return state
    }
    override suspend fun stop(reason: String): EngineState {
        state = EngineState.Stopped
        return state
    }
    override suspend fun health() = HealthReport(
        engineId = manifest.id,
        healthy = state == EngineState.Running,
        state = state,
        detail = "tasks=${tasks.size} git=${gitBackend::class.simpleName}",
        checkedAtEpochMs = clockMs(),
    )

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            TaskCapabilities.CREATE -> create(request)
            TaskCapabilities.GET -> get(request)
            TaskCapabilities.LIST -> list(request)
            TaskCapabilities.QUEUE -> queue(request)
            TaskCapabilities.START -> startTask(request)
            TaskCapabilities.REPORT -> report(request)
            TaskCapabilities.SUBMIT_REVIEW -> submitReview(request)
            TaskCapabilities.APPROVE_MERGE -> approveMerge(request)
            TaskCapabilities.CANCEL -> cancel(request)
            TaskCapabilities.RECOVER -> recover(request)
            else -> InvocationResult.Err(request.requestId, TaskErrorCodes.UNSUPPORTED, request.capability.id.value)
        }
    }

    private fun create(request: InvocationRequest): InvocationResult {
        val title = request.input["title"] ?: return inv(request, "title")
        val isolation = request.input["isolation"]?.let {
            runCatching { IsolationKind.valueOf(it) }.getOrNull()
        } ?: IsolationKind.TempDir
        counter++
        val id = WorkTaskId("task-$counter")
        val task = WorkTask(
            taskId = id,
            title = title,
            state = TaskState.Todo,
            isolation = isolation,
            agentId = request.input["agentId"],
            contextPackageId = request.input["contextPackageId"],
            createdAtEpochMs = clockMs(),
        )
        tasks[id.value] = task
        request.input["basePath"]?.let { repoRoots[id.value] = it }
        return InvocationResult.Ok(
            request.requestId,
            mapOf("taskId" to id.value, "state" to task.state.name, "isolation" to isolation.name),
        )
    }

    private fun get(request: InvocationRequest): InvocationResult {
        val id = request.input["taskId"] ?: return inv(request, "taskId")
        val t = tasks[id] ?: return InvocationResult.Err(request.requestId, TaskErrorCodes.NOT_FOUND, id)
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "taskId" to t.taskId.value,
                "title" to t.title,
                "state" to t.state.name,
                "isolation" to t.isolation.name,
                "workspacePath" to (t.workspacePath ?: ""),
                "reviewNotes" to (t.reviewNotes ?: ""),
            ),
        )
    }

    private fun list(request: InvocationRequest): InvocationResult {
        val list = tasks.values.toList()
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "count" to list.size.toString(),
                "ids" to list.joinToString(",") { it.taskId.value },
                "states" to list.joinToString(",") { it.state.name },
            ),
        )
    }

    private fun queue(request: InvocationRequest): InvocationResult {
        val id = request.input["taskId"] ?: return inv(request, "taskId")
        val t = tasks[id] ?: return InvocationResult.Err(request.requestId, TaskErrorCodes.NOT_FOUND, id)
        if (t.state != TaskState.Todo) {
            return InvocationResult.Err(request.requestId, TaskErrorCodes.INVALID_STATE, t.state.name)
        }
        tasks[id] = t.copy(state = TaskState.Queued)
        return InvocationResult.Ok(request.requestId, mapOf("taskId" to id, "state" to "Queued"))
    }

    private fun startTask(request: InvocationRequest): InvocationResult {
        val id = request.input["taskId"] ?: return inv(request, "taskId")
        val t = tasks[id] ?: return InvocationResult.Err(request.requestId, TaskErrorCodes.NOT_FOUND, id)
        if (t.state != TaskState.Todo && t.state != TaskState.Queued && t.state != TaskState.Preparing) {
            return InvocationResult.Err(request.requestId, TaskErrorCodes.INVALID_STATE, t.state.name)
        }
        val backend = backends[t.isolation] ?: NoneIsolationBackend()
        val base = request.input["basePath"] ?: repoRoots[id]
        if (t.isolation == IsolationKind.GitWorktree && base.isNullOrBlank()) {
            return InvocationResult.Err(
                request.requestId,
                TaskErrorCodes.INVALID_INPUT,
                "basePath required for GitWorktree (path to git repo)",
            )
        }
        val handle = backend.prepare(id, base)
        if (handle == null) {
            return InvocationResult.Err(
                request.requestId,
                TaskErrorCodes.NOT_IMPLEMENTED,
                "isolation_prepare_failed:${t.isolation.name}",
            )
        }
        workspaces[id] = handle
        if (base != null) repoRoots[id] = base
        tasks[id] = t.copy(state = TaskState.Running, workspacePath = handle.path)
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "taskId" to id,
                "state" to TaskState.Running.name,
                "workspacePath" to handle.path,
                "isolation" to handle.kind.name,
                "backend" to backend::class.simpleName.orEmpty(),
            ),
        )
    }

    private fun report(request: InvocationRequest): InvocationResult {
        val id = request.input["taskId"] ?: return inv(request, "taskId")
        val t = tasks[id] ?: return InvocationResult.Err(request.requestId, TaskErrorCodes.NOT_FOUND, id)
        val dirty = request.input["dirty"]?.equals("true", true) == true
        val ws = workspaces[id]
        if (ws != null && dirty) {
            val backend = backends[t.isolation] ?: NoneIsolationBackend()
            workspaces[id] = backend.markDirty(ws)
        }
        // re-check git dirty if worktree
        val liveDirty = workspaces[id]?.let { backends[t.isolation]?.isCleanForMerge(it) == false } ?: dirty
        return InvocationResult.Ok(
            request.requestId,
            mapOf("taskId" to id, "state" to t.state.name, "dirty" to liveDirty.toString()),
        )
    }

    private fun submitReview(request: InvocationRequest): InvocationResult {
        val id = request.input["taskId"] ?: return inv(request, "taskId")
        val t = tasks[id] ?: return InvocationResult.Err(request.requestId, TaskErrorCodes.NOT_FOUND, id)
        if (t.state != TaskState.Running && t.state != TaskState.AwaitingInput) {
            return InvocationResult.Err(request.requestId, TaskErrorCodes.INVALID_STATE, t.state.name)
        }
        tasks[id] = t.copy(state = TaskState.ToReview, reviewNotes = request.input["notes"])
        return InvocationResult.Ok(request.requestId, mapOf("taskId" to id, "state" to TaskState.ToReview.name))
    }

    private fun approveMerge(request: InvocationRequest): InvocationResult {
        val id = request.input["taskId"] ?: return inv(request, "taskId")
        val t = tasks[id] ?: return InvocationResult.Err(request.requestId, TaskErrorCodes.NOT_FOUND, id)
        if (t.state != TaskState.ToReview) {
            return InvocationResult.Err(request.requestId, TaskErrorCodes.REVIEW_REQUIRED, t.state.name)
        }
        // Kernel ApprovalToken only — never trust approvalGranted boolean
        if (request.input["approvalToken"].isNullOrBlank()) {
            return InvocationResult.Err(request.requestId, TaskErrorCodes.REVIEW_REQUIRED, "approval_token_required")
        }
        val ws = workspaces[id]
        val backend = backends[t.isolation] ?: NoneIsolationBackend()
        val clean = ws?.let { backend.isCleanForMerge(it) } ?: true
        // Merge validation: for GitWorktree we require clean OR explicit forceMerge
        val force = request.input["forceMerge"]?.equals("true", true) == true
        if (t.isolation == IsolationKind.GitWorktree && !clean && !force) {
            return InvocationResult.Err(
                request.requestId,
                TaskErrorCodes.INVALID_STATE,
                "worktree_dirty_use_forceMerge_or_commit",
            )
        }
        tasks[id] = t.copy(
            state = TaskState.Done,
            reviewNotes = (t.reviewNotes ?: "") + if (clean) " | merged_clean" else " | force_merged_dirty",
        )
        return InvocationResult.Ok(
            request.requestId,
            mapOf("taskId" to id, "state" to TaskState.Done.name, "backendClean" to clean.toString()),
        )
    }

    private fun cancel(request: InvocationRequest): InvocationResult {
        val id = request.input["taskId"] ?: return inv(request, "taskId")
        val t = tasks[id] ?: return InvocationResult.Err(request.requestId, TaskErrorCodes.NOT_FOUND, id)
        tasks[id] = t.copy(state = TaskState.Cancelled)
        return InvocationResult.Ok(request.requestId, mapOf("taskId" to id, "state" to "Cancelled"))
    }

    private fun recover(request: InvocationRequest): InvocationResult {
        val id = request.input["taskId"] ?: return inv(request, "taskId")
        val t = tasks[id] ?: return InvocationResult.Err(request.requestId, TaskErrorCodes.NOT_FOUND, id)
        val ws = workspaces[id]
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "taskId" to id,
                "state" to t.state.name,
                "workspacePath" to (ws?.path ?: t.workspacePath ?: ""),
                "repoRoot" to (repoRoots[id] ?: ""),
                "recoverable" to (t.state != TaskState.Done && t.state != TaskState.Cancelled).toString(),
            ),
        )
    }

    private fun inv(request: InvocationRequest, msg: String) =
        InvocationResult.Err(request.requestId, TaskErrorCodes.INVALID_INPUT, msg)
}

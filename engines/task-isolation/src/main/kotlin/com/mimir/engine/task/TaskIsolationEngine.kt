package com.mimir.engine.task

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object TaskIsolationCapabilities {
    const val CREATE = "task.isolation.create"
    const val CLAIM = "task.isolation.claim"
    const val RELEASE = "task.isolation.release"
    const val STATUS = "task.isolation.status"
    const val APPROVE_MERGE = "task.approve_merge"
    const val LIST = "task.isolation.list"
    val ALL = listOf(CREATE, CLAIM, RELEASE, STATUS, APPROVE_MERGE, LIST)
}

enum class IsolationKind { Memory, WorktreeStub }

data class IsolationWorkspace(
    val id: String,
    val taskId: String,
    val kind: IsolationKind,
    var ownerAgentId: String? = null,
    var status: String = "Ready", // Ready | Claimed | Released | MergeAccepted
    val rootPath: String = "",
)

/**
 * Task isolation — in-memory workspaces (+ optional worktree path metadata).
 * Git merge is never automatic without approvalToken; slim backends don't run git.
 */
class TaskIsolationEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val spaces = ConcurrentHashMap<String, IsolationWorkspace>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("task-isolation"),
        name = "Mimir Task Isolation",
        version = "0.6.0",
        provides = TaskIsolationCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("task.read", "task.run", "task.merge"),
        dataNamespace = "engine.task-isolation",
        priority = 80,
        platforms = listOf("desktop", "android"),
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
        HealthReport(manifest.id, state == EngineState.Running, state, "workspaces=${spaces.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            TaskIsolationCapabilities.CREATE -> {
                val taskId = request.input["taskId"] ?: return inv(request, "taskId")
                val kind = try {
                    IsolationKind.valueOf(request.input["kind"] ?: "Memory")
                } catch (_: Exception) {
                    return InvocationResult.Err(request.requestId, "invalid", "kind")
                }
                val id = "iso-${seq.incrementAndGet()}"
                val root = request.input["rootPath"] ?: "memory://$id"
                spaces[id] = IsolationWorkspace(id, taskId, kind, rootPath = root)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("id" to id, "taskId" to taskId, "kind" to kind.name, "status" to "Ready"),
                )
            }
            TaskIsolationCapabilities.CLAIM -> {
                val ws = space(request) ?: return notFound(request)
                val agent = request.input["agentId"] ?: return inv(request, "agentId")
                if (ws.status == "Claimed" && ws.ownerAgentId != null && ws.ownerAgentId != agent) {
                    return InvocationResult.Err(request.requestId, "owned_by", ws.ownerAgentId!!)
                }
                ws.ownerAgentId = agent
                ws.status = "Claimed"
                InvocationResult.Ok(request.requestId, mapOf("id" to ws.id, "owner" to agent, "status" to "Claimed"))
            }
            TaskIsolationCapabilities.RELEASE -> {
                val ws = space(request) ?: return notFound(request)
                val agent = request.input["agentId"]
                if (agent != null && ws.ownerAgentId != null && ws.ownerAgentId != agent) {
                    return InvocationResult.Err(request.requestId, "not_owner", agent)
                }
                ws.ownerAgentId = null
                ws.status = "Released"
                InvocationResult.Ok(request.requestId, mapOf("id" to ws.id, "status" to "Released"))
            }
            TaskIsolationCapabilities.STATUS -> {
                val ws = space(request) ?: return notFound(request)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "id" to ws.id,
                        "taskId" to ws.taskId,
                        "kind" to ws.kind.name,
                        "status" to ws.status,
                        "owner" to (ws.ownerAgentId ?: ""),
                        "rootPath" to ws.rootPath,
                    ),
                )
            }
            TaskIsolationCapabilities.LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("count" to spaces.size.toString(), "ids" to spaces.keys.joinToString(",")),
                )
            TaskIsolationCapabilities.APPROVE_MERGE -> {
                if (request.input["approvalToken"].isNullOrBlank()) {
                    return InvocationResult.Err(request.requestId, "approval_token_required", "no_boolean_bypass")
                }
                val ws = space(request)
                ws?.status = "MergeAccepted"
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "status" to "AcceptedNotMerged",
                        "message" to "approval_recorded_git_merge_not_executed_in_core",
                        "workspaceId" to (ws?.id ?: ""),
                    ),
                )
            }
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun space(request: InvocationRequest): IsolationWorkspace? =
        request.input["id"]?.let { spaces[it] }

    private fun notFound(request: InvocationRequest) =
        InvocationResult.Err(request.requestId, "not_found", request.input["id"] ?: "")

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)
}

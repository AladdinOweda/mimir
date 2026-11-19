package com.mimir.engine.spe

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object SpeCapabilities {
    const val SPEC_CREATE = "spe.spec.create"
    const val PLAN_CREATE = "spe.plan.create"
    const val TASK_ADD = "spe.task.add"
    const val TASK_LIST = "spe.task.list"
    const val CONVERGE = "spe.converge"
    const val TASK_COMPLETE = "spe.task.complete"
    const val METRICS = "spe.metrics"
    val ALL = listOf(SPEC_CREATE, PLAN_CREATE, TASK_ADD, TASK_LIST, TASK_COMPLETE, CONVERGE, METRICS)
}

class SpeEngine(private val clockMs: () -> Long = { System.currentTimeMillis() }) : MimirEngine {
    private data class Spec(val id: String, val title: String, val body: String)
    private data class Task(val id: String, val specId: String, val title: String, var done: Boolean = false)
    private val specs = ConcurrentHashMap<String, Spec>()
    private val tasks = ConcurrentHashMap<String, Task>()
    private val plans = ConcurrentHashMap<String, String>() // planId -> specId
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered
    private val manifest = EngineManifest(
        id = EngineId("spe-core"), name = "Mimir SPE", version = "0.3.0",
        provides = SpeCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("spe.read", "spe.write"),
        dataNamespace = "engine.spe", priority = 86,
        platforms = listOf("android", "desktop"), runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x", publisher = "mimir",
    )
    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState { state = EngineState.Configured; return state }
    override suspend fun start(): EngineState { state = EngineState.Running; return state }
    override suspend fun stop(reason: String): EngineState { state = EngineState.Stopped; return state }
    override suspend fun health() = HealthReport(manifest.id, state == EngineState.Running, state, "spe=0.3.0 specs=${specs.size} tasks=${tasks.size}", clockMs())
    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded)
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        return when (request.capability.id.value) {
            SpeCapabilities.SPEC_CREATE -> {
                val id = "sp-${seq.incrementAndGet()}"
                specs[id] = Spec(id, request.input["title"] ?: id, request.input["body"] ?: "")
                InvocationResult.Ok(request.requestId, mapOf("specId" to id))
            }
            SpeCapabilities.PLAN_CREATE -> {
                val sid = request.input["specId"] ?: return InvocationResult.Err(request.requestId, "invalid", "specId")
                if (!specs.containsKey(sid)) return InvocationResult.Err(request.requestId, "spec_not_found", sid)
                val pid = "pl-${seq.incrementAndGet()}"
                plans[pid] = sid
                InvocationResult.Ok(request.requestId, mapOf("planId" to pid, "specId" to sid))
            }
            SpeCapabilities.TASK_ADD -> {
                val id = "tk-${seq.incrementAndGet()}"
                val sid = request.input["specId"] ?: return InvocationResult.Err(request.requestId, "invalid", "specId")
                tasks[id] = Task(id, sid, request.input["title"] ?: id)
                InvocationResult.Ok(request.requestId, mapOf("taskId" to id))
            }
            SpeCapabilities.TASK_LIST -> {
                val sid = request.input["specId"]
                val related = tasks.values.filter { sid == null || it.specId == sid }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "count" to related.size.toString(),
                        "ids" to related.joinToString(",") { it.id },
                        "done" to related.count { it.done }.toString(),
                    ),
                )
            }
            SpeCapabilities.TASK_COMPLETE -> {
                val id = request.input["taskId"] ?: return InvocationResult.Err(request.requestId, "invalid", "taskId")
                val task = tasks[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                task.done = true
                InvocationResult.Ok(request.requestId, mapOf("taskId" to id, "done" to "true"))
            }
            SpeCapabilities.CONVERGE -> {
                val sid = request.input["specId"]
                val related = tasks.values.filter { sid == null || it.specId == sid }
                val done = related.count { it.done }
                InvocationResult.Ok(request.requestId, mapOf("total" to related.size.toString(), "done" to done.toString(), "converged" to (related.isNotEmpty() && done == related.size).toString()))
            }
            SpeCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "specs" to specs.size.toString(),
                        "tasks" to tasks.size.toString(),
                        "plans" to plans.size.toString(),
                        "state" to state.name,
                    ),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    fun speMetrics(): Map<String, String> = mapOf(
        "specs" to specs.size.toString(),
        "plans" to plans.size.toString(),
        "tasks" to tasks.size.toString(),
        "doneTasks" to tasks.values.count { it.done }.toString(),
        "state" to state.name,
    )

}

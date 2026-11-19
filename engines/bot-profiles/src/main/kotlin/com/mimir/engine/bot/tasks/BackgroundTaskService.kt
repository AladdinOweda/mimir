package com.mimir.engine.bot.tasks

import com.mimir.kernel.contract.*
import com.mimir.kernel.delivery.DurableOutbox
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object BgTaskCapabilities {
    const val TASK_CREATE = "bot.task.create"
    const val TASK_START = "bot.task.start"
    const val TASK_PAUSE = "bot.task.pause"
    const val TASK_RESUME = "bot.task.resume"
    const val TASK_CANCEL = "bot.task.cancel"
    const val TASK_PROGRESS = "bot.task.progress"
    const val TASK_COMPLETE = "bot.task.complete"
    const val TASK_STATUS = "bot.task.status"
    const val TASK_LIST = "bot.task.list"
    const val SCHEDULE_FIRE = "bot.task.schedule.fire"
    const val METRICS = "bot.task.metrics"
    val ALL = listOf(
        TASK_CREATE, TASK_START, TASK_PAUSE, TASK_RESUME, TASK_CANCEL,
        TASK_PROGRESS, TASK_COMPLETE, TASK_STATUS, TASK_LIST, SCHEDULE_FIRE, METRICS,
    )
}

enum class BgTaskStatus { Created, Running, Paused, Succeeded, Failed, Cancelled }

data class BgTask(
    val id: String,
    val botId: String,
    val title: String,
    var status: BgTaskStatus = BgTaskStatus.Created,
    var progress: Int = 0,
    val scheduleCronMeta: String = "",
    var lastError: String = "",
    val createdAt: Long = 0L,
)

/**
 * Background tasks + optional notify via DurableOutbox.
 * Scheduler: metadata + explicit fire only (no cron daemon).
 */
class BackgroundTaskService(
    private val outbox: DurableOutbox = DurableOutbox(),
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val tasks = ConcurrentHashMap<String, BgTask>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("bot-background-tasks"),
        name = "Mimir Bot Background Tasks",
        version = "0.5.0",
        provides = BgTaskCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("bot.task"),
        dataNamespace = "engine.bot.tasks",
        priority = 77,
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
        HealthReport(manifest.id, state == EngineState.Running, state, "tasks=${tasks.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            BgTaskCapabilities.TASK_CREATE -> {
                val id = request.input["id"] ?: "bg-${seq.incrementAndGet()}"
                val bot = request.input["botId"] ?: return inv(request, "botId")
                val title = request.input["title"] ?: id
                tasks[id] = BgTask(
                    id, bot, title,
                    scheduleCronMeta = request.input["cron"] ?: "",
                    createdAt = clockMs(),
                )
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "status" to "Created"))
            }
            BgTaskCapabilities.TASK_START -> transition(request, BgTaskStatus.Created, BgTaskStatus.Paused, to = BgTaskStatus.Running)
            BgTaskCapabilities.TASK_PAUSE -> transition(request, BgTaskStatus.Running, to = BgTaskStatus.Paused)
            BgTaskCapabilities.TASK_RESUME -> transition(request, BgTaskStatus.Paused, to = BgTaskStatus.Running)
            BgTaskCapabilities.TASK_CANCEL -> {
                val t = task(request) ?: return notFound(request)
                if (t.status == BgTaskStatus.Succeeded) {
                    return InvocationResult.Err(request.requestId, "already_done", t.id)
                }
                t.status = BgTaskStatus.Cancelled
                InvocationResult.Ok(request.requestId, mapOf("id" to t.id, "status" to t.status.name))
            }
            BgTaskCapabilities.TASK_PROGRESS -> {
                val t = task(request) ?: return notFound(request)
                if (t.status != BgTaskStatus.Running) {
                    return InvocationResult.Err(request.requestId, "not_running", t.status.name)
                }
                val p = request.input["progress"]?.toIntOrNull()?.coerceIn(0, 100)
                    ?: return inv(request, "progress")
                t.progress = p
                InvocationResult.Ok(request.requestId, mapOf("id" to t.id, "progress" to p.toString()))
            }
            BgTaskCapabilities.TASK_COMPLETE -> {
                val t = task(request) ?: return notFound(request)
                val fail = request.input["failed"]?.equals("true", true) == true
                t.status = if (fail) BgTaskStatus.Failed else BgTaskStatus.Succeeded
                if (fail) t.lastError = request.input["error"] ?: "failed"
                else t.progress = 100
                if (request.input["notify"]?.equals("true", true) == true) {
                    outbox.enqueue(
                        conversationId = t.botId,
                        payload = "bot.task.done:${t.id}:${t.status.name}",
                        dedupeKey = "bg-done-${t.id}",
                    )
                }
                InvocationResult.Ok(request.requestId, mapOf("id" to t.id, "status" to t.status.name))
            }
            BgTaskCapabilities.TASK_STATUS -> {
                val t = task(request) ?: return notFound(request)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "id" to t.id,
                        "botId" to t.botId,
                        "status" to t.status.name,
                        "progress" to t.progress.toString(),
                        "error" to t.lastError,
                    ),
                )
            }
            BgTaskCapabilities.TASK_LIST -> {
                val bot = request.input["botId"]
                val list = tasks.values.filter { bot == null || it.botId == bot }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("count" to list.size.toString(), "ids" to list.joinToString(",") { it.id }),
                )
            }
            BgTaskCapabilities.SCHEDULE_FIRE -> {
                // Honest: only starts Created tasks that have cron meta, no wall-clock cron
                val id = request.input["id"] ?: return inv(request, "id")
                val t = tasks[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                if (t.scheduleCronMeta.isBlank()) {
                    return InvocationResult.Err(request.requestId, "no_schedule", id)
                }
                if (t.status != BgTaskStatus.Created && t.status != BgTaskStatus.Paused) {
                    return InvocationResult.Err(request.requestId, "bad_state", t.status.name)
                }
                t.status = BgTaskStatus.Running
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "status" to "Running", "fired" to "true"))
            }
            BgTaskCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tasks" to tasks.size.toString(),
                        "running" to tasks.values.count { it.status == BgTaskStatus.Running }.toString(),
                        "state" to state.name,
                    ),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun task(request: InvocationRequest): BgTask? {
        val id = request.input["id"] ?: return null
        return tasks[id]
    }

    private fun transition(
        request: InvocationRequest,
        vararg from: BgTaskStatus,
        to: BgTaskStatus,
    ): InvocationResult {
        val t = task(request) ?: return notFound(request)
        if (t.status !in from) {
            return InvocationResult.Err(request.requestId, "bad_state", t.status.name)
        }
        t.status = to
        return InvocationResult.Ok(request.requestId, mapOf("id" to t.id, "status" to t.status.name))
    }

    private fun notFound(request: InvocationRequest) =
        InvocationResult.Err(request.requestId, "not_found", request.input["id"] ?: "")

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)
}

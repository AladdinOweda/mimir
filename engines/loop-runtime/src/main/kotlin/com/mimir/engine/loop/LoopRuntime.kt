package com.mimir.engine.loop

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object LoopCapabilities {
    const val RUN = "loop.run"
    const val STOP = "loop.stop"
    const val STATUS = "loop.status"
    const val METRICS = "loop.metrics"
    val ALL = listOf(RUN, STOP, STATUS, METRICS)
}

data class LoopDefinition(
    val id: String,
    val name: String,
    val maxIterations: Int = 10,
    val budgetMs: Long = 60_000,
)

data class LoopRun(
    val definitionId: String,
    val iteration: Int,
    val stopped: Boolean,
    val reason: String?,
)

/**
 * Loop Runtime (Blueprint §5.5): bounded recurring execution with stop + budget.
 * Also exposed as [MimirEngine] for KernelHost attach.
 */
class LoopRuntime(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val stops = ConcurrentHashMap.newKeySet<String>()
    private val lastRuns = ConcurrentHashMap<String, LoopRun>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("loop-runtime"),
        name = "Mimir Loop Runtime",
        version = "0.5.0",
        provides = LoopCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("loop.run"),
        dataNamespace = "engine.loop",
        priority = 75,
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
        HealthReport(manifest.id, state == EngineState.Running, state, "lastRuns=${lastRuns.size}", clockMs())

    fun requestStop(definitionId: String) {
        stops.add(definitionId)
    }

    /** Runs [body] until maxIterations, budget, or stop. Always bounded. */
    fun run(def: LoopDefinition, body: (iteration: Int) -> Boolean): LoopRun {
        val start = clockMs()
        var i = 0
        while (i < def.maxIterations) {
            if (stops.contains(def.id)) {
                val r = LoopRun(def.id, i, true, "user-stop")
                lastRuns[def.id] = r
                return r
            }
            if (clockMs() - start > def.budgetMs) {
                val r = LoopRun(def.id, i, true, "budget")
                lastRuns[def.id] = r
                return r
            }
            val cont = body(i)
            i++
            if (!cont) {
                val r = LoopRun(def.id, i, true, "body-complete")
                lastRuns[def.id] = r
                return r
            }
        }
        val r = LoopRun(def.id, i, true, "max-iterations")
        lastRuns[def.id] = r
        return r
    }

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            LoopCapabilities.STOP -> {
                val id = request.input["loopId"] ?: return InvocationResult.Err(request.requestId, "invalid", "loopId")
                requestStop(id)
                InvocationResult.Ok(request.requestId, mapOf("loopId" to id, "stopRequested" to "true"))
            }
            LoopCapabilities.STATUS -> {
                val id = request.input["loopId"] ?: return InvocationResult.Err(request.requestId, "invalid", "loopId")
                val last = lastRuns[id]
                    ?: return InvocationResult.Ok(request.requestId, mapOf("loopId" to id, "status" to "none"))
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "loopId" to id,
                        "iteration" to last.iteration.toString(),
                        "stopped" to last.stopped.toString(),
                        "reason" to (last.reason ?: ""),
                    ),
                )
            }
            LoopCapabilities.RUN -> {
                val id = request.input["loopId"] ?: "loop-${seq.incrementAndGet()}"
                val max = request.input["maxIterations"]?.toIntOrNull() ?: 5
                val budget = request.input["budgetMs"]?.toLongOrNull() ?: 5_000L
                val steps = request.input["steps"]?.toIntOrNull() ?: max
                val def = LoopDefinition(id, request.input["name"] ?: id, maxIterations = max, budgetMs = budget)
                var count = 0
                val result = run(def) {
                    count++
                    count < steps
                }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "loopId" to id,
                        "iteration" to result.iteration.toString(),
                        "reason" to (result.reason ?: ""),
                        "stopped" to result.stopped.toString(),
                    ),
                )
            }
            LoopCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("runs" to lastRuns.size.toString(), "stops" to stops.size.toString(), "state" to state.name),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }
}

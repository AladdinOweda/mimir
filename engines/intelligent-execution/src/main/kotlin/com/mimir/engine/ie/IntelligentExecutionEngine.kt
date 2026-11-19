package com.mimir.engine.ie

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object IeCapabilities {
    const val PLAN = "ie.plan"
    const val EXECUTE = "ie.execute"
    const val RELAY = "ie.relay"
    const val STATUS = "ie.status"
    const val STEP_ADVANCE = "ie.step.advance"
    const val METRICS = "ie.metrics"
    val ALL = listOf(PLAN, EXECUTE, RELAY, STATUS, STEP_ADVANCE, METRICS)
}

data class IePlan(
    val id: String,
    val goal: String,
    val steps: List<String>,
    var status: String = "Planned",
    var lastExecutionId: String? = null,
    var stepIndex: Int = 0,
)

/**
 * Intelligent Execution — plan/execute/relay with approval on execute.
 * Does not run tools itself; records intent for Kernel/engines to dispatch.
 */
class IntelligentExecutionEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val plans = ConcurrentHashMap<String, IePlan>()
    private val relays = ConcurrentHashMap<String, Map<String, String>>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered
    private val defaultSteps = listOf("intent", "context", "act", "verify")

    private val manifest = EngineManifest(
        id = EngineId("ie-core"),
        name = "Mimir Intelligent Execution",
        version = "0.5.0",
        provides = IeCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("ie.run"),
        dataNamespace = "engine.ie",
        priority = 83,
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
        HealthReport(manifest.id, state == EngineState.Running, state, "plans=${plans.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            IeCapabilities.PLAN -> {
                val goal = request.input["goal"] ?: return inv(request, "goal")
                val steps = request.input["steps"]?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                    ?: defaultSteps
                val id = "iep-${seq.incrementAndGet()}"
                plans[id] = IePlan(id, goal, steps)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "planId" to id,
                        "goal" to goal,
                        "steps" to steps.joinToString(","),
                        "stepCount" to steps.size.toString(),
                    ),
                )
            }
            IeCapabilities.EXECUTE -> {
                if (request.input["approvalToken"].isNullOrBlank()) {
                    return InvocationResult.Err(request.requestId, "approval_required", "ie.execute")
                }
                val planId = request.input["planId"]
                val plan = planId?.let { plans[it] }
                val eid = "iex-${seq.incrementAndGet()}"
                plan?.let {
                    it.status = "Executed"
                    it.lastExecutionId = eid
                }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "executionId" to eid,
                        "planId" to (planId ?: ""),
                        "status" to "recorded",
                        "live" to "false",
                        "goal" to (plan?.goal ?: request.input["goal"] ?: ""),
                    ),
                )
            }
            IeCapabilities.RELAY -> {
                val from = request.input["from"] ?: return inv(request, "from")
                val to = request.input["to"] ?: return inv(request, "to")
                val payload = request.input["payload"] ?: ""
                val id = "ier-${seq.incrementAndGet()}"
                relays[id] = mapOf("from" to from, "to" to to, "payload" to payload)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "relayId" to id,
                        "from" to from,
                        "to" to to,
                        "payloadChars" to payload.length.toString(),
                    ),
                )
            }
            IeCapabilities.STATUS -> {
                val planId = request.input["planId"] ?: return inv(request, "planId")
                val p = plans[planId] ?: return InvocationResult.Err(request.requestId, "not_found", planId)
                val step = p.steps.getOrNull(p.stepIndex) ?: "done"
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "planId" to p.id,
                        "goal" to p.goal,
                        "status" to p.status,
                        "lastExecutionId" to (p.lastExecutionId ?: ""),
                        "steps" to p.steps.joinToString(","),
                        "stepIndex" to p.stepIndex.toString(),
                        "currentStep" to step,
                    ),
                )
            }
            IeCapabilities.STEP_ADVANCE -> {
                val planId = request.input["planId"] ?: return inv(request, "planId")
                val p = plans[planId] ?: return InvocationResult.Err(request.requestId, "not_found", planId)
                if (p.stepIndex >= p.steps.size) {
                    return InvocationResult.Ok(request.requestId, mapOf("planId" to planId, "done" to "true", "stepIndex" to p.stepIndex.toString()))
                }
                p.stepIndex += 1
                val done = p.stepIndex >= p.steps.size
                if (done) p.status = "StepsComplete"
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "planId" to planId,
                        "stepIndex" to p.stepIndex.toString(),
                        "done" to done.toString(),
                        "currentStep" to (p.steps.getOrNull(p.stepIndex) ?: "done"),
                    ),
                )
            }
            IeCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "plans" to plans.size.toString(),
                        "relays" to relays.size.toString(),
                        "state" to state.name,
                    ),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)
}

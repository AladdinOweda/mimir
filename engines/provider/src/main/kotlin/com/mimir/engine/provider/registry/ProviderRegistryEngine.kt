package com.mimir.engine.provider.registry

import com.mimir.engine.provider.policy.CostGuard
import com.mimir.kernel.contract.*

object ProviderCapabilities {
    const val REGISTER = "provider.register"
    const val LIST = "provider.list"
    const val AUTHORIZE_CALL = "provider.authorize_call"
    const val BUDGET_STATUS = "provider.budget.status"
    const val UNREGISTER = "provider.unregister"
    const val METRICS = "provider.metrics"
    val ALL = listOf(REGISTER, LIST, AUTHORIZE_CALL, BUDGET_STATUS, UNREGISTER, METRICS)
}

/**
 * R7 slim provider registry — cost gate ignores allowSpend boolean from client.
 */
class ProviderRegistryEngine(
    private val costGuard: CostGuard = CostGuard(),
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val providers = linkedMapOf<String, Map<String, String>>()
    private var state = EngineState.Discovered
    private val manifest = EngineManifest(
        id = EngineId("provider-registry"),
        name = "Mimir Provider Registry",
        version = "0.5.0",
        provides = ProviderCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("provider.admin", "provider.call"),
        dataNamespace = "engine.provider",
        priority = 88,
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
        HealthReport(manifest.id, state == EngineState.Running, state, "providers=${providers.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            ProviderCapabilities.REGISTER -> {
                val id = request.input["id"] ?: return InvocationResult.Err(request.requestId, "invalid", "id")
                providers[id] = request.input
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "registered" to "true"))
            }
            ProviderCapabilities.LIST ->
                InvocationResult.Ok(request.requestId, mapOf("ids" to providers.keys.joinToString(","), "count" to providers.size.toString()))
            ProviderCapabilities.AUTHORIZE_CALL -> {
                // Ignore allowSpend from client — CostGuard is authoritative
                val principal = request.input["principalId"] ?: return InvocationResult.Err(request.requestId, "invalid", "principalId")
                val providerId = request.input["providerId"]
                if (providerId != null && providerId !in providers) {
                    return InvocationResult.Err(request.requestId, "provider_not_registered", providerId)
                }
                val estimate = request.input["estimatedUsd"]?.toDoubleOrNull() ?: 0.01
                if (estimate < 0) return InvocationResult.Err(request.requestId, "invalid", "estimatedUsd")
                request.input["budgetUsd"]?.toDoubleOrNull()?.let { costGuard.setBudget(principal, it) }
                if (!costGuard.authorizeSpend(principal, estimate)) {
                    return InvocationResult.Err(
                        request.requestId,
                        "budget_exceeded",
                        "remaining=${costGuard.remaining(principal)}",
                    )
                }
                costGuard.recordSpend(principal, estimate)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "authorized" to "true",
                        "remaining" to costGuard.remaining(principal).toString(),
                        "spentThisCall" to estimate.toString(),
                        "ignoredAllowSpend" to (request.input["allowSpend"] ?: ""),
                        "providerId" to (providerId ?: ""),
                    ),
                )
            }
            ProviderCapabilities.BUDGET_STATUS -> {
                val principal = request.input["principalId"] ?: return InvocationResult.Err(request.requestId, "invalid", "principalId")
                request.input["budgetUsd"]?.toDoubleOrNull()?.let { costGuard.setBudget(principal, it) }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "principalId" to principal,
                        "remaining" to costGuard.remaining(principal).toString(),
                    ),
                )
            }
            ProviderCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("providers" to providers.size.toString(), "state" to state.name),
                )
            ProviderCapabilities.UNREGISTER -> {
                val id = request.input["id"] ?: return InvocationResult.Err(request.requestId, "invalid", "id")
                val removed = providers.remove(id) != null
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "removed" to removed.toString()))
            }
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }
}

package com.mimir.engine.aihub

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object AiHubCapabilities {
    const val ROUTE = "aihub.route"
    const val REGISTER_ENDPOINT = "aihub.endpoint.register"
    const val LIST_ENDPOINTS = "aihub.endpoint.list"
    const val SELECT = "aihub.select"
    const val DISABLE = "aihub.endpoint.disable"
    const val METRICS = "aihub.metrics"
    val ALL = listOf(ROUTE, REGISTER_ENDPOINT, LIST_ENDPOINTS, SELECT, DISABLE, METRICS)
}

data class AiEndpoint(val id: String, val kind: String, val modelRef: String, val priority: Int = 100, var enabled: Boolean = true)

class AiHubEngine(private val clockMs: () -> Long = { System.currentTimeMillis() }) : MimirEngine {
    private val endpoints = ConcurrentHashMap<String, AiEndpoint>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered
    private var selected: String? = null
    private val manifest = EngineManifest(
        id = EngineId("ai-hub"), name = "Mimir AI Hub", version = "0.1.0",
        provides = AiHubCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("ai.route"), dataNamespace = "engine.aihub", priority = 87,
        platforms = listOf("android", "desktop"), runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x", publisher = "mimir",
    )
    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState { state = EngineState.Configured; return state }
    override suspend fun start(): EngineState { state = EngineState.Running; return state }
    override suspend fun stop(reason: String): EngineState { state = EngineState.Stopped; return state }
    override suspend fun health() =
        HealthReport(manifest.id, state == EngineState.Running, state, "endpoints=${endpoints.size} selected=${selected ?: "none"}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded)
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        return when (request.capability.id.value) {
            AiHubCapabilities.REGISTER_ENDPOINT -> {
                val id = request.input["id"] ?: "ep-${seq.incrementAndGet()}"
                val kind = request.input["kind"] ?: "local"
                val model = request.input["modelRef"] ?: return inv(request, "modelRef")
                val priority = request.input["priority"]?.toIntOrNull() ?: 100
                endpoints[id] = AiEndpoint(id, kind, model, priority)
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "kind" to kind, "modelRef" to model))
            }
            AiHubCapabilities.LIST_ENDPOINTS ->
                InvocationResult.Ok(request.requestId, mapOf("count" to endpoints.size.toString(), "ids" to endpoints.keys.joinToString(",")))
            AiHubCapabilities.SELECT -> {
                val id = request.input["id"] ?: return inv(request, "id")
                val ep = endpoints[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                if (!ep.enabled) return InvocationResult.Err(request.requestId, "disabled", id)
                selected = id
                InvocationResult.Ok(request.requestId, mapOf("selected" to id, "modelRef" to ep.modelRef))
            }
            AiHubCapabilities.ROUTE -> {
                val kind = request.input["kind"]
                val candidates = endpoints.values.filter { it.enabled }.filter { kind == null || it.kind == kind }.sortedBy { it.priority }
                if (candidates.isEmpty()) return InvocationResult.Err(request.requestId, "no_endpoint", kind ?: "any")
                val pick = selected?.let { endpoints[it] }?.takeIf { it.enabled && (kind == null || it.kind == kind) } ?: candidates.first()
                InvocationResult.Ok(request.requestId, mapOf(
                    "endpointId" to pick.id, "modelRef" to pick.modelRef, "kind" to pick.kind, "priority" to pick.priority.toString(),
                ))
            }
            AiHubCapabilities.DISABLE -> {
                val id = request.input["id"] ?: return inv(request, "id")
                val ep = endpoints[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                ep.enabled = false
                if (selected == id) selected = null
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "enabled" to "false"))
            }
            AiHubCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "endpoints" to endpoints.size.toString(),
                        "enabled" to endpoints.values.count { it.enabled }.toString(),
                        "selected" to (selected ?: ""),
                        "state" to state.name,
                    ),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }
    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)

    fun aiHubMetrics(): Map<String, String> = mapOf(
        "endpoints" to endpoints.size.toString(),
        "selected" to (selected ?: ""),
        "state" to state.name,
    )

}

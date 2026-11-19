package com.mimir.engine.ai

import com.mimir.engine.ai.contract.*
import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap

/** No-op local runtime — always available, no network. */
class NoopModelRuntime : ModelRuntimeContract {
    override val runtimeId = RuntimeId("noop")
    override val displayName = "Noop Runtime"
    private val loaded = ConcurrentHashMap.newKeySet<String>()
    override fun capabilities() = ModelRuntimeCapabilities(generate = true, stream = false, train = false)
    override suspend fun load(model: ModelManifest, options: Map<String, String>): RuntimeLoadResult {
        if (!model.licenseApproved) return RuntimeLoadResult(false, model.id, errorCode = "license_blocked")
        loaded += model.id.value
        return RuntimeLoadResult(true, model.id, "noop_loaded")
    }
    override suspend fun unload(modelId: ModelId) = loaded.remove(modelId.value)
    override suspend fun generate(request: GenerateRequest): GenerateResult {
        if (request.modelId.value !in loaded) return GenerateResult(false, errorCode = "not_loaded")
        return GenerateResult(
            ok = true,
            text = "NOOP:${request.prompt.take(200)}",
            usageInputTokens = request.prompt.length / 4,
            usageOutputTokens = 8,
            stub = true,
        )
    }
    override suspend fun health() = RuntimeHealth(runtimeId, true, loaded.toList(), "noop")
}

/**
 * AI / Model Engine — Mimir-owned routing policy.
 * Runtimes are adapters only (ADR-022).
 */
class AiModelEngine(
    private val runtimes: List<ModelRuntimeContract> = listOf(NoopModelRuntime()),
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val models = ConcurrentHashMap<String, ModelManifest>()
    private val loadedRuntime = ConcurrentHashMap<String, RuntimeId>() // modelId -> runtime
    private var state = EngineState.Discovered
    private val manifest = EngineManifest(
        id = EngineId("ai-model"),
        name = "Mimir AI Model",
        version = "0.4.0",
        provides = AiModelCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("ai.model.read", "ai.model.invoke"),
        dataNamespace = "engine.ai",
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
    override suspend fun health(): HealthReport {
        val parts = runtimes.map { r -> "${r.runtimeId.value}:${r.health().healthy}" }
        return HealthReport(manifest.id, state == EngineState.Running, state, "ai=0.4.0 ${parts.joinToString()}", clockMs())
    }

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            AiModelCapabilities.MODEL_REGISTER -> register(request)
            AiModelCapabilities.MODEL_LIST ->
                InvocationResult.Ok(request.requestId, mapOf("count" to models.size.toString(), "ids" to models.keys.joinToString(",")))
            AiModelCapabilities.MODEL_GENERATE -> generate(request)
            AiModelCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "models" to models.size.toString(),
                        "loaded" to loadedRuntime.size.toString(),
                        "runtimes" to runtimes.size.toString(),
                        "state" to state.name,
                    ),
                )
            AiModelCapabilities.RUNTIME_HEALTH -> {
                val parts = mutableListOf<String>()
                for (r in runtimes) {
                    parts += "${r.runtimeId.value}=${r.health().healthy}"
                }
                InvocationResult.Ok(request.requestId, mapOf("runtimes" to parts.joinToString(";")))
            }
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private suspend fun register(request: InvocationRequest): InvocationResult {
        val id = request.input["modelId"] ?: return InvocationResult.Err(request.requestId, "invalid", "modelId")
        val approved = request.input["licenseApproved"]?.equals("true", true) == true
        val caps = ModelCapabilities(
            chat = true,
            stream = request.input["stream"]?.equals("true", true) == true,
            toolCalling = request.input["toolCalling"]?.equals("true", true) == true,
            structuredOutput = request.input["structured"]?.equals("true", true) == true,
        )
        val m = ModelManifest(
            id = ModelId(id),
            displayName = request.input["displayName"] ?: id,
            providerId = request.input["providerId"] ?: "local",
            capabilities = caps,
            licenseApproved = approved,
            privacyClass = request.input["privacyClass"] ?: "local",
        )
        if (!approved) return InvocationResult.Err(request.requestId, "license_blocked", id)
        val runtime = pickRuntime(m) ?: return InvocationResult.Err(request.requestId, "no_runtime", id)
        val load = runtime.load(m)
        if (!load.ok) return InvocationResult.Err(request.requestId, load.errorCode ?: "load_failed", load.detail)
        models[id] = m
        loadedRuntime[id] = runtime.runtimeId
        return InvocationResult.Ok(request.requestId, mapOf("modelId" to id, "runtime" to runtime.runtimeId.value))
    }

    private suspend fun generate(request: InvocationRequest): InvocationResult {
        val id = request.input["modelId"] ?: return InvocationResult.Err(request.requestId, "invalid", "modelId")
        if (!models.containsKey(id)) return InvocationResult.Err(request.requestId, "not_registered", id)
        val rid = loadedRuntime[id] ?: return InvocationResult.Err(request.requestId, "not_loaded", id)
        val runtime = runtimes.firstOrNull { it.runtimeId == rid }
            ?: return InvocationResult.Err(request.requestId, "no_runtime", id)
        val prompt = request.input["prompt"] ?: return InvocationResult.Err(request.requestId, "invalid", "prompt")
        val result = runtime.generate(GenerateRequest(ModelId(id), prompt))
        if (!result.ok) return InvocationResult.Err(request.requestId, result.errorCode ?: "generate_failed", "")
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "text" to result.text,
                "inputTokens" to result.usageInputTokens.toString(),
                "outputTokens" to result.usageOutputTokens.toString(),
                "stub" to result.stub.toString(),
            ),
        )
    }

    private fun pickRuntime(model: ModelManifest): ModelRuntimeContract? {
        // Policy: prefer local/noop; never pick disabled python bridge implicitly
        return runtimes.firstOrNull { it.capabilities().generate }
    }
}

package com.mimir.engine.ai.contract

/** Provider-neutral model layer (ADR-022). No LangChain types. */

data class ModelId(val value: String)
data class RuntimeId(val value: String)

data class ModelCapabilities(
    val chat: Boolean = true,
    val stream: Boolean = false,
    val toolCalling: Boolean = false,
    val structuredOutput: Boolean = false,
    val embeddings: Boolean = false,
    val maxContextTokens: Int? = null,
)

data class ModelManifest(
    val id: ModelId,
    val displayName: String,
    val providerId: String,
    val capabilities: ModelCapabilities,
    val licenseApproved: Boolean = false,
    val privacyClass: String = "local", // local | cloud | hybrid
)

data class ChatMessage(val role: String, val content: String)

data class GenerateRequest(
    val modelId: ModelId,
    val prompt: String,
    val messages: List<ChatMessage> = emptyList(),
    val tools: List<String> = emptyList(),
    val stream: Boolean = false,
    val maxTokens: Int? = null,
)

data class GenerateResult(
    val ok: Boolean,
    val text: String = "",
    val toolCalls: List<String> = emptyList(),
    val usageInputTokens: Int = 0,
    val usageOutputTokens: Int = 0,
    val errorCode: String? = null,
    val stub: Boolean = true,
)

data class RuntimeLoadResult(
    val ok: Boolean,
    val modelId: ModelId,
    val detail: String = "",
    val errorCode: String? = null,
)

data class RuntimeHealth(
    val runtimeId: RuntimeId,
    val healthy: Boolean,
    val loadedModelIds: List<String> = emptyList(),
    val detail: String = "",
)

data class ModelRuntimeCapabilities(
    val generate: Boolean = true,
    val stream: Boolean = false,
    val train: Boolean = false,
)

interface ModelRuntimeContract {
    val runtimeId: RuntimeId
    val displayName: String
    fun capabilities(): ModelRuntimeCapabilities
    suspend fun load(model: ModelManifest, options: Map<String, String> = emptyMap()): RuntimeLoadResult
    suspend fun unload(modelId: ModelId): Boolean
    suspend fun generate(request: GenerateRequest): GenerateResult
    suspend fun health(): RuntimeHealth
}

object AiModelCapabilities {
    const val MODEL_REGISTER = "ai.model.register"
    const val MODEL_LIST = "ai.model.list"
    const val MODEL_GENERATE = "ai.model.generate"
    const val RUNTIME_HEALTH = "ai.runtime.health"
    const val METRICS = "ai.model.metrics"
    val ALL = listOf(MODEL_REGISTER, MODEL_LIST, MODEL_GENERATE, RUNTIME_HEALTH, METRICS)
}

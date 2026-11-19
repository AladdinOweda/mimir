package com.mimir.engine.chat

/**
 * Bridges ChatEngine completion to an AiHub-like router without hard dependency on ai-hub module.
 * Host injects [routeGenerate]; returns model text or null → ChatEngine can fall back.
 */
fun interface GenerateFn {
    fun generate(modelRef: String, prompt: String): String?
}

class AiHubCompletionBridge(
    private val generate: GenerateFn,
    private val fallback: CompletionPort = EchoCompletionPort(),
) : CompletionPort {
    override fun complete(threadId: String, modelRef: String, history: List<ChatMessage>): String {
        val prompt = history.joinToString("\n") { "${it.role}: ${it.text}" }.take(12_000)
        return generate.generate(modelRef.ifBlank { "default" }, prompt)
            ?: fallback.complete(threadId, modelRef, history)
    }
}

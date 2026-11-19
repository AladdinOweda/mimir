package com.mimir.engine.chat

import com.mimir.kernel.contract.*
import kotlinx.coroutines.runBlocking

/**
 * Capability IDs used for routing (string-stable; contracts own formal catalog).
 * Chat must not import AiHubEngine / AiModelEngine (H-03 / Claude F1).
 */
object ChatPipelineCaps {
    const val AIHUB_ROUTE = "aihub.route"
    const val MODEL_LIST = "ai.model.list"
    const val MODEL_REGISTER = "ai.model.register"
    const val MODEL_GENERATE = "ai.model.generate"
}

/**
 * Production path: inject [invoke] from KernelHost / ProductContext composition.
 * Unit tests may pass a local lambda; never hard-codes concrete engines.
 */
class ChatModelPipeline(
    private val invoke: suspend (InvocationRequest) -> InvocationResult,
    private val fallback: CompletionPort = EchoCompletionPort(),
) : CompletionPort {
    override fun complete(threadId: String, modelRef: String, history: List<ChatMessage>): String {
        val prompt = history.joinToString("\n") { "${it.role}: ${it.text}" }.take(12_000)
        return runBlocking {
            val route = invoke(
                InvocationRequest(
                    "pipe-route", "chat-pipeline",
                    CapabilityRef(CapabilityId(ChatPipelineCaps.AIHUB_ROUTE)),
                    mapOf("prompt" to prompt.take(200)),
                ),
            )
            val routedModel = when (route) {
                is InvocationResult.Ok -> route.output["modelRef"]?.takeIf { it.isNotBlank() } ?: modelRef
                else -> modelRef
            }.ifBlank { "chat-local" }

            val list = invoke(
                InvocationRequest(
                    "pipe-list", "chat-pipeline",
                    CapabilityRef(CapabilityId(ChatPipelineCaps.MODEL_LIST)),
                    emptyMap(),
                ),
            )
            val ids = (list as? InvocationResult.Ok)?.output?.get("ids").orEmpty()
            if (routedModel !in ids.split(",")) {
                invoke(
                    InvocationRequest(
                        "pipe-reg", "chat-pipeline",
                        CapabilityRef(CapabilityId(ChatPipelineCaps.MODEL_REGISTER)),
                        mapOf("modelId" to routedModel, "licenseApproved" to "true", "displayName" to routedModel),
                    ),
                )
            }
            val gen = invoke(
                InvocationRequest(
                    "pipe-gen", "chat-pipeline",
                    CapabilityRef(CapabilityId(ChatPipelineCaps.MODEL_GENERATE)),
                    mapOf("modelId" to routedModel, "prompt" to prompt),
                ),
            )
            when (gen) {
                is InvocationResult.Ok -> gen.output["text"] ?: fallback.complete(threadId, routedModel, history)
                else -> fallback.complete(threadId, routedModel, history)
            }
        }
    }
}

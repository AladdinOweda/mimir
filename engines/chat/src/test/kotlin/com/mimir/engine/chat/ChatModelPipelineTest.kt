package com.mimir.engine.chat

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ChatModelPipelineTest {
    @Test
    fun complete_uses_injected_invoke_not_concrete_engines() = runTest {
        val invoke: suspend (InvocationRequest) -> InvocationResult = { req ->
            when (req.capability.id.value) {
                ChatPipelineCaps.AIHUB_ROUTE ->
                    InvocationResult.Ok(req.requestId, mapOf("modelRef" to "local", "endpointId" to "ep"))
                ChatPipelineCaps.MODEL_LIST ->
                    InvocationResult.Ok(req.requestId, mapOf("ids" to "local", "count" to "1"))
                ChatPipelineCaps.MODEL_GENERATE ->
                    InvocationResult.Ok(req.requestId, mapOf("text" to "NOOP:ok"))
                else -> InvocationResult.Ok(req.requestId, mapOf("ok" to "true"))
            }
        }
        val eng = ChatEngine(completion = ChatModelPipeline(invoke))
        eng.start()
        val tid = (eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ChatCapabilities.THREAD_CREATE)), mapOf("title" to "t")),
        ) as InvocationResult.Ok).output["threadId"]!!
        eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(ChatCapabilities.MESSAGE_SEND)),
                mapOf("threadId" to tid, "text" to "hi"),
            ),
        )
        val done = eng.invoke(
            InvocationRequest("3", "j", CapabilityRef(CapabilityId(ChatCapabilities.COMPLETE)), mapOf("threadId" to tid)),
        )
        assertTrue(done is InvocationResult.Ok, done.toString())
        assertTrue((done as InvocationResult.Ok).output["text"]!!.contains("NOOP"))
    }
}

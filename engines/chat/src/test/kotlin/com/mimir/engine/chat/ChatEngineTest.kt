package com.mimir.engine.chat

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatEngineTest {
    @Test
    fun thread_send_complete_list_archive() = runTest {
        val eng = ChatEngine()
        eng.start()
        val create = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ChatCapabilities.THREAD_CREATE)),
                mapOf("title" to "Hello", "modelRef" to "local-echo"),
            ),
        )
        assertTrue(create is InvocationResult.Ok, create.toString())
        val tid = (create as InvocationResult.Ok).output["threadId"]!!
        val send = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(ChatCapabilities.MESSAGE_SEND)),
                mapOf("threadId" to tid, "role" to "user", "text" to "ping"),
            ),
        )
        assertTrue(send is InvocationResult.Ok, send.toString())
        val complete = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(ChatCapabilities.COMPLETE)),
                mapOf("threadId" to tid),
            ),
        )
        assertTrue(complete is InvocationResult.Ok, complete.toString())
        assertTrue((complete as InvocationResult.Ok).output["text"]!!.contains("echo: ping"))
        val list = eng.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(ChatCapabilities.MESSAGE_LIST)),
                mapOf("threadId" to tid),
            ),
        )
        assertTrue(list is InvocationResult.Ok)
        assertTrue((list as InvocationResult.Ok).output["count"]!!.toInt() >= 2)
        val arch = eng.invoke(
            InvocationRequest(
                "5", "j", CapabilityRef(CapabilityId(ChatCapabilities.THREAD_ARCHIVE)),
                mapOf("threadId" to tid),
            ),
        )
        assertTrue(arch is InvocationResult.Ok)
        val blocked = eng.invoke(
            InvocationRequest(
                "6", "j", CapabilityRef(CapabilityId(ChatCapabilities.COMPLETE)),
                mapOf("threadId" to tid),
            ),
        )
        assertTrue(blocked is InvocationResult.Err)
        assertEquals("archived", (blocked as InvocationResult.Err).code)
    }

    @Test
    fun metrics_and_list_threads() = runTest {
        val eng = ChatEngine()
        eng.start()
        eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ChatCapabilities.THREAD_CREATE)), mapOf("title" to "A")),
        )
        eng.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(ChatCapabilities.THREAD_CREATE)), mapOf("title" to "B")),
        )
        val list = eng.invoke(
            InvocationRequest("3", "j", CapabilityRef(CapabilityId(ChatCapabilities.THREAD_LIST)), emptyMap()),
        )
        assertTrue(list is InvocationResult.Ok)
        assertTrue((list as InvocationResult.Ok).output["count"]!!.toInt() >= 2)
        val m = eng.invoke(
            InvocationRequest("4", "j", CapabilityRef(CapabilityId(ChatCapabilities.METRICS)), emptyMap()),
        )
        assertTrue(m is InvocationResult.Ok)
        assertTrue((m as InvocationResult.Ok).output["threads"]!!.toInt() >= 2)
    }

    @Test
    fun custom_completion_port() = runTest {
        val eng = ChatEngine(completion = CompletionPort { _, _, hist ->
            "custom:${hist.lastOrNull { it.role == "user" }?.text}"
        })
        eng.start()
        val tid = (eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ChatCapabilities.THREAD_CREATE)), mapOf("title" to "c")),
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
        assertTrue(done is InvocationResult.Ok)
        assertEquals("custom:hi", (done as InvocationResult.Ok).output["text"])
    }
}

class ChatEngineDeepenTest {
    @Test
    fun stream_tool_context_and_bridge() = kotlinx.coroutines.test.runTest {
        val eng = ChatEngine(
            completion = AiHubCompletionBridge(
                generate = object : GenerateFn {
                    override fun generate(modelRef: String, prompt: String): String? =
                        "hub($modelRef):${prompt.takeLast(40)}"
                },
            ),
        )
        eng.start()
        val tid = (eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ChatCapabilities.THREAD_CREATE)), mapOf("title" to "t", "modelRef" to "m1")),
        ) as InvocationResult.Ok).output["threadId"]!!
        eng.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(ChatCapabilities.MESSAGE_SEND)), mapOf("threadId" to tid, "text" to "hello-world")),
        )
        eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(ChatCapabilities.ATTACH_CONTEXT)),
                mapOf("threadId" to tid, "contextRef" to "vault:doc1", "sessionId" to "sess-9"),
            ),
        )
        eng.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(ChatCapabilities.TOOL_RESULT)),
                mapOf("threadId" to tid, "toolName" to "search", "text" to "3 hits"),
            ),
        )
        val stream = eng.invoke(
            InvocationRequest("5", "j", CapabilityRef(CapabilityId(ChatCapabilities.STREAM_START)), mapOf("threadId" to tid)),
        )
        assertTrue(stream is InvocationResult.Ok, stream.toString())
        val done = eng.invoke(
            InvocationRequest("6", "j", CapabilityRef(CapabilityId(ChatCapabilities.COMPLETE)), mapOf("threadId" to tid)),
        )
        assertTrue(done is InvocationResult.Ok, "complete=$done")
        val txt = (done as InvocationResult.Ok).output["text"]!!
        assertTrue(txt.contains("hub") || txt.contains("hello"), "text=$txt")
    }
}

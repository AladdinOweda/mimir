package com.mimir.engine.chat

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ChatMemoryTest {
    @Test
    fun memory_store_persists_after_send() = runTest {
        val dir = File(System.getProperty("java.io.tmpdir"), "mimir-chat-mem-${System.nanoTime()}")
        val store = ChatMemoryStore(dir)
        val eng = ChatEngine(memory = store)
        eng.start()
        val tid = (eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ChatCapabilities.THREAD_CREATE)), mapOf("title" to "m")),
        ) as InvocationResult.Ok).output["threadId"]!!
        eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(ChatCapabilities.MESSAGE_SEND)),
                mapOf("threadId" to tid, "text" to "persist-me"),
            ),
        )
        assertTrue(store.size() >= 1)
        assertTrue(store.get(tid)!!.contains("msgs="))
        val m = eng.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(ChatCapabilities.METRICS)), emptyMap()))
        assertTrue((m as InvocationResult.Ok).output["memoryKeys"]!!.toInt() >= 1)
    }
}

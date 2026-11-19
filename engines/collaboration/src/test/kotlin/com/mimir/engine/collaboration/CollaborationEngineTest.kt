package com.mimir.engine.collaboration

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollaborationEngineTest {
    @Test
    fun session_message_member_gate() = runTest {
        val e = CollaborationEngine().also { it.start() }
        val sid = (e.invoke(InvocationRequest("1", "j", CapabilityRef(CapabilityId(CollaborationCapabilities.SESSION_OPEN)),
            mapOf("topic" to "design", "members" to "a,b"))) as InvocationResult.Ok).output.getValue("sessionId")
        val denied = e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(CollaborationCapabilities.MESSAGE)),
            mapOf("sessionId" to sid, "from" to "c", "text" to "hi")))
        assertTrue(denied is InvocationResult.Err)
        assertEquals("not_member", (denied as InvocationResult.Err).code)
        val ok = e.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(CollaborationCapabilities.MESSAGE)),
            mapOf("sessionId" to sid, "from" to "a", "text" to "hello")))
        assertTrue(ok is InvocationResult.Ok)
    }
}

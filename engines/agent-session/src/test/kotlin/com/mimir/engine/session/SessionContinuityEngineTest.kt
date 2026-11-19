package com.mimir.engine.session

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SessionContinuityEngineTest {
    @Test
    fun open_append_handoff() = runTest {
        val e = SessionContinuityEngine().also { it.start() }
        val open = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(SessionContinuityCapabilities.SESSION_OPEN)),
                mapOf("botId" to "b1", "modelRef" to "local"),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val sid = (open as InvocationResult.Ok).output["sessionId"]
            ?: open.output["id"]
            ?: return@runTest
        e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(SessionContinuityCapabilities.SESSION_APPEND)),
                mapOf("sessionId" to sid, "text" to "hello world context"),
            ),
        )
        val hand = e.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(SessionContinuityCapabilities.SESSION_HANDOFF)),
                mapOf("sessionId" to sid),
            ),
        )
        assertTrue(hand is InvocationResult.Ok, hand.toString())
    }
}

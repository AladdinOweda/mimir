package com.mimir.engine.agent

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AgentConnectivityEngineTest {
    @Test
    fun max_turns_and_protocol() = runTest {
        val e = AgentConnectivityEngine().also { it.start() }
        val bad = e.invoke(InvocationRequest("0", "j", CapabilityRef(CapabilityId(AgentConnectivityCapabilities.SESSION_START)),
            mapOf("protocol" to "evil")))
        assertTrue(bad is InvocationResult.Err)
        val sid = (e.invoke(InvocationRequest("1", "j", CapabilityRef(CapabilityId(AgentConnectivityCapabilities.SESSION_START)),
            mapOf("protocol" to "noop", "maxTurns" to "2"))) as InvocationResult.Ok).output.getValue("sessionId")
        e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(AgentConnectivityCapabilities.SESSION_PROMPT)),
            mapOf("sessionId" to sid, "prompt" to "one")))
        e.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(AgentConnectivityCapabilities.SESSION_PROMPT)),
            mapOf("sessionId" to sid, "prompt" to "two")))
        val third = e.invoke(InvocationRequest("4", "j", CapabilityRef(CapabilityId(AgentConnectivityCapabilities.SESSION_PROMPT)),
            mapOf("sessionId" to sid, "prompt" to "three")))
        assertTrue(third is InvocationResult.Err)
        assertEquals("max_turns", (third as InvocationResult.Err).code)
    }
}

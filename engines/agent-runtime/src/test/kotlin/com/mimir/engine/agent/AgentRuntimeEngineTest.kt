package com.mimir.engine.agent

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AgentRuntimeEngineTest {
    @Test
    fun allowlist_and_cost_ceiling() = runTest {
        val e = AgentRuntimeEngine().also { it.start() }
        e.invoke(InvocationRequest("1", "j", CapabilityRef(CapabilityId(AgentRuntimeCapabilities.AGENT_CREATE)),
            mapOf("id" to "a1", "tools" to "browser.open,terminal.exec", "maxCostUsd" to "0.05")))
        val sid = (e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(AgentRuntimeCapabilities.SESSION_START)),
            mapOf("agentId" to "a1"))) as InvocationResult.Ok).output.getValue("sessionId")
        val denied = e.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(AgentRuntimeCapabilities.TOOL_INVOKE)),
            mapOf("sessionId" to sid, "tool" to "vault.put", "estimatedUsd" to "0.01")))
        assertTrue(denied is InvocationResult.Err)
        assertEquals("tool_not_allowed", (denied as InvocationResult.Err).code)
        e.invoke(InvocationRequest("4", "j", CapabilityRef(CapabilityId(AgentRuntimeCapabilities.TOOL_INVOKE)),
            mapOf("sessionId" to sid, "tool" to "browser.open", "estimatedUsd" to "0.04")))
        val over = e.invoke(InvocationRequest("5", "j", CapabilityRef(CapabilityId(AgentRuntimeCapabilities.TOOL_INVOKE)),
            mapOf("sessionId" to sid, "tool" to "terminal.exec", "estimatedUsd" to "0.02")))
        assertTrue(over is InvocationResult.Err)
        assertEquals("cost_limit", (over as InvocationResult.Err).code)
    }
}

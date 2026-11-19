package com.mimir.engine.tools

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToolRegistryEngineTest {
    @Test
    fun authorize_requires_approval_for_high_risk() = runTest {
        val e = ToolRegistryEngine().also { it.start() }
        val blocked = e.invoke(InvocationRequest("1", "j", CapabilityRef(CapabilityId(ToolCapabilities.AUTHORIZE)),
            mapOf("toolId" to "terminal.command.execute", "grantedPermissions" to "terminal.execute", "command" to "echo x")))
        assertTrue(blocked is InvocationResult.Err)
        assertEquals("approval_required", (blocked as InvocationResult.Err).code)
        val ok = e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(ToolCapabilities.AUTHORIZE)),
            mapOf("toolId" to "terminal.command.execute", "grantedPermissions" to "terminal.execute", "approvalToken" to "t", "command" to "echo x")))
        assertTrue(ok is InvocationResult.Ok)
    }

    @Test
    fun deny_blocks_tool() = runTest {
        val e = ToolRegistryEngine().also { it.start() }
        e.invoke(InvocationRequest("1", "j", CapabilityRef(CapabilityId(ToolCapabilities.DENY)),
            mapOf("toolId" to "browser.tab.open")))
        val r = e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(ToolCapabilities.AUTHORIZE)),
            mapOf("toolId" to "browser.tab.open", "grantedPermissions" to "browser.navigate")))
        assertTrue(r is InvocationResult.Err)
        assertEquals("tool_denied", (r as InvocationResult.Err).code)
    }
}

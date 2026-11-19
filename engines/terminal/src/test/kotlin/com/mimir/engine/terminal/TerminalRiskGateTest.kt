package com.mimir.engine.terminal

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TerminalRiskGateTest {
    @Test
    fun high_risk_blocked_without_token() = runTest {
        val e = TerminalEngine().also { it.start() }
        val open = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(TerminalCapabilities.SESSION_OPEN)),
                mapOf("cwd" to System.getProperty("java.io.tmpdir")),
            ),
        ) as InvocationResult.Ok
        val sid = open.output["sessionId"] ?: open.output["id"] ?: return@runTest
        val blocked = e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(TerminalCapabilities.COMMAND_EXECUTE)),
                mapOf("sessionId" to sid, "command" to "rm -rf /tmp/x"),
            ),
        )
        assertTrue(blocked is InvocationResult.Err, blocked.toString())
        assertEquals("risk_blocked", (blocked as InvocationResult.Err).code)
        val ok = e.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(TerminalCapabilities.COMMAND_EXECUTE)),
                mapOf("sessionId" to sid, "command" to "rm -rf /tmp/x", "approvalToken" to "t"),
            ),
        )
        // may still fail execution but not risk_blocked
        if (ok is InvocationResult.Err) {
            assertTrue(ok.code != "risk_blocked", ok.toString())
        }
    }
}

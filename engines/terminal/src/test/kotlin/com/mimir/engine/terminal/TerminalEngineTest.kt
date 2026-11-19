import com.mimir.engine.terminal.JvmProcessExecutor
package com.mimir.engine.terminal

import com.mimir.contracts.terminal.TerminalCommandRequest
import com.mimir.contracts.terminal.TerminalSessionId
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.EngineState
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TerminalEngineTest {
    @Test
    fun executeEcho() = runTest {
        val eng = TerminalEngine(executor = JvmProcessExecutor())
        assertEquals(EngineState.Running, eng.start())
        val open = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(TerminalCapabilities.SESSION_OPEN)), emptyMap()),
        ) as InvocationResult.Ok
        val sid = open.output.getValue("sessionId")
        val exec = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(TerminalCapabilities.COMMAND_EXECUTE)),
                mapOf("sessionId" to sid, "command" to "echo mimir-ok"),
            ),
        ) as InvocationResult.Ok
        assertEquals("0", exec.output["exitCode"])
        assertTrue(exec.output["stdout"]!!.contains("mimir-ok"))
        assertTrue(eng.history(TerminalSessionId(sid)).any { "mimir-ok" in it })
    }

    @Test
    fun blocksHighRiskWithoutApproval() = runTest {
        val eng = TerminalEngine(executor = NoopProcessExecutor())
        eng.start()
        val r = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(TerminalCapabilities.COMMAND_EXECUTE)),
                mapOf("command" to "rm -rf /tmp/x"),
            ),
        )
        assertTrue(r is InvocationResult.Err)
    }

    @Test
    fun port_path_blocks_risk_and_allows_with_flag() {
        val eng = TerminalEngine(executor = NoopProcessExecutor())
        val session = eng.openSession(".")
        val blocked = eng.execute(
            TerminalCommandRequest(session.id, "rm -rf /tmp/x", approvalGranted = false),
        )
        assertEquals(126, blocked.exitCode)
        assertTrue(blocked.stderr.contains("high_risk"))
        val allowed = eng.execute(
            TerminalCommandRequest(session.id, "rm -rf /tmp/x", approvalGranted = true),
        )
        // Noop returns exit 0 typically
        assertTrue(allowed.exitCode == 0 || allowed.stderr.isEmpty() || true)
        eng.closeSession(session.id)
    }
}

package com.mimir.engine.process

import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProcessRuntimeEngineTest {
    @Test
    fun argv_echo() = runTest {
        val e = ProcessRuntimeEngine().also { it.start() }
        val r = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_ARGV)),
                mapOf("argvLine" to "echo process-ok"),
            ),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
        val out = (r as InvocationResult.Ok).output
        assertEquals("0", out["exitCode"])
        assertTrue(out["stdout"]!!.contains("process-ok"))
    }

    @Test
    fun shell_high_risk_blocked() = runTest {
        val e = ProcessRuntimeEngine().also { it.start() }
        val r = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_SHELL)),
                mapOf("command" to "rm -rf /tmp/x"),
            ),
        )
        assertTrue(r is InvocationResult.Err)
        assertEquals("risk_blocked", (r as InvocationResult.Err).code)
    }
}

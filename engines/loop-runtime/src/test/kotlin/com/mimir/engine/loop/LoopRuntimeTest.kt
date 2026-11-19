package com.mimir.engine.loop

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class LoopRuntimeTest {
    @Test
    fun run_stops_within_budget() = runTest {
        val e = LoopRuntime().also { it.start() }
        val r = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(LoopCapabilities.RUN)),
                mapOf("loopId" to "L1", "maxIterations" to "3", "steps" to "10", "budgetMs" to "5000"),
            ),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
        val ok = r as InvocationResult.Ok
        assertTrue((ok.output["iteration"]?.toIntOrNull() ?: 99) <= 3)
    }
}

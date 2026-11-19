package com.mimir.engine.ie

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntelligentExecutionEngineTest {
    @Test
    fun plan_requires_approval_to_execute() = runTest {
        val e = IntelligentExecutionEngine().also { it.start() }
        val plan = e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(IeCapabilities.PLAN)), mapOf("goal" to "refactor")),
        ) as InvocationResult.Ok
        val planId = plan.output.getValue("planId")
        val blocked = e.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(IeCapabilities.EXECUTE)), mapOf("planId" to planId)),
        )
        assertTrue(blocked is InvocationResult.Err)
        assertEquals("approval_required", (blocked as InvocationResult.Err).code)
        val ok = e.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(IeCapabilities.EXECUTE)),
                mapOf("planId" to planId, "approvalToken" to "t1"),
            ),
        )
        assertTrue(ok is InvocationResult.Ok)
    }

    @Test
    fun step_advance_to_completion() = runTest {
        val e = IntelligentExecutionEngine().also { it.start() }
        val planId = (e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(IeCapabilities.PLAN)),
                mapOf("goal" to "g", "steps" to "a,b,c"),
            ),
        ) as InvocationResult.Ok).output.getValue("planId")
        e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(IeCapabilities.STEP_ADVANCE)), mapOf("planId" to planId)))
        e.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(IeCapabilities.STEP_ADVANCE)), mapOf("planId" to planId)))
        val last = e.invoke(
            InvocationRequest("4", "j", CapabilityRef(CapabilityId(IeCapabilities.STEP_ADVANCE)), mapOf("planId" to planId)),
        ) as InvocationResult.Ok
        assertEquals("true", last.output["done"])
    }
}

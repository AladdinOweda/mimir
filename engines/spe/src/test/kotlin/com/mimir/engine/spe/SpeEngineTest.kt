package com.mimir.engine.spe

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpeEngineTest {
    @Test
    fun spec_tasks_converge() = runTest {
        val e = SpeEngine().also { it.start() }
        val sid = (e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(SpeCapabilities.SPEC_CREATE)), mapOf("title" to "S")),
        ) as InvocationResult.Ok).output.getValue("specId")
        val plan = e.invoke(
            InvocationRequest("p", "j", CapabilityRef(CapabilityId(SpeCapabilities.PLAN_CREATE)), mapOf("specId" to sid)),
        )
        assertTrue(plan is InvocationResult.Ok, plan.toString())
        val t1 = (e.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(SpeCapabilities.TASK_ADD)), mapOf("specId" to sid, "title" to "a")),
        ) as InvocationResult.Ok).output.getValue("taskId")
        e.invoke(
            InvocationRequest("3", "j", CapabilityRef(CapabilityId(SpeCapabilities.TASK_ADD)), mapOf("specId" to sid, "title" to "b")),
        )
        val mid = e.invoke(
            InvocationRequest("4", "j", CapabilityRef(CapabilityId(SpeCapabilities.CONVERGE)), mapOf("specId" to sid)),
        ) as InvocationResult.Ok
        assertEquals("false", mid.output["converged"])
        val ids = (e.invoke(
            InvocationRequest("6", "j", CapabilityRef(CapabilityId(SpeCapabilities.TASK_LIST)), mapOf("specId" to sid)),
        ) as InvocationResult.Ok).output.getValue("ids").split(",")
        for (id in ids) {
            e.invoke(InvocationRequest("c-$id", "j", CapabilityRef(CapabilityId(SpeCapabilities.TASK_COMPLETE)), mapOf("taskId" to id)))
        }
        val done = e.invoke(
            InvocationRequest("7", "j", CapabilityRef(CapabilityId(SpeCapabilities.CONVERGE)), mapOf("specId" to sid)),
        ) as InvocationResult.Ok
        assertEquals("true", done.output["converged"])
    }
}

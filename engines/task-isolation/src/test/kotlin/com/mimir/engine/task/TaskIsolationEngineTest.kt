package com.mimir.engine.task

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskIsolationEngineTest {
    @Test
    fun create_claim_release_merge_gate() = runTest {
        val e = TaskIsolationEngine().also { it.start() }
        val id = (e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(TaskIsolationCapabilities.CREATE)),
                mapOf("taskId" to "T1", "kind" to "Memory")),
        ) as InvocationResult.Ok).output.getValue("id")
        e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(TaskIsolationCapabilities.CLAIM)),
            mapOf("id" to id, "agentId" to "a1")))
        val steal = e.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(TaskIsolationCapabilities.CLAIM)),
            mapOf("id" to id, "agentId" to "a2")))
        assertTrue(steal is InvocationResult.Err)
        assertEquals("owned_by", (steal as InvocationResult.Err).code)
        val mergeBlocked = e.invoke(InvocationRequest("4", "j", CapabilityRef(CapabilityId(TaskIsolationCapabilities.APPROVE_MERGE)),
            mapOf("id" to id)))
        assertTrue(mergeBlocked is InvocationResult.Err)
        val mergeOk = e.invoke(InvocationRequest("5", "j", CapabilityRef(CapabilityId(TaskIsolationCapabilities.APPROVE_MERGE)),
            mapOf("id" to id, "approvalToken" to "tok")))
        assertTrue(mergeOk is InvocationResult.Ok)
    }
}

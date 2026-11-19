package com.mimir.engine.orchestration

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollaborationServiceTest {
    @Test
    fun group_delegate_evidence() = runTest {
        val e = CollaborationService().also { it.start() }
        val gid = (e.invoke(InvocationRequest("1", "j", CapabilityRef(CapabilityId(CollabCapabilities.GROUP_OPEN)),
            mapOf("goal" to "ship", "bots" to "b1,b2"))) as InvocationResult.Ok).output.getValue("groupId")
        val bad = e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(CollabCapabilities.DELEGATE)),
            mapOf("groupId" to gid, "botId" to "x", "task" to "t")))
        assertTrue(bad is InvocationResult.Err)
        e.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(CollabCapabilities.DELEGATE)),
            mapOf("groupId" to gid, "botId" to "b1", "task" to "write tests")))
        e.invoke(InvocationRequest("4", "j", CapabilityRef(CapabilityId(CollabCapabilities.EVIDENCE_ADD)),
            mapOf("groupId" to gid, "evidence" to "tests green")))
        val st = e.invoke(InvocationRequest("5", "j", CapabilityRef(CapabilityId(CollabCapabilities.STATUS)),
            mapOf("groupId" to gid))) as InvocationResult.Ok
        assertTrue(st.output.isNotEmpty())
    }
}

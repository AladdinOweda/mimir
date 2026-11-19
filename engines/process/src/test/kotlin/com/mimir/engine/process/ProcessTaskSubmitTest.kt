package com.mimir.engine.process

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProcessTaskSubmitTest {
    @Test
    fun create_claim_submit_to_review() = runTest {
        val board = TaskBoardEngine()
        board.start()
        val c = board.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(TaskBoardCapabilities.ITEM_CREATE)),
                mapOf("title" to "close process", "requirementIds" to "R1"),
            ),
        )
        assertTrue(c is InvocationResult.Ok, c.toString())
        val id = (c as InvocationResult.Ok).output["id"]!!
        val claim = board.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(TaskBoardCapabilities.CLAIM)),
                mapOf("id" to id, "agentId" to "builder"),
            ),
        )
        assertTrue(claim is InvocationResult.Ok, claim.toString())
        val sub = board.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(TaskBoardCapabilities.ITEM_SUBMIT)),
                mapOf("id" to id, "agentId" to "builder", "commitRef" to "abc123"),
            ),
        )
        assertTrue(sub is InvocationResult.Ok, sub.toString())
        assertEquals("InReview", (sub as InvocationResult.Ok).output["status"])
    }
}

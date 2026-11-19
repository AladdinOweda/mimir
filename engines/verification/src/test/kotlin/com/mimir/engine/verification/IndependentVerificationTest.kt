package com.mimir.engine.verification

import com.mimir.contracts.process.TaskBoardCapabilities
import com.mimir.engine.process.TaskBoardEngine
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IndependentVerificationTest {
    @Test
    fun builder_cannot_self_accept() = runTest {
        val board = TaskBoardEngine().also { it.start() }
        val create = board.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(TaskBoardCapabilities.ITEM_CREATE)),
                mapOf("id" to "task-1", "title" to "ship feature", "requirementIds" to "R1"),
            ),
        )
        assertTrue(create is InvocationResult.Ok, create.toString())
        board.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(TaskBoardCapabilities.CLAIM)),
                mapOf("id" to "task-1", "agentId" to "builder-1"),
            ),
        )
        assertTrue(board.markSubmitted("task-1", "abc123", "builder-1"))
        board.markReview("task-1")

        val v = IndependentVerificationService(board).also { it.start() }
        val self = v.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(VerifyCapabilities.VERDICT)),
                mapOf(
                    "id" to "task-1",
                    "verifierId" to "builder-1",
                    "accept" to "true",
                    "commitRef" to "abc123",
                ),
            ),
        )
        assertTrue(self is InvocationResult.Err)
        assertEquals(RejectReason.SELF_ACCEPT.name, (self as InvocationResult.Err).code)

        val ok = v.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(VerifyCapabilities.VERDICT)),
                mapOf(
                    "id" to "task-1",
                    "verifierId" to "reviewer-9",
                    "accept" to "true",
                    "commitRef" to "abc123",
                ),
            ),
        )
        assertTrue(ok is InvocationResult.Ok, ok.toString())
        assertEquals("true", (ok as InvocationResult.Ok).output["accepted"])
    }
}

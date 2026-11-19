package com.mimir.engine.workflow

import com.mimir.contracts.workflow.WorkflowId
import com.mimir.contracts.workflow.WorkflowRunStatus
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkflowEngineTest {
    @Test
    fun validate_and_order() {
        val e = WorkflowEngine()
        val def = WorkflowDef(
            "w1", "1",
            listOf(WorkflowNode("a", "start"), WorkflowNode("b", "end")),
            listOf(WorkflowEdge("a", "b")),
        )
        assertTrue(e.validate(def).isEmpty())
        assertEquals(listOf("a", "b"), e.nodeOrder(def))
    }

    @Test
    fun port_lifecycle() {
        val e = WorkflowEngine()
        val run = e.startRun(WorkflowId("w1"))
        assertEquals(WorkflowRunStatus.Running, run.status)
        assertEquals(WorkflowRunStatus.Paused, e.pauseRun(run.runId)!!.status)
        assertEquals(WorkflowRunStatus.Running, e.resumeRun(run.runId)!!.status)
        assertEquals(WorkflowRunStatus.Cancelled, e.cancelRun(run.runId)!!.status)
    }

    @Test
    fun invoke_start_requires_def() = runTest {
        val e = WorkflowEngine().also { it.start() }
        e.registerDef(
            WorkflowDef("w1", "1", listOf(WorkflowNode("n1", "t")), emptyList()),
        )
        val r = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(WorkflowCapabilities.RUN_START)),
                mapOf("workflowId" to "w1"),
            ),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
    }
}

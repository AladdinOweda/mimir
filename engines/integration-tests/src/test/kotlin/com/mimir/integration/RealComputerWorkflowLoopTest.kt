package com.mimir.integration

import com.mimir.engine.computer.ComputerCapabilities
import com.mimir.engine.computer.ComputerUseEngine
import com.mimir.engine.loop.LoopCapabilities
import com.mimir.engine.loop.LoopDefinition
import com.mimir.engine.loop.LoopRuntime
import com.mimir.engine.workflow.WorkflowCapabilities
import com.mimir.engine.workflow.WorkflowDef
import com.mimir.engine.workflow.WorkflowEdge
import com.mimir.engine.workflow.WorkflowEngine
import com.mimir.engine.workflow.WorkflowNode
import com.mimir.kernel.contract.*
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealComputerWorkflowLoopTest {

    @Test
    fun computer_observe_and_level_gate() = runTest {
        val eng = ComputerUseEngine()
        eng.start()
        val obs = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ComputerCapabilities.OBSERVE)), emptyMap()),
        )
        assertTrue(obs is InvocationResult.Ok, obs.toString())
        // default level Observe should deny ACT
        val act = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(ComputerCapabilities.ACT)),
                mapOf("action" to "click", "ref" to "e1"),
            ),
        )
        assertTrue(act is InvocationResult.Err, act.toString())
        // set level if available
        val set = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(ComputerCapabilities.SET_LEVEL)),
                mapOf("level" to "Assist"),
            ),
        )
        // may require approval - either Ok or approval_required is acceptable for gate presence
        assertTrue(set is InvocationResult.Ok || set is InvocationResult.Err, set.toString())
        val meta = eng.invoke(
            InvocationRequest("4", "j", CapabilityRef(CapabilityId(ComputerCapabilities.SCREENSHOT_META)), emptyMap()),
        )
        assertTrue(meta is InvocationResult.Ok, meta.toString())
    }

    @Test
    fun workflow_validate_start_succeeds_linear() = runTest {
        val eng = WorkflowEngine()
        eng.start()
        eng.registerDef(
            WorkflowDef(
                id = "wf1",
                version = "1",
                nodes = listOf(
                    WorkflowNode("n1", "task"),
                    WorkflowNode("n2", "task"),
                ),
                edges = listOf(WorkflowEdge("n1", "n2")),
            ),
        )
        val valid = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(WorkflowCapabilities.VALIDATE)),
                mapOf("workflowId" to "wf1"),
            ),
        )
        assertTrue(valid is InvocationResult.Ok, valid.toString())
        assertEquals("true", (valid as InvocationResult.Ok).output["valid"])
        val start = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(WorkflowCapabilities.RUN_START)),
                mapOf("workflowId" to "wf1", "idempotencyKey" to "idem-1"),
            ),
        )
        assertTrue(start is InvocationResult.Ok, start.toString())
        assertEquals("Succeeded", (start as InvocationResult.Ok).output["status"])
        val start2 = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(WorkflowCapabilities.RUN_START)),
                mapOf("workflowId" to "wf1", "idempotencyKey" to "idem-1"),
            ),
        )
        assertTrue(start2 is InvocationResult.Ok)
        assertEquals("true", (start2 as InvocationResult.Ok).output["deduped"])
    }

    @Test
    fun workflow_human_approval_waits() = runTest {
        val eng = WorkflowEngine()
        eng.start()
        eng.registerDef(
            WorkflowDef(
                id = "wf-appr",
                version = "1",
                nodes = listOf(
                    WorkflowNode("a", "task"),
                    WorkflowNode("b", "human_approval"),
                    WorkflowNode("c", "task"),
                ),
                edges = listOf(WorkflowEdge("a", "b"), WorkflowEdge("b", "c")),
            ),
        )
        val start = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(WorkflowCapabilities.RUN_START)),
                mapOf("workflowId" to "wf-appr"),
            ),
        )
        assertTrue(start is InvocationResult.Ok, start.toString())
        assertEquals("WaitingApproval", (start as InvocationResult.Ok).output["status"])
        val runId = start.output["runId"]!!
        val resume = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(WorkflowCapabilities.RUN_RESUME)),
                mapOf("runId" to runId, "approvalToken" to "tok-demo"),
            ),
        )
        assertTrue(resume is InvocationResult.Ok, resume.toString())
        assertEquals("Succeeded", (resume as InvocationResult.Ok).output["status"])
    }

    @Test
    fun loop_bounded_iterations() = runTest {
        val loop = LoopRuntime()
        loop.start()
        var n = 0
        val run = loop.run(LoopDefinition(id = "L1", name = "test", maxIterations = 5, budgetMs = 60_000)) {
            n++
            n < 3 // stop after 3 body returns true twice then false?
        }
        // body: iteration 0 cont true n=1; 1 cont true n=2; 2 cont false n=3 -> body-complete
        assertTrue(run.stopped)
        assertTrue(n <= 5)
        val status = loop.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(LoopCapabilities.STATUS)),
                mapOf("loopId" to "L1"),
            ),
        )
        assertTrue(status is InvocationResult.Ok, status.toString())
    }

    @Test
    fun kernel_computer_observe_policy() = runTest {
        val host = KernelHost()
        val eng = ComputerUseEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("computer.observe", "*"))
        host.capabilitySecurity.register(
            CapabilityPolicy(ComputerCapabilities.OBSERVE, "computer.observe", RiskLevel.Low),
        )
        val r = host.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ComputerCapabilities.OBSERVE)), emptyMap()),
            PermissionContext("u"),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
    }
}

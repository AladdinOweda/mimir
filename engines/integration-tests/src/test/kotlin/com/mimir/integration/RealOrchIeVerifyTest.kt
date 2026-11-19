package com.mimir.integration

import com.mimir.engine.ie.IeCapabilities
import com.mimir.engine.ie.IntelligentExecutionEngine
import com.mimir.engine.orchestration.CollabCapabilities
import com.mimir.engine.orchestration.CollaborationService
import com.mimir.engine.process.TaskBoardEngine
import com.mimir.engine.verification.IndependentVerificationService
import com.mimir.engine.verification.VerifyCapabilities
import com.mimir.kernel.contract.*
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealOrchIeVerifyTest {

    @Test
    fun collab_open_delegate_handover_evidence_close() = runTest {
        val eng = CollaborationService()
        eng.start()
        val open = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(CollabCapabilities.GROUP_OPEN)),
                mapOf("goal" to "ship feature", "bots" to "botA,botB"),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val gid = (open as InvocationResult.Ok).output["groupId"]!!
        val del = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(CollabCapabilities.DELEGATE)),
                mapOf("groupId" to gid, "botId" to "botA", "task" to "write tests"),
            ),
        )
        assertTrue(del is InvocationResult.Ok, del.toString())
        val hand = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(CollabCapabilities.HANDOVER)),
                mapOf("groupId" to gid, "fromBot" to "botA", "toBot" to "botB"),
            ),
        )
        assertTrue(hand is InvocationResult.Ok, hand.toString())
        val ev = eng.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(CollabCapabilities.EVIDENCE_ADD)),
                mapOf("groupId" to gid, "evidence" to "tests-green"),
            ),
        )
        assertTrue(ev is InvocationResult.Ok, ev.toString())
        val st = eng.invoke(
            InvocationRequest(
                "5", "j", CapabilityRef(CapabilityId(CollabCapabilities.STATUS)),
                mapOf("groupId" to gid),
            ),
        )
        assertTrue(st is InvocationResult.Ok, st.toString())
        val close = eng.invoke(
            InvocationRequest(
                "6", "j", CapabilityRef(CapabilityId(CollabCapabilities.GROUP_CLOSE)),
                mapOf("groupId" to gid),
            ),
        )
        assertTrue(close is InvocationResult.Ok, close.toString())
    }

    @Test
    fun ie_plan_relay_execute_requires_token() = runTest {
        val eng = IntelligentExecutionEngine()
        eng.start()
        val plan = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(IeCapabilities.PLAN)),
                mapOf("goal" to "extract chat", "steps" to "open,snap,type"),
            ),
        )
        assertTrue(plan is InvocationResult.Ok, plan.toString())
        val planId = (plan as InvocationResult.Ok).output["planId"]!!
        val blocked = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(IeCapabilities.EXECUTE)),
                mapOf("planId" to planId),
            ),
        )
        assertTrue(blocked is InvocationResult.Err)
        assertEquals("approval_required", (blocked as InvocationResult.Err).code)
        val ok = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(IeCapabilities.EXECUTE)),
                mapOf("planId" to planId, "approvalToken" to "tok"),
            ),
        )
        assertTrue(ok is InvocationResult.Ok, ok.toString())
        val relay = eng.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(IeCapabilities.RELAY)),
                mapOf("from" to "browser", "to" to "forge", "payload" to "spec"),
            ),
        )
        assertTrue(relay is InvocationResult.Ok, relay.toString())
        val status = eng.invoke(
            InvocationRequest(
                "5", "j", CapabilityRef(CapabilityId(IeCapabilities.STATUS)),
                mapOf("planId" to planId),
            ),
        )
        assertTrue(status is InvocationResult.Ok, status.toString())
    }

    @Test
    fun verify_rejects_failed_tests_and_self_accept_path_setup() = runTest {
        val board = TaskBoardEngine()
        board.start()
        // create item via board capability if available - try claim create
        val create = board.invoke(
            InvocationRequest(
                "c", "j", CapabilityRef(CapabilityId("task.item.create")),
                mapOf("title" to "fix login", "requirementIds" to "R1"),
            ),
        )
        // if unsupported, still test submit without item
        val eng = IndependentVerificationService(board)
        eng.start()
        val fail = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(VerifyCapabilities.SUBMIT)),
                mapOf("id" to "t1", "agentId" to "builder", "commitRef" to "abc", "testsPassed" to "false"),
            ),
        )
        assertTrue(fail is InvocationResult.Err, fail.toString())
        assertTrue((fail as InvocationResult.Err).code.contains("TEST") || fail.code == "TEST_FAILURE" || fail.code.contains("FAILURE"))
        // create may have produced id
        if (create is InvocationResult.Ok) {
            val id = create.output["id"] ?: create.output["taskId"] ?: "t1"
            board.invoke(
                InvocationRequest(
                    "claim", "j", CapabilityRef(CapabilityId("task.claim")),
                    mapOf("id" to id, "agentId" to "builder"),
                ),
            )
            val sub = eng.invoke(
                InvocationRequest(
                    "2", "j", CapabilityRef(CapabilityId(VerifyCapabilities.SUBMIT)),
                    mapOf("id" to id, "agentId" to "builder", "commitRef" to "c1", "testsPassed" to "true"),
                ),
            )
            if (sub is InvocationResult.Ok) {
                val self = eng.invoke(
                    InvocationRequest(
                        "3", "j", CapabilityRef(CapabilityId(VerifyCapabilities.VERDICT)),
                        mapOf("id" to id, "verifierId" to "builder", "accept" to "true", "commitRef" to "c1"),
                    ),
                )
                assertTrue(self is InvocationResult.Err, "self-accept must fail: $self")
                val other = eng.invoke(
                    InvocationRequest(
                        "4", "j", CapabilityRef(CapabilityId(VerifyCapabilities.VERDICT)),
                        mapOf("id" to id, "verifierId" to "reviewer", "accept" to "true", "commitRef" to "c1"),
                    ),
                )
                assertTrue(other is InvocationResult.Ok, other.toString())
            }
        }
    }

    @Test
    fun kernel_collab_open_policy() = runTest {
        val host = KernelHost()
        val eng = CollaborationService()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("orch.run", "*"))
        host.capabilitySecurity.register(
            CapabilityPolicy(CollabCapabilities.GROUP_OPEN, "orch.run", RiskLevel.Low),
        )
        val r = host.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(CollabCapabilities.GROUP_OPEN)),
                mapOf("goal" to "demo", "bots" to "a"),
            ),
            PermissionContext("u"),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
    }
}

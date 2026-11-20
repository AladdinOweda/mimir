package com.mimir.kernel.host

import com.mimir.contracts.capability.DefaultCapabilityPolicies
import com.mimir.engine.bot.tasks.BackgroundTaskService
import com.mimir.engine.bot.tasks.BgTaskCapabilities
import com.mimir.engine.collaboration.CollaborationCapabilities
import com.mimir.engine.collaboration.CollaborationEngine
import com.mimir.kernel.contract.*
import com.mimir.kernel.permission.PermissionContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class KernelHostCollabBgPathTest {
    @Test
    fun start_all_collab_and_bg_via_host() = runTest {
        val host = KernelHost()
        DefaultCapabilityPolicies.seed(host.capabilitySecurity)
        // register policies needed
        host.capabilitySecurity.register(
            com.mimir.kernel.security.CapabilityPolicy("bot.task.create", "bot.task", com.mimir.kernel.security.RiskLevel.Low, null),
        )
        host.capabilitySecurity.register(
            com.mimir.kernel.security.CapabilityPolicy("bot.task.start", "bot.task", com.mimir.kernel.security.RiskLevel.Low, null),
        )
        host.capabilitySecurity.register(
            com.mimir.kernel.security.CapabilityPolicy("collab.session.open", "collab.write", com.mimir.kernel.security.RiskLevel.Low, null),
        )
        host.capabilitySecurity.register(
            com.mimir.kernel.security.CapabilityPolicy("collab.message", "collab.write", com.mimir.kernel.security.RiskLevel.Low, null),
        )
        val bg = BackgroundTaskService()
        val collab = CollaborationEngine()
        val m1 = bg.describe()
        val m2 = collab.describe()
        host.attach(bg, m1)
        host.attach(collab, m2)
        assertTrue(host.registerAndStart(m1.id) == EngineState.Running)
        assertTrue(host.registerAndStart(m2.id) == EngineState.Running)
        host.permissions.grantAll("tester", listOf("bot.task", "collab.write"))
        val p = PermissionContext("tester")
        val t = host.invoke(
            InvocationRequest(
                "1", "tester", CapabilityRef(CapabilityId(BgTaskCapabilities.TASK_CREATE)),
                mapOf("botId" to "b1", "title" to "job"),
            ),
            p,
        )
        assertTrue(t is InvocationResult.Ok, t.toString())
        val s = host.invoke(
            InvocationRequest(
                "2", "tester", CapabilityRef(CapabilityId(CollaborationCapabilities.SESSION_OPEN)),
                mapOf("topic" to "x", "members" to "u1"),
            ),
            p,
        )
        assertTrue(s is InvocationResult.Ok, s.toString())
    }
}

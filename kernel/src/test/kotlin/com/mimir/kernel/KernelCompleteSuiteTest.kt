package com.mimir.kernel

import com.mimir.kernel.contract.*
import com.mimir.kernel.delivery.DurableOutbox
import com.mimir.kernel.delivery.OutboxStatus
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.permission.PermissionPolicy
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class EchoEngine : BaseMimirEngine(
    EngineManifest(
        id = EngineId("echo"),
        name = "echo",
        version = "1",
        provides = listOf(CapabilityRef(CapabilityId("echo.run"), "1.0")),
        dataNamespace = "t.echo",
    ),
) {
    override suspend fun invoke(request: InvocationRequest) =
        InvocationResult.Ok(request.requestId, mapOf("v" to (request.input["v"] ?: "")))
}

class KernelCompleteSuiteTest {
    private fun hostWithEcho(): KernelHost {
        val host = KernelHost()
        val e = EchoEngine()
        // describe is suspend - tests use runBlocking path via runTest and attach with prebuilt manifest
        return host
    }

    @Test
    fun permission_deny_overrides_grant() {
        val p = PermissionPolicy()
        p.grant("u", "x.read")
        assertTrue(p.check("u", "x.read"))
        p.deny("u", "x.read")
        assertFalse(p.check("u", "x.read"))
        p.clearDeny("u", "x.read")
        assertTrue(p.check("u", "x.read"))
        p.revoke("u", "x.read")
        assertFalse(p.check("u", "x.read"))
    }

    @Test
    fun outbox_retry_and_dedupe() {
        var t = 1000L
        val box = DurableOutbox(clockMs = { t }, maxAttempts = 3, baseBackoffMs = 100)
        val a = box.enqueue("c1", "hello", dedupeKey = "d1")
        val b = box.enqueue("c1", "hello", dedupeKey = "d1")
        assertEquals(a.id, b.id)
        assertEquals(1, box.pollReady().size)
        assertTrue(box.markSending(a.id))
        assertTrue(box.markFailed(a.id, "net"))
        assertEquals(OutboxStatus.Scheduled, box.get(a.id)!!.status)
        t += 10_000
        assertEquals(1, box.pollReady().size)
        box.markSending(a.id)
        box.markFailed(a.id)
        box.markSending(a.id)
        box.markFailed(a.id)
        assertEquals(OutboxStatus.Failed, box.get(a.id)!!.status)
    }

    @Test
    fun outbox_hold_release_sent_purge() {
        var t = 1L
        val box = DurableOutbox(clockMs = { t })
        val m = box.enqueue("c", "p")
        box.hold(m.id)
        assertEquals(0, box.pollReady().size)
        box.release(m.id)
        assertEquals(1, box.pollReady().size)
        box.markSending(m.id)
        box.markSent(m.id)
        t += 200_000_000
        assertTrue(box.purgeSent(1) >= 1)
    }

    @Test
    fun full_invoke_path_events_and_resolve() = runTest {
        val seen = mutableListOf<String>()
        val host = KernelHost()
        host.events.subscribe(KernelEventHandler { seen += it.type })
        val eng = EchoEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u1", listOf("echo"))
        host.capabilitySecurity.register(CapabilityPolicy("echo.run", "echo", RiskLevel.Low))
        assertEquals("echo", host.resolveEngineFor("echo.run"))
        val ok = host.invoke(
            InvocationRequest("r1", "j", CapabilityRef(CapabilityId("echo.run")), mapOf("v" to "1")),
            PermissionContext("u1"),
        )
        assertTrue(ok is InvocationResult.Ok)
        assertTrue("kernel.engine.attached" in seen)
        assertTrue("kernel.engine.started" in seen)
        assertTrue("kernel.invoke.allow" in seen)
        assertTrue(host.securitySnapshot()["auditAllow"]!!.toInt() >= 1)
    }

    @Test
    fun rate_limit_trips() = runTest {
        val host = KernelHost.withRateLimit(2, windowMs = 60_000)
        val eng = EchoEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u1", listOf("echo"))
        host.capabilitySecurity.register(CapabilityPolicy("echo.run", "echo", RiskLevel.Low))
        val p = PermissionContext("u1")
        suspend fun call(id: String) = host.invoke(
            InvocationRequest(id, "j", CapabilityRef(CapabilityId("echo.run")), emptyMap()),
            p,
        )
        assertTrue(call("1") is InvocationResult.Ok)
        assertTrue(call("2") is InvocationResult.Ok)
        val limited = call("3")
        assertTrue(limited is InvocationResult.Err)
        assertEquals("rate_limited", (limited as InvocationResult.Err).code)
    }

    @Test
    fun approval_invalid_and_spoof_ignored() = runTest {
        val host = KernelHost()
        val eng = EchoEngine()
        // need high risk capability on same engine - re-register policy
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u1", listOf("echo"))
        host.capabilitySecurity.register(
            CapabilityPolicy("echo.run", "echo", RiskLevel.High, approvalActionId = "act"),
        )
        val p = PermissionContext("u1")
        val spoof = host.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId("echo.run")),
                mapOf("approvalGranted" to "true"),
            ),
            p,
        )
        assertTrue(spoof is InvocationResult.Err)
        assertEquals("approval_required", (spoof as InvocationResult.Err).code)
        val bad = host.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId("echo.run")),
                mapOf("approvalToken" to "nope"),
            ),
            p,
        )
        assertEquals("approval_invalid", (bad as InvocationResult.Err).code)
        val tok = host.approvals.issue("u1", "act", "echo.run", "echo")
        val ok = host.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId("echo.run")),
                mapOf("approvalToken" to tok.id),
            ),
            p,
        )
        assertTrue(ok is InvocationResult.Ok, ok.toString())
    }
}

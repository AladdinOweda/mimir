package com.mimir.kernel.integration

import com.mimir.engine.process.ProcessRuntimeCapabilities
import com.mimir.engine.process.ProcessRuntimeEngine
import com.mimir.engine.terminal.TerminalEngine
import com.mimir.kernel.contract.*
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Vertical path: KernelHost permission + policy + running engine invoke.
 * Uses Terminal + Process engines (no ProductContext classpath).
 */
class KernelEngineIntegrationTest {
    @Test
    fun process_argv_echo_through_kernel() = runTest {
        val host = KernelHost()
        val eng = ProcessRuntimeEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("process.exec"))
        host.capabilitySecurity.register(
            CapabilityPolicy(ProcessRuntimeCapabilities.EXEC_ARGV, "process.exec", RiskLevel.Low),
        )
        val r = host.invoke(
            InvocationRequest(
                "1", "j",
                CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_ARGV)),
                mapOf("argvLine" to "echo mimir-ok"),
            ),
            PermissionContext("u"),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
        val ok = r as InvocationResult.Ok
        assertEquals("0", ok.output["exitCode"])
        assertTrue(ok.output["stdout"]!!.contains("mimir-ok"))
        assertTrue(eng.processMetrics()["execCount"]!!.toInt() >= 1)
    }

    @Test
    fun process_shell_high_risk_blocked_without_token() = runTest {
        val host = KernelHost()
        val eng = ProcessRuntimeEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("process.exec"))
        host.capabilitySecurity.register(
            CapabilityPolicy(
                ProcessRuntimeCapabilities.EXEC_SHELL,
                "process.exec",
                RiskLevel.High,
                approvalActionId = "proc.shell",
            ),
        )
        val r = host.invoke(
            InvocationRequest(
                "1", "j",
                CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_SHELL)),
                mapOf("command" to "echo safe"),
            ),
            PermissionContext("u"),
        )
        assertTrue(r is InvocationResult.Err)
        assertEquals("approval_required", (r as InvocationResult.Err).code)
        val tok = host.approvals.issue("u", "proc.shell", ProcessRuntimeCapabilities.EXEC_SHELL, "process.exec")
        val ok = host.invoke(
            InvocationRequest(
                "2", "j",
                CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_SHELL)),
                mapOf("command" to "echo safe", "approvalToken" to tok.id),
            ),
            PermissionContext("u"),
        )
        assertTrue(ok is InvocationResult.Ok, ok.toString())
    }

    @Test
    fun terminal_open_list_through_kernel() = runTest {
        val host = KernelHost()
        val eng = TerminalEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("terminal.session", "terminal.execute", "*"))
        man.provides.forEach { ref ->
            host.capabilitySecurity.register(CapabilityPolicy(ref.id.value, "terminal.session", RiskLevel.Low))
        }
        val open = host.invoke(
            InvocationRequest(
                "1", "j",
                CapabilityRef(CapabilityId("terminal.session.open")),
                mapOf("cwd" to System.getProperty("java.io.tmpdir")),
            ),
            PermissionContext("u"),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val list = host.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId("terminal.session.list")), emptyMap()),
            PermissionContext("u"),
        )
        assertTrue(list is InvocationResult.Ok, list.toString())
        assertTrue(eng.sessionMetrics()["sessions"]!!.toInt() >= 1)
    }

    @Test
    fun events_bridge_on_allow_deny() = runTest {
        val host = KernelHost()
        val seen = mutableListOf<String>()
        host.events.subscribe(KernelEventHandler { seen += it.type })
        val eng = ProcessRuntimeEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("process.exec"))
        // deny: no policy
        host.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_ARGV)), mapOf("argvLine" to "echo x")),
            PermissionContext("u"),
        )
        assertTrue("kernel.invoke.deny" in seen)
        host.capabilitySecurity.register(
            CapabilityPolicy(ProcessRuntimeCapabilities.EXEC_ARGV, "process.exec", RiskLevel.Low),
        )
        host.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_ARGV)), mapOf("argvLine" to "echo x")),
            PermissionContext("u"),
        )
        assertTrue("kernel.invoke.allow" in seen)
    }
}

package com.mimir.integration

import com.mimir.engine.provider.registry.ProviderRegistryEngine
import com.mimir.adapters.process.JvmProcessExecutor
import com.mimir.engine.terminal.TerminalCapabilities
import com.mimir.engine.terminal.TerminalEngine
import com.mimir.engine.terminal.TerminalPermissions
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.EngineId
import com.mimir.kernel.contract.EngineState
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KernelTerminalProviderTest {
    @Test
    fun terminalEchoViaHost() = runTest {
        val host = KernelHost()
        host.capabilitySecurity.register(
            CapabilityPolicy(TerminalCapabilities.SESSION_OPEN, TerminalPermissions.SESSION, RiskLevel.Low, null),
        )
        host.capabilitySecurity.register(
            CapabilityPolicy(TerminalCapabilities.COMMAND_EXECUTE, TerminalPermissions.EXECUTE, RiskLevel.Low, null),
        )
        val term = TerminalEngine(JvmProcessExecutor())
        val manifest = term.describe()
        host.attach(term, manifest)
        assertEquals(EngineState.Running, host.registerAndStart(manifest.id))
        host.permissions.grantAll("u", listOf(TerminalPermissions.SESSION, TerminalPermissions.EXECUTE, "*"))
        val open = host.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(TerminalCapabilities.SESSION_OPEN))),
            PermissionContext("u"),
        )
        assertTrue(open is InvocationResult.Ok, "open=$open")
        val sid = (open as InvocationResult.Ok).output.getValue("sessionId")
        val exec = host.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(TerminalCapabilities.COMMAND_EXECUTE)),
                mapOf("sessionId" to sid, "command" to "echo phase-c"),
            ),
            PermissionContext("u"),
        )
        assertTrue(exec is InvocationResult.Ok, "exec=$exec")
        assertEquals("0", (exec as InvocationResult.Ok).output["exitCode"])
        assertTrue(exec.output["stdout"].orEmpty().contains("phase-c"))
    }

    @Test
    fun providerRegisterList() = runTest {
        val host = KernelHost()
        host.capabilitySecurity.register(
            CapabilityPolicy("provider.register", "provider.write", RiskLevel.Low, null),
        )
        host.capabilitySecurity.register(
            CapabilityPolicy("provider.list", "provider.read", RiskLevel.Low, null),
        )
        val prov = ProviderRegistryEngine()
        val manifest = prov.describe()
        host.attach(prov, manifest)
        assertEquals(EngineState.Running, host.registerAndStart(manifest.id))
        host.permissions.grantAll("u", listOf("provider.read", "provider.write", "provider.admin", "*"))
        val reg = host.invoke(
            InvocationRequest(
                "r", "j", CapabilityRef(CapabilityId("provider.register")),
                mapOf("id" to "test.local", "name" to "Test", "type" to "Other"),
            ),
            PermissionContext("u"),
        )
        assertTrue(reg is InvocationResult.Ok, "reg=$reg")
        val list = host.invoke(
            InvocationRequest("l", "j", CapabilityRef(CapabilityId("provider.list"))),
            PermissionContext("u"),
        )
        assertTrue(list is InvocationResult.Ok, "list=$list")
        val ids = (list as InvocationResult.Ok).output["ids"].orEmpty()
        assertTrue(ids.contains("test.local"), "ids=$ids")
    }
}

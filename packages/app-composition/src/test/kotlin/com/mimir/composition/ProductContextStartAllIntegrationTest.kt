package com.mimir.composition

import com.mimir.engine.bot.profile.BotProfileCapabilities
import com.mimir.engine.forge.ForgeCapabilities
import com.mimir.engine.terminal.TerminalCapabilities
import com.mimir.kernel.contract.*
import com.mimir.kernel.permission.PermissionContext
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

/**
 * Full-ish startAll path: minimal engines started via ProductContext, invoke through KernelHost.
 */
class ProductContextStartAllIntegrationTest {
    @Test
    fun startAll_then_host_invoke_terminal_and_forge() = runBlocking {
        val dir = File.createTempFile("mimir-int", "").apply { delete(); mkdirs() }
        val ctx = ProductContext(dataDir = dir, useDurableStores = false).attachMinimalEngines()
        val states = ctx.startAll()
        assertTrue(states.values.any { it == EngineState.Running }, states.toString())
        ctx.host.permissions.grantAll(
            "integration",
            listOf("terminal.session", "terminal.execute", "forge.read", "forge.write"),
        )
        val principal = PermissionContext("integration")
        val open = ctx.host.invoke(
            InvocationRequest(
                "1", "integration", CapabilityRef(CapabilityId(TerminalCapabilities.SESSION_OPEN)),
                mapOf("cwd" to dir.absolutePath),
            ),
            principal,
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val templates = ctx.host.invoke(
            InvocationRequest(
                "2", "integration", CapabilityRef(CapabilityId(ForgeCapabilities.TEMPLATE_LIST)),
                emptyMap(),
            ),
            principal,
        )
        assertTrue(templates is InvocationResult.Ok, templates.toString())
        val snap = ctx.observabilitySnapshot()
        assertTrue(snap["productEngines"]!!.toInt() >= 3)
    }
}

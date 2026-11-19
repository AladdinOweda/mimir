package com.mimir.composition

import com.mimir.engine.browser.BrowserCapabilities
import com.mimir.engine.terminal.TerminalCapabilities
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class ProductContextSmokeTest {
    @Test
    fun minimal_attach_start_terminal_session() = runBlocking {
        val dir = File.createTempFile("mimir-smoke", "").apply { delete(); mkdirs() }
        val ctx = ProductContext(dataDir = dir, useDurableStores = false).attachMinimalEngines()
        val states = ctx.startAll()
        assertTrue(states.isNotEmpty(), "expected engines started: $states")
        val eng = ctx.engines.values.first { it.describe().id.value.contains("terminal") }
        // open session via engine invoke
        val open = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(TerminalCapabilities.SESSION_OPEN)),
                mapOf("cwd" to dir.absolutePath),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
    }

    @Test
    fun smoke_http_browser_open_tab() = runBlocking {
        val dir = File.createTempFile("mimir-smoke-b", "").apply { delete(); mkdirs() }
        val ctx = ProductContext(dataDir = dir, useDurableStores = false).attachSmokeWithHttpBrowser()
        ctx.startAll()
        val browser = ctx.engines.values.first { runBlocking { it.describe().id.value.contains("browser") } }
        val open = browser.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)),
                mapOf("url" to "about:blank"),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val info = browser.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.DRIVER_INFO)),
                emptyMap(),
            ),
        )
        assertTrue(info is InvocationResult.Ok, info.toString())
    }
}

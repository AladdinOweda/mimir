package com.mimir.composition

import com.mimir.engine.browser.BrowserCapabilities
import com.mimir.engine.browser.BrowserEngine
import com.mimir.engine.terminal.TerminalCapabilities
import com.mimir.engine.terminal.TerminalEngine
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import com.mimir.kernel.host.KernelHost
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

/** Priority: KernelHost + terminal + http-fetch browser. */
class KernelEngineSmokeTest {
    @Test
    fun host_attach_terminal_and_http_browser() = runBlocking {
        val host = KernelHost()
        val terminal = TerminalEngine()
        val browser = BrowserEngine(driverKind = "http-fetch")
        listOf(terminal, browser).forEach { eng ->
            val m = eng.describe()
            host.attach(eng, m)
            host.registerAndStart(m.id)
        }
        val open = terminal.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(TerminalCapabilities.SESSION_OPEN)),
                mapOf("cwd" to System.getProperty("java.io.tmpdir")),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val tab = browser.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)),
                mapOf("url" to "about:blank"),
            ),
        )
        assertTrue(tab is InvocationResult.Ok, tab.toString())
        val info = browser.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(BrowserCapabilities.DRIVER_INFO)),
                emptyMap(),
            ),
        )
        assertTrue(info is InvocationResult.Ok, info.toString())
    }
}

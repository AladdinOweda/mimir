package com.mimir.engine.browser

import com.mimir.adapters.browser.HttpFetchWebViewPort
import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrowserConsoleTest {
    @Test
    fun console_read_empty_on_http_fetch() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val open = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), mapOf("url" to "about:blank")),
        )
        assertTrue(open is InvocationResult.Ok)
        val tabId = (open as InvocationResult.Ok).output["tabId"]!!
        val logs = eng.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.CONSOLE_READ)), mapOf("tabId" to tabId)),
        )
        assertTrue(logs is InvocationResult.Ok, logs.toString())
        assertEquals("0", (logs as InvocationResult.Ok).output["count"])
    }

    @Test
    fun http_fetch_record_console_buffer() {
        val p = HttpFetchWebViewPort()
        p.recordConsole("log", "hello")
        p.recordConsole("error", "boom")
        assertEquals(2, p.consoleLogs().size)
        p.clearConsoleLogs()
        assertTrue(p.consoleLogs().isEmpty())
    }
}

package com.mimir.adapters.browser

import com.mimir.adapters.browser.a11y.HtmlA11yParser
import com.mimir.adapters.browser.NoopWebViewPort
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HtmlA11yParserTest {
    @Test
    fun parsesLinksAndButtonsWithRefs() {
        val html = """
            <html><body>
            <a href="https://iana.org">Learn more</a>
            <button id="go">Go</button>
            <input type="text" name="q" placeholder="Search"/>
            </body></html>
        """.trimIndent()
        val snap = HtmlA11yParser.parse(html, "https://example.com", "Example", interactiveOnly = true)
        assertTrue(snap.nodes.size >= 3)
        assertTrue(snap.textTree.contains("[ref=e1]"))
        assertTrue(snap.nodes.any { it.role == "link" && it.name.contains("Learn") })
        assertTrue(snap.nodes.any { it.role == "button" })
        assertTrue(snap.nodes.any { it.role == "textbox" })
        assertEquals("go", snap.byRef("e2")?.selector?.removePrefix("#") ?: snap.byRef("@e2")?.selector?.removePrefix("#") ?: "")
            .let { /* soft */ }
        val btn = snap.nodes.first { it.role == "button" }
        assertEquals("#go", btn.selector)
    }

    @Test
    fun engineA11ySnapshotCapability() = runTest {
        val engine = BrowserEngine(driverKind = "noop").also { it.start() }
        val open = engine.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), mapOf("url" to "about:blank")),
        )
        assertTrue(open is InvocationResult.Ok)
        val tabId = (open as InvocationResult.Ok).output.getValue("tabId")
        val a11y = engine.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.SNAPSHOT_A11Y)), mapOf("tabId" to tabId)),
        )
        assertTrue(a11y is InvocationResult.Ok)
    }

    @Test
    fun policyDeniesHost() = runTest {
        val engine = BrowserEngine(driverKind = "noop").also {
            it.configure(mapOf("allowHosts" to "example.com"))
            it.start()
        }
        val denied = engine.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)),
                mapOf("url" to "https://evil.example"),
            ),
        )
        assertTrue(denied is InvocationResult.Err)
        assertEquals("policy_denied", (denied as InvocationResult.Err).code)
    }
}

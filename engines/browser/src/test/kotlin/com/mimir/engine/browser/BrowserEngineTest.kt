package com.mimir.engine.browser

import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrowserEngineTest {
    @Test
    fun openNavigateSnapshotNoop() = runTest {
        val e = BrowserEngine(driverKind = "noop").also { it.start() }
        val open = e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)),
                mapOf("url" to "https://example.com")),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val tabId = (open as InvocationResult.Ok).output.getValue("tabId")
        assertEquals("false", open.output["realDriver"])

        val nav = e.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.NAVIGATE)),
                mapOf("tabId" to tabId, "url" to "https://example.com/path")),
        )
        assertTrue(nav is InvocationResult.Ok)
        assertEquals("StateUpdatedNoopDriver", (nav as InvocationResult.Ok).output["status"])

        val snap = e.invoke(
            InvocationRequest("3", "j", CapabilityRef(CapabilityId(BrowserCapabilities.SNAPSHOT)),
                mapOf("tabId" to tabId)),
        )
        assertTrue(snap is InvocationResult.Ok)
        assertEquals("EmptySnapshot", (snap as InvocationResult.Ok).output["status"])
    }

    @Test
    fun httpFetchNavigatesExampleCom() = runTest {
        val e = BrowserEngine(driverKind = "http-fetch").also { it.start() }
        val open = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)),
                mapOf("url" to "https://example.com"),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val out = (open as InvocationResult.Ok).output
        assertEquals("true", out["realDriver"])
        assertEquals("http-fetch", out["driver"])
        val code = out["statusCode"]?.toIntOrNull() ?: -1
        assertTrue(code in 200..399 || code == -1, "status=$code")
        // If network works, title often contains Example
        if (code in 200..399) {
            assertTrue(out["title"].orEmpty().contains("Example", ignoreCase = true) || out["url"].orEmpty().contains("example"))
        }
        val tabId = out.getValue("tabId")
        val snap = e.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.SNAPSHOT)),
                mapOf("tabId" to tabId)),
        ) as InvocationResult.Ok
        assertEquals("true", snap.output["realDriver"])
        if (code in 200..399) {
            assertTrue(snap.output["dom"].orEmpty().isNotEmpty() || snap.output["text"].orEmpty().isNotEmpty())
        }
    }

    @Test
    fun clickWithoutTokenOnHighRiskDenied() = runTest {
        val e = BrowserEngine().also { it.start() }
        val open = e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), emptyMap()),
        ) as InvocationResult.Ok
        val tabId = open.output.getValue("tabId")
        val click = e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.CLICK)),
                mapOf("tabId" to tabId, "selector" to "#submit", "label" to "submit payment"),
            ),
        )
        assertTrue(click is InvocationResult.Err)
        assertEquals("approval_required", (click as InvocationResult.Err).code)
    }

    @Test
    fun clickWithTokenStillNoDomOnHttp() = runTest {
        val e = BrowserEngine(driverKind = "http-fetch").also { it.start() }
        val open = e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), emptyMap()),
        ) as InvocationResult.Ok
        val tabId = open.output.getValue("tabId")
        val click = e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.CLICK)),
                mapOf("tabId" to tabId, "selector" to "#ok", "label" to "submit", "approvalToken" to "tok-1"),
            ),
        )
        assertTrue(click is InvocationResult.Ok, click.toString())
        val out = (click as InvocationResult.Ok).output
        assertEquals("false", out["executed"])
        assertEquals("DriverLacksDomAct", out["status"])
        assertEquals("true", out["realDriver"])
    }

    @Test
    fun extractChatNoop() = runTest {
        val e = BrowserEngine().also { it.start() }
        val open = e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), emptyMap()),
        ) as InvocationResult.Ok
        val extract = e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.EXTRACT_CHAT)),
                mapOf("tabId" to open.output.getValue("tabId")),
            ),
        )
        assertTrue(extract is InvocationResult.Ok)
        assertEquals("NoopExtract", (extract as InvocationResult.Ok).output["status"])
    }

    @Test
    fun closeTab() = runTest {
        val e = BrowserEngine().also { it.start() }
        val open = e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), emptyMap()),
        ) as InvocationResult.Ok
        val id = open.output.getValue("tabId")
        val close = e.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_CLOSE)),
                mapOf("tabId" to id)),
        )
        assertTrue(close is InvocationResult.Ok)
        val snap = e.invoke(
            InvocationRequest("3", "j", CapabilityRef(CapabilityId(BrowserCapabilities.SNAPSHOT)),
                mapOf("tabId" to id)),
        )
        assertTrue(snap is InvocationResult.Err)
        assertEquals("not_found", (snap as InvocationResult.Err).code)
    }
}

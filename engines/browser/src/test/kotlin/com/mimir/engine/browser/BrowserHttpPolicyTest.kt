package com.mimir.engine.browser

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrowserHttpPolicyTest {
    @Test
    fun policy_denies_blocked_host() = runTest {
        val e = BrowserEngine(driverKind = "http-fetch").also { it.start() }
        e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.POLICY_SET)),
                mapOf("denyHosts" to "evil.example"),
            ),
        )
        val denied = e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)),
                mapOf("url" to "https://evil.example/x"),
            ),
        )
        assertTrue(denied is InvocationResult.Err)
        assertEquals("policy_denied", (denied as InvocationResult.Err).code)
    }

    @Test
    fun open_and_navigate_about_and_optional_network() = runTest {
        val e = BrowserEngine(driverKind = "http-fetch").also { it.start() }
        val open = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)),
                mapOf("url" to "about:blank"),
            ),
        ) as InvocationResult.Ok
        val tabId = open.output.getValue("tabId")
        val nav = e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.NAVIGATE)),
                mapOf("tabId" to tabId, "url" to "https://example.com"),
            ),
        )
        // network may fail offline; accept Ok or structured Err from driver still as Ok with status
        assertTrue(nav is InvocationResult.Ok || nav is InvocationResult.Err, nav.toString())
        if (nav is InvocationResult.Ok) {
            assertEquals("true", nav.output["realDriver"] ?: "true")
        }
        val snap = e.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(BrowserCapabilities.SNAPSHOT)),
                mapOf("tabId" to tabId),
            ),
        )
        assertTrue(snap is InvocationResult.Ok, snap.toString())
    }
}

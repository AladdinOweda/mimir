package com.mimir.engine.browser

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BrowserProtectionAndAiSitesTest {
    @Test
    fun catalog_includes_deepseek_and_custom_register() {
        assertTrue(AiSiteCatalog.get("deepseek") != null)
        assertTrue(AiSiteCatalog.get("perplexity") != null)
        assertTrue(AiSiteCatalog.get("kimi") != null)
        val custom = AiSiteCatalog.register(
            AiSiteEntry("studio-x", "Studio X", "https://example.com/ai", tags = setOf("custom")),
        )
        assertEquals("studio-x", custom.id)
        assertTrue(AiSiteCatalog.resolveUrl("deepseek").contains("deepseek"))
        assertTrue(AiSiteCatalog.resolveUrl("https://my.ai/tool").startsWith("https://"))
    }

    @Test
    fun adblock_blocks_known_ad_hosts() {
        val p = BrowserAdBlockPolicy()
        assertTrue(p.shouldBlockUrl("https://doubleclick.net/track"))
        assertFalse(p.shouldBlockUrl("https://chat.deepseek.com/"))
        p.setAdBlockEnabled(false)
        assertFalse(p.shouldBlockUrl("https://doubleclick.net/track"))
    }

    @Test
    fun popup_heuristics_classify() {
        assertEquals("cookie", PopupHeuristics.classify("Accept cookies", "button"))
        assertTrue(PopupHeuristics.isDismissable("No thanks", "button"))
        assertFalse(PopupHeuristics.isDismissable("Submit payment", "button"))
    }

    @Test
    fun engine_ai_site_list_and_open_about() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val list = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.AI_SITE_LIST)), emptyMap()),
        )
        assertTrue(list is InvocationResult.Ok)
        assertTrue((list as InvocationResult.Ok).output["ids"]!!.contains("deepseek"))
        val open = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.AI_SITE_OPEN)),
                mapOf("siteId" to "about:blank"),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
    }

    @Test
    fun engine_adblock_blocks_navigate() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        eng.adBlock.setAdBlockEnabled(true)
        val r = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.NAVIGATE)),
                // need tab first
                mapOf("tabId" to "none", "url" to "https://pagead2.googlesyndication.com/x"),
            ),
        )
        // may not_found tab or adblock — open tab then navigate
        val open = eng.invoke(
            InvocationRequest("o", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), mapOf("url" to "about:blank")),
        )
        val tabId = (open as InvocationResult.Ok).output["tabId"]!!
        val nav = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.NAVIGATE)),
                mapOf("tabId" to tabId, "url" to "https://pagead2.googlesyndication.com/x"),
            ),
        )
        assertTrue(nav is InvocationResult.Err)
        assertEquals("adblock", (nav as InvocationResult.Err).code)
    }

    @Test
    fun extensions_list_and_disable_popup_guard() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val list = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.EXTENSION_LIST)), emptyMap()),
        )
        assertTrue(list is InvocationResult.Ok)
        assertTrue((list as InvocationResult.Ok).output["ids"]!!.contains("mimir-adblock"))
        eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.EXTENSION_ENABLE)),
                mapOf("extensionId" to "mimir-popup-guard", "enabled" to "false"),
            ),
        )
        val open = eng.invoke(
            InvocationRequest("o", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), mapOf("url" to "about:blank")),
        )
        val tabId = (open as InvocationResult.Ok).output["tabId"]!!
        val dismiss = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(BrowserCapabilities.POPUP_DISMISS)),
                mapOf("tabId" to tabId, "label" to "Accept"),
            ),
        )
        assertTrue(dismiss is InvocationResult.Err)
        assertEquals("extension_disabled", (dismiss as InvocationResult.Err).code)
    }

    @Test
    fun credential_fill_never_returns_secret_value() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        eng.secretResolver = SecretResolver { ref -> if (ref == "login.pass") "S3cret-Value!" else null }
        val open = eng.invoke(
            InvocationRequest("o", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), mapOf("url" to "about:blank")),
        )
        val tabId = (open as InvocationResult.Ok).output["tabId"]!!
        val fill = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.CREDENTIAL_FILL)),
                mapOf("tabId" to tabId, "secretRef" to "login.pass", "selector" to "input[type=password]", "fieldHint" to "password"),
            ),
        )
        assertTrue(fill is InvocationResult.Ok, fill.toString())
        val out = (fill as InvocationResult.Ok).output.toString()
        assertFalse(out.contains("S3cret-Value!"), "secret leaked: $out")
        assertTrue(out.contains("filled") || out.contains("type_failed") || out.contains("ok"))
    }

    @Test
    fun controller_lists_many_ai_sites() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val ai = BrowserAiController(eng)
        val sites = ai.listAiSites()
        assertTrue(sites.size >= 10)
        assertTrue(sites.any { it["id"] == "deepseek" })
        ai.registerAiSite("custom-llm", "Custom LLM", "https://example.com/llm", "chat,custom")
        assertTrue(ai.listAiSites().any { it["id"] == "custom-llm" })
    }
}

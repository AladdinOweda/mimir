package com.mimir.engine.browser

import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Browser AI Controller (ADR-025): multi-tab observe/act/orchestrate under user permissions.
 *
 * Capabilities for an AI agent (via KernelHost.invoke + policies):
 * - open/list/focus tabs (e.g. Claude / ChatGPT / Grok)
 * - observe: snapshot + a11y + console + chat extract
 * - act: click/type/press/scroll/eval (gated)
 * - transferContext: copy extracted text from tab A into tab B input
 * - killSwitch: freeze all acts
 *
 * Does not bypass Kernel approval; high-risk still needs tokens from host.
 */
class BrowserAiController(
    private val engine: BrowserEngine,
) {
    enum class ControlLevel { Observe, Assist, Full }

    private var level: ControlLevel = ControlLevel.Observe
    private var killSwitch: Boolean = false
    private val focused = ConcurrentHashMap<String, String>() // sessionId -> tabId
    private val contextBag = ConcurrentHashMap<String, String>() // key -> payload
    private val seq = AtomicLong(0)

    fun setLevel(l: ControlLevel) {
        level = l
    }

    fun armKillSwitch(on: Boolean) {
        killSwitch = on
    }

    fun controlState(): Map<String, String> =
        mapOf(
            "level" to level.name,
            "killSwitch" to killSwitch.toString(),
            "focusedTabs" to focused.size.toString(),
            "contextKeys" to contextBag.size.toString(),
            "driver" to engine.browserMetrics()["driverKind"].orEmpty(),
        )


    /** Open any catalog AI site (or arbitrary URL) in a labeled tab. */
    suspend fun openAiSite(siteIdOrUrl: String, label: String = siteIdOrUrl): InvocationResult {
        val url = AiSiteCatalog.resolveUrl(siteIdOrUrl)
        return openTab(url, label.ifBlank { siteIdOrUrl })
    }

    fun listAiSites(): List<Map<String, String>> =
        AiSiteCatalog.all().map {
            mapOf(
                "id" to it.id,
                "name" to it.displayName,
                "url" to it.homeUrl,
                "tags" to it.tags.joinToString(","),
            )
        }

    fun registerAiSite(id: String, name: String, url: String, tags: String = ""): AiSiteEntry =
        AiSiteCatalog.register(
            AiSiteEntry(
                id = id,
                displayName = name,
                homeUrl = url,
                tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet(),
            ),
        )

    suspend fun openTab(url: String, label: String = ""): InvocationResult {
        val r = engine.invoke(
            InvocationRequest(
                "ai-${seq.incrementAndGet()}", "browser-ai",
                CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)),
                mapOf("url" to url, "label" to label),
            ),
        )
        if (r is InvocationResult.Ok && label.isNotBlank()) {
            r.output["tabId"]?.let { focused[label] = it }
        }
        return r
    }

    suspend fun observe(tabId: String): Map<String, String> {
        val snap = engine.invoke(
            InvocationRequest(
                "ai-${seq.incrementAndGet()}", "browser-ai",
                CapabilityRef(CapabilityId(BrowserCapabilities.SNAPSHOT)),
                mapOf("tabId" to tabId),
            ),
        )
        val a11y = engine.invoke(
            InvocationRequest(
                "ai-${seq.incrementAndGet()}", "browser-ai",
                CapabilityRef(CapabilityId(BrowserCapabilities.SNAPSHOT_A11Y)),
                mapOf("tabId" to tabId),
            ),
        )
        val console = engine.invoke(
            InvocationRequest(
                "ai-${seq.incrementAndGet()}", "browser-ai",
                CapabilityRef(CapabilityId(BrowserCapabilities.CONSOLE_READ)),
                mapOf("tabId" to tabId, "limit" to "50"),
            ),
        )
        val chat = engine.invoke(
            InvocationRequest(
                "ai-${seq.incrementAndGet()}", "browser-ai",
                CapabilityRef(CapabilityId(BrowserCapabilities.EXTRACT_CHAT)),
                mapOf("tabId" to tabId),
            ),
        )
        fun out(r: InvocationResult): Map<String, String> =
            when (r) {
                is InvocationResult.Ok -> r.output
                is InvocationResult.Err -> mapOf("error" to r.code, "message" to r.message)
            }
        return mapOf(
            "tabId" to tabId,
            "level" to level.name,
            "snap" to out(snap).toString().take(2000),
            "a11y" to out(a11y).toString().take(2000),
            "console" to out(console).toString().take(1500),
            "chat" to out(chat).toString().take(3000),
        )
    }

    suspend fun act(
        tabId: String,
        kind: String,
        selector: String = "",
        text: String = "",
        approvalToken: String = "",
    ): InvocationResult {
        if (killSwitch) return InvocationResult.Err("ai", "kill_switch", "armed")
        if (level == ControlLevel.Observe) return InvocationResult.Err("ai", "level_denied", "Observe")
        val cap = when (kind.lowercase()) {
            "click" -> BrowserCapabilities.CLICK
            "type" -> BrowserCapabilities.TYPE
            "press" -> BrowserCapabilities.PRESS
            "scroll" -> BrowserCapabilities.SCROLL
            "eval" -> BrowserCapabilities.EVAL
            "navigate" -> BrowserCapabilities.NAVIGATE
            else -> return InvocationResult.Err("ai", "invalid", kind)
        }
        if (level == ControlLevel.Assist && kind.lowercase() in setOf("eval", "press")) {
            if (approvalToken.isBlank()) return InvocationResult.Err("ai", "approval_required", kind)
        }
        if (level != ControlLevel.Full && kind.lowercase() == "eval" && approvalToken.isBlank()) {
            return InvocationResult.Err("ai", "approval_required", "eval")
        }
        val input = mutableMapOf("tabId" to tabId)
        if (selector.isNotBlank()) input["selector"] = selector
        if (text.isNotBlank()) {
            input["text"] = text
            input["url"] = text // navigate
        }
        if (approvalToken.isNotBlank()) input["approvalToken"] = approvalToken
        return engine.invoke(
            InvocationRequest(
                "ai-${seq.incrementAndGet()}", "browser-ai",
                CapabilityRef(CapabilityId(cap)),
                input,
            ),
        )
    }

    /** Store observation from source tab for later paste into another tab. */
    suspend fun captureContext(key: String, tabId: String): String {
        val obs = observe(tabId)
        val payload = obs["chat"].orEmpty().ifBlank { obs["snap"].orEmpty() }
        contextBag[key] = payload.take(12_000)
        return contextBag[key]!!
    }

    suspend fun transferContext(
        key: String,
        targetTabId: String,
        selector: String,
        approvalToken: String = "",
    ): InvocationResult {
        if (killSwitch) return InvocationResult.Err("ai", "kill_switch", "armed")
        if (level == ControlLevel.Observe) return InvocationResult.Err("ai", "level_denied", "Observe")
        val payload = contextBag[key] ?: return InvocationResult.Err("ai", "not_found", key)
        return act(targetTabId, "type", selector = selector, text = payload, approvalToken = approvalToken)
    }

    fun listContextKeys(): List<String> = contextBag.keys().toList()
}

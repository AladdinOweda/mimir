package com.mimir.contracts.browser

/**
 * Browser engine **contract** (G-C3).
 * Driver (WebView / Gecko / CDP) is an adapter concern; this is the stable port.
 */
object BrowserCapabilityIds {
    const val CONSOLE_READ = "browser.console.read"
    const val CONSOLE_CLEAR = "browser.console.clear"
    const val AI_SITE_LIST = "browser.ai.site.list"
    const val AI_SITE_REGISTER = "browser.ai.site.register"
    const val AI_SITE_OPEN = "browser.ai.site.open"
    const val POPUP_DISMISS = "browser.popup.dismiss"
    const val AD_BLOCK_SET = "browser.adblock.set"
    const val AD_BLOCK_GET = "browser.adblock.get"
    const val EXTENSION_LIST = "browser.extension.list"
    const val EXTENSION_ENABLE = "browser.extension.enable"
    const val CREDENTIAL_FILL = "browser.credential.fill"
    const val TAB_OPEN = "browser.tab.open"
    const val TAB_CLOSE = "browser.tab.close"
    const val TAB_LIST = "browser.tab.list"
    const val NAVIGATE = "browser.navigate"
    const val SNAPSHOT = "browser.snapshot"
    const val SNAPSHOT_A11Y = "browser.snapshot.a11y"
    const val CLICK = "browser.click"
    const val TYPE = "browser.type"
    const val ACT_REF = "browser.act.ref"
    val ALL = listOf(
        TAB_OPEN, TAB_CLOSE, TAB_LIST, NAVIGATE, SNAPSHOT, SNAPSHOT_A11Y, CLICK, TYPE, ACT_REF, CONSOLE_READ, CONSOLE_CLEAR, AI_SITE_LIST, AI_SITE_REGISTER, AI_SITE_OPEN, POPUP_DISMISS, AD_BLOCK_SET, AD_BLOCK_GET, EXTENSION_LIST, EXTENSION_ENABLE, CREDENTIAL_FILL,
    )
}

data class BrowserTabId(val value: String)

data class BrowserTabInfo(
    val id: BrowserTabId,
    val url: String,
    val title: String = "",
)

data class BrowserNavigateRequest(
    val tabId: BrowserTabId,
    val url: String,
)

data class BrowserSnapshot(
    val tabId: BrowserTabId,
    val url: String,
    val title: String,
    val textExcerpt: String = "",
)

interface BrowserPort {
    fun openTab(url: String = "about:blank"): BrowserTabInfo
    fun closeTab(id: BrowserTabId): Boolean
    fun listTabs(): List<BrowserTabInfo>
    fun navigate(request: BrowserNavigateRequest): BrowserTabInfo
    fun snapshot(tabId: BrowserTabId): BrowserSnapshot
}

/** Optional observability — engines may expose richer maps. */
interface BrowserObservability {
    fun tabCount(): Int
    fun driverKind(): String
}

/** AI control levels for BrowserAiController (ADR-025). */
enum class BrowserAiControlLevel { Observe, Assist, Full }

interface BrowserAiControlPort {
    fun setLevel(level: BrowserAiControlLevel)
    fun armKillSwitch(on: Boolean)
    fun controlState(): Map<String, String>
    fun listAiSites(): List<Map<String, String>>
}

/** Popup/ad policy for AI-driven dismiss without leaking page secrets to models. */
interface BrowserProtectionPort {
    fun setAdBlockEnabled(enabled: Boolean)
    fun isAdBlockEnabled(): Boolean
}

/**
 * Credential fill: host resolves secrets from Vault by *reference id* only.
 * AI never receives passphrase/password values — only success/failure.
 */
interface BrowserCredentialFillPort {
    fun fillBySecretRef(tabId: String, secretRef: String, fieldHint: String): Boolean
}

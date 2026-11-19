package com.mimir.adapters.browser

import com.mimir.adapters.browser.a11y.A11ySnapshot
import com.mimir.adapters.browser.a11y.HtmlA11yParser

data class WebViewSnapshot(
    val url: String,
    val title: String = "",
    val htmlPrefix: String = "",
    val statusCode: Int = 0,
    val canGoBack: Boolean = false,
    val realDriver: Boolean = false,
    val driverName: String = "noop",
)

interface WebViewPort {
    val realDriver: Boolean
    val driverName: String
    fun navigate(url: String): WebViewSnapshot
    fun snapshot(): WebViewSnapshot
    fun evaluateJs(script: String): String
    fun clickSelector(selector: String): Boolean
    fun typeSelector(selector: String, text: String): Boolean
    fun extractVisibleText(maxChars: Int = 8_000): String

    fun a11ySnapshot(interactiveOnly: Boolean = true): A11ySnapshot {
        val s = snapshot()
        return HtmlA11yParser.parse(s.htmlPrefix, s.url, s.title, interactiveOnly)
            .copy(realDriver = realDriver, driverName = driverName)
    }

    fun clickRef(ref: String): Boolean {
        val node = a11ySnapshot(false).byRef(ref) ?: return false
        return clickSelector(node.selector)
    }

    fun typeRef(ref: String, text: String): Boolean {
        val node = a11ySnapshot(false).byRef(ref) ?: return false
        return typeSelector(node.selector, text)
    }

    fun hoverSelector(selector: String): Boolean = false
    fun scrollBy(dx: Int, dy: Int): Boolean = false
    fun pressKey(key: String): Boolean = false
    fun waitMs(ms: Long): Boolean {
        try {
            Thread.sleep(ms.coerceIn(0, 30_000))
            return true
        } catch (_: InterruptedException) {
            return false
        }
    }

    fun screenshotMeta(): Map<String, String> =
        mapOf("available" to "false", "reason" to "driver_no_screenshot", "driver" to driverName)

    /**
     * Page console buffer (log/warn/error). Empty unless driver captures Console/CDP events.
     * HttpFetch cannot capture JS console; Chromium/WebView can when wired.
     */
    fun consoleLogs(limit: Int = 100): List<Map<String, String>> = emptyList()
    fun clearConsoleLogs() {}
}


class NoopWebViewPort : WebViewPort {
    private var url: String = "about:blank"
    override val realDriver: Boolean = false
    override val driverName: String = "noop"
    override fun navigate(url: String): WebViewSnapshot {
        this.url = url
        return WebViewSnapshot(url, title = "noop", realDriver = false, driverName = driverName)
    }
    override fun snapshot() =
        WebViewSnapshot(url, title = "noop", realDriver = false, driverName = driverName)
    override fun evaluateJs(script: String) = ""
    override fun clickSelector(selector: String) = false
    override fun typeSelector(selector: String, text: String) = false
    override fun extractVisibleText(maxChars: Int) = ""
}

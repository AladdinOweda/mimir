package com.mimir.adapters.browser

/**
 * Platform WebView contracts for Android (android.webkit.WebView) and iOS (WKWebView).
 *
 * On pure JVM desktop these adapters report realDriver=false and explain missing runtime.
 * Product apps must inject a platform instance via [WebViewDrivers.register] or constructor.
 */

/** Hooks a host app implements with a live WebView / WKWebView. */
interface NativeWebViewHost {
    fun loadUrl(url: String)
    fun currentUrl(): String
    fun title(): String
    fun htmlSnapshot(): String
    fun evaluateJavascript(script: String): String
    fun clickCss(selector: String): Boolean
    fun typeCss(selector: String, text: String): Boolean
}

/**
 * Generic bridge: any platform host (Android/iOS) becomes a [WebViewPort].
 */
class NativeWebViewPort(
    private val host: NativeWebViewHost,
    override val driverName: String,
) : WebViewPort {
    private var lastUrl: String = "about:blank"
    override val realDriver: Boolean = true

    override fun navigate(url: String): WebViewSnapshot {
        host.loadUrl(url)
        lastUrl = host.currentUrl().ifBlank { url }
        val html = host.htmlSnapshot()
        return WebViewSnapshot(
            url = lastUrl,
            title = host.title(),
            htmlPrefix = html.take(32_000),
            statusCode = 200,
            realDriver = true,
            driverName = driverName,
        )
    }

    override fun snapshot(): WebViewSnapshot {
        val html = host.htmlSnapshot()
        lastUrl = host.currentUrl().ifBlank { lastUrl }
        return WebViewSnapshot(
            url = lastUrl,
            title = host.title(),
            htmlPrefix = html.take(32_000),
            statusCode = 200,
            realDriver = true,
            driverName = driverName,
        )
    }

    override fun evaluateJs(script: String) = host.evaluateJavascript(script)
    override fun clickSelector(selector: String) = host.clickCss(selector)
    override fun typeSelector(selector: String, text: String) = host.typeCss(selector, text)
    override fun extractVisibleText(maxChars: Int): String {
        val html = host.htmlSnapshot()
        return html
            .replace(Regex("(?is)<script[^>]*>.*?</script>"), " ")
            .replace(Regex("(?is)<style[^>]*>.*?</style>"), " ")
            .replace(Regex("(?is)<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(maxChars)
    }
}

/**
 * JVM placeholder when Android runtime is absent.
 * Real wiring: apps/android injects NativeWebViewPort(AndroidWebViewHost(webView)).
 */
class AndroidWebViewPortPlaceholder : WebViewPort {
    override val realDriver = false
    override val driverName = "android-webview-missing"
    override fun navigate(url: String) =
        WebViewSnapshot(url, title = "android_runtime_required", realDriver = false, driverName = driverName)
    override fun snapshot() =
        WebViewSnapshot("about:blank", title = "android_runtime_required", realDriver = false, driverName = driverName)
    override fun evaluateJs(script: String) = "/* android_webview_required */"
    override fun clickSelector(selector: String) = false
    override fun typeSelector(selector: String, text: String) = false
    override fun extractVisibleText(maxChars: Int) = ""
}

/** JVM placeholder when iOS / WKWebView runtime is absent. */
class IosWkWebViewPortPlaceholder : WebViewPort {
    override val realDriver = false
    override val driverName = "ios-wkwebview-missing"
    override fun navigate(url: String) =
        WebViewSnapshot(url, title = "ios_runtime_required", realDriver = false, driverName = driverName)
    override fun snapshot() =
        WebViewSnapshot("about:blank", title = "ios_runtime_required", realDriver = false, driverName = driverName)
    override fun evaluateJs(script: String) = "/* ios_wkwebview_required */"
    override fun clickSelector(selector: String) = false
    override fun typeSelector(selector: String, text: String) = false
    override fun extractVisibleText(maxChars: Int) = ""
}

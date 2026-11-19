package com.mimir.android.browser

/**
 * REFERENCE implementation for product Android module (requires android.webkit.WebView).
 *
 * Copy into a real Android target (AGP) and uncomment. Pure-JVM `:apps:android` cannot
 * resolve android.* — this file stays as documentation-compile-safe stubs.
 *
 * ```
 * class AndroidWebViewHost(private val webView: android.webkit.WebView) : NativeWebViewHost {
 *   override fun loadUrl(url: String) { webView.post { webView.loadUrl(url) } }
 *   override fun currentUrl(): String = webView.url ?: "about:blank"
 *   override fun title(): String = webView.title ?: ""
 *   override fun htmlSnapshot(): String {
 *     // evaluate JS document.documentElement.outerHTML via ValueCallback latch
 *     return ""
 *   }
 *   override fun evaluateJavascript(script: String): String { ... }
 *   override fun clickCss(selector: String): Boolean =
 *     evaluateJavascript("""document.querySelector("$selector")?.click()""") == ...
 * }
 * ```
 *
 * Wire: BrowserEngine(driverFactory = { NativeWebViewPort(AndroidWebViewHost(wv), "android-webview") })
 */
object AndroidWebViewIntegrationNotes {
    const val DRIVER = "android-webview"
    const val PERMISSION = "android.permission.INTERNET"
    val steps = listOf(
        "Use AGP android application module (not pure JVM)",
        "Add WebView in Compose/AndroidView",
        "Implement NativeWebViewHost against android.webkit.WebView",
        "Inject NativeWebViewPort into BrowserEngine / ComputerUseEngine",
        "Gate high-risk actions with ApprovalToken via KernelHost",
    )
}

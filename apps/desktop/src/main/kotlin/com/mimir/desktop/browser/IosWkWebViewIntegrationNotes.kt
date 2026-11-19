package com.mimir.desktop.browser

/**
 * iOS WKWebView integration notes (Kotlin/Native or Swift bridge).
 * JVM desktop cannot host WKWebView; ship via KMP iosMain or native shell.
 *
 * Wire: NativeWebViewPort(host = WkWebViewHost(wkWebView), driverName = "ios-wkwebview")
 */
object IosWkWebViewIntegrationNotes {
    const val DRIVER = "ios-wkwebview"
    val steps = listOf(
        "Create iosApp target with WKWebView",
        "Implement NativeWebViewHost using evaluateJavaScript completion handler",
        "clickCss/typeCss via injected JS querySelector",
        "Pass port into shared BrowserEngine factory from iOS entry",
        "Respect ApprovalToken for computer.act / browser.click high risk",
    )
}

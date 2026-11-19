package com.mimir.androidapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mimir.engine.browser.BrowserEngine
import com.mimir.engine.browser.webview.NativeWebViewPort
import com.mimir.engine.browser.webview.NoopWebViewPort
import com.mimir.engine.browser.webview.WebViewDrivers
import com.mimir.engine.computer.BrowserBackedUiPort
import com.mimir.engine.computer.ComputerUseEngine
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Real Android entry: system WebView bound to BrowserEngine + ComputerUseEngine.
 */
class MainActivity : Activity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.mimirWebView)
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        val host = AndroidWebViewHost(webView)
        val port = NativeWebViewPort(host, "android-webview")
        WebViewDrivers.register { port }

        scope.launch {
            val browser = BrowserEngine(driverKind = "android-webview", driverFactory = { port })
            browser.start()
            browser.invoke(
                InvocationRequest(
                    "boot",
                    "android",
                    CapabilityRef(CapabilityId("browser.tab.open")),
                    mapOf("url" to "https://example.com"),
                ),
            )
            val computer = ComputerUseEngine(
                uiKind = "android-webview",
                ui = BrowserBackedUiPort(port),
            )
            computer.start()
        }
    }

    override fun onDestroy() {
        WebViewDrivers.register { NoopWebViewPort() }
        super.onDestroy()
    }
}

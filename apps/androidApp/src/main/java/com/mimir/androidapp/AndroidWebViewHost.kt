package com.mimir.androidapp

import android.os.Handler
import android.os.Looper
import android.webkit.ValueCallback
import android.webkit.WebView
import com.mimir.engine.browser.webview.NativeWebViewHost
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Live Android WebView host for [NativeWebViewPort] / BrowserEngine.
 * All WebView calls hop to the main looper.
 */
class AndroidWebViewHost(
    private val webView: WebView,
    private val timeoutMs: Long = 8_000,
) : NativeWebViewHost {
    private val main = Handler(Looper.getMainLooper())

    override fun loadUrl(url: String) {
        runOnMainBlocking { webView.loadUrl(url) }
        // Give navigation a brief moment; product can also wait on WebViewClient
        try { Thread.sleep(250) } catch (_: InterruptedException) {}
    }

    override fun currentUrl(): String =
        runOnMainBlocking { webView.url ?: "about:blank" }

    override fun title(): String =
        runOnMainBlocking { webView.title ?: "" }

    override fun htmlSnapshot(): String {
        val js = "(function(){try{return document.documentElement.outerHTML;}catch(e){return '';}})();"
        return evaluateJavascript(js)
    }

    override fun evaluateJavascript(script: String): String {
        val result = AtomicReference("")
        val latch = CountDownLatch(1)
        main.post {
            webView.evaluateJavascript(script) { value ->
                result.set(unwrapJsString(value))
                latch.countDown()
            }
        }
        latch.await(timeoutMs, TimeUnit.MILLISECONDS)
        return result.get()
    }

    override fun clickCss(selector: String): Boolean {
        val escaped = selector.replace("\\", "\\\\").replace("\"", "\\\"")
        val js = """
            (function(){
              var el = document.querySelector("$escaped");
              if(!el) return 'missing';
              el.click();
              return 'ok';
            })();
        """.trimIndent()
        return evaluateJavascript(js) == "ok"
    }

    override fun typeCss(selector: String, text: String): Boolean {
        val escSel = selector.replace("\\", "\\\\").replace("\"", "\\\"")
        val escText = text.replace("\\", "\\\\").replace("\"", "\\\"")
            .replace("\n", "\\n")
        val js = """
            (function(){
              var el = document.querySelector("$escSel");
              if(!el) return 'missing';
              el.focus();
              el.value = "$escText";
              el.dispatchEvent(new Event('input', {bubbles:true}));
              el.dispatchEvent(new Event('change', {bubbles:true}));
              return 'ok';
            })();
        """.trimIndent()
        return evaluateJavascript(js) == "ok"
    }

    private fun <T> runOnMainBlocking(block: () -> T): T {
        if (Looper.myLooper() == Looper.getMainLooper()) return block()
        val ref = AtomicReference<T>()
        val latch = CountDownLatch(1)
        main.post {
            ref.set(block())
            latch.countDown()
        }
        latch.await(timeoutMs, TimeUnit.MILLISECONDS)
        @Suppress("UNCHECKED_CAST")
        return ref.get() as T
    }

    private fun unwrapJsString(raw: String?): String {
        if (raw == null || raw == "null") return ""
        var s = raw.trim()
        if (s.startsWith("\"") && s.endsWith("\"") && s.length >= 2) {
            s = s.substring(1, s.length - 1)
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\\\", "\\")
        }
        return s
    }
}

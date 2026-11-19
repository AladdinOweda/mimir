package com.mimir.adapters.browser

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.WebSocket
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Chromium DOM driver:
 * - snapshot/navigate: `--dump-dom` (stable rendered HTML)
 * - click/type/evaluate: CDP attached to a **page** target (not browser/extension)
 */
class ChromiumDomWebViewPort(
    private val chromePath: String = detectChrome(),
    private val timeoutSec: Long = 30,
) : WebViewPort {
    private var url: String = "about:blank"
    private var title: String = ""
    private var dom: String = ""
    private var statusCode: Int = 0
    private var cdp: CdpPageSession? = null
    private val consoleBuf = java.util.concurrent.CopyOnWriteArrayList<Map<String, String>>()

    override val realDriver: Boolean = true
    override val driverName: String = "chromium-dom"

    override fun navigate(url: String): WebViewSnapshot {
        val normalized = normalizeUrl(url)
        this.url = normalized
        if (normalized.startsWith("about:")) {
            dom = ""; title = "about"; statusCode = 0
            try {
                ensureCdp()
                cdp?.navigate(normalized)
            } catch (_: Throwable) {
            }
            return snapshot()
        }
        return try {
            dumpDom(normalized)
            try {
                ensureCdp()
                cdp?.navigate(normalized)
                cdp?.waitDocumentComplete(timeoutSec)
            } catch (_: Throwable) {
            }
            snapshot()
        } catch (t: Throwable) {
            title = t.message ?: "navigate_failed"
            statusCode = -1
            dom = ""
            WebViewSnapshot(this.url, title, "", -1, realDriver = true, driverName = driverName)
        }
    }

    override fun snapshot() =
        WebViewSnapshot(
            url = url,
            title = title,
            htmlPrefix = dom.take(48_000),
            statusCode = statusCode,
            realDriver = true,
            driverName = driverName,
        )

    override fun evaluateJs(script: String): String {
        ensureCdp()
        return try {
            cdp?.evaluate(script)?.take(8_000) ?: "/* cdp_unavailable */"
        } catch (_: Throwable) {
            "/* cdp_error */"
        }
    }

    override fun consoleLogs(limit: Int): List<Map<String, String>> {
        cdp?.drainConsole()?.forEach { consoleBuf.add(it) }
        return consoleBuf.takeLast(limit.coerceAtLeast(0))
    }

    override fun clearConsoleLogs() {
        consoleBuf.clear()
        cdp?.clearConsole()
    }

    override fun clickSelector(selector: String): Boolean {
        ensureCdp()
        val s = cdp ?: return false
        return try {
            if (!url.startsWith("about:")) {
                s.navigate(url)
                s.waitDocumentComplete(timeoutSec)
            }
            val result = s.evaluate(
                """
                (() => {
                  const el = document.querySelector(${jsonStr(selector)});
                  if (!el) return 'missing';
                  el.scrollIntoView({block:'center'});
                  el.dispatchEvent(new MouseEvent('click', {bubbles:true,cancelable:true,view:window}));
                  if (typeof el.click === 'function') el.click();
                  return 'ok';
                })()
                """.trimIndent(),
            )
            result == "ok"
        } catch (_: Throwable) {
            false
        }
    }

    override fun typeSelector(selector: String, text: String): Boolean {
        ensureCdp()
        val s = cdp ?: return false
        return try {
            if (!url.startsWith("about:")) {
                s.navigate(url)
                s.waitDocumentComplete(timeoutSec)
            }
            s.evaluate(
                """
                (() => {
                  const el = document.querySelector(${jsonStr(selector)});
                  if (!el) return 'missing';
                  el.focus();
                  el.value = ${jsonStr(text)};
                  el.dispatchEvent(new Event('input', {bubbles:true}));
                  el.dispatchEvent(new Event('change', {bubbles:true}));
                  return 'ok';
                })()
                """.trimIndent(),
            ) == "ok"
        } catch (_: Throwable) {
            false
        }
    }

    override fun extractVisibleText(maxChars: Int): String {
        if (dom.isBlank()) return ""
        return dom
            .replace(Regex("(?is)<script[^>]*>.*?</script>"), " ")
            .replace(Regex("(?is)<style[^>]*>.*?</style>"), " ")
            .replace(Regex("(?is)<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(maxChars)
    }

    fun close() {
        try { cdp?.close() } catch (_: Throwable) {}
        cdp = null
    }

    private fun dumpDom(target: String) {
        val pb = ProcessBuilder(
            chromePath, "--headless=new", "--disable-gpu", "--no-sandbox",
            "--disable-dev-shm-usage", "--dump-dom", target,
        )
        pb.redirectError(ProcessBuilder.Redirect.DISCARD)
        val proc = pb.start()
        val out = proc.inputStream.bufferedReader().readText()
        if (!proc.waitFor(timeoutSec, TimeUnit.SECONDS)) {
            proc.destroyForcibly()
            throw RuntimeException("chrome_dump_timeout")
        }
        dom = out
        title = extractTitle(out).ifBlank { target }
        statusCode = if (out.isNotBlank()) 200 else -1
        url = target
    }

    private fun ensureCdp() {
        if (cdp != null) return
        cdp = CdpPageSession.start(chromePath, timeoutSec)
    }

    private fun normalizeUrl(raw: String): String {
        val t = raw.trim()
        if (t.startsWith("http://") || t.startsWith("https://") ||
            t.startsWith("about:") || t.startsWith("file:")
        ) return t
        return "https://$t"
    }

    private fun extractTitle(html: String): String {
        val m = Regex("(?is)<title[^>]*>(.*?)</title>").find(html) ?: return ""
        return m.groupValues[1].replace(Regex("\\s+"), " ").trim()
    }

    private fun jsonStr(s: String): String =
        "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""

    companion object {
        fun detectChrome(): String {
            val candidates = listOf(
                System.getenv("MIMIR_CHROME"),
                "/usr/bin/google-chrome-stable",
                "/usr/bin/google-chrome",
                "/usr/bin/chromium-browser",
                "/usr/bin/chromium",
                "google-chrome",
            ).filterNotNull()
            return candidates.firstOrNull { p ->
                val f = File(p)
                (f.exists() && f.canExecute()) || p == "google-chrome"
            } ?: "google-chrome"
        }
    }
}

/**
 * CDP session bound to a **page** target WebSocket (`/devtools/page/...`).
 */
class CdpPageSession private constructor(
    private val process: Process,
    private val ws: WebSocket,
    private val port: Int,
) {
    private val nextId = AtomicInteger(1)
    private val pending = java.util.concurrent.ConcurrentHashMap<Int, CompletableFuture<String>>()
    private val consoleEvents = java.util.concurrent.CopyOnWriteArrayList<Map<String, String>>()

    fun navigate(url: String) {
        call("Page.navigate", """{"url":${json(url)}}""")
    }

    fun waitDocumentComplete(timeoutSec: Long) {
        val deadline = System.currentTimeMillis() + timeoutSec * 1000
        while (System.currentTimeMillis() < deadline) {
            val state = evaluate("document.readyState || ''")
            if (state == "complete" || state == "interactive") return
            Thread.sleep(100)
        }
    }

    fun evaluate(expression: String): String {
        val raw = call(
            "Runtime.evaluate",
            """{"expression":${json(expression)},"returnByValue":true,"awaitPromise":true}""",
        )
        if (raw.contains("wasn't found") || raw.contains("exceptionDetails")) return "error"
        val m = Regex("\"value\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").find(raw)
        if (m != null) return unescape(m.groupValues[1])
        val m2 = Regex("\"value\"\\s*:\\s*([0-9.]+|true|false|null)").find(raw)
        if (m2 != null) return m2.groupValues[1]
        return raw.take(300)
    }

    fun close() {
        try { ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye") } catch (_: Throwable) {}
        process.destroyForcibly()
    }

    fun onMessage(text: String) {
        if ("Runtime.consoleAPICalled" in text || "Log.entryAdded" in text) {
            val level = Regex("\"type\"\\s*:\\s*\"(\\w+)\"").find(text)?.groupValues?.get(1)
                ?: Regex("\"level\"\\s*:\\s*\"(\\w+)\"").find(text)?.groupValues?.get(1)
                ?: "log"
            val msg = Regex("\"value\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").find(text)?.groupValues?.get(1)
                ?: text.take(200)
            consoleEvents.add(mapOf("level" to level, "text" to msg, "source" to "cdp"))
            while (consoleEvents.size > 500) consoleEvents.removeAt(0)
        }
        val idM = Regex("\"id\"\\s*:\\s*(\\d+)").find(text) ?: return
        pending.remove(idM.groupValues[1].toInt())?.complete(text)
    }

    fun drainConsole(): List<Map<String, String>> {
        val out = consoleEvents.toList()
        consoleEvents.clear()
        return out
    }

    fun clearConsole() { consoleEvents.clear() }

    private fun call(method: String, paramsJson: String): String {
        val id = nextId.getAndIncrement()
        val fut = CompletableFuture<String>()
        pending[id] = fut
        ws.sendText("""{"id":$id,"method":"$method","params":$paramsJson}""", true)
        return fut.get(25, TimeUnit.SECONDS)
    }

    companion object {
        fun start(chromePath: String, timeoutSec: Long): CdpPageSession {
            val port = java.net.ServerSocket(0).use { it.localPort }
            val userData = File(System.getProperty("java.io.tmpdir"), "mimir-cdp-$port").apply {
                deleteRecursively()
                mkdirs()
            }
            val pb = ProcessBuilder(
                chromePath,
                "--headless=new",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--remote-debugging-port=$port",
                "--user-data-dir=${userData.absolutePath}",
                "about:blank",
            )
            pb.redirectError(ProcessBuilder.Redirect.DISCARD)
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD)
            val proc = pb.start()
            val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()
            val pageWs = waitForPageWebSocket(client, port, timeoutSec)
                ?: run {
                    proc.destroyForcibly()
                    throw RuntimeException("cdp_page_target_missing")
                }
            val sessionRef = AtomicReference<CdpPageSession>()
            val listener = object : WebSocket.Listener {
                private val buf = StringBuilder()
                override fun onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletionStage<*>? {
                    buf.append(data)
                    if (last) {
                        val msg = buf.toString()
                        buf.setLength(0)
                        sessionRef.get()?.onMessage(msg)
                    }
                    webSocket.request(1)
                    return null
                }
            }
            val ws = client.newWebSocketBuilder().buildAsync(URI(pageWs), listener).get(20, TimeUnit.SECONDS)
            ws.request(1)
            val session = CdpPageSession(proc, ws, port)
            sessionRef.set(session)
            session.call("Page.enable", "{}")
            session.call("Runtime.enable", "{}")
            session.call("DOM.enable", "{}")
            try { session.call("Console.enable", "{}") } catch (_: Throwable) {}
            return session
        }

        /**
         * Prefer targets with type=page (about:blank or http/file), never extension/background.
         */
        private fun waitForPageWebSocket(client: HttpClient, port: Int, timeoutSec: Long): String? {
            val deadline = System.currentTimeMillis() + timeoutSec * 1000
            while (System.currentTimeMillis() < deadline) {
                try {
                    val req = HttpRequest.newBuilder(URI("http://127.0.0.1:$port/json/list")).GET().build()
                    val resp = client.send(req, HttpResponse.BodyHandlers.ofString())
                    if (resp.statusCode() == 200) {
                        val targets = parseTargets(resp.body())
                        val page = targets.firstOrNull {
                            it.type == "page" &&
                                (it.url.startsWith("about:") || it.url.startsWith("http") ||
                                    it.url.startsWith("file:") || it.url.isBlank())
                        } ?: targets.firstOrNull { it.type == "page" }
                        if (page != null) return page.wsUrl
                    }
                } catch (_: Throwable) {
                }
                Thread.sleep(150)
            }
            return null
        }

        private data class Target(val type: String, val url: String, val wsUrl: String)

        private fun parseTargets(body: String): List<Target> {
            val out = mutableListOf<Target>()
            // Split rough JSON objects
            val objs = Regex("\\{[^{}]+\\}").findAll(body)
            for (o in objs) {
                val block = o.value
                val type = Regex("\"type\"\\s*:\\s*\"([^\"]+)\"").find(block)?.groupValues?.get(1) ?: continue
                val url = Regex("\"url\"\\s*:\\s*\"([^\"]*)\"").find(block)?.groupValues?.get(1) ?: ""
                val ws = Regex("\"webSocketDebuggerUrl\"\\s*:\\s*\"([^\"]+)\"").find(block)?.groupValues?.get(1)
                    ?: continue
                out += Target(type, url, ws)
            }
            return out
        }

        private fun json(s: String) =
            "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "") + "\""

        private fun unescape(s: String) =
            s.replace("\\n", "\n").replace("\\t", "\t").replace("\\\"", "\"").replace("\\\\", "\\")
    }
}

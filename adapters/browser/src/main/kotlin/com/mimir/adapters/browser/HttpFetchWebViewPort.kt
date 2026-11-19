package com.mimir.adapters.browser

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Real network browser-lite driver: HTTP(S) navigation + HTML snapshot.
 * Does NOT execute JS or true DOM click/type — those stay false until CEF/WebView.
 */
class HttpFetchWebViewPort(
    private val connectTimeoutMs: Int = 5_000,
    private val readTimeoutMs: Int = 8_000,
    private val maxBodyBytes: Int = 512_000,
    private val userAgent: String = "MimirBrowser/0.5 (+http-fetch)",
) : WebViewPort {
    private var url: String = "about:blank"
    private var title: String = ""
    private var body: String = ""
    private var statusCode: Int = 0
    private val consoleBuf = java.util.concurrent.CopyOnWriteArrayList<Map<String, String>>()

    override val realDriver: Boolean = true
    override val driverName: String = "http-fetch"

    override fun navigate(url: String): WebViewSnapshot {
        val normalized = normalizeUrl(url)
        this.url = normalized
        if (normalized.startsWith("about:")) {
            body = ""
            title = "about"
            statusCode = 0
            return snapshot()
        }
        return try {
            val conn = (URI(normalized).toURL().openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                requestMethod = "GET"
                setRequestProperty("User-Agent", userAgent)
                setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            }
            statusCode = conn.responseCode
            val stream = if (statusCode in 200..399) conn.inputStream else conn.errorStream
            body = stream?.use { readLimited(it, maxBodyBytes, charsetFrom(conn)) } ?: ""
            title = extractTitle(body).ifBlank { normalized }
            conn.disconnect()
            snapshot()
        } catch (t: Throwable) {
            body = ""
            title = "error"
            statusCode = -1
            WebViewSnapshot(
                url = this.url,
                title = t.message ?: "navigate_failed",
                htmlPrefix = "",
                statusCode = -1,
                realDriver = true,
                driverName = driverName,
            )
        }
    }

    override fun snapshot() =
        WebViewSnapshot(
            url = url,
            title = title,
            htmlPrefix = body.take(32_000),
            statusCode = statusCode,
            realDriver = true,
            driverName = driverName,
        )

    override fun evaluateJs(script: String) =
        "/* http-fetch does not execute JS */"

    override fun clickSelector(selector: String) = false

    override fun typeSelector(selector: String, text: String) = false

    override fun extractVisibleText(maxChars: Int): String {
        if (body.isBlank()) return ""
        val noScript = body
            .replace(Regex("(?is)<script[^>]*>.*?</script>"), " ")
            .replace(Regex("(?is)<style[^>]*>.*?</style>"), " ")
            .replace(Regex("(?is)<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        return noScript.take(maxChars)
    }

    private fun normalizeUrl(raw: String): String {
        val t = raw.trim()
        if (t.startsWith("http://") || t.startsWith("https://") || t.startsWith("about:")) return t
        return "https://$t"
    }

    private fun extractTitle(html: String): String {
        val m = Regex("(?is)<title[^>]*>(.*?)</title>").find(html) ?: return ""
        return m.groupValues[1].replace(Regex("\\s+"), " ").trim()
    }

    private fun charsetFrom(conn: HttpURLConnection): Charset {
        val ct = conn.contentType ?: return Charsets.UTF_8
        val m = Regex("charset=([\\w-]+)", RegexOption.IGNORE_CASE).find(ct)
        return try {
            Charset.forName(m?.groupValues?.get(1) ?: "UTF-8")
        } catch (_: Exception) {
            Charsets.UTF_8
        }
    }

    private fun readLimited(stream: java.io.InputStream, max: Int, cs: Charset): String {
        val reader = BufferedReader(InputStreamReader(stream, cs))
        val sb = StringBuilder()
        val buf = CharArray(4096)
        var total = 0
        while (total < max) {
            val n = reader.read(buf)
            if (n < 0) break
            val take = minOf(n, max - total)
            sb.append(buf, 0, take)
            total += take
        }
        return sb.toString()
    }

    override fun consoleLogs(limit: Int): List<Map<String, String>> =
        consoleBuf.takeLast(limit.coerceAtLeast(0))

    override fun clearConsoleLogs() { consoleBuf.clear() }

    /** HttpFetch cannot hear page console; host may inject synthetic entries. */
    fun recordConsole(level: String, text: String) {
        consoleBuf.add(mapOf("level" to level, "text" to text, "source" to "host"))
        while (consoleBuf.size > 500) consoleBuf.removeAt(0)
    }
}

/**
 * Factory: prefer http-fetch when [preferReal] true; else noop.
 * Future: "chromium" process driver / Android WebView.
 */
object WebViewDrivers {
    @Volatile private var overrideFactory: (() -> WebViewPort)? = null

    fun register(factory: () -> WebViewPort) {
        overrideFactory = factory
    }

    fun create(kind: String = "noop"): WebViewPort {
        overrideFactory?.let { return it.invoke() }
        return when (kind.lowercase()) {
            "http", "http-fetch", "network" -> HttpFetchWebViewPort()
            "chromium", "chromium-dom", "chrome", "cef", "dom", "real" -> ChromiumDomWebViewPort()
            "android", "android-webview" -> AndroidWebViewPortPlaceholder()
            "ios", "ios-wkwebview", "wkwebview" -> IosWkWebViewPortPlaceholder()
            else -> NoopWebViewPort()
        }
    }

}

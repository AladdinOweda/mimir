package com.mimir.adapters.browser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HttpFetchWebViewPortTest {
    @Test
    fun navigate_about_blank() {
        val p = HttpFetchWebViewPort()
        val s = p.navigate("about:blank")
        assertTrue(p.realDriver)
        assertEquals("http-fetch", p.driverName)
        assertTrue(s.url.contains("about") || s.url == "about:blank")
    }

    @Test
    fun navigate_example_http() {
        val p = HttpFetchWebViewPort(connectTimeoutMs = 8_000, readTimeoutMs = 10_000)
        val s = p.navigate("https://example.com")
        // Network may fail in locked-down environments; accept success or structured error
        if (s.statusCode in 200..399) {
            assertTrue(s.htmlPrefix.isNotEmpty() || s.title.isNotEmpty())
            assertTrue(p.extractVisibleText(500).isNotEmpty() || s.title.isNotEmpty())
        } else {
            assertTrue(s.statusCode == -1 || s.statusCode >= 400, "status=${s.statusCode}")
        }
    }
}

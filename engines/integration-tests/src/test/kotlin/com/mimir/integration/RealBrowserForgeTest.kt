package com.mimir.integration

import com.mimir.adapters.browser.HttpFetchWebViewPort
import com.mimir.engine.browser.BrowserEngine
import com.mimir.engine.forge.ForgeCapabilities
import com.mimir.engine.forge.ForgeEngine
import com.mimir.kernel.contract.*
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealBrowserForgeTest {

    @Test
    fun http_fetch_example_com() {
        val p = HttpFetchWebViewPort(connectTimeoutMs = 10_000, readTimeoutMs = 12_000)
        val s = p.navigate("https://example.com")
        assertTrue(p.realDriver)
        assertEquals("http-fetch", p.driverName)
        // Require success in this environment (internet enabled)
        assertTrue(s.statusCode in 200..399, "status=${s.statusCode} title=${s.title}")
        assertTrue(s.title.isNotBlank() || s.htmlPrefix.isNotEmpty())
        val text = p.extractVisibleText(800)
        assertTrue(text.contains("Example", ignoreCase = true) || text.length > 10, text.take(100))
    }

    @Test
    fun browser_engine_tab_navigate_http_fetch() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val open = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId("browser.tab.open")), mapOf("url" to "about:blank")),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val tabId = (open as InvocationResult.Ok).output["tabId"]!!
        val nav = eng.invoke(
            InvocationRequest(
                "2", "j",
                CapabilityRef(CapabilityId("browser.navigate")),
                mapOf("tabId" to tabId, "url" to "https://example.com"),
            ),
        )
        assertTrue(nav is InvocationResult.Ok, nav.toString())
        val out = (nav as InvocationResult.Ok).output
        assertTrue(out["realDriver"] == "true" || out["driver"]?.contains("http") == true, out.toString())
        assertTrue(eng.browserMetrics()["tabs"]!!.toInt() >= 1)
    }

    @Test
    fun forge_scaffold_writes_real_files() = runTest {
        val root = File(System.getProperty("java.io.tmpdir"), "mimir-forge-real-${System.currentTimeMillis()}")
        root.mkdirs()
        val eng = ForgeEngine(allowRealFs = true)
        eng.start()
        val list = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ForgeCapabilities.TEMPLATE_LIST)), emptyMap()),
        )
        assertTrue(list is InvocationResult.Ok, list.toString())
        val templates = (list as InvocationResult.Ok).output["templates"] ?: ""
        assertTrue(templates.isNotBlank(), list.output.toString())
        val tid = templates.split(",").first().trim()
        val sc = eng.invoke(
            InvocationRequest(
                "2", "j",
                CapabilityRef(CapabilityId(ForgeCapabilities.SCAFFOLD_RUN)),
                mapOf(
                    "templateId" to tid,
                    "targetPath" to root.absolutePath,
                    "workspaceRoot" to root.absolutePath,
                ),
            ),
        )
        assertTrue(sc is InvocationResult.Ok, sc.toString())
        val written = (sc as InvocationResult.Ok).output["written"] ?: ""
        assertTrue(written.isNotBlank(), sc.output.toString())
        val files = root.walkTopDown().filter { it.isFile }.toList()
        assertTrue(files.isNotEmpty(), "no files under $root")
        assertTrue(eng.boardSnapshot()["templates"]!!.toInt() >= 1)
        root.deleteRecursively()
    }

    @Test
    fun forge_scaffold_path_jail() = runTest {
        val ws = File(System.getProperty("java.io.tmpdir"), "mimir-ws-${System.currentTimeMillis()}").also { it.mkdirs() }
        val eng = ForgeEngine(allowRealFs = true)
        eng.start()
        val list = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ForgeCapabilities.TEMPLATE_LIST)), emptyMap()),
        )
        val tid = ((list as InvocationResult.Ok).output["templates"] ?: "kotlin-lib").split(",").first().trim()
        val sc = eng.invoke(
            InvocationRequest(
                "2", "j",
                CapabilityRef(CapabilityId(ForgeCapabilities.SCAFFOLD_RUN)),
                mapOf(
                    "templateId" to tid,
                    "targetPath" to "/tmp/mimir-outside-jail",
                    "workspaceRoot" to ws.absolutePath,
                ),
            ),
        )
        assertTrue(sc is InvocationResult.Err)
        assertEquals("path_jail", (sc as InvocationResult.Err).code)
        ws.deleteRecursively()
    }

    @Test
    fun kernel_browser_open_policy() = runTest {
        val host = KernelHost()
        val eng = BrowserEngine(driverKind = "http-fetch")
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("browser.navigate", "*"))
        host.capabilitySecurity.register(
            CapabilityPolicy("browser.tab.open", "browser.navigate", RiskLevel.Low),
        )
        val r = host.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId("browser.tab.open")), mapOf("url" to "about:blank")),
            PermissionContext("u"),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
    }
}

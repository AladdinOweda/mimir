package com.mimir.engine.browser

import com.mimir.adapters.browser.WebViewDrivers
import com.mimir.adapters.browser.WebViewPort
import com.mimir.contracts.browser.BrowserPort
import com.mimir.contracts.browser.BrowserTabId
import com.mimir.contracts.browser.BrowserTabInfo
import com.mimir.contracts.browser.BrowserNavigateRequest
import com.mimir.contracts.browser.BrowserSnapshot
import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Capabilities aligned with agent-browser command surface (Kotlin / Kernel style).
 * External CLI is NOT embedded — patterns only.
 */
object BrowserCapabilities {
    const val CONSOLE_READ = "browser.console.read"
    const val CONSOLE_CLEAR = "browser.console.clear"
    const val CREDENTIAL_FILL = "browser.credential.fill"
    const val EXTENSION_ENABLE = "browser.extension.enable"
    const val EXTENSION_LIST = "browser.extension.list"
    const val AD_BLOCK_GET = "browser.adblock.get"
    const val AD_BLOCK_SET = "browser.adblock.set"
    const val POPUP_DISMISS = "browser.popup.dismiss"
    const val AI_SITE_OPEN = "browser.ai.site.open"
    const val AI_SITE_REGISTER = "browser.ai.site.register"
    const val AI_SITE_LIST = "browser.ai.site.list"
    const val TAB_OPEN = "browser.tab.open"
    const val TAB_CLOSE = "browser.tab.close"
    const val TAB_LIST = "browser.tab.list"
    const val NAVIGATE = "browser.navigate"
    const val SNAPSHOT = "browser.snapshot"
    const val SNAPSHOT_A11Y = "browser.snapshot.a11y"
    const val CLICK = "browser.click"
    const val TYPE = "browser.type"
    const val ACT_REF = "browser.act.ref"
    const val HOVER = "browser.hover"
    const val SCROLL = "browser.scroll"
    const val PRESS = "browser.press"
    const val WAIT = "browser.wait"
    const val EVAL = "browser.eval"
    const val EXTRACT_CHAT = "browser.chat.extract"
    const val DRIVER_INFO = "browser.driver.info"
    const val POLICY_SET = "browser.policy.set"
    const val POLICY_GET = "browser.policy.get"
    const val SCREENSHOT_META = "browser.screenshot.meta"
    val ALL = listOf(
        TAB_OPEN, TAB_CLOSE, TAB_LIST, NAVIGATE, SNAPSHOT, SNAPSHOT_A11Y,
        CLICK, TYPE, ACT_REF, HOVER, SCROLL, PRESS, WAIT, EVAL,
        EXTRACT_CHAT, DRIVER_INFO, POLICY_SET, POLICY_GET, SCREENSHOT_META,
        CONSOLE_READ, CONSOLE_CLEAR,
        AI_SITE_LIST, AI_SITE_REGISTER, AI_SITE_OPEN,
        POPUP_DISMISS, AD_BLOCK_SET, AD_BLOCK_GET,
        EXTENSION_LIST, EXTENSION_ENABLE, CREDENTIAL_FILL,
    )
}

enum class BrowserActionRisk { Low, Medium, High, Critical }

/**
 * Browser engine with pluggable [WebViewPort] per tab.
 * agent-browser ideas: a11y refs, allowlist policy, richer acts — honest driver reporting.
 */
class BrowserEngine(
    private val driverKind: String = "noop",
    private val driverFactory: () -> WebViewPort = { WebViewDrivers.create(driverKind) },
    val adBlock: BrowserAdBlockPolicy = BrowserAdBlockPolicy(),
    val extensions: BrowserExtensionRegistry = BrowserExtensionRegistry(),
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine, BrowserPort {
    @Volatile var secretResolver: SecretResolver = SecretResolver { null }
    private val policy: BrowserPolicy = BrowserPolicy()

    private data class Tab(
        val id: String,
        var url: String,
        var title: String = "",
        val driver: WebViewPort,
    )

    private val tabs = ConcurrentHashMap<String, Tab>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("browser-core"),
        name = "Mimir Browser",
        version = "0.7.0",
        provides = BrowserCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("browser.navigate", "browser.act"),
        dataNamespace = "engine.browser",
        priority = 80,
        platforms = listOf("android", "desktop"),
        runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x",
        publisher = "mimir",
    )

    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState {
        config["allowHosts"]?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?.let { policy.setAllow(it) }
        config["denyHosts"]?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?.let { policy.setDeny(it) }
        state = EngineState.Configured
        return state
    }
    override suspend fun start(): EngineState {
        state = EngineState.Running
        return state
    }
    override suspend fun stop(reason: String): EngineState {
        state = EngineState.Stopped
        return state
    }
    override suspend fun health() =
        HealthReport(
            manifest.id,
            state == EngineState.Running,
            state,
            "tabs=${tabs.size} driver=$driverKind policy=${policy.snapshot()["mode"]}",
            clockMs(),
        )

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        val input = request.input - "approvalGranted"
        return when (request.capability.id.value) {
            BrowserCapabilities.DRIVER_INFO ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "defaultDriverKind" to driverKind,
                        "tabs" to tabs.size.toString(),
                        "supportsA11yRefs" to "true",
                        "supportsHttpNavigate" to (driverKind != "noop").toString(),
                        "agentBrowserParity" to "snapshot-refs,policy,act-ref,eval,wait,scroll,hover",
                    ),
                )
            BrowserCapabilities.POLICY_GET ->
                InvocationResult.Ok(request.requestId, policy.snapshot())
            BrowserCapabilities.POLICY_SET -> {
                input["allowHosts"]?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                    ?.let { policy.setAllow(it) }
                input["denyHosts"]?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                    ?.let { policy.setDeny(it) }
                InvocationResult.Ok(request.requestId, policy.snapshot())
            }
            BrowserCapabilities.TAB_OPEN -> {
                val url = input["url"] ?: "about:blank"
                if (!policy.allowsUrl(url)) {
                    return InvocationResult.Err(request.requestId, "policy_denied", url)
                }
                val id = "tab-${seq.incrementAndGet()}"
                val driver = driverFactory()
                val snap = if (url != "about:blank") driver.navigate(url) else driver.snapshot()
                tabs[id] = Tab(id, snap.url.ifBlank { url }, snap.title, driver)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tabId" to id,
                        "url" to tabs[id]!!.url,
                        "title" to tabs[id]!!.title,
                        "realDriver" to driver.realDriver.toString(),
                        "driver" to driver.driverName,
                        "statusCode" to snap.statusCode.toString(),
                    ),
                )
            }
            BrowserCapabilities.TAB_CLOSE -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                tabs.remove(id)
                InvocationResult.Ok(request.requestId, mapOf("closed" to id))
            }
            BrowserCapabilities.TAB_LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tabs" to tabs.keys.joinToString(","),
                        "count" to tabs.size.toString(),
                        "detail" to tabs.values.joinToString(";") { "${it.id}|${it.url}|${it.title}" },
                    ),
                )
            BrowserCapabilities.NAVIGATE -> {
                val blockUrl = input["url"] ?: ""
                if (blockUrl.isNotBlank() && adBlock.shouldBlockUrl(blockUrl)) {
                    return InvocationResult.Err(request.requestId, "adblock", blockUrl.take(120))
                }

                val id = input["tabId"] ?: return inv(request, "tabId")
                val url = input["url"] ?: return inv(request, "url")
                if (!policy.allowsUrl(url)) {
                    return InvocationResult.Err(request.requestId, "policy_denied", url)
                }
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val snap = tab.driver.navigate(url)
                tab.url = snap.url.ifBlank { url }
                tab.title = snap.title
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tabId" to id,
                        "url" to tab.url,
                        "title" to tab.title,
                        "statusCode" to snap.statusCode.toString(),
                        "realDriver" to tab.driver.realDriver.toString(),
                        "driver" to tab.driver.driverName,
                    ),
                )
            }
            BrowserCapabilities.SNAPSHOT -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val snap = tab.driver.snapshot()
                tab.url = snap.url.ifBlank { tab.url }
                tab.title = snap.title.ifBlank { tab.title }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tabId" to id,
                        "url" to tab.url,
                        "title" to tab.title,
                        "htmlLen" to snap.htmlPrefix.length.toString(),
                        "htmlPrefix" to snap.htmlPrefix.take(4_000),
                        "text" to tab.driver.extractVisibleText(2_000),
                        "realDriver" to tab.driver.realDriver.toString(),
                        "driver" to tab.driver.driverName,
                    ),
                )
            }
            BrowserCapabilities.SNAPSHOT_A11Y -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val interactive = input["interactive"]?.lowercase() != "false"
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val a11y = tab.driver.a11ySnapshot(interactive)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tabId" to id,
                        "url" to a11y.url.ifBlank { tab.url },
                        "title" to a11y.title.ifBlank { tab.title },
                        "tree" to a11y.textTree.take(8_000),
                        "nodeCount" to a11y.nodes.size.toString(),
                        "refs" to a11y.nodes.joinToString(",") { it.ref },
                        "realDriver" to a11y.realDriver.toString(),
                        "driver" to a11y.driverName,
                    ),
                )
            }
            BrowserCapabilities.CLICK -> act(request, input, "click")
            BrowserCapabilities.TYPE -> act(request, input, "type")
            BrowserCapabilities.ACT_REF -> actRef(request, input)
            BrowserCapabilities.HOVER -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val sel = input["selector"] ?: return inv(request, "selector")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val ok = tab.driver.hoverSelector(sel)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("executed" to ok.toString(), "status" to if (ok) "ok" else "unsupported_or_failed"),
                )
            }
            BrowserCapabilities.SCROLL -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val dx = input["dx"]?.toIntOrNull() ?: 0
                val dy = input["dy"]?.toIntOrNull() ?: 400
                val ok = tab.driver.scrollBy(dx, dy)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("executed" to ok.toString(), "dx" to dx.toString(), "dy" to dy.toString()),
                )
            }
            BrowserCapabilities.PRESS -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val key = input["key"] ?: return inv(request, "key")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val ok = tab.driver.pressKey(key)
                InvocationResult.Ok(request.requestId, mapOf("executed" to ok.toString(), "key" to key))
            }
            BrowserCapabilities.WAIT -> {
                val ms = input["ms"]?.toLongOrNull() ?: 500L
                val ok = driverFactory().waitMs(ms) // pure sleep; no tab required
                // Prefer tab driver if present
                val tabId = input["tabId"]
                val waited = if (tabId != null) tabs[tabId]?.driver?.waitMs(ms) ?: ok else ok
                InvocationResult.Ok(request.requestId, mapOf("waitedMs" to ms.toString(), "ok" to waited.toString()))
            }

            BrowserCapabilities.AI_SITE_LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "count" to AiSiteCatalog.all().size.toString(),
                        "ids" to AiSiteCatalog.all().joinToString(",") { it.id },
                    ),
                )
            BrowserCapabilities.AI_SITE_REGISTER -> {
                val id = input["id"] ?: return inv(request, "id")
                val name = input["name"] ?: id
                val url = input["url"] ?: return inv(request, "url")
                val e = AiSiteCatalog.register(AiSiteEntry(id, name, url, tags = (input["tags"] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()))
                InvocationResult.Ok(request.requestId, mapOf("id" to e.id, "url" to e.homeUrl))
            }
            BrowserCapabilities.AI_SITE_OPEN -> {
                val site = input["siteId"] ?: input["url"] ?: return inv(request, "siteId")
                val url = AiSiteCatalog.resolveUrl(site)
                if (adBlock.shouldBlockUrl(url)) return InvocationResult.Err(request.requestId, "adblock", url.take(120))
                val openInput = input.toMutableMap()
                openInput["url"] = url
                // reuse tab open path by recursive-style: inline open
                val id = "tab-${seq.incrementAndGet()}"
                val driver = driverFactory()
                val snap = driver.navigate(url)
                tabs[id] = Tab(id, snap.url.ifBlank { url }, snap.title, driver)
                InvocationResult.Ok(request.requestId, mapOf("tabId" to id, "url" to tabs[id]!!.url, "site" to site))
            }
            BrowserCapabilities.AD_BLOCK_GET ->
                InvocationResult.Ok(request.requestId, adBlock.snapshot())
            BrowserCapabilities.AD_BLOCK_SET -> {
                input["enabled"]?.let { adBlock.setAdBlockEnabled(it.equals("true", true)) }
                input["blockRule"]?.let { adBlock.addBlockRule(it) }
                input["allowHost"]?.let { adBlock.addAllowHost(it) }
                InvocationResult.Ok(request.requestId, adBlock.snapshot())
            }
            BrowserCapabilities.POPUP_DISMISS -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                if (!extensions.isEnabled("mimir-popup-guard")) {
                    return InvocationResult.Err(request.requestId, "extension_disabled", "mimir-popup-guard")
                }
                val label = input["label"] ?: ""
                val role = input["role"] ?: "button"
                val ref = input["ref"] ?: ""
                if (!PopupHeuristics.isDismissable(label, role) && ref.isBlank()) {
                    return InvocationResult.Err(request.requestId, "not_dismissable", label.take(40))
                }
                val kind = PopupHeuristics.classify(label, role)
                val ok = when {
                    ref.isNotBlank() -> tab.driver.clickRef(ref)
                    input["selector"] != null -> tab.driver.clickSelector(input["selector"]!!)
                    else -> false
                }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tabId" to id,
                        "dismissed" to ok.toString(),
                        "kind" to kind,
                        "label" to label.take(80),
                    ),
                )
            }
            BrowserCapabilities.EXTENSION_LIST -> {
                val list = extensions.list()
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "count" to list.size.toString(),
                        "ids" to list.joinToString(",") { it.id },
                        "enabled" to list.filter { it.enabled }.joinToString(",") { it.id },
                    ),
                )
            }
            BrowserCapabilities.EXTENSION_ENABLE -> {
                val id = input["extensionId"] ?: return inv(request, "extensionId")
                val en = input["enabled"]?.equals("true", true) != false
                val ok = extensions.setEnabled(id, en)
                InvocationResult.Ok(request.requestId, mapOf("extensionId" to id, "enabled" to en.toString(), "ok" to ok.toString()))
            }
            BrowserCapabilities.CREDENTIAL_FILL -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                if (!extensions.isEnabled("mimir-secrets-fill")) {
                    return InvocationResult.Err(request.requestId, "extension_disabled", "mimir-secrets-fill")
                }
                val secretRef = input["secretRef"] ?: return inv(request, "secretRef")
                val fieldHint = input["fieldHint"] ?: input["selector"] ?: return inv(request, "fieldHint")
                // AI must not receive secret: result codes only
                val svc = BrowserCredentialService(secretResolver)
                val result = svc.fill(secretRef, fieldHint) { value ->
                    val sel = input["selector"] ?: fieldHint
                    tab.driver.typeSelector(sel, value)
                }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tabId" to id,
                        "ok" to result.ok.toString(),
                        "code" to result.code,
                        "fieldHint" to result.fieldHint,
                        // NEVER include secret value
                    ),
                )
            }

            BrowserCapabilities.CONSOLE_READ -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val limit = input["limit"]?.toIntOrNull() ?: 100
                val logs = tab.driver.consoleLogs(limit)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tabId" to id,
                        "count" to logs.size.toString(),
                        "logs" to logs.joinToString("\n") { "${it["level"]}|${it["text"]}" }.take(8000),
                        "driver" to tab.driver.driverName,
                    ),
                )
            }
            BrowserCapabilities.CONSOLE_CLEAR -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                tab.driver.clearConsoleLogs()
                InvocationResult.Ok(request.requestId, mapOf("tabId" to id, "cleared" to "true"))
            }
            BrowserCapabilities.EVAL -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val script = input["script"] ?: return inv(request, "script")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                if (input["approvalToken"].isNullOrBlank()) {
                    // eval is powerful — require token always
                    return InvocationResult.Err(request.requestId, "approval_required", "eval")
                }
                val out = tab.driver.evaluateJs(script)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("result" to out.take(4_000), "realDriver" to tab.driver.realDriver.toString()),
                )
            }
            BrowserCapabilities.EXTRACT_CHAT -> {
                val id = input["tabId"] ?: return inv(request, "tabId")
                val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val text = tab.driver.extractVisibleText(8_000)
                val messages = ChatExtractParser.parse(text)
                val sum = ChatExtractParser.summary(messages)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "tabId" to id,
                        "text" to text.take(4_000),
                        "messageCount" to sum.getValue("messageCount"),
                        "roles" to sum.getValue("roles"),
                        "preview" to sum.getValue("preview"),
                        "realDriver" to tab.driver.realDriver.toString(),
                        "status" to if (tab.driver.realDriver) "ChatStructured" else "NoopExtract",
                    ),
                )
            }
            BrowserCapabilities.SCREENSHOT_META -> {
                val id = input["tabId"]
                val meta = if (id != null) tabs[id]?.driver?.screenshotMeta()
                    ?: mapOf("available" to "false", "reason" to "tab_not_found")
                else mapOf("available" to "false", "reason" to "no_tab")
                InvocationResult.Ok(request.requestId, meta)
            }
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun actRef(request: InvocationRequest, input: Map<String, String>): InvocationResult {
        val id = input["tabId"] ?: return inv(request, "tabId")
        val ref = input["ref"] ?: return inv(request, "ref")
        val kind = (input["action"] ?: "click").lowercase()
        val text = input["text"] ?: ""
        val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        val risk = classify(kind, ref, text)
        if (risk == BrowserActionRisk.High || risk == BrowserActionRisk.Critical) {
            if (input["approvalToken"].isNullOrBlank()) {
                return InvocationResult.Err(request.requestId, "approval_required", risk.name)
            }
        }
        val executed = when (kind) {
            "click" -> tab.driver.clickRef(ref)
            "type", "fill" -> tab.driver.typeRef(ref, text)
            else -> false
        }
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "tabId" to id,
                "ref" to ref,
                "action" to kind,
                "executed" to executed.toString(),
                "risk" to risk.name,
                "realDriver" to tab.driver.realDriver.toString(),
                "driver" to tab.driver.driverName,
                "status" to when {
                    executed -> "Executed"
                    tab.driver.realDriver -> "DriverActFailedOrMissingRef"
                    else -> "BlockedNoopDriver"
                },
            ),
        )
    }

    private fun act(
        request: InvocationRequest,
        input: Map<String, String>,
        kind: String,
    ): InvocationResult {
        val id = input["tabId"] ?: return inv(request, "tabId")
        val tab = tabs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
        val selector = input["selector"] ?: return inv(request, "selector")
        val text = input["text"] ?: input["label"] ?: ""
        val risk = classify(kind, selector, text)
        if (risk == BrowserActionRisk.High || risk == BrowserActionRisk.Critical) {
            if (input["approvalToken"].isNullOrBlank()) {
                return InvocationResult.Err(request.requestId, "approval_required", risk.name)
            }
        }
        val executed = when (kind) {
            "click" -> tab.driver.clickSelector(selector)
            "type" -> tab.driver.typeSelector(selector, text)
            else -> false
        }
        val status = when {
            executed -> "Executed"
            tab.driver.realDriver -> "DriverLacksDomAct"
            else -> "BlockedNoopDriver"
        }
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "tabId" to id,
                "kind" to kind,
                "selector" to selector,
                "risk" to risk.name,
                "executed" to executed.toString(),
                "realDriver" to tab.driver.realDriver.toString(),
                "driver" to tab.driver.driverName,
                "status" to status,
            ),
        )
    }

    private fun classify(kind: String, selector: String, text: String): BrowserActionRisk {
        val blob = "$kind $selector $text".lowercase()
        return when {
            listOf("pay", "checkout", "delete account", "password", "transfer").any { blob.contains(it) } ->
                BrowserActionRisk.Critical
            listOf("submit", "send", "upload", "purchase", "buy").any { blob.contains(it) } ->
                BrowserActionRisk.High
            kind == "type" || kind == "fill" -> BrowserActionRisk.Medium
            else -> BrowserActionRisk.Low
        }
    }

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)


    // --- BrowserPort (deepened: policy + real tab map) ---
    override fun openTab(url: String): BrowserTabInfo {
        val u = url.ifBlank { "about:blank" }
        if (!policy.allowsUrl(u)) {
            return BrowserTabInfo(BrowserTabId("denied"), u, "policy_denied")
        }
        val id = "tab-${seq.incrementAndGet()}"
        val driver = driverFactory()
        val snap = try {
            driver.navigate(u)
        } catch (_: Exception) {
            null
        }
        val finalUrl = snap?.url?.ifBlank { u } ?: u
        val title = snap?.title ?: ""
        tabs[id] = Tab(id, finalUrl, title, driver)
        return BrowserTabInfo(BrowserTabId(id), finalUrl, title)
    }

    override fun closeTab(id: BrowserTabId): Boolean = tabs.remove(id.value) != null

    override fun listTabs(): List<BrowserTabInfo> =
        tabs.values.map { BrowserTabInfo(BrowserTabId(it.id), it.url, it.title) }

    override fun navigate(request: BrowserNavigateRequest): BrowserTabInfo {
        if (!policy.allowsUrl(request.url)) {
            return BrowserTabInfo(request.tabId, request.url, "policy_denied")
        }
        val tab = tabs[request.tabId.value]
            ?: return BrowserTabInfo(request.tabId, request.url, "not_found")
        val snap = tab.driver.navigate(request.url)
        tab.url = snap.url.ifBlank { request.url }
        tab.title = snap.title
        return BrowserTabInfo(request.tabId, tab.url, tab.title)
    }

    override fun snapshot(tabId: BrowserTabId): BrowserSnapshot {
        val tab = tabs[tabId.value] ?: return BrowserSnapshot(tabId, "", "", "not_found")
        val snap = tab.driver.snapshot()
        tab.url = snap.url.ifBlank { tab.url }
        tab.title = snap.title.ifBlank { tab.title }
        val excerpt = try {
            tab.driver.extractVisibleText(500)
        } catch (_: Exception) {
            ""
        }
        return BrowserSnapshot(tabId, tab.url, tab.title, excerpt)
    }

    /** Expose policy snapshot for tests/composition. */
    fun policySnapshot(): Map<String, String> = policy.snapshot()

    fun browserMetrics(): Map<String, String> = mapOf(
        "tabs" to tabs.size.toString(),
        "driverKind" to driverKind,
        "state" to state.name,
        "tabIds" to tabs.keys.joinToString(",").take(200),
    )

    fun listTabSummaries(): List<String> =
        tabs.values.map { "${it.id}|${it.url}|${it.title}" }
}

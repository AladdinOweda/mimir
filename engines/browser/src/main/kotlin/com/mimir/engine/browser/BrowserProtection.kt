package com.mimir.engine.browser

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Lightweight ad/popup protection policy (engine-level).
 * Full network-level blocking needs extension/WebView filter lists on device.
 */
class BrowserAdBlockPolicy {
    @Volatile var enabled: Boolean = true
    /** Host suffix / substring rules (user-extensible). */
    private val blockHostRules = CopyOnWriteArrayList(
        listOf(
            "doubleclick.net", "googlesyndication.com", "adservice.", "adnxs.com",
            "adsystem.", "pagead2.", "sponsor", "/ads/", "popup=1",
        ),
    )
    private val allowHosts = CopyOnWriteArrayList<String>()

    fun setAdBlockEnabled(v: Boolean) { enabled = v }
    fun addBlockRule(rule: String) { if (rule.isNotBlank()) blockHostRules.add(rule.lowercase()) }
    fun addAllowHost(host: String) { if (host.isNotBlank()) allowHosts.add(host.lowercase()) }

    fun shouldBlockUrl(url: String): Boolean {
        if (!enabled) return false
        val u = url.lowercase()
        if (allowHosts.any { u.contains(it) }) return false
        return blockHostRules.any { u.contains(it) }
    }

    fun snapshot(): Map<String, String> = mapOf(
        "enabled" to enabled.toString(),
        "rules" to blockHostRules.size.toString(),
        "allow" to allowHosts.size.toString(),
    )
}

/**
 * Heuristic popup candidates for AI dismiss flow.
 * AI sees *roles/labels only*, not raw PII.
 */
data class PopupCandidate(
    val ref: String,
    val role: String,
    val label: String,
    val kind: String, // cookie | ad | newsletter | auth | unknown
)

object PopupHeuristics {
    private val dismissLabels = listOf(
        "accept", "agree", "got it", "ok", "close", "dismiss", "no thanks",
        "not now", "رفض", "موافق", "إغلاق", "حسنا", "قبول",
    )
    private val adHints = listOf("ad", "ads", "sponsor", "promo", "إعلان")
    private val cookieHints = listOf("cookie", "consent", "privacy", "كوكيز")

    fun classify(label: String, role: String = ""): String {
        val t = (label + " " + role).lowercase()
        return when {
            cookieHints.any { it in t } -> "cookie"
            adHints.any { it in t } -> "ad"
            dismissLabels.any { it in t } -> "dismissable"
            else -> "unknown"
        }
    }

    fun isDismissable(label: String, role: String = ""): Boolean {
        val t = (label + " " + role).lowercase()
        return dismissLabels.any { it in t } || classify(label, role) in setOf("cookie", "ad", "dismissable")
    }
}

data class ExtensionInfo(
    val id: String,
    val name: String,
    val version: String,
    val enabled: Boolean,
    val permissions: List<String> = emptyList(),
)

/**
 * Extension registry surface (Chrome-like). Desktop WebExtension host is adapter work;
 * here we track enablement + declared permissions for product/AI policy.
 */
class BrowserExtensionRegistry {
    private val items = ConcurrentHashMap<String, ExtensionInfo>()

    init {
        // Built-in virtual extensions (engine-backed, not Chrome CRX files)
        register(ExtensionInfo("mimir-adblock", "Mimir Ad Block", "0.1.0", true, listOf("webRequest")))
        register(ExtensionInfo("mimir-popup-guard", "Mimir Popup Guard", "0.1.0", true, listOf("scripting")))
        register(ExtensionInfo("mimir-secrets-fill", "Mimir Secrets Autofill", "0.1.0", true, listOf("vault.read")))
    }

    fun register(info: ExtensionInfo) { items[info.id] = info }
    fun list(): List<ExtensionInfo> = items.values.sortedBy { it.id }
    fun setEnabled(id: String, enabled: Boolean): Boolean {
        val cur = items[id] ?: return false
        items[id] = cur.copy(enabled = enabled)
        return true
    }
    fun isEnabled(id: String): Boolean = items[id]?.enabled == true
}

/**
 * Credential fill: AI passes only [secretRef] (Vault key id). Host loads secret out-of-band.
 * The model never sees the secret value in tool results.
 */
fun interface SecretResolver {
    /** Returns secret value or null; must not be logged to AI-visible channels. */
    fun resolve(secretRef: String): String?
}

class BrowserCredentialService(
    private val resolver: SecretResolver = SecretResolver { null },
) {
    data class FillResult(val ok: Boolean, val code: String, val fieldHint: String)

    fun fill(secretRef: String, fieldHint: String, typeInto: (String) -> Boolean): FillResult {
        if (secretRef.isBlank()) return FillResult(false, "invalid_ref", fieldHint)
        val value = resolver.resolve(secretRef)
            ?: return FillResult(false, "secret_not_found", fieldHint)
        return try {
            val ok = typeInto(value)
            // Do not include value in result
            FillResult(ok, if (ok) "filled" else "type_failed", fieldHint)
        } catch (_: Throwable) {
            FillResult(false, "error", fieldHint)
        }
    }
}

package com.mimir.engine.browser

import java.net.URI

/** Domain allowlist / deny inspired by agent-browser security. */
class BrowserPolicy(
    private var allowHosts: Set<String> = emptySet(),
    private var denyHosts: Set<String> = emptySet(),
) {
    fun setAllow(hosts: Collection<String>) {
        allowHosts = hosts.map { normalizeHost(it) }.filter { it.isNotBlank() }.toSet()
    }

    fun setDeny(hosts: Collection<String>) {
        denyHosts = hosts.map { normalizeHost(it) }.filter { it.isNotBlank() }.toSet()
    }

    fun allowsUrl(url: String): Boolean {
        if (url.startsWith("about:") || url.startsWith("file:")) return true
        val host = hostOf(url) ?: return false
        if (denyHosts.any { host == it || host.endsWith(".$it") }) return false
        if (allowHosts.isEmpty()) return true
        return allowHosts.any { host == it || host.endsWith(".$it") }
    }

    fun snapshot(): Map<String, String> = mapOf(
        "allowHosts" to allowHosts.joinToString(","),
        "denyHosts" to denyHosts.joinToString(","),
        "mode" to if (allowHosts.isEmpty()) "permissive" else "allowlist",
    )

    private fun hostOf(url: String): String? = try {
        normalizeHost(URI(url).host ?: "")
    } catch (_: Exception) {
        null
    }

    private fun normalizeHost(h: String) = h.trim().lowercase().removePrefix("www.")
}

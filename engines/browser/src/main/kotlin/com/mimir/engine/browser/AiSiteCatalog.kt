package com.mimir.engine.browser

/**
 * Catalog of AI web UIs the user may open in tabs.
 * Not limited to Claude/ChatGPT/Grok — any tool can be registered.
 */
data class AiSiteEntry(
    val id: String,
    val displayName: String,
    val homeUrl: String,
    /** Optional CSS/role hints for chat extract / compose (best-effort). */
    val chatRootHint: String = "",
    val inputHint: String = "",
    val sendHint: String = "",
    val tags: Set<String> = emptySet(),
)

object AiSiteCatalog {
    private val custom = java.util.concurrent.ConcurrentHashMap<String, AiSiteEntry>()

    /** Built-in seeds — user/product can register more at runtime. */
    private val seeds = listOf(
        AiSiteEntry("chatgpt", "ChatGPT", "https://chatgpt.com", tags = setOf("openai", "chat")),
        AiSiteEntry("claude", "Claude", "https://claude.ai", tags = setOf("anthropic", "chat")),
        AiSiteEntry("grok", "Grok", "https://x.com/i/grok", tags = setOf("xai", "chat")),
        AiSiteEntry("deepseek", "DeepSeek", "https://chat.deepseek.com", tags = setOf("deepseek", "chat")),
        AiSiteEntry("perplexity", "Perplexity", "https://www.perplexity.ai", tags = setOf("search", "chat")),
        AiSiteEntry("qwen", "Qwen", "https://chat.qwen.ai", tags = setOf("alibaba", "chat")),
        AiSiteEntry("kimi", "Kimi", "https://kimi.moonshot.cn", tags = setOf("moonshot", "chat")),
        AiSiteEntry("gemini", "Gemini", "https://gemini.google.com", tags = setOf("google", "chat")),
        AiSiteEntry("copilot", "Copilot", "https://copilot.microsoft.com", tags = setOf("microsoft", "chat")),
        AiSiteEntry("you", "You.com", "https://you.com", tags = setOf("search", "chat")),
        AiSiteEntry("poe", "Poe", "https://poe.com", tags = setOf("multi", "chat")),
        AiSiteEntry("huggingface", "HuggingChat", "https://huggingface.co/chat", tags = setOf("hf", "chat")),
    )

    fun all(): List<AiSiteEntry> {
        val map = linkedMapOf<String, AiSiteEntry>()
        seeds.forEach { map[it.id] = it }
        custom.values.forEach { map[it.id] = it }
        return map.values.toList()
    }

    fun get(id: String): AiSiteEntry? =
        custom[id] ?: seeds.find { it.id.equals(id, true) }

    fun register(entry: AiSiteEntry): AiSiteEntry {
        require(entry.id.isNotBlank() && entry.homeUrl.isNotBlank())
        custom[entry.id.lowercase()] = entry.copy(id = entry.id.lowercase())
        return custom[entry.id.lowercase()]!!
    }

    fun resolveUrl(idOrUrl: String): String {
        if (idOrUrl.startsWith("http://") || idOrUrl.startsWith("https://") || idOrUrl.startsWith("about:")) {
            return idOrUrl
        }
        return get(idOrUrl)?.homeUrl ?: idOrUrl
    }
}

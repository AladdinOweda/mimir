package com.mimir.engine.browser

/**
 * Best-effort chat message segmentation from visible page text.
 * Works across ChatGPT/Claude/Grok/DeepSeek/etc. without hard-coding one vendor DOM.
 */
data class ChatMessage(
    val role: String, // user | assistant | system | unknown
    val text: String,
)

object ChatExtractParser {
    private val rolePrefixes = listOf(
        Regex("""^(?i)(user|you|human)\s*[:：]\s*(.*)$"""),
        Regex("""^(?i)(assistant|ai|bot|claude|chatgpt|gpt|grok|deepseek|kimi|qwen)\s*[:：]\s*(.*)$"""),
        Regex("""^(?i)(system)\s*[:：]\s*(.*)$"""),
    )

    fun parse(visibleText: String, maxMessages: Int = 40): List<ChatMessage> {
        if (visibleText.isBlank()) return emptyList()
        val lines = visibleText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val out = mutableListOf<ChatMessage>()
        var currentRole = "unknown"
        val buf = StringBuilder()
        fun flush() {
            val t = buf.toString().trim()
            if (t.isNotEmpty()) out += ChatMessage(currentRole, t.take(2_000))
            buf.clear()
        }
        for (line in lines) {
            var matched = false
            for ((idx, re) in rolePrefixes.withIndex()) {
                val m = re.matchEntire(line)
                if (m != null) {
                    flush()
                    currentRole = when (idx) {
                        0 -> "user"
                        1 -> "assistant"
                        2 -> "system"
                        else -> "unknown"
                    }
                    buf.append(m.groupValues.getOrNull(2) ?: "")
                    matched = true
                    break
                }
            }
            if (!matched) {
                if (buf.isNotEmpty()) buf.append('\n')
                buf.append(line)
            }
            if (out.size >= maxMessages) break
        }
        flush()
        return out.take(maxMessages)
    }

    fun summary(messages: List<ChatMessage>): Map<String, String> = mapOf(
        "messageCount" to messages.size.toString(),
        "roles" to messages.map { it.role }.distinct().joinToString(","),
        "preview" to messages.takeLast(3).joinToString(" || ") { "${it.role}:${it.text.take(80)}" }.take(500),
    )
}

package com.mimir.adapters.browser.a11y

/**
 * Lightweight HTML → a11y-style ref tree (Kotlin adaptation of agent-browser snapshot idea).
 */
object HtmlA11yParser {
    private val simpleOpenClose = Regex(
        """(?is)<(a|button|textarea|select|label|summary|h[1-6])(\s[^>]*)?>(.*?)</\1>""",
    )
    private val inputTag = Regex("""(?is)<input(\s[^>]*)?/?>""")
    private val imgTag = Regex("""(?is)<img(\s[^>]*)?/?>""")

    fun parse(
        html: String,
        url: String,
        title: String = "",
        interactiveOnly: Boolean = true,
    ): A11ySnapshot {
        val nodes = mutableListOf<A11yNode>()
        var n = 0
        fun nextRef(): String {
            n += 1
            return "e$n"
        }

        simpleOpenClose.findAll(html).forEach { m ->
            val tag = m.groupValues[1].lowercase()
            val attrs = m.groupValues[2]
            val body = stripTags(m.groupValues[3]).trim().take(120)
            val role = when (tag) {
                "a" -> "link"
                "button" -> "button"
                "textarea" -> "textbox"
                "select" -> "combobox"
                "label" -> "label"
                "summary" -> "button"
                else -> if (tag.startsWith("h")) "heading" else tag
            }
            val interactive = tag in setOf("a", "button", "textarea", "select", "summary")
            if (interactiveOnly && !interactive) return@forEach
            val href = attr(attrs, "href")
            val id = attr(attrs, "id")
            val nameAttr = attr(attrs, "name")
            val aria = attr(attrs, "aria-label")
            val name = aria.ifBlank { body }.ifBlank { nameAttr }.ifBlank { tag }
            val selector = when {
                id.isNotBlank() -> "#$id"
                href.isNotBlank() && tag == "a" -> """a[href="${cssEscape(href)}"]"""
                nameAttr.isNotBlank() -> """$tag[name="${cssEscape(nameAttr)}"]"""
                else -> tag
            }
            nodes += A11yNode(nextRef(), role, name, selector, interactive, href = href)
        }

        inputTag.findAll(html).forEach { m ->
            val attrs = m.groupValues[1]
            val type = attr(attrs, "type").ifBlank { "text" }.lowercase()
            val interactive = type != "hidden"
            if (interactiveOnly && !interactive) return@forEach
            val id = attr(attrs, "id")
            val nameAttr = attr(attrs, "name")
            val aria = attr(attrs, "aria-label")
            val placeholder = attr(attrs, "placeholder")
            val value = attr(attrs, "value")
            val role = when (type) {
                "checkbox", "radio" -> type
                "submit", "button" -> "button"
                else -> "textbox"
            }
            val name = aria.ifBlank { placeholder }.ifBlank { nameAttr }.ifBlank { type }
            val selector = when {
                id.isNotBlank() -> "#$id"
                nameAttr.isNotBlank() -> """input[name="${cssEscape(nameAttr)}"]"""
                else -> """input[type="$type"]"""
            }
            nodes += A11yNode(nextRef(), role, name, selector, interactive, value = value)
        }

        if (!interactiveOnly) {
            imgTag.findAll(html).forEach { m ->
                val attrs = m.groupValues[1]
                val alt = attr(attrs, "alt")
                val id = attr(attrs, "id")
                nodes += A11yNode(
                    nextRef(),
                    "img",
                    alt.ifBlank { "image" },
                    if (id.isNotBlank()) "#$id" else "img",
                    false,
                )
            }
        }

        val tree = nodes.joinToString("\n") {
            val extra = if (it.href.isNotBlank()) " href=${it.href}" else ""
            """- ${it.role} "${it.name}" [ref=${it.ref}]$extra"""
        }
        return A11ySnapshot(url, title, nodes, tree, realDriver = false, driverName = "html-parser")
    }

    private fun attr(attrs: String, key: String): String {
        val r = Regex("""(?i)\b$key\s*=\s*(?:"([^"]*)"|'([^']*)'|([^\s>]+))""")
        val m = r.find(attrs) ?: return ""
        return m.groupValues.drop(1).firstOrNull { it.isNotBlank() } ?: ""
    }

    private fun stripTags(s: String): String =
        s.replace(Regex("<[^>]+>"), " ").replace(Regex("\\s+"), " ").trim()

    private fun cssEscape(s: String): String =
        s.replace("\\", "\\\\").replace("\"", "\\\"")
}

package com.mimir.adapters.browser.a11y

/**
 * Accessibility-oriented snapshot inspired by agent-browser:
 * compact text + stable refs (e1) agents can act on.
 */
data class A11yNode(
    val ref: String,
    val role: String,
    val name: String,
    val selector: String,
    val interactive: Boolean,
    val value: String = "",
    val href: String = "",
)

data class A11ySnapshot(
    val url: String,
    val title: String,
    val nodes: List<A11yNode>,
    val textTree: String,
    val realDriver: Boolean,
    val driverName: String,
) {
    fun byRef(ref: String): A11yNode? {
        val key = ref.trim().removePrefix("@")
        return nodes.firstOrNull {
            it.ref == key || it.ref == "@$key" || it.ref.removePrefix("@") == key
        }
    }
}

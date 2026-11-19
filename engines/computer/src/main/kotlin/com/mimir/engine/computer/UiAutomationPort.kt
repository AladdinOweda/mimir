package com.mimir.engine.computer

/**
 * Observe result (a11y-style refs). Real drivers live in adapters/.
 */
data class UiObserveResult(
    val url: String = "",
    val title: String = "",
    val text: String = "",
    val htmlPrefix: String = "",
    val tree: String = "",
    val refs: String = "",
    val nodeCount: Int = 0,
    val realDriver: Boolean = false,
    val driverName: String = "noop",
)

interface UiAutomationPort {
    val realDriver: Boolean
    val driverName: String
    fun observe(interactiveOnly: Boolean = true): UiObserveResult
    fun click(selector: String): Boolean
    fun type(selector: String, text: String): Boolean
    fun clickRef(ref: String): Boolean
    fun typeRef(ref: String, text: String): Boolean
    fun navigate(url: String): UiObserveResult
    fun webViewPortOrNull(): WebViewPort? = null
}

class NoopUiAutomationPort : UiAutomationPort {
    override val realDriver = false
    override val driverName = "noop"
    override fun observe(interactiveOnly: Boolean) = UiObserveResult()
    override fun click(selector: String) = false
    override fun type(selector: String, text: String) = false
    override fun clickRef(ref: String) = false
    override fun typeRef(ref: String, text: String) = false
    override fun navigate(url: String) = UiObserveResult(url = url, driverName = "noop")
}

object UiDrivers {
    fun create(kind: String = "noop"): UiAutomationPort = when (kind.lowercase()) {
        "noop", "" -> NoopUiAutomationPort()
        else -> NoopUiAutomationPort() // adapters bind real drivers later
    }

    /** Shared browser-backed UI port; falls back to noop when port is null. */
    fun shared(port: Any? = null): UiAutomationPort =
        // Real adapter wiring is host-side; engine stays free of adapter package coupling.
        NoopUiAutomationPort()
}

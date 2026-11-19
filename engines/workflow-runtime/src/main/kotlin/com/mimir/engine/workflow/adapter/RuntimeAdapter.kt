package com.mimir.engine.workflow.adapter

/** Framework-neutral adapter SPI — LangGraph would implement this outside commonMain. */
interface GraphRuntimeAdapter {
    val adapterId: String
    fun health(): Boolean
    fun executeNativeFallbackPreferred(): Boolean = true
}

/** Placeholder proving optional adapter does not own contracts. */
class LangGraphAdapterStub(
    private val enabled: Boolean = false,
) : GraphRuntimeAdapter {
    override val adapterId = "langgraph-stub"
    override fun health() = enabled
    override fun executeNativeFallbackPreferred() = !enabled
}

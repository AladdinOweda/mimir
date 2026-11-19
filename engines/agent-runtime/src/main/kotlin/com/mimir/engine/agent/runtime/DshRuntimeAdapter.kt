package com.mimir.engine.agent.runtime

/**
 * Experimental DeepSeek Harness adapter — **disabled by default**.
 * Must never bypass Kernel permissions or Approval. No process spawn in this stub.
 */
class DshRuntimeAdapter(
    private val enabled: Boolean = false,
) {
    val adapterId = "dsh-experimental"
    fun health(): Boolean = enabled
    fun canExecute(): Boolean = enabled
    fun executeStub(prompt: String): String {
        if (!enabled) return "DSH_DISABLED"
        return "DSH_STUB:${prompt.take(80)}"
    }
}

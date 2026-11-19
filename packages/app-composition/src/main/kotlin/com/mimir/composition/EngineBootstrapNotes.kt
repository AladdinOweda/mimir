package com.mimir.composition

/** Recommended attach order for a local product host (not kernel). */
object EngineBootstrapNotes {
    val recommendedOrder = listOf(
        "terminal", "process", "forge", "browser", "computer", "vault",
        "provider", "agent-runtime", "orchestration", "workflow-runtime", "loop-runtime",
    )
}

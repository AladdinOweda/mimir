package com.mimir.engine.session.steward

/**
 * Context Steward — monitor token budget and compact / handoff (AnyModelBot pattern, adaptive).
 */
data class ContextBudget(
    val modelContextWindow: Int,
    val reservedOutputTokens: Int = 1024,
    val softThresholdRatio: Double = 0.80,
    val hardThresholdRatio: Double = 0.95,
) {
    val usable get() = (modelContextWindow - reservedOutputTokens).coerceAtLeast(1)
    val softLimit get() = (usable * softThresholdRatio).toInt()
    val hardLimit get() = (usable * hardThresholdRatio).toInt()
}

enum class StewardAction { None, Compact, Handoff, Block }

data class StewardDecision(
    val action: StewardAction,
    val estimatedTokens: Int,
    val softLimit: Int,
    val hardLimit: Int,
    val summary: String = "",
)

object ContextSteward {
    /** Rough estimator: ~4 chars / token */
    fun estimateTokens(text: String): Int = (text.length / 4).coerceAtLeast(1)

    fun evaluate(historyText: String, budget: ContextBudget): StewardDecision {
        val tokens = estimateTokens(historyText)
        return when {
            tokens >= budget.hardLimit -> StewardDecision(
                StewardAction.Handoff, tokens, budget.softLimit, budget.hardLimit,
                summary = compact(historyText, maxChars = 2000),
            )
            tokens >= budget.softLimit -> StewardDecision(
                StewardAction.Compact, tokens, budget.softLimit, budget.hardLimit,
                summary = compact(historyText, maxChars = 4000),
            )
            else -> StewardDecision(StewardAction.None, tokens, budget.softLimit, budget.hardLimit)
        }
    }

    fun compact(text: String, maxChars: Int): String {
        if (text.length <= maxChars) return text
        val head = text.take(maxChars / 3)
        val tail = text.takeLast(maxChars / 3)
        return "$head\n...\n[compacted ${text.length - maxChars} chars]\n...\n$tail"
    }
}

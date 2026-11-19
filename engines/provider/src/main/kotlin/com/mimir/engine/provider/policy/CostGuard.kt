package com.mimir.engine.provider.policy

/**
 * R7 — cost limits enforced server-side. request.input["allowSpend"] is ignored.
 */
class CostGuard(
    private val defaultBudgetUsd: Double = 1.0,
) {
    private val spent = mutableMapOf<String, Double>() // principal -> spent
    private val budgets = mutableMapOf<String, Double>()

    fun setBudget(principalId: String, usd: Double) {
        budgets[principalId] = usd
    }

    fun authorizeSpend(principalId: String, estimatedUsd: Double): Boolean {
        val budget = budgets[principalId] ?: defaultBudgetUsd
        val used = spent[principalId] ?: 0.0
        return used + estimatedUsd <= budget
    }

    fun recordSpend(principalId: String, actualUsd: Double) {
        spent[principalId] = (spent[principalId] ?: 0.0) + actualUsd
    }

    fun remaining(principalId: String): Double {
        val budget = budgets[principalId] ?: defaultBudgetUsd
        return budget - (spent[principalId] ?: 0.0)
    }
}

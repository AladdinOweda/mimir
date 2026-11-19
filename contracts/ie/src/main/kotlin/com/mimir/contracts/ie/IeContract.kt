package com.mimir.contracts.ie

object IeCapabilityIds {
    const val PLAN = "ie.plan"
    const val EXECUTE = "ie.execute"
    const val RELAY = "ie.relay"
    const val STATUS = "ie.status"
    const val STEP_ADVANCE = "ie.step.advance"
    val ALL = listOf(PLAN, EXECUTE, RELAY, STATUS, STEP_ADVANCE)
}

data class IePlanId(val value: String)

interface IePort {
    fun plan(goal: String, steps: List<String>): IePlanId
    /** Requires approval token at host when capability is High. */
    fun execute(planId: IePlanId, approvalToken: String): String
}

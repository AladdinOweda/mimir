package com.mimir.contracts.verify

object VerifyCapabilityIds {
    const val SUBMIT = "verify.submit"
    const val VERDICT = "verify.verdict"
    const val CHECK_STALE = "verify.check_stale"
    val ALL = listOf(SUBMIT, VERDICT, CHECK_STALE)
}

/** PB-07: builder cannot self-accept. */
interface VerifyPort {
    fun submit(taskId: String, agentId: String, commitRef: String, testsPassed: Boolean): Boolean
    fun verdict(taskId: String, verifierId: String, accept: Boolean, commitRef: String): Boolean
}

package com.mimir.contracts.spe

object SpeCapabilityIds {
    const val SPEC_CREATE = "spe.spec.create"
    const val PLAN_CREATE = "spe.plan.create"
    const val TASK_ADD = "spe.task.add"
    const val TASK_LIST = "spe.task.list"
    const val CONVERGE = "spe.converge"
    const val TASK_COMPLETE = "spe.task.complete"
    val ALL = listOf(SPEC_CREATE, PLAN_CREATE, TASK_ADD, TASK_LIST, CONVERGE, TASK_COMPLETE)
}

data class SpeSpecId(val value: String)
data class SpeTaskId(val value: String)

interface SpePort {
    fun createSpec(title: String, body: String): SpeSpecId
    fun addTask(specId: SpeSpecId, title: String): SpeTaskId
    fun converge(specId: SpeSpecId): Boolean
}

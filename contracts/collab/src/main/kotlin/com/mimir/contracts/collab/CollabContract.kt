package com.mimir.contracts.collab

object CollabCapabilityIds {
    const val GROUP_OPEN = "collab.group.open"
    const val DELEGATE = "collab.delegate"
    const val HANDOVER = "collab.handover"
    const val STATUS = "collab.status"
    const val EVIDENCE_ADD = "collab.evidence.add"
    const val GROUP_CLOSE = "collab.group.close"
    val ALL = listOf(GROUP_OPEN, DELEGATE, HANDOVER, STATUS, EVIDENCE_ADD, GROUP_CLOSE)
}

data class CollabGroupId(val value: String)

interface CollabPort {
    fun openGroup(goal: String, bots: List<String>): CollabGroupId
    fun delegate(groupId: CollabGroupId, botId: String, task: String): Boolean
    fun closeGroup(groupId: CollabGroupId): Boolean
}

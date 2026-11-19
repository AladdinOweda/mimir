package com.mimir.platform.events

/** Subset of INT-010 event names used by vertical slices. */
object EventCatalog {
    const val TASK_CREATED = "task.created"
    const val TASK_STATUS_CHANGED = "task.status_changed"
    const val APPROVAL_REQUESTED = "approval.requested"
    const val APPROVAL_GRANTED = "approval.granted"
    const val FORGE_COLUMN_PROJECTED = "forge.column.projected"
    const val DOWNLOAD_ENQUEUED = "download.enqueued"
    const val DOWNLOAD_COMPLETED = "download.completed"
    const val KNOWLEDGE_INDEXED = "knowledge.indexed"
    const val FILES_REGISTERED = "files.registered"
    const val PORTABILITY_REQUESTED = "portability.requested"
}

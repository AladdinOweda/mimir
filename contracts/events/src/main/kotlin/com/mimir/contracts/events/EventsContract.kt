package com.mimir.contracts.events

/**
 * Typed product event names (INT-010 subset) + publish port.
 * Implementation lives in shared/events (EventBus); this is the stable contract surface.
 */
object ProductEventNames {
    const val TASK_CREATED = "task.created"
    const val TASK_STATUS_CHANGED = "task.status_changed"
    const val APPROVAL_REQUESTED = "approval.requested"
    const val APPROVAL_GRANTED = "approval.granted"
    const val FORGE_COLUMN_PROJECTED = "forge.column.projected"
    const val DOWNLOAD_ENQUEUED = "download.enqueued"
    const val DOWNLOAD_COMPLETED = "download.completed"
    const val KNOWLEDGE_INDEXED = "knowledge.indexed"
    const val ENGINE_ATTACHED = "engines.attached"
}

data class ProductEvent(
    val type: String,
    val source: String,
    val payload: Map<String, String> = emptyMap(),
    val correlationId: String? = null,
)

fun interface ProductEventHandler {
    fun onEvent(event: ProductEvent)
}

interface EventPublisherPort {
    fun publish(event: ProductEvent)
    fun subscribe(handler: ProductEventHandler)
}

package com.mimir.service.tasks

import com.mimir.platform.events.EventBus
import com.mimir.platform.events.EventCatalog

/**
 * S1: projects task lifecycle to Forge columns and emits INT-010-aligned events.
 */
class TaskForgeProjector(private val events: EventBus? = null) {
    fun onCreated(task: TaskSummary) {
        events?.publish(
            EventCatalog.TASK_CREATED,
            "tasks",
            mapOf("id" to task.id.value, "status" to task.status.name),
        )
        emitColumn(task)
    }

    fun onStatusChanged(task: TaskSummary) {
        events?.publish(
            EventCatalog.TASK_STATUS_CHANGED,
            "tasks",
            mapOf(
                "id" to task.id.value,
                "status" to task.status.name,
                "verification" to task.verification.name,
            ),
        )
        emitColumn(task)
    }

    private fun emitColumn(task: TaskSummary) {
        val col = task.toForgeColumn()
        events?.publish(
            EventCatalog.FORGE_COLUMN_PROJECTED,
            "forge",
            mapOf("id" to task.id.value, "column" to col.name),
        )
    }
}

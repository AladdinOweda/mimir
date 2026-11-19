package com.mimir.ui.forge

import com.mimir.service.tasks.ForgeColumn
import com.mimir.service.tasks.TaskSummary
import com.mimir.service.tasks.toForgeColumn

/**
 * Forge Kanban projection (MIMIR-ENG-FORGE-001 / ADR-039).
 *
 * Compose-ready: apps render [ForgeBoardState] with LazyRow/LazyColumn.
 * This module stays JVM-clean (no Compose runtime).
 */
data class ForgeCardUi(
    val id: String,
    val title: String,
    val status: String,
    val verification: String,
    val column: ForgeColumn,
)

data class ForgeBoardState(
    val columns: Map<ForgeColumn, List<ForgeCardUi>>,
) {
    fun cardsIn(column: ForgeColumn): List<ForgeCardUi> = columns[column].orEmpty()
}

fun projectForgeBoard(tasks: List<TaskSummary>): ForgeBoardState {
    val grouped = ForgeColumn.entries.associateWith { mutableListOf<ForgeCardUi>() }
    for (t in tasks) {
        val col = t.toForgeColumn()
        grouped.getValue(col).add(
            ForgeCardUi(
                id = t.id.value,
                title = t.title,
                status = t.status.name,
                verification = t.verification.name,
                column = col,
            ),
        )
    }
    return ForgeBoardState(columns = grouped.mapValues { it.value.toList() })
}

/** Ordered columns for a typical Forge board (ADR-039 view). */
val ForgeBoardColumnOrder: List<ForgeColumn> = listOf(
    ForgeColumn.Backlog,
    ForgeColumn.Ready,
    ForgeColumn.InProgress,
    ForgeColumn.Blocked,
    ForgeColumn.WaitingForApproval,
    ForgeColumn.InReview,
    ForgeColumn.Done,
)

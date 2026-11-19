package com.mimir.app.android

import com.mimir.service.tasks.ForgeColumn
import com.mimir.service.tasks.TaskSummary
import com.mimir.ui.forge.ForgeBoardColumnOrder
import com.mimir.ui.forge.ForgeBoardState
import com.mimir.ui.forge.projectForgeBoard

/**
 * Android host binds [lines] or [board] to RecyclerView / LazyColumn later.
 * No Android framework dependency in this module.
 */
class ForgeBoardPresenter {
    fun board(tasks: List<TaskSummary>): ForgeBoardState = projectForgeBoard(tasks)

    /** Simple column list text for debug / accessibility tree. */
    fun lines(tasks: List<TaskSummary>): List<String> {
        val state = board(tasks)
        val out = mutableListOf<String>()
        for (col in ForgeBoardColumnOrder) {
            val cards = state.cardsIn(col)
            if (cards.isEmpty()) continue
            out += "## ${col.name}"
            cards.forEach { c -> out += "- [${c.id}] ${c.title} (${c.status}/${c.verification})" }
        }
        return out
    }

    fun columnTitles(): List<String> = ForgeBoardColumnOrder.map { it.name }
}

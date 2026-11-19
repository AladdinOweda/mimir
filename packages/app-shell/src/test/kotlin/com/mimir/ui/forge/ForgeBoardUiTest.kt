package com.mimir.ui.forge

import com.mimir.service.tasks.ForgeColumn
import com.mimir.service.tasks.TaskId
import com.mimir.service.tasks.TaskStatus
import com.mimir.service.tasks.TaskSummary
import com.mimir.service.tasks.VerificationVerdict
import kotlin.test.Test
import kotlin.test.assertEquals

class ForgeBoardUiTest {
    @Test
    fun projects_done_and_in_progress_columns() {
        val tasks = listOf(
            TaskSummary(TaskId("1"), "done", TaskStatus.Completed, VerificationVerdict.Verified),
            TaskSummary(TaskId("2"), "run", TaskStatus.Running),
            TaskSummary(TaskId("3"), "draft", TaskStatus.Draft),
        )
        val board = projectForgeBoard(tasks)
        assertEquals(1, board.cardsIn(ForgeColumn.Done).size)
        assertEquals(1, board.cardsIn(ForgeColumn.InProgress).size)
        assertEquals(1, board.cardsIn(ForgeColumn.Backlog).size)
        assertEquals("done", board.cardsIn(ForgeColumn.Done).first().title)
    }
}

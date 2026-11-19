package com.mimir.app.android

import com.mimir.service.tasks.ForgeColumn
import com.mimir.service.tasks.TaskId
import com.mimir.service.tasks.TaskStatus
import com.mimir.service.tasks.TaskSummary
import com.mimir.service.tasks.VerificationVerdict
import kotlin.test.Test
import kotlin.test.assertTrue

class ForgeBoardPresenterTest {
    @Test
    fun lines_include_done_column() {
        val p = ForgeBoardPresenter()
        val tasks = listOf(
            TaskSummary(TaskId("1"), "ship", TaskStatus.Completed, VerificationVerdict.Verified),
        )
        val lines = p.lines(tasks)
        assertTrue(lines.any { it.contains("Done") })
        assertTrue(lines.any { it.contains("ship") })
        assertTrue(p.board(tasks).cardsIn(ForgeColumn.Done).isNotEmpty())
    }
}

package com.mimir.service.tasks

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/** ADR-039: Forge column is a VIEW over lifecycle + verification. Only the specified rows are pinned. */
class TaskContractsTest {

    private fun t(s: TaskStatus, v: VerificationVerdict = VerificationVerdict.Unverified) =
        TaskSummary(TaskId("t"), "task", s, v)

    @Test
    fun lifecycle_rows_map_to_expected_columns() {
        assertEquals(ForgeColumn.Backlog, t(TaskStatus.Draft).toForgeColumn())
        assertEquals(ForgeColumn.Backlog, t(TaskStatus.Queued).toForgeColumn())
        assertEquals(ForgeColumn.Ready, t(TaskStatus.Ready).toForgeColumn())
        assertEquals(ForgeColumn.InProgress, t(TaskStatus.Running).toForgeColumn())
        assertEquals(ForgeColumn.InProgress, t(TaskStatus.Paused).toForgeColumn())
        assertEquals(ForgeColumn.Blocked, t(TaskStatus.Blocked).toForgeColumn())
        assertEquals(ForgeColumn.WaitingForApproval, t(TaskStatus.WaitingApproval).toForgeColumn())
    }

    @Test
    fun completed_unverified_is_in_review_and_verified_is_done() {
        assertEquals(ForgeColumn.InReview, t(TaskStatus.Completed, VerificationVerdict.Unverified).toForgeColumn())
        assertEquals(ForgeColumn.Done, t(TaskStatus.Completed, VerificationVerdict.Verified).toForgeColumn())
    }

    @Test
    fun done_column_requires_completed_and_verified() {
        for (s in TaskStatus.values()) for (v in VerificationVerdict.values()) {
            val col = t(s, v).toForgeColumn()
            if (col == ForgeColumn.Done) {
                assertEquals(TaskStatus.Completed, s)
                assertEquals(VerificationVerdict.Verified, v)
            }
        }
    }

    @Test
    fun legacy_done_is_completed_not_verified() {
        assertEquals(TaskStatus.Completed, TaskStatus.fromLegacy("Done"))
        assertNotEquals(ForgeColumn.Done, t(TaskStatus.fromLegacy("Done")).toForgeColumn())
    }
}

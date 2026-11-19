package com.mimir.service.tasks

import com.mimir.kernel.contract.EventEnvelope
import com.mimir.platform.events.EventBus
import com.mimir.platform.events.EventCatalog
import com.mimir.platform.events.EventHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskForgeProjectorTest {
    @Test
    fun completed_verified_projects_done_and_emits_events() {
        val bus = EventBus()
        val seen = mutableListOf<String>()
        bus.subscribe(EventHandler { e: EventEnvelope -> seen += e.type })
        val projector = TaskForgeProjector(bus)
        val t0 = TaskSummary(TaskId("1"), "demo", TaskStatus.Draft)
        projector.onCreated(t0)
        val t1 = t0.copy(status = TaskStatus.Completed, verification = VerificationVerdict.Verified)
        projector.onStatusChanged(t1)
        assertEquals(ForgeColumn.Done, t1.toForgeColumn())
        assertTrue(EventCatalog.TASK_CREATED in seen)
        assertTrue(EventCatalog.TASK_STATUS_CHANGED in seen)
        assertTrue(EventCatalog.FORGE_COLUMN_PROJECTED in seen)
    }
}

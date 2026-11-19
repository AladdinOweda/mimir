package com.mimir.composition

import com.mimir.kernel.contract.EventEnvelope
import com.mimir.service.downloads.DownloadStatus
import com.mimir.service.retention.RetentionClass
import com.mimir.service.tasks.ForgeColumn
import com.mimir.service.tasks.TaskId
import com.mimir.service.tasks.TaskStatus
import com.mimir.service.tasks.TaskSummary
import com.mimir.service.tasks.VerificationVerdict
import com.mimir.platform.events.EventCatalog
import com.mimir.platform.events.EventHandler
import com.mimir.ui.forge.projectForgeBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductServicesSliceTest {
    @Test
    fun integration_services_without_engines() {
        val svc = ProductServices(useDurableFiles = false)
        val seen = mutableListOf<String>()
        svc.events.subscribe(EventHandler { e: EventEnvelope -> seen += e.type })

        assertEquals(RetentionClass.LegalHold, svc.retention.classify("vault"))
        assertTrue(svc.portability.requestExport("knowledge").id.value.startsWith("port-"))

        svc.knowledge.index("k1", "Mimir", "kernel neutral knowledge")
        assertEquals(1, svc.knowledge.search("kernel").size)

        val dl = svc.downloads.enqueue("https://example.com/x.zip", "x.zip")
        svc.downloads.update(dl.id, DownloadStatus.Completed, 100)
        assertEquals(DownloadStatus.Completed, svc.downloads.get(dl.id)!!.status)

        val tasks = listOf(
            TaskSummary(TaskId("t1"), "ship", TaskStatus.Completed, VerificationVerdict.Verified),
            TaskSummary(TaskId("t2"), "wip", TaskStatus.Running),
            TaskSummary(TaskId("t3"), "wait", TaskStatus.WaitingApproval),
        )
        tasks.forEach { svc.taskProjector.onStatusChanged(it) }
        val board = projectForgeBoard(tasks)
        assertEquals(1, board.cardsIn(ForgeColumn.Done).size)
        assertEquals(1, board.cardsIn(ForgeColumn.InProgress).size)
        assertEquals(1, board.cardsIn(ForgeColumn.WaitingForApproval).size)
        assertTrue(EventCatalog.FORGE_COLUMN_PROJECTED in seen)

        svc.files.register("note.txt")
        assertTrue(svc.files.list().isNotEmpty())
    }
}

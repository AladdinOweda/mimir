package com.mimir.composition

import com.mimir.contracts.knowledge.InMemoryKnowledgeFacade
import com.mimir.contracts.knowledge.KnowledgeFacade
import com.mimir.service.activity.ActivityLog
import com.mimir.service.activity.InMemoryActivityLog
import com.mimir.service.downloads.DownloadDirectory
import com.mimir.service.downloads.InMemoryDownloadDirectory
import com.mimir.service.files.FileCatalog
import com.mimir.service.files.InMemoryFileCatalog
import com.mimir.service.files.LocalFileSystemCatalog
import com.mimir.service.portability.DataPortability
import com.mimir.service.portability.InMemoryDataPortability
import com.mimir.service.projects.InMemoryProjectDirectory
import com.mimir.service.projects.ProjectDirectory
import com.mimir.service.retention.DefaultRetentionPolicy
import com.mimir.service.retention.RetentionPolicy
import com.mimir.service.tasks.InMemoryTaskDirectory
import com.mimir.service.tasks.TaskDirectory
import com.mimir.service.tasks.TaskForgeProjector
import com.mimir.platform.events.EventBus
import com.mimir.platform.memory.InMemoryMemoryService
import com.mimir.platform.memory.MemoryService
import java.io.File

/**
 * Engine-free product services graph for integration tests and light hosts.
 * [ProductContext] can mirror these fields; attach engines separately.
 */
class ProductServices(
    dataDir: File = File(System.getProperty("java.io.tmpdir"), "mimir-services"),
    useDurableFiles: Boolean = false,
) {
    val events: EventBus = EventBus()
    val activity: ActivityLog = InMemoryActivityLog()
    val files: FileCatalog =
        if (useDurableFiles) LocalFileSystemCatalog(File(dataDir, "files"))
        else InMemoryFileCatalog()
    val downloads: DownloadDirectory = InMemoryDownloadDirectory()
    val knowledge: KnowledgeFacade = InMemoryKnowledgeFacade()
    val projects: ProjectDirectory = InMemoryProjectDirectory()
    val tasks: TaskDirectory = InMemoryTaskDirectory()
    val memory: MemoryService = InMemoryMemoryService()
    val taskProjector: TaskForgeProjector = TaskForgeProjector(events)
    val retention: RetentionPolicy = DefaultRetentionPolicy()
    val portability: DataPortability = InMemoryDataPortability()
}

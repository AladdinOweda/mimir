package com.mimir.service.activity

import java.io.File
import java.util.ArrayDeque

/** File-backed activity log (tab-separated). Survives process restart. */
class DurableFileActivityLog(
    private val storeFile: File,
    private val maxInMemory: Int = 500,
) : ActivityLog {
    private val events = ArrayDeque<ActivityEvent>()
    private var seq = 0L

    init {
        storeFile.parentFile?.mkdirs()
        if (storeFile.exists()) {
            storeFile.readLines().forEach { line ->
                val p = line.split("\t", limit = 5)
                if (p.size >= 4) {
                    val id = p[0]
                    val type = p[1]
                    val epoch = p[2].toLongOrNull() ?: 0L
                    val resource = p[3].ifBlank { null }
                    val summary = if (p.size >= 5) p[4].replace("\\n", "\n").replace("\\t", "\t") else ""
                    events.addLast(ActivityEvent(id, type, summary, resource, epoch))
                    val n = id.removePrefix("act-").toLongOrNull()
                    if (n != null && n > seq) seq = n
                }
            }
            while (events.size > maxInMemory) events.removeFirst()
        }
    }

    @Synchronized
    override fun record(type: String, summary: String, resource: String?): ActivityEvent {
        val e = ActivityEvent(
            id = "act-${++seq}",
            type = type,
            summary = summary,
            resource = resource,
        )
        events.addLast(e)
        while (events.size > maxInMemory) events.removeFirst()
        val line = listOf(
            e.id, e.type, e.epochMs.toString(), e.resource ?: "",
            e.summary.replace("\t", "\\t").replace("\n", "\\n"),
        ).joinToString("\t")
        storeFile.appendText(line + "\n")
        return e
    }

    @Synchronized
    override fun recent(limit: Int): List<ActivityEvent> = events.toList().takeLast(limit).reversed()
}

package com.mimir.platform.memory

import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Append-only file durable memory. Format per line:
 * id\t epoch \t tagsCsv \t provenance \t content (content last, may contain tabs escaped as \t)
 */
class DurableFileMemoryService(
    private val storeFile: File,
) : MemoryService {
    private val cache = ConcurrentHashMap<String, MemoryRecord>()
    private var seq = 0L

    init {
        storeFile.parentFile?.mkdirs()
        if (storeFile.exists()) {
            storeFile.readLines().forEach { line ->
                val parts = line.split("\t", limit = 5)
                if (parts.size >= 5) {
                    val id = parts[0]
                    val epoch = parts[1].toLongOrNull() ?: 0L
                    val tags = parts[2].split(",").filter { it.isNotBlank() }.toSet()
                    val prov = parts[3].ifBlank { null }
                    val content = parts[4].replace("\\n", "\n").replace("\\t", "\t")
                    cache[id] = MemoryRecord(MemoryId(id), content, tags, prov, epoch)
                    val n = id.removePrefix("mem-").toLongOrNull()
                    if (n != null && n > seq) seq = n
                }
            }
        }
    }

    @Synchronized
    override fun capture(content: String, tags: Set<String>, provenance: String?): MemoryId {
        val id = MemoryId("mem-${++seq}")
        val rec = MemoryRecord(id, content, tags, provenance)
        cache[id.value] = rec
        append(rec)
        return id
    }

    override fun get(id: MemoryId) = cache[id.value]

    override fun search(query: String, limit: Int): List<MemoryRecord> {
        val q = query.lowercase()
        return cache.values.filter {
            it.content.lowercase().contains(q) || it.tags.any { t -> t.lowercase().contains(q) }
        }.take(limit)
    }

    @Synchronized
    override fun delete(id: MemoryId): Boolean {
        if (cache.remove(id.value) == null) return false
        rewriteAll()
        return true
    }

    private fun append(rec: MemoryRecord) {
        val line = listOf(
            rec.id.value,
            rec.createdAtEpochMs.toString(),
            rec.tags.joinToString(","),
            rec.provenance ?: "",
            rec.content.replace("\t", "\\t").replace("\n", "\\n"),
        ).joinToString("\t")
        storeFile.appendText(line + "\n")
    }

    private fun rewriteAll() {
        storeFile.writeText("")
        cache.values.sortedBy { it.createdAtEpochMs }.forEach { append(it) }
    }
}

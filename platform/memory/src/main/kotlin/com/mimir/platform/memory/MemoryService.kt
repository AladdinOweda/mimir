package com.mimir.platform.memory

/**
 * Shared Memory platform service (MIMIR-REPO-002 §9.9).
 * Not an Engine. Engines and agents consume via this contract.
 */
data class MemoryId(val value: String)
data class MemoryRecord(
    val id: MemoryId,
    val content: String,
    val tags: Set<String> = emptySet(),
    val provenance: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

interface MemoryService {
    fun capture(content: String, tags: Set<String> = emptySet(), provenance: String? = null): MemoryId
    fun get(id: MemoryId): MemoryRecord?
    fun search(query: String, limit: Int = 20): List<MemoryRecord>
    fun delete(id: MemoryId): Boolean
}

/** In-process implementation — product path may later use durable store. */
class InMemoryMemoryService : MemoryService {
    private val store = linkedMapOf<String, MemoryRecord>()
    private var seq = 0L
    override fun capture(content: String, tags: Set<String>, provenance: String?): MemoryId {
        val id = MemoryId("mem-${++seq}")
        store[id.value] = MemoryRecord(id, content, tags, provenance)
        return id
    }
    override fun get(id: MemoryId) = store[id.value]
    override fun search(query: String, limit: Int): List<MemoryRecord> {
        val q = query.lowercase()
        return store.values.filter {
            it.content.lowercase().contains(q) || it.tags.any { t -> t.lowercase().contains(q) }
        }.take(limit)
    }
    override fun delete(id: MemoryId) = store.remove(id.value) != null
}

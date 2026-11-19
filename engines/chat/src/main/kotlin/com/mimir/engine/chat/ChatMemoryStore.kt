package com.mimir.engine.chat

import java.io.File
import java.util.concurrent.ConcurrentHashMap

/** Optional in-process durable snapshot for threads (file-backed JSON-ish lines). */
class ChatMemoryStore(private val dir: File) {
    private val file = File(dir, "chat-threads.tsv").also {
        dir.mkdirs()
        if (!it.exists()) it.writeText("")
    }
    private val cache = ConcurrentHashMap<String, String>()

    init {
        file.readLines().forEach { line ->
            val i = line.indexOf('\t')
            if (i > 0) cache[line.substring(0, i)] = line.substring(i + 1)
        }
    }

    fun put(threadId: String, snapshot: String) {
        cache[threadId] = snapshot.take(8_000)
        flush()
    }

    fun get(threadId: String): String? = cache[threadId]
    fun keys(): Set<String> = cache.keys
    fun size(): Int = cache.size

    private fun flush() {
        file.writeText(cache.entries.joinToString("\n") { "${it.key}\t${it.value}" })
    }
}

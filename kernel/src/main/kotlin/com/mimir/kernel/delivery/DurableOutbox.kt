package com.mimir.kernel.delivery

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

enum class OutboxStatus { Pending, Sending, Sent, Held, Scheduled, Gone, Failed }

data class OutboxMessage(
    val id: String,
    val conversationId: String,
    val dedupeKey: String,
    val payload: String,
    var status: OutboxStatus = OutboxStatus.Pending,
    var attempts: Int = 0,
    var nextAttemptEpochMs: Long = 0L,
    var lastError: String = "",
    val createdAtEpochMs: Long,
)

/**
 * Delivery outbox — channel-agnostic. Adapters mark sent/failed; kernel owns retry schedule.
 */
class DurableOutbox(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
    private val maxAttempts: Int = 8,
    private val baseBackoffMs: Long = 1_000L,
) {
    private val messages = ConcurrentHashMap<String, OutboxMessage>()
    private val dedupe = ConcurrentHashMap<String, String>()
    private val seq = AtomicLong(0)

    fun enqueue(conversationId: String, payload: String, dedupeKey: String = ""): OutboxMessage {
        if (dedupeKey.isNotBlank()) {
            dedupe[dedupeKey]?.let { existing -> messages[existing]?.let { return it } }
        }
        val id = "ob-${seq.incrementAndGet()}"
        val msg = OutboxMessage(
            id = id,
            conversationId = conversationId,
            dedupeKey = dedupeKey,
            payload = payload,
            createdAtEpochMs = clockMs(),
            nextAttemptEpochMs = clockMs(),
        )
        messages[id] = msg
        if (dedupeKey.isNotBlank()) dedupe[dedupeKey] = id
        return msg
    }

    fun pollReady(limit: Int = 32): List<OutboxMessage> {
        val now = clockMs()
        return messages.values
            .filter { it.status == OutboxStatus.Pending || it.status == OutboxStatus.Scheduled }
            .filter { it.nextAttemptEpochMs <= now }
            .sortedBy { it.createdAtEpochMs }
            .take(limit.coerceAtLeast(1))
    }

    fun markSending(id: String): Boolean {
        val m = messages[id] ?: return false
        if (m.status != OutboxStatus.Pending && m.status != OutboxStatus.Scheduled) return false
        m.status = OutboxStatus.Sending
        m.attempts += 1
        return true
    }

    fun markSent(id: String): Boolean {
        val m = messages[id] ?: return false
        m.status = OutboxStatus.Sent
        m.lastError = ""
        return true
    }

    fun markFailed(id: String, error: String = "send_failed"): Boolean {
        val m = messages[id] ?: return false
        m.lastError = error
        if (m.attempts >= maxAttempts) {
            m.status = OutboxStatus.Failed
            return true
        }
        val backoff = min(baseBackoffMs * (1L shl (m.attempts.coerceAtMost(10))), 300_000L)
        m.nextAttemptEpochMs = clockMs() + backoff
        m.status = OutboxStatus.Scheduled
        return true
    }

    fun hold(id: String): Boolean {
        val m = messages[id] ?: return false
        m.status = OutboxStatus.Held
        return true
    }

    fun release(id: String): Boolean {
        val m = messages[id] ?: return false
        if (m.status != OutboxStatus.Held) return false
        m.status = OutboxStatus.Pending
        m.nextAttemptEpochMs = clockMs()
        return true
    }

    fun purgeSent(olderThanMs: Long = 86_400_000L): Int {
        val cutoff = clockMs() - olderThanMs
        val ids = messages.values.filter { it.status == OutboxStatus.Sent && it.createdAtEpochMs < cutoff }.map { it.id }
        ids.forEach {
            messages.remove(it)?.dedupeKey?.takeIf { k -> k.isNotBlank() }?.let { k -> dedupe.remove(k, it) }
        }
        return ids.size
    }

    fun get(id: String): OutboxMessage? = messages[id]

    fun size(): Int = messages.size

    fun stats(): Map<String, Int> = OutboxStatus.entries.associate { st ->
        st.name to messages.values.count { it.status == st }
    }
}

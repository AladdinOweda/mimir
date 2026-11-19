package com.mimir.kernel.audit

import com.mimir.kernel.persist.JdbcKernelStore
import java.util.concurrent.CopyOnWriteArrayList

data class AuditEntry(
    val atEpochMs: Long,
    val principalId: String,
    val capabilityId: String,
    val decision: String,
    val reason: String,
    val requestId: String,
    val approvalTokenId: String? = null,
)

/**
 * In-memory audit ring + optional JDBC recovery.
 */
class AuditLog(
    private val store: JdbcKernelStore? = null,
    private val maxInMemory: Int = 10_000,
) {
    private val entries = CopyOnWriteArrayList<AuditEntry>()

    fun append(entry: AuditEntry) {
        entries.add(entry)
        while (entries.size > maxInMemory) {
            entries.removeAt(0)
        }
        store?.appendAudit(entry)
    }

    fun recent(limit: Int = 100): List<AuditEntry> =
        entries.takeLast(limit.coerceIn(1, maxInMemory))

    fun byPrincipal(principalId: String, limit: Int = 100): List<AuditEntry> =
        entries.filter { it.principalId == principalId }.takeLast(limit)

    fun byCapability(capabilityId: String, limit: Int = 100): List<AuditEntry> =
        entries.filter { it.capabilityId == capabilityId }.takeLast(limit)

    fun denyCount(): Int = entries.count { it.decision == "DENY" }

    fun allowCount(): Int = entries.count { it.decision == "ALLOW" }

    fun size(): Int = entries.size

    fun recover() {
        store?.loadAudit(maxInMemory)?.forEach { entries.add(it) }
    }
}

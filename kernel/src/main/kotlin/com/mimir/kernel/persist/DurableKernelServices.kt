package com.mimir.kernel.persist

import com.mimir.kernel.audit.AuditEntry
import com.mimir.kernel.audit.AuditLog
import com.mimir.kernel.permission.PermissionPolicy
import com.mimir.kernel.security.ApprovalService
import com.mimir.kernel.security.ApprovalToken

class PersistingAuditLog(
    private val store: JdbcKernelStore,
) {
    private val mem = AuditLog()
    fun append(e: AuditEntry) {
        mem.append(e)
        store.appendAudit(e)
    }
    fun snapshot(): List<AuditEntry> {
        val m = mem.recent(10_000)
        return if (m.isNotEmpty()) m else store.loadAudit()
    }
    fun size(): Int {
        val s = mem.size()
        return if (s > 0) s else store.auditCount()
    }
}

class DurablePermissionPolicy(
    private val store: JdbcKernelStore,
) {
    private val mem = PermissionPolicy()
    fun grantAll(principalId: String, permissions: List<String>) {
        mem.grantAll(principalId, permissions)
        permissions.forEach { store.grantPermission(principalId, it) }
    }
    fun check(principalId: String, permission: String): Boolean {
        if (mem.check(principalId, permission)) return true
        val loaded = store.loadPermissions(principalId)
        if (loaded.isNotEmpty()) mem.grantAll(principalId, loaded.toList())
        return mem.check(principalId, permission)
    }
    fun recoverPrincipal(principalId: String) {
        val loaded = store.loadPermissions(principalId)
        if (loaded.isNotEmpty()) mem.grantAll(principalId, loaded.toList())
    }
}

class DurableApprovalService(
    private val store: JdbcKernelStore,
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) {
    private val mem = ApprovalService(clockMs)

    fun issue(
        principalId: String,
        actionId: String,
        targetResource: String,
        grantedScope: String,
        ttlMs: Long = 300_000,
        singleUse: Boolean = true,
    ): ApprovalToken {
        val t = mem.issue(principalId, actionId, targetResource, grantedScope, ttlMs, singleUse)
        store.saveToken(t)
        return t
    }

    fun consumeIfValid(
        tokenId: String,
        principalId: String,
        actionId: String,
        targetResource: String,
        requiredScope: String,
    ): ApprovalToken? {
        mem.consumeIfValid(tokenId, principalId, actionId, targetResource, requiredScope)?.let {
            store.saveToken(it)
            return it
        }
        val loaded = store.loadToken(tokenId) ?: return null
        if (loaded.revoked || loaded.consumed) return null
        if (loaded.expiresAtEpochMs < clockMs()) return null
        if (loaded.principalId != principalId) return null
        if (loaded.actionId != actionId) return null
        if (loaded.targetResource != targetResource && loaded.targetResource != "*") return null
        if (loaded.grantedScope != requiredScope && loaded.grantedScope != "*") return null
        if (loaded.singleUse) loaded.consumed = true
        store.saveToken(loaded)
        return loaded
    }

    fun peek(tokenId: String) = mem.peek(tokenId) ?: store.loadToken(tokenId)
}

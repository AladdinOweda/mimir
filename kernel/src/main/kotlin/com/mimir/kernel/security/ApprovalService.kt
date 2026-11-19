package com.mimir.kernel.security

import com.mimir.kernel.persist.JdbcKernelStore
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Approval tokens. Never trust request.input["approvalGranted"].
 * Optional [store] persists issued tokens for restart recovery.
 */
data class ApprovalToken(
    val id: String,
    val principalId: String,
    val actionId: String,
    val targetResource: String,
    val grantedScope: String,
    val expiresAtEpochMs: Long,
    val singleUse: Boolean = true,
    var revoked: Boolean = false,
    var consumed: Boolean = false,
)

class ApprovalService(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
    private val store: JdbcKernelStore? = null,
) {
    private val tokens = ConcurrentHashMap<String, ApprovalToken>()
    private val seq = AtomicLong(0)

    fun issue(
        principalId: String,
        actionId: String,
        targetResource: String,
        grantedScope: String,
        ttlMs: Long = 300_000,
        singleUse: Boolean = true,
    ): ApprovalToken {
        val id = "ap-${seq.incrementAndGet()}-${clockMs()}"
        val t = ApprovalToken(
            id = id,
            principalId = principalId,
            actionId = actionId,
            targetResource = targetResource,
            grantedScope = grantedScope,
            expiresAtEpochMs = clockMs() + ttlMs,
            singleUse = singleUse,
        )
        tokens[id] = t
        store?.saveToken(t)
        return t
    }

    fun revoke(id: String) {
        tokens[id]?.revoked = true
        tokens[id]?.let { store?.saveToken(it) }
    }

    fun consumeIfValid(
        tokenId: String,
        principalId: String,
        actionId: String,
        targetResource: String,
        requiredScope: String,
    ): ApprovalToken? {
        val t = tokens[tokenId] ?: store?.loadToken(tokenId)?.also { tokens[it.id] = it } ?: return null
        if (t.revoked || t.consumed) return null
        if (t.expiresAtEpochMs < clockMs()) return null
        if (t.principalId != principalId) return null
        if (t.actionId != actionId) return null
        if (t.targetResource != targetResource && t.targetResource != "*") return null
        if (t.grantedScope != requiredScope && t.grantedScope != "*") return null
        if (t.singleUse) {
            t.consumed = true
            store?.saveToken(t)
        }
        return t
    }

    fun peek(tokenId: String) = tokens[tokenId] ?: store?.loadToken(tokenId)

    fun recoverToken(id: String): ApprovalToken? {
        val t = store?.loadToken(id) ?: return null
        tokens[t.id] = t
        return t
    }

    fun activeCount(): Int =
        tokens.values.count { !it.revoked && !it.consumed && it.expiresAtEpochMs >= clockMs() }

    fun expireSweep(): Int {
        val now = clockMs()
        var n = 0
        tokens.values.filter { it.expiresAtEpochMs < now && !it.consumed }.forEach {
            it.revoked = true
            n++
        }
        return n
    }
}

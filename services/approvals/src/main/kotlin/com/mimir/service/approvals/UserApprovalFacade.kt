package com.mimir.service.approvals

import com.mimir.kernel.security.ApprovalService
import com.mimir.kernel.security.ApprovalToken

/**
 * Application-facing approval UX facade (Blueprint Application Services).
 * Delegates issuance to Kernel [ApprovalService] — never invents approvalGranted flags.
 */
class UserApprovalFacade(
    private val kernel: ApprovalService = ApprovalService()
) {
    data class Pending(
        val actionId: String,
        val targetResource: String,
        val scope: String,
        val reason: String,
        val principalId: String = "local-user"
    )

    private val pending = linkedMapOf<String, Pending>()
    private var seq = 0L

    fun request(actionId: String, targetResource: String, scope: String, reason: String): String {
        val id = "req-${++seq}"
        pending[id] = Pending(actionId, targetResource, scope, reason)
        return id
    }

    fun grant(requestId: String, ttlMs: Long = 300_000): ApprovalToken? {
        val p = pending.remove(requestId) ?: return null
        return kernel.issue(
            principalId = p.principalId,
            actionId = p.actionId,
            targetResource = p.targetResource,
            grantedScope = p.scope,
            ttlMs = ttlMs
        )
    }

    fun deny(requestId: String): Boolean = pending.remove(requestId) != null
}

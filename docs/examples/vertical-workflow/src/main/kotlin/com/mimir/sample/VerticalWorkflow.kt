package com.mimir.sample

import com.mimir.kernel.security.ApprovalService
import com.mimir.service.activity.InMemoryActivityLog
import com.mimir.service.approvals.UserApprovalFacade
import com.mimir.platform.memory.InMemoryMemoryService

/**
 * Blueprint roadmap item 8: thin vertical path across UI intent → services → kernel.
 * Not a full product feature — demonstrates wiring without engine bypass.
 */
class VerticalWorkflow(
    private val approvals: UserApprovalFacade = UserApprovalFacade(ApprovalService()),
    private val activity: InMemoryActivityLog = InMemoryActivityLog(),
    private val memory: InMemoryMemoryService = InMemoryMemoryService()
) {
    fun userRequestsBrowserNavigate(url: String): String {
        val reqId = approvals.request(
            actionId = "browser.navigate",
            targetResource = url,
            scope = "browser.nav",
            reason = "User requested navigation"
        )
        activity.record("approval.requested", "navigate $url", url)
        return reqId
    }

    fun userGrants(requestId: String): Boolean {
        val token = approvals.grant(requestId) ?: return false
        activity.record("approval.granted", token.id, token.targetResource)
        memory.capture("Approved ${token.actionId} on ${token.targetResource}", setOf("approval"))
        return true
    }
}

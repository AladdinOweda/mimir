package com.mimir.engine.provider.policy

import com.mimir.engine.provider.contract.ProviderLifecycle
import com.mimir.engine.provider.contract.ProviderRecord
import com.mimir.engine.provider.contract.TrustStatus

data class PolicyVerdict(
    val ok: Boolean,
    val code: String,
    val message: String,
)

/**
 * Hard gates before Approve/Enable — independent of vendor marketing claims.
 */
object ProviderPolicy {
    fun mayApprove(record: ProviderRecord): PolicyVerdict {
        if (record.lifecycle != ProviderLifecycle.TechnicallyVerified &&
            record.lifecycle != ProviderLifecycle.Approved
        ) {
            return PolicyVerdict(false, "lifecycle", "need_TechnicallyVerified")
        }
        if (record.adapterId.isNullOrBlank()) {
            return PolicyVerdict(false, "adapter", "adapter_required")
        }
        if (record.source.officialUrl.isNullOrBlank()) {
            return PolicyVerdict(false, "source", "official_url_required")
        }
        if (record.trust == TrustStatus.Rejected) {
            return PolicyVerdict(false, "trust", "rejected")
        }
        return PolicyVerdict(true, "ok", "may_approve")
    }

    fun mayEnable(record: ProviderRecord): PolicyVerdict {
        if (record.lifecycle != ProviderLifecycle.Approved && record.lifecycle != ProviderLifecycle.Enabled) {
            return PolicyVerdict(false, "lifecycle", "need_Approved")
        }
        if (record.adapterId.isNullOrBlank()) {
            return PolicyVerdict(false, "adapter", "adapter_required")
        }
        return PolicyVerdict(true, "ok", "may_enable")
    }

    fun mayInvoke(record: ProviderRecord, requestingEngineId: String?): PolicyVerdict {
        if (record.lifecycle != ProviderLifecycle.Enabled) {
            return PolicyVerdict(false, "not_enabled", record.lifecycle.name)
        }
        if (requestingEngineId != null &&
            record.enabledForEngines.isNotEmpty() &&
            requestingEngineId !in record.enabledForEngines
        ) {
            return PolicyVerdict(false, "engine_not_allowed", requestingEngineId)
        }
        return PolicyVerdict(true, "ok", "may_invoke")
    }
}

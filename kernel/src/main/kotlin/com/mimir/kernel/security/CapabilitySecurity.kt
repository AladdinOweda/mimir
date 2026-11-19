package com.mimir.kernel.security

import java.util.concurrent.ConcurrentHashMap

enum class RiskLevel { Low, Medium, High, Critical }

data class CapabilityPolicy(
    val capabilityId: String,
    val requiredPermission: String,
    val risk: RiskLevel = RiskLevel.Low,
    val approvalActionId: String? = null,
)

/**
 * Fail-closed capability → permission map. Kernel does not invent policies (G-06).
 */
class CapabilitySecurityRegistry {
    private val policies = ConcurrentHashMap<String, CapabilityPolicy>()

    fun register(policy: CapabilityPolicy) {
        policies[policy.capabilityId] = policy
    }

    fun registerAll(list: Iterable<CapabilityPolicy>) {
        list.forEach { register(it) }
    }

    fun policyFor(capabilityId: String): CapabilityPolicy? = policies[capabilityId]

    fun unregister(capabilityId: String) {
        policies.remove(capabilityId)
    }

    fun listPolicies(): List<CapabilityPolicy> = policies.values.sortedBy { it.capabilityId }

    fun size(): Int = policies.size

    fun clear() {
        policies.clear()
    }
}

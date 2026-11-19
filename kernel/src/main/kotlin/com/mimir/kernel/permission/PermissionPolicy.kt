package com.mimir.kernel.permission

import com.mimir.kernel.persist.JdbcKernelStore

data class PermissionContext(
    val principalId: String,
    val workspaceId: String? = null,
    val roles: Set<String> = emptySet(),
)

/**
 * Principal → permission set. Fail-closed: missing grant = deny.
 */
class PermissionPolicy(
    private val store: JdbcKernelStore? = null,
) {
    private val grants = mutableMapOf<String, MutableSet<String>>()
    private val explicitDenies = mutableMapOf<String, MutableSet<String>>()

    fun grant(principalId: String, permission: String) {
        grants.getOrPut(principalId) { mutableSetOf() }.add(permission)
        store?.grantPermission(principalId, permission)
    }

    fun grantAll(principalId: String, permissions: List<String>) {
        permissions.forEach { grant(principalId, it) }
    }

    fun revoke(principalId: String, permission: String) {
        grants[principalId]?.remove(permission)
        explicitDenies.getOrPut(principalId) { mutableSetOf() }.remove(permission)
    }

    fun revokeAll(principalId: String) {
        grants.remove(principalId)
        explicitDenies.remove(principalId)
    }

    /** Hard deny overrides grants until cleared. */
    fun deny(principalId: String, permission: String) {
        explicitDenies.getOrPut(principalId) { mutableSetOf() }.add(permission)
    }

    fun clearDeny(principalId: String, permission: String) {
        explicitDenies[principalId]?.remove(permission)
    }

    fun check(principalId: String, permission: String): Boolean {
        if (permission in (explicitDenies[principalId] ?: emptySet())) return false
        val set = grants[principalId]
        if (set != null && (permission in set || "*" in set)) return true
        if (store != null) {
            val loaded = store.loadPermissions(principalId)
            if (loaded.isNotEmpty()) {
                grants.getOrPut(principalId) { mutableSetOf() }.addAll(loaded)
                if (permission in (explicitDenies[principalId] ?: emptySet())) return false
                return permission in loaded || "*" in loaded
            }
        }
        return false
    }

    fun listGrants(principalId: String): Set<String> {
        recoverPrincipal(principalId)
        return grants[principalId]?.toSet() ?: emptySet()
    }

    fun recoverPrincipal(principalId: String) {
        store?.loadPermissions(principalId)?.let {
            if (it.isNotEmpty()) grants.getOrPut(principalId) { mutableSetOf() }.addAll(it)
        }
    }
}

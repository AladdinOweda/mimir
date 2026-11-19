package com.mimir.engine.process

/**
 * R6 — lane ownership: longest-prefix path wins. Prevents cross-lane claim without transfer.
 */
class OwnershipRegistry {
    private val lanes = linkedMapOf<String, String>() // pathPrefix -> ownerAgent

    fun claimLane(pathPrefix: String, agentId: String): Boolean {
        synchronized(this) {
            val conflict = lanes.entries.firstOrNull { (p, owner) ->
                owner != agentId && (pathPrefix.startsWith(p) || p.startsWith(pathPrefix))
            }
            if (conflict != null) return false
            lanes[pathPrefix] = agentId
            return true
        }
    }

    fun ownerOf(path: String): String? {
        synchronized(this) {
            return lanes.entries
                .filter { path.startsWith(it.key) }
                .maxByOrNull { it.key.length }
                ?.value
        }
    }

    fun release(pathPrefix: String, agentId: String): Boolean {
        synchronized(this) {
            if (lanes[pathPrefix] != agentId) return false
            lanes.remove(pathPrefix)
            return true
        }
    }

    fun snapshot() = synchronized(this) { lanes.toMap() }
}

package com.mimir.engine.forge.ownership

class PathLaneRegistry {
    private val rules = linkedMapOf<String, String>()

    fun setLane(pathPrefix: String, lane: String) {
        rules[pathPrefix.trimEnd('/')] = lane
    }

    fun ownerOf(path: String): String {
        val normalized = path.replace('\\', '/')
        val match = rules.keys
            .filter { normalized == it || normalized.startsWith("$it/") }
            .maxByOrNull { it.length }
        return match?.let { rules[it] } ?: "coordinator"
    }

    fun allows(path: String, lane: String): Boolean {
        val owner = ownerOf(path)
        return owner == lane || (owner == "coordinator" && lane == "coordinator")
    }

    fun size(): Int = rules.size
    fun prefixes(): List<String> = rules.keys.toList()
}

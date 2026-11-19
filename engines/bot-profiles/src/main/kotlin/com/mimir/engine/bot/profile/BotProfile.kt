package com.mimir.engine.bot.profile

data class BotProfile(
    val id: String,
    val version: String = "1.0",
    val displayName: String,
    val persona: String = "",
    val instructions: String = "",
    val modelRef: String = "",
    val workspaceId: String = "",
    val memoryScope: String = "bot", // bot | project | platform | none
    val permissionProfile: String = "default",
    val maxSteps: Int = 50,
    val maxCostUnits: Double = 100.0,
    val maxRuntimeMs: Long = 600_000,
    val computerUse: String = "off", // off | read_only | workspace | full_requires_approval
    val enabled: Boolean = true,
)

object BotProfileCapabilities {
    const val PROFILE_CREATE = "bot.profile.create"
    const val PROFILE_GET = "bot.profile.get"
    const val PROFILE_LIST = "bot.profile.list"
    const val PROFILE_UPDATE = "bot.profile.update"
    const val PROFILE_DELETE = "bot.profile.delete"
    const val MEMORY_PUT = "bot.memory.put"
    const val MEMORY_GET = "bot.memory.get"
    const val MEMORY_ISOLATION_CHECK = "bot.memory.isolation.check"
    const val METRICS = "bot.metrics"
    val ALL = listOf(
        PROFILE_CREATE, PROFILE_GET, PROFILE_LIST, PROFILE_UPDATE, PROFILE_DELETE,
        MEMORY_PUT, MEMORY_GET, MEMORY_ISOLATION_CHECK,
    )
}

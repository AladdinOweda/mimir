package com.mimir.contracts.bot

object BotCapabilityIds {
    const val PROFILE_CREATE = "bot.profile.create"
    const val PROFILE_GET = "bot.profile.get"
    const val PROFILE_LIST = "bot.profile.list"
    const val PROFILE_UPDATE = "bot.profile.update"
    const val PROFILE_DELETE = "bot.profile.delete"
    const val MEMORY_PUT = "bot.memory.put"
    const val MEMORY_GET = "bot.memory.get"
    const val MEMORY_ISOLATION_CHECK = "bot.memory.isolation.check"
    val ALL = listOf(
        PROFILE_CREATE, PROFILE_GET, PROFILE_LIST, PROFILE_UPDATE, PROFILE_DELETE,
        MEMORY_PUT, MEMORY_GET, MEMORY_ISOLATION_CHECK,
    )
}

data class BotId(val value: String)

interface BotPort {
    fun createProfile(id: String, displayName: String): BotId
    fun memoryPut(botId: BotId, key: String, value: String): Boolean
    fun memoryGet(botId: BotId, requesterBotId: BotId, key: String): String?
}

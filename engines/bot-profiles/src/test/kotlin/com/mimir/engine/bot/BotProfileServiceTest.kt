package com.mimir.engine.bot

import com.mimir.engine.bot.profile.BotProfileCapabilities
import com.mimir.engine.bot.profile.BotProfileService
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BotProfileServiceTest {
    @Test
    fun create_and_memory_isolation() = runTest {
        val e = BotProfileService().also { it.start() }
        e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.PROFILE_CREATE)),
                mapOf("id" to "bot-a", "displayName" to "A"),
            ),
        )
        e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.PROFILE_CREATE)),
                mapOf("id" to "bot-b", "displayName" to "B"),
            ),
        )
        e.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.MEMORY_PUT)),
                mapOf("botId" to "bot-a", "key" to "secret", "value" to "aaa", "requesterBotId" to "bot-a"),
            ),
        )
        val denied = e.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.MEMORY_GET)),
                mapOf("botId" to "bot-a", "key" to "secret", "requesterBotId" to "bot-b"),
            ),
        )
        assertTrue(denied is InvocationResult.Err, denied.toString())
        val allowed = e.invoke(
            InvocationRequest(
                "5", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.MEMORY_GET)),
                mapOf("botId" to "bot-a", "key" to "secret", "requesterBotId" to "bot-a"),
            ),
        )
        assertTrue(allowed is InvocationResult.Ok, allowed.toString())
    }
}

package com.mimir.kernel.host

import com.mimir.contracts.capability.DefaultCapabilityPolicies
import com.mimir.engine.bot.profile.BotProfileCapabilities
import com.mimir.engine.bot.profile.BotProfileService
import com.mimir.engine.knowledge.KnowledgeWorkspaceCapabilities
import com.mimir.engine.knowledge.KnowledgeWorkspaceEngine
import com.mimir.engine.spe.SpeCapabilities
import com.mimir.engine.spe.SpeEngine
import com.mimir.kernel.contract.*
import com.mimir.kernel.permission.PermissionContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Simulates ProductContext.startAll for bot + knowledge + spe:
 * seed policies → attach → registerAndStart all → grant → invoke via host.
 */
class KernelHostStartAllPathTest {
    @Test
    fun attach_start_all_bot_knowledge_spe() = runTest {
        val host = KernelHost()
        DefaultCapabilityPolicies.seed(host.capabilitySecurity)
        val bot = BotProfileService()
        val knowledge = KnowledgeWorkspaceEngine()
        val spe = SpeEngine()
        val engines = listOf(bot, knowledge, spe)
        val manifests = engines.map { it.describe() }
        engines.zip(manifests).forEach { (e, m) -> host.attach(e, m) }
        val states = manifests.map { host.registerAndStart(it.id) }
        assertTrue(states.all { it == EngineState.Running }, states.toString())
        host.permissions.grantAll(
            "tester",
            listOf(
                "bot.admin", "bot.read",
                "knowledge.write", "knowledge.read",
                "spe.write", "spe.read",
            ),
        )
        val principal = PermissionContext("tester")
        val create = host.invoke(
            InvocationRequest(
                "1", "tester", CapabilityRef(CapabilityId(BotProfileCapabilities.PROFILE_CREATE)),
                mapOf("id" to "builder", "displayName" to "Builder"),
            ),
            principal,
        )
        assertTrue(create is InvocationResult.Ok, create.toString())
        val idx = host.invoke(
            InvocationRequest(
                "2", "tester", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.INDEX)),
                mapOf("title" to "spec", "body" to "startAll path"),
            ),
            principal,
        )
        assertTrue(idx is InvocationResult.Ok, idx.toString())
        val spec = host.invoke(
            InvocationRequest(
                "3", "tester", CapabilityRef(CapabilityId(SpeCapabilities.SPEC_CREATE)),
                mapOf("title" to "T", "body" to "B"),
            ),
            principal,
        )
        assertTrue(spec is InvocationResult.Ok, spec.toString())
    }
}

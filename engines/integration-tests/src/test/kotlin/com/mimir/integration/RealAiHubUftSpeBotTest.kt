package com.mimir.integration

import com.mimir.engine.aihub.AiHubCapabilities
import com.mimir.engine.aihub.AiHubEngine
import com.mimir.engine.bot.profile.BotProfileCapabilities
import com.mimir.engine.bot.profile.BotProfileService
import com.mimir.engine.spe.SpeCapabilities
import com.mimir.engine.spe.SpeEngine
import com.mimir.engine.uft.UftCapabilities
import com.mimir.engine.uft.UnifiedFileTransferEngine
import com.mimir.kernel.contract.*
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealAiHubUftSpeBotTest {

    @Test
    fun aihub_register_list_select_route() = runTest {
        val eng = AiHubEngine()
        eng.start()
        val reg = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(AiHubCapabilities.REGISTER_ENDPOINT)),
                mapOf("id" to "local1", "kind" to "local", "modelRef" to "noop-v1", "priority" to "10"),
            ),
        )
        assertTrue(reg is InvocationResult.Ok, reg.toString())
        eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(AiHubCapabilities.REGISTER_ENDPOINT)),
                mapOf("id" to "cloud1", "kind" to "cloud", "modelRef" to "gpt-x", "priority" to "50"),
            ),
        )
        val list = eng.invoke(
            InvocationRequest("3", "j", CapabilityRef(CapabilityId(AiHubCapabilities.LIST_ENDPOINTS)), emptyMap()),
        )
        assertTrue(list is InvocationResult.Ok)
        assertTrue((list as InvocationResult.Ok).output["count"]!!.toInt() >= 2)
        val sel = eng.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(AiHubCapabilities.SELECT)),
                mapOf("id" to "local1"),
            ),
        )
        assertTrue(sel is InvocationResult.Ok, sel.toString())
        val route = eng.invoke(
            InvocationRequest(
                "5", "j", CapabilityRef(CapabilityId(AiHubCapabilities.ROUTE)),
                mapOf("kind" to "local"),
            ),
        )
        assertTrue(route is InvocationResult.Ok, route.toString())
    }

    @Test
    fun uft_create_progress_complete_list() = runTest {
        val eng = UnifiedFileTransferEngine()
        eng.start()
        val create = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_CREATE)),
                mapOf(
                    "direction" to "Copy",
                    "source" to "/tmp/a.bin",
                    "destination" to "/tmp/b.bin",
                    "bytesTotal" to "1000",
                    "resumable" to "true",
                ),
            ),
        )
        assertTrue(create is InvocationResult.Ok, create.toString())
        val id = (create as InvocationResult.Ok).output["id"]!!
        val prog = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_PROGRESS)),
                mapOf("id" to id, "bytesDone" to "500", "progress" to "50"),
            ),
        )
        assertTrue(prog is InvocationResult.Ok, prog.toString())
        val done = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_COMPLETE)),
                mapOf("id" to id),
            ),
        )
        assertTrue(done is InvocationResult.Ok, done.toString())
        assertEquals("Completed", (done as InvocationResult.Ok).output["status"])
        val list = eng.invoke(
            InvocationRequest("4", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_LIST)), emptyMap()),
        )
        assertTrue(list is InvocationResult.Ok, list.toString())
    }

    @Test
    fun spe_spec_plan_tasks_converge() = runTest {
        val eng = SpeEngine()
        eng.start()
        val spec = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(SpeCapabilities.SPEC_CREATE)),
                mapOf("title" to "Login", "body" to "user can login"),
            ),
        )
        assertTrue(spec is InvocationResult.Ok, spec.toString())
        val sid = (spec as InvocationResult.Ok).output["specId"]!!
        val plan = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(SpeCapabilities.PLAN_CREATE)),
                mapOf("specId" to sid),
            ),
        )
        assertTrue(plan is InvocationResult.Ok, plan.toString())
        val t1 = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(SpeCapabilities.TASK_ADD)),
                mapOf("specId" to sid, "title" to "write test"),
            ),
        )
        assertTrue(t1 is InvocationResult.Ok, t1.toString())
        val tid = (t1 as InvocationResult.Ok).output["taskId"]!!
        eng.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(SpeCapabilities.TASK_COMPLETE)),
                mapOf("taskId" to tid),
            ),
        )
        val conv = eng.invoke(
            InvocationRequest(
                "5", "j", CapabilityRef(CapabilityId(SpeCapabilities.CONVERGE)),
                mapOf("specId" to sid),
            ),
        )
        assertTrue(conv is InvocationResult.Ok, conv.toString())
        assertEquals("true", (conv as InvocationResult.Ok).output["converged"])
    }

    @Test
    fun bot_profile_memory_isolation() = runTest {
        val eng = BotProfileService()
        eng.start()
        val c1 = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.PROFILE_CREATE)),
                mapOf("id" to "botA", "displayName" to "A", "persona" to "coder"),
            ),
        )
        assertTrue(c1 is InvocationResult.Ok, c1.toString())
        eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.PROFILE_CREATE)),
                mapOf("id" to "botB", "displayName" to "B"),
            ),
        )
        val put = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.MEMORY_PUT)),
                mapOf("botId" to "botA", "key" to "secret", "value" to "alpha"),
            ),
        )
        assertTrue(put is InvocationResult.Ok, put.toString())
        val leak = eng.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.MEMORY_GET)),
                mapOf("botId" to "botA", "requesterBotId" to "botB", "key" to "secret"),
            ),
        )
        // isolation: botB should not read botA memory
        assertTrue(leak is InvocationResult.Err || (leak is InvocationResult.Ok && (leak as InvocationResult.Ok).output["value"].isNullOrEmpty()), leak.toString())
        val own = eng.invoke(
            InvocationRequest(
                "5", "j", CapabilityRef(CapabilityId(BotProfileCapabilities.MEMORY_GET)),
                mapOf("botId" to "botA", "requesterBotId" to "botA", "key" to "secret"),
            ),
        )
        assertTrue(own is InvocationResult.Ok, own.toString())
        assertEquals("alpha", (own as InvocationResult.Ok).output["value"])
    }

    @Test
    fun kernel_aihub_list_policy() = runTest {
        val host = KernelHost()
        val eng = AiHubEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("*"))
        host.capabilitySecurity.register(
            CapabilityPolicy(AiHubCapabilities.LIST_ENDPOINTS, "ai.hub.read", RiskLevel.Low),
        )
        host.permissions.grantAll("u", listOf("ai.hub.read"))
        val r = host.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(AiHubCapabilities.LIST_ENDPOINTS)), emptyMap()),
            PermissionContext("u"),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
    }
}

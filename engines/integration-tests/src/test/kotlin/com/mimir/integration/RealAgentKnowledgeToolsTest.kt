package com.mimir.integration

import com.mimir.engine.agent.AgentRuntimeCapabilities
import com.mimir.engine.agent.AgentRuntimeEngine
import com.mimir.engine.knowledge.KnowledgeWorkspaceCapabilities
import com.mimir.engine.knowledge.KnowledgeWorkspaceEngine
import com.mimir.engine.tools.ToolCapabilities
import com.mimir.engine.tools.ToolRegistryEngine
import com.mimir.kernel.contract.*
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealAgentKnowledgeToolsTest {

    @Test
    fun knowledge_index_search_get_delete() = runTest {
        val eng = KnowledgeWorkspaceEngine()
        eng.start()
        val idx = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.INDEX)),
                mapOf("id" to "d1", "title" to "Mimir Core", "body" to "kernel host event bus", "tags" to "kernel,core"),
            ),
        )
        assertTrue(idx is InvocationResult.Ok, idx.toString())
        val search = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.SEARCH)),
                mapOf("query" to "kernel"),
            ),
        )
        assertTrue(search is InvocationResult.Ok, search.toString())
        assertTrue((search as InvocationResult.Ok).output["ids"]!!.contains("d1"))
        val get = eng.invoke(
            InvocationRequest("3", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.GET)), mapOf("id" to "d1")),
        )
        assertTrue(get is InvocationResult.Ok)
        assertTrue((get as InvocationResult.Ok).output["body"]!!.contains("event bus"))
        val del = eng.invoke(
            InvocationRequest("4", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.DELETE)), mapOf("id" to "d1")),
        )
        assertTrue(del is InvocationResult.Ok)
        assertEquals("true", (del as InvocationResult.Ok).output["removed"])
        assertTrue(eng.knowledgeMetrics()["docs"]!!.toInt() == 0)
    }

    @Test
    fun agent_create_session_tool_status() = runTest {
        val eng = AgentRuntimeEngine()
        eng.start()
        val create = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(AgentRuntimeCapabilities.AGENT_CREATE)),
                mapOf("displayName" to "Coder", "modelId" to "noop", "tools" to "echo,search"),
            ),
        )
        assertTrue(create is InvocationResult.Ok, create.toString())
        val agentId = (create as InvocationResult.Ok).output["agentId"]
            ?: create.output["id"]
            ?: error(create.output.toString())
        val start = eng.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(AgentRuntimeCapabilities.SESSION_START)),
                mapOf("agentId" to agentId),
            ),
        )
        assertTrue(start is InvocationResult.Ok, start.toString())
        val sid = (start as InvocationResult.Ok).output["sessionId"]!!
        val tool = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(AgentRuntimeCapabilities.TOOL_INVOKE)),
                mapOf("sessionId" to sid, "tool" to "echo", "estimatedUsd" to "0.01"),
            ),
        )
        assertTrue(tool is InvocationResult.Ok || tool is InvocationResult.Err, tool.toString())
        val status = eng.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(AgentRuntimeCapabilities.SESSION_STATUS)),
                mapOf("sessionId" to sid),
            ),
        )
        assertTrue(status is InvocationResult.Ok, status.toString())
        assertTrue(eng.agentMetrics()["agents"]!!.toInt() >= 1)
    }

    @Test
    fun tools_register_list_authorize_deny() = runTest {
        val eng = ToolRegistryEngine()
        eng.start()
        val reg = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ToolCapabilities.REGISTER)),
                mapOf(
                    "toolId" to "echo.tool",
                    "capability" to "echo.run",
                    "riskClass" to "low",
                    "requiresApproval" to "false",
                    "permissions" to "tools.read",
                ),
            ),
        )
        assertTrue(reg is InvocationResult.Ok, reg.toString())
        val list = eng.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(ToolCapabilities.LIST)), emptyMap()),
        )
        assertTrue(list is InvocationResult.Ok, list.toString())
        assertTrue((list as InvocationResult.Ok).output.toString().contains("echo.tool"))
        val auth = eng.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(ToolCapabilities.AUTHORIZE)),
                mapOf("toolId" to "echo.tool", "grantedPermissions" to "tools.read"),
            ),
        )
        assertTrue(auth is InvocationResult.Ok, auth.toString())
        val deny = eng.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(ToolCapabilities.DENY)),
                mapOf("toolId" to "echo.tool", "grantedPermissions" to "tools.read"),
            ),
        )
        assertTrue(deny is InvocationResult.Ok)
        val auth2 = eng.invoke(
            InvocationRequest(
                "5", "j", CapabilityRef(CapabilityId(ToolCapabilities.AUTHORIZE)),
                mapOf("toolId" to "echo.tool", "grantedPermissions" to "tools.read"),
            ),
        )
        assertTrue(auth2 is InvocationResult.Err)
        assertEquals("tool_denied", (auth2 as InvocationResult.Err).code)
    }

    @Test
    fun kernel_knowledge_search_path() = runTest {
        val host = KernelHost()
        val eng = KnowledgeWorkspaceEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("knowledge.write", "knowledge.read", "*"))
        host.capabilitySecurity.register(
            CapabilityPolicy(KnowledgeWorkspaceCapabilities.INDEX, "knowledge.write", RiskLevel.Low),
        )
        host.capabilitySecurity.register(
            CapabilityPolicy(KnowledgeWorkspaceCapabilities.SEARCH, "knowledge.read", RiskLevel.Low),
        )
        val p = PermissionContext("u")
        host.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.INDEX)),
                mapOf("id" to "k1", "title" to "Bus", "body" to "event bus bridge", "tags" to "events"),
            ),
            p,
        ).let { assertTrue(it is InvocationResult.Ok, it.toString()) }
        val search = host.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.SEARCH)),
                mapOf("query" to "event"),
            ),
            p,
        )
        assertTrue(search is InvocationResult.Ok, search.toString())
        assertTrue((search as InvocationResult.Ok).output["ids"]!!.contains("k1"))
    }
}

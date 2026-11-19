package com.mimir.engine.knowledge

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KnowledgeWorkspaceEngineTest {
    @Test
    fun index_search_tag_delete() = runTest {
        val e = KnowledgeWorkspaceEngine().also { it.start() }
        e.invoke(InvocationRequest("1", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.INDEX)),
            mapOf("id" to "d1", "title" to "Kotlin Guide", "body" to "coroutines and flow", "tags" to "kotlin")))
        e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.INDEX)),
            mapOf("id" to "d2", "title" to "Other", "body" to "unrelated")))
        val hits = e.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.SEARCH)),
            mapOf("query" to "coroutines"))) as InvocationResult.Ok
        assertEquals("1", hits.output["count"])
        e.invoke(InvocationRequest("4", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.TAG)),
            mapOf("id" to "d1", "tag" to "kmp")))
        val byTag = e.invoke(InvocationRequest("5", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.SEARCH)),
            mapOf("query" to "kmp"))) as InvocationResult.Ok
        assertEquals("1", byTag.output["count"])
        e.invoke(InvocationRequest("6", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.DELETE)), mapOf("id" to "d1")))
        val after = e.invoke(InvocationRequest("7", "j", CapabilityRef(CapabilityId(KnowledgeWorkspaceCapabilities.SEARCH)),
            mapOf("query" to "kotlin"))) as InvocationResult.Ok
        assertEquals("0", after.output["count"])
    }
}

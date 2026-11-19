package com.mimir.contracts.knowledge

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryKnowledgeFacadeTest {
    @Test
    fun index_and_search() {
        val k = InMemoryKnowledgeFacade()
        k.index("1", "Kotlin Multiplatform", "Mimir kernel is vendor neutral")
        k.index("2", "Other", "unrelated")
        val hits = k.search("kernel")
        assertEquals(1, hits.size)
        assertTrue(hits.first().snippet.contains("vendor"))
    }
}

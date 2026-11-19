package com.mimir.platform.memory

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MemoryServiceMinTest {
    @Test
    fun get_missing_returns_null() {
        assertNull(InMemoryMemoryService().get(MemoryId("nope")))
    }
    @Test
    fun delete_missing_returns_false() {
        assertFalse(InMemoryMemoryService().delete(MemoryId("nope")))
    }
    @Test
    fun capture_and_search_roundtrip() {
        val s = InMemoryMemoryService()
        val id = s.capture("hello world", setOf("t1"))
        assertTrue(s.search("hello").any { it.id == id })
        assertTrue(s.delete(id))
        assertNull(s.get(id))
    }
}

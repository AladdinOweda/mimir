package com.mimir.service.activity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivityLogMinTest {
    @Test
    fun recent_empty_is_empty() {
        assertTrue(InMemoryActivityLog().recent().isEmpty())
    }

    @Test
    fun record_appears_in_recent() {
        val log = InMemoryActivityLog()
        log.record("nav", "opened")
        assertEquals(1, log.recent(10).size)
    }
}

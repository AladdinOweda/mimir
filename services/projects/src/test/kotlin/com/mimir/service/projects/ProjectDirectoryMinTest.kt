package com.mimir.service.projects

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProjectDirectoryMinTest {
    @Test
    fun get_missing_returns_null() {
        assertNull(InMemoryProjectDirectory().get(ProjectId("x")))
    }

    @Test
    fun list_starts_empty() {
        assertTrue(InMemoryProjectDirectory().list().isEmpty())
    }
}

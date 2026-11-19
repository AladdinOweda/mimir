package com.mimir.service.portability

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DataPortabilityMinTest {
    @Test
    fun status_missing_returns_null() {
        assertNull(InMemoryDataPortability().status(PortabilityJobId("nope")))
    }

    @Test
    fun export_creates_pending_job() {
        val p = InMemoryDataPortability()
        val job = p.requestExport("chat")
        assertNotNull(p.status(job.id))
        assertTrue(job.kind.startsWith("export"))
    }
}

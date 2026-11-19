package com.mimir.contracts.process

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProcessContractTest {
    @Test
    fun capability_ids_stable() {
        assertEquals("process.exec.argv", ProcessCapabilityIds.EXEC_ARGV)
        assertEquals("process.exec.shell", ProcessCapabilityIds.EXEC_SHELL)
        assertTrue(ProcessCapabilityIds.ALL.size == 2)
    }
}

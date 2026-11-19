package com.mimir.contracts.terminal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TerminalContractTest {
    @Test
    fun fine_grained_ids_stable() {
        assertEquals("terminal.session.open", TerminalCapabilityIds.SESSION_OPEN)
        assertTrue(TerminalCapabilityIds.ALL_FINE.contains(TerminalCapabilityIds.COMMAND_EXECUTE))
    }
}

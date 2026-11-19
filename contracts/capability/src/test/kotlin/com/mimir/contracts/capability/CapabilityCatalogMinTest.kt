package com.mimir.contracts.capability

import com.mimir.kernel.security.CapabilitySecurityRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CapabilityCatalogMinTest {
    @Test
    fun catalog_ids_are_non_blank_and_stable() {
        assertEquals("browser.tab.open", CapabilityCatalog.BROWSER_TAB_OPEN)
        assertTrue(CapabilityCatalog.TERMINAL_EXEC.contains('.'))
    }

    @Test
    fun seed_registers_policies_on_empty_registry() {
        val reg = CapabilitySecurityRegistry()
        DefaultCapabilityPolicies.seed(reg)
        assertNotNull(reg.policyFor("browser.tab.open"))
        assertNotNull(reg.policyFor("terminal.command.execute"))
    }
}

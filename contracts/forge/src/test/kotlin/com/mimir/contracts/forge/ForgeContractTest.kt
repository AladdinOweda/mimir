package com.mimir.contracts.forge

import kotlin.test.Test
import kotlin.test.assertEquals

class ForgeContractTest {
    @Test
    fun capability_ids_match_catalog_style() {
        assertEquals("forge.build", ForgeCapabilityIds.BUILD)
        assertEquals("forge.test", ForgeCapabilityIds.TEST)
        assertEquals(7, ForgeBoardColumn.entries.size)
    }
}

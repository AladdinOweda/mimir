package com.mimir.contracts.vault

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VaultContractTest {
    @Test
    fun capability_ids_stable() {
        assertEquals("vault.put", VaultCapabilityIds.PUT)
        assertTrue(VaultCapabilityIds.ALL.contains(VaultCapabilityIds.LIST))
    }
}

package com.mimir.engine.vault

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VaultEngineTest {
    @Test
    fun put_get_wrong_passphrase_fails() = runTest {
        val e = VaultEngine().also { it.start() }
        val put = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(VaultCapabilities.PUT)),
                mapOf("key" to "k1", "value" to "secret-data", "passphrase" to "correct-horse"),
            ),
        )
        assertTrue(put is InvocationResult.Ok, put.toString())
        val bad = e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(VaultCapabilities.GET)),
                mapOf("key" to "k1", "passphrase" to "wrong"),
            ),
        )
        assertTrue(bad is InvocationResult.Err)
        val ok = e.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(VaultCapabilities.GET)),
                mapOf("key" to "k1", "passphrase" to "correct-horse"),
            ),
        ) as InvocationResult.Ok
        assertEquals("secret-data", ok.output["value"])
    }
}

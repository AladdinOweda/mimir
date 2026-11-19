package com.mimir.platform.secrets

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SecretsVaultMinTest {
    @Test
    fun get_missing_returns_null() {
        assertNull(LocalAesSecretsVault().get(SecretId("missing")))
    }
    @Test
    fun delete_missing_returns_false() {
        assertFalse(LocalAesSecretsVault().delete(SecretId("missing")))
    }
    @Test
    fun put_get_roundtrip() {
        val v = LocalAesSecretsVault()
        val id = v.put("token", "secret-value".toByteArray())
        assertContentEquals("secret-value".toByteArray(), v.get(id))
        assertTrue(v.listNames().contains("token"))
        assertTrue(v.delete(id))
        assertNull(v.get(id))
    }
}

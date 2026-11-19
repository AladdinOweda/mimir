package com.mimir.service.retention

import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultRetentionPolicyTest {
    @Test
    fun vault_is_legal_hold() {
        val p = DefaultRetentionPolicy()
        assertEquals(RetentionClass.LegalHold, p.classify("vault"))
        assertEquals(RetentionClass.Ephemeral, p.classify("download"))
    }
}

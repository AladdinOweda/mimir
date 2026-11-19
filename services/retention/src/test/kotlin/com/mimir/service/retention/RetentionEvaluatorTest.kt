package com.mimir.service.retention

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RetentionEvaluatorTest {
    @Test
    fun ephemeral_over_age_actionable_vault_held() {
        val ev = RetentionEvaluator()
        val dl = ev.evaluate("download", "d1", 45)
        assertTrue(dl.actionable)
        assertEquals(RetentionClass.Ephemeral, dl.clazz)
        val vault = ev.evaluate("vault", "v1", 9999)
        assertFalse(vault.actionable)
        assertEquals(RetentionClass.LegalHold, vault.clazz)
        val plan = ev.plan(
            listOf(
                Triple("download", "d1", 45),
                Triple("activity", "a1", 10),
                Triple("vault", "v1", 100),
                Triple("knowledge", "k1", 500),
            ),
        )
        assertTrue(plan["actionable"]!!.toInt() >= 1)
        assertTrue(plan["held"]!!.toInt() >= 2)
    }
}

package com.mimir.kernel

import com.mimir.kernel.permission.PermissionPolicy
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PermissionPolicyTest {
    @Test
    fun grantAndCheck() {
        val p = PermissionPolicy()
        assertFalse(p.check("u", "a"))
        p.grantAll("u", listOf("a", "b"))
        assertTrue(p.check("u", "a"))
        assertFalse(p.check("u", "c"))
        p.grantAll("u", listOf("*"))
        assertTrue(p.check("u", "anything"))
    }
}

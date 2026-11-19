package com.mimir.platform.configuration

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppConfigMinTest {
    @Test
    fun defaults_are_stable() {
        val c = AppConfigLoader.defaults()
        assertTrue(c.dataDir.isNotBlank())
        assertEquals("en", c.locale)
    }
    @Test
    fun fromMap_parses_bools() {
        val c = AppConfigLoader.fromMap(mapOf("locale" to "ar", "durableStores" to "false"))
        assertEquals("ar", c.locale)
        assertEquals(false, c.durableStores)
    }
}

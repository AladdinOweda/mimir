package com.mimir.contracts.browser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrowserContractTest {
    @Test
    fun ids_align_with_product_catalog_names() {
        assertEquals("browser.tab.open", BrowserCapabilityIds.TAB_OPEN)
        assertEquals("browser.navigate", BrowserCapabilityIds.NAVIGATE)
        assertTrue(BrowserCapabilityIds.ALL.contains(BrowserCapabilityIds.CLICK))
    }
}

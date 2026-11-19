package com.mimir.engine.browser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrowserPortDeepTest {
    @Test
    fun port_open_list_close() {
        val e = BrowserEngine(driverKind = "noop")
        val tab = e.openTab("about:blank")
        assertTrue(tab.id.value.startsWith("tab-"))
        assertEquals(1, e.listTabs().size)
        val snap = e.snapshot(tab.id)
        assertEquals(tab.id, snap.tabId)
        assertTrue(e.closeTab(tab.id))
        assertEquals(0, e.listTabs().size)
    }
}

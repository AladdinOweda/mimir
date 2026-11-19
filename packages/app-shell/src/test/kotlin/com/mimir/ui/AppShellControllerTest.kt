package com.mimir.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppShellControllerTest {
    @Test
    fun bottomNavHasFourDestinations() {
        assertEquals(4, AppNavCatalog.bottom.size)
        assertEquals(
            listOf(AppDestination.Home, AppDestination.Projects, AppDestination.Tasks, AppDestination.More),
            AppNavCatalog.bottom.map { it.id },
        )
    }

    @Test
    fun enginesAreDrawerNotBottom() {
        val bottom = AppNavCatalog.bottomIds()
        assertTrue(AppDestination.Chat !in bottom)
        assertTrue(AppDestination.Forge !in bottom)
        assertTrue(AppDestination.Browser !in bottom)
        assertTrue(AppNavCatalog.drawer.any { it.id == AppDestination.Browser })
    }

    @Test
    fun backClosesDrawerBeforeHistory() {
        val c = AppShellController()
        c.openDrawer()
        assertTrue(c.drawerOpen)
        c.back()
        assertEquals(false, c.drawerOpen)
        assertEquals(AppDestination.Home, c.current)
    }

    @Test
    fun navigateToBrowserFromDrawer() {
        val c = AppShellController()
        val nodes = c.navigate(AppDestination.Browser)
        assertTrue(nodes.any { it is UiNode.Screen && it.destination == AppDestination.Browser })
        assertEquals(BottomDestination.More, c.shellBottom())
    }

    private fun AppShellController.shellBottom(): BottomDestination {
        val nav = snapshot().filterIsInstance<UiNode.BottomNav>().first()
        return nav.selected
    }
}

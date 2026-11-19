package com.mimir.ui

/**
 * Shell navigation controller — presentation only.
 * Back priority (spec): overlay → drawer → collapse composer → prior tab → prior screen.
 * Never cancels execution (execution is not owned here).
 */
class AppShellController(
    private val shell: AppShell = AppShell(),
    initial: AppDestination = AppDestination.Home,
    private val widthDp: Int = 400,
) {
    var current: AppDestination = initial
        private set
    var history: List<AppDestination> = listOf(initial)
        private set
    var drawerOpen: Boolean = false
        private set
    var overlayOpen: Boolean = false
        private set

    fun navigate(to: AppDestination): List<UiNode> {
        current = to
        history = history + to
        drawerOpen = false
        return shell.render(to, widthDp)
    }

    fun openDrawer(): List<UiNode> {
        drawerOpen = true
        return snapshot()
    }

    fun closeDrawer(): List<UiNode> {
        drawerOpen = false
        return snapshot()
    }

    fun openOverlay(): List<UiNode> {
        overlayOpen = true
        return snapshot()
    }

    fun closeOverlay(): List<UiNode> {
        overlayOpen = false
        return snapshot()
    }

    /** Spec §6.4 Back priority. */
    fun back(): List<UiNode> {
        if (overlayOpen) {
            overlayOpen = false
            return snapshot()
        }
        if (drawerOpen) {
            drawerOpen = false
            return snapshot()
        }
        if (shell.composer.state == ComposerState.Expanded ||
            shell.composer.state == ComposerState.KeyboardActive
        ) {
            shell.composer.collapse()
            return snapshot()
        }
        if (history.size > 1) {
            history = history.dropLast(1)
            current = history.last()
        }
        return shell.render(current, widthDp)
    }

    fun snapshot(): List<UiNode> = shell.render(current, widthDp) +
        listOfNotNull(
            if (drawerOpen) UiNode.Drawer(shell.visibleDrawer(), current) else null,
        )

    fun composer() = shell.composer
}

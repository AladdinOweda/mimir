package com.mimir.ui.compose

import com.mimir.ui.AppDestination
import com.mimir.ui.AppShellController
import com.mimir.ui.ComposerSnapshot
import com.mimir.ui.UiNode

/**
 * Compose-ready shell projection (MIMIR-UX-SHELL-001).
 *
 * No Compose runtime dependency in this module so `:shared:ui` stays JVM-clean.
 * Apps (androidApp / desktop) bind [ShellUiState] to Compose Multiplatform widgets:
 * NavigationBar, ModalNavigationDrawer, DockedComposer.
 */
data class ShellUiState(
    val current: AppDestination,
    val drawerOpen: Boolean,
    val overlayOpen: Boolean,
    val nodes: List<UiNode>,
    val composer: ComposerSnapshot
)

fun AppShellController.toUiState(): ShellUiState =
    ShellUiState(
        current = current,
        drawerOpen = drawerOpen,
        overlayOpen = overlayOpen,
        nodes = snapshot(),
        composer = composer().snapshot()
    )

sealed class ShellUiEvent {
    data class Navigate(val dest: AppDestination) : ShellUiEvent()
    data object OpenDrawer : ShellUiEvent()
    data object CloseDrawer : ShellUiEvent()
    data class ComposerText(val text: String) : ShellUiEvent()
    data object ComposerExpand : ShellUiEvent()
    data object ComposerCollapse : ShellUiEvent()
    data object ComposerSend : ShellUiEvent()
    data object Back : ShellUiEvent()
}

fun AppShellController.dispatch(event: ShellUiEvent) {
    when (event) {
        is ShellUiEvent.Navigate -> navigate(event.dest)
        ShellUiEvent.OpenDrawer -> openDrawer()
        ShellUiEvent.CloseDrawer -> closeDrawer()
        is ShellUiEvent.ComposerText -> composer().updateText(event.text)
        ShellUiEvent.ComposerExpand -> composer().expand()
        ShellUiEvent.ComposerCollapse -> composer().collapse()
        ShellUiEvent.ComposerSend -> composer().requestSend()
        ShellUiEvent.Back -> back()
    }
}

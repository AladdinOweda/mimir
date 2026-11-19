package com.mimir.app.desktop

import com.mimir.ui.AppDestination
import com.mimir.ui.AppShellController
import com.mimir.ui.compose.ShellUiEvent
import com.mimir.ui.compose.ShellUiState
import com.mimir.ui.compose.dispatch
import com.mimir.ui.compose.toUiState

/**
 * Desktop application bridges Shell controller to UI toolkit.
 * Replace [render] body with Compose Desktop Window { } when CMP is on classpath.
 */
class DesktopShellBridge(private val controller: AppShellController = AppShellController()) {
    fun state(): ShellUiState = controller.toUiState()
    fun onNavigate(dest: AppDestination) = controller.dispatch(ShellUiEvent.Navigate(dest))
    fun onBack() = controller.dispatch(ShellUiEvent.Back)
    /** Placeholder paint — real Compose UI attaches here. */
    fun renderDescription(): String {
        val s = state()
        return "DesktopShell current=${s.current} drawer=${s.drawerOpen} composer=${s.composer.state}"
    }
}

package com.mimir.app.android

import com.mimir.ui.AppShellController
import com.mimir.ui.compose.ShellUiState
import com.mimir.ui.compose.toUiState
import com.mimir.service.tasks.TaskSummary

/**
 * Android-first shell bridge. Host Activity/Fragment observes [state] and applies Material3.
 */
class AndroidShellBridge(private val controller: AppShellController = AppShellController()) {
    fun state(): ShellUiState = controller.toUiState()
    fun controller(): AppShellController = controller
}

    private val forge = ForgeBoardPresenter()

    fun forgeLines(tasks: List<TaskSummary>): List<String> = forge.lines(tasks)
    fun forgeBoard(tasks: List<TaskSummary>) = forge.board(tasks)

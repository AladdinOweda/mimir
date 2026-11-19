package com.mimir.android

/**
 * Android app shell placeholder.
 * Full Compose + product destinations land in UX cycle.
 * Shares kernel contracts via :shared:kernel / :shared:integration.
 */
object AndroidAppContract {
    const val APPLICATION_ID = "com.mimir.app"
    const val VERSION_NAME = "0.5.0"
    val primaryDestinations = listOf(
        "Home", "Chat", "Projects", "Tasks", "Workspace", "Library", "Settings",
    )
}

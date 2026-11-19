package com.mimir.androidapp

/**
 * View model for a simple multi-column Forge list (TextView / RecyclerView).
 * Host Activity can call [com.mimir.app.android.ForgeBoardPresenter] when
 * :apps:android is on the classpath; this type stays free of Android widgets
 * so it can be unit-tested on JVM where the app module is linked.
 */
data class ForgeBoardListModel(
    val columnTitle: String,
    val cardLines: List<String>,
)

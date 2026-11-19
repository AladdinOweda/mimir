package com.mimir.contracts.computer

/** Computer Use contract — observe / act / level (G-C). */
object ComputerCapabilityIds {
    const val OBSERVE = "computer.observe"
    const val ACT = "computer.act"
    const val SET_LEVEL = "computer.level.set"
    const val SCREENSHOT_META = "computer.screenshot.meta"
    const val NAVIGATE = "computer.navigate"
    val ALL = listOf(OBSERVE, ACT, SET_LEVEL, SCREENSHOT_META, NAVIGATE)
}

enum class ComputerLevelId { Observe, Assist, Full }

data class ComputerObserveSnapshot(
    val url: String,
    val title: String,
    val text: String,
    val nodeCount: Int = 0,
    val realDriver: Boolean = false,
)

interface ComputerPort {
    fun observe(interactive: Boolean = true): ComputerObserveSnapshot
    fun setLevel(level: ComputerLevelId): Boolean
}

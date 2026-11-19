package com.mimir.contracts.loop

object LoopCapabilityIds {
    const val RUN = "loop.run"
    const val STOP = "loop.stop"
    const val STATUS = "loop.status"
    val ALL = listOf(RUN, STOP, STATUS)
}

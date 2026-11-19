package com.mimir.contracts.forge

/**
 * Forge contract — templates, scaffold, build, toolchain, board view, path lanes.
 */
object ForgeCapabilityIds {
    const val TEMPLATE_LIST = "forge.template.list"
    const val SCAFFOLD_RUN = "forge.scaffold.run"
    const val BUILD_RUN = "forge.build.run"
    const val TOOLCHAIN_DETECT = "forge.toolchain.detect"
    const val TOOLCHAIN_INSTALL = "forge.toolchain.install"
    const val BOARD_SNAPSHOT = "forge.board.snapshot"
    const val LANE_REGISTER = "forge.lane.register"
    const val LANE_LIST = "forge.lane.list"
    val ALL = listOf(
        TEMPLATE_LIST, SCAFFOLD_RUN, BUILD_RUN, TOOLCHAIN_DETECT, TOOLCHAIN_INSTALL,
        BOARD_SNAPSHOT, LANE_REGISTER, LANE_LIST,
    )
}

object ForgePermissionIds {
    const val READ = "forge.read"
    const val WRITE = "forge.write"
    const val BUILD = "forge.build"
}

enum class ForgeBoardColumn {
    Backlog, Ready, InProgress, Blocked, WaitingForApproval, InReview, Done,
}

data class ForgeLaneId(val value: String)

data class ForgeLane(
    val id: ForgeLaneId,
    val pathPrefix: String,
    val label: String = id.value,
)

data class ForgeScaffoldRequest(
    val templateId: String,
    val targetDir: String,
    val projectName: String = "app",
)

data class ForgeBuildRequest(
    val projectPath: String,
    val goals: List<String> = listOf("build"),
    val laneId: ForgeLaneId? = null,
)

data class ForgeBuildResult(
    val success: Boolean,
    val output: String,
    val exitCode: Int = if (success) 0 else 1,
)

interface ForgePort {
    fun listTemplates(): List<String>
    fun scaffold(templateId: String, targetDir: String, projectName: String = "app"): Boolean
    fun listLanes(): List<ForgeLane>
    fun registerLane(pathPrefix: String, label: String = pathPrefix): ForgeLane
    fun build(request: ForgeBuildRequest): ForgeBuildResult
}

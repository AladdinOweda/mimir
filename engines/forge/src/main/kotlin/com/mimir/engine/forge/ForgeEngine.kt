package com.mimir.engine.forge
import com.mimir.contracts.process.ProcessExecResult

import com.mimir.engine.forge.ownership.PathLaneRegistry
import java.io.File

import com.mimir.contracts.forge.ForgePort
import com.mimir.contracts.forge.ForgeLane
import com.mimir.contracts.forge.ForgeLaneId
import com.mimir.contracts.forge.ForgeBuildRequest
import com.mimir.contracts.forge.ForgeBuildResult

import com.mimir.contracts.process.ProcessExecutorPort
import com.mimir.contracts.process.UnavailableProcessExecutor
import com.mimir.contracts.process.ProcessExecRequest
import com.mimir.kernel.contract.*

object ForgeCapabilities {
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

object ForgePermissions {
    const val READ = "forge.read"
    const val WRITE = "forge.write"
    const val BUILD = "forge.build"
}

/**
 * R8 Forge — real FS scaffold; honest build/toolchain status (no Fake Ready).
 * Build without invoke=false returns status=NotInvoked (not Succeeded).
 * Toolchain install without real package manager returns Unavailable.
 */
class ForgeEngine(
    private val allowRealFs: Boolean = true,
    private val executor: ProcessExecutorPort = UnavailableProcessExecutor,
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine, ForgePort {
    private val contractLanes = java.util.concurrent.ConcurrentHashMap<String, ForgeLane>()
    private val pathLanes = PathLaneRegistry()
    private val laneSeq = java.util.concurrent.atomic.AtomicLong(0)

    private var state = EngineState.Discovered

    private val templates: Map<String, Map<String, String>> = mapOf(
        "kotlin-jvm-lib" to mapOf(
            "settings.gradle.kts" to "rootProject.name = \"sample-lib\"\n",
            "build.gradle.kts" to """
                plugins { kotlin("jvm") version "2.0.21" }
                repositories { mavenCentral() }
                dependencies { implementation(kotlin("stdlib")) }
            """.trimIndent() + "\n",
            "src/main/kotlin/Sample.kt" to "package sample\nfun hello() = \"ok\"\n",
            "README.md" to "# sample-lib\n",
        ),
        "markdown-note" to mapOf(
            "README.md" to "# Note\n",
            "notes/index.md" to "# Index\n",
        ),
        "empty-workspace" to mapOf(
            ".gitkeep" to "",
        ),
    )

    private val manifest = EngineManifest(
        id = EngineId("forge-core"),
        name = "Mimir Forge",
        version = "0.5.0",
        provides = ForgeCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf(ForgePermissions.READ, ForgePermissions.WRITE, ForgePermissions.BUILD),
        dataNamespace = "engine.forge",
        priority = 85,
        platforms = listOf("android", "desktop"),
        runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x",
        publisher = "mimir",
    )

    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState {
        state = EngineState.Configured; return state
    }
    override suspend fun start(): EngineState {
        state = EngineState.Running; return state
    }
    override suspend fun stop(reason: String): EngineState {
        state = EngineState.Stopped; return state
    }
    override suspend fun health() =
        HealthReport(manifest.id, state == EngineState.Running, state, "templates=${templates.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            ForgeCapabilities.TEMPLATE_LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("templates" to templates.keys.joinToString(","), "count" to templates.size.toString()),
                )
            ForgeCapabilities.SCAFFOLD_RUN -> scaffold(request)
            ForgeCapabilities.BUILD_RUN -> build(request)
            ForgeCapabilities.TOOLCHAIN_DETECT -> detect(request)
            ForgeCapabilities.TOOLCHAIN_INSTALL -> install(request)
            ForgeCapabilities.BOARD_SNAPSHOT ->
                InvocationResult.Ok(request.requestId, boardSnapshot())
            ForgeCapabilities.LANE_REGISTER -> {
                val prefix = request.input["pathPrefix"] ?: return InvocationResult.Err(request.requestId, "invalid", "pathPrefix")
                val label = request.input["label"] ?: prefix
                val lane = registerLane(prefix, label)
                pathLanes.setLane(prefix, lane.id.value)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("laneId" to lane.id.value, "pathPrefix" to prefix, "label" to label),
                )
            }
            ForgeCapabilities.LANE_LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "count" to listLanes().size.toString(),
                        "lanes" to listLanes().joinToString(";") { "${it.id.value}|${it.pathPrefix}" },
                    ),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun scaffold(request: InvocationRequest): InvocationResult {
        val tid = request.input["templateId"] ?: return InvocationResult.Err(request.requestId, "invalid", "templateId")
        val target = request.input["targetPath"] ?: return InvocationResult.Err(request.requestId, "invalid", "targetPath")
        val files = templates[tid] ?: return InvocationResult.Err(request.requestId, "not_found", tid)
        val root = File(target).canonicalFile
        // soft jail: refuse path with .. components after normalize that escape tmp if workspaceRoot set
        request.input["workspaceRoot"]?.let { ws ->
            val w = File(ws).canonicalFile
            if (root.path != w.path && !root.path.startsWith(w.path + File.separator)) {
                return InvocationResult.Err(request.requestId, "path_jail", target)
            }
        }
        if (!allowRealFs) {
            return InvocationResult.Ok(
                request.requestId,
                mapOf(
                    "templateId" to tid,
                    "targetPath" to target,
                    "written" to files.keys.joinToString(","),
                    "realFs" to "false",
                    "status" to "Simulated",
                ),
            )
        }
        root.mkdirs()
        val written = mutableListOf<String>()
        for ((rel, content) in files) {
            val f = File(root, rel)
            f.parentFile?.mkdirs()
            f.writeText(content)
            written += rel
        }
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "templateId" to tid,
                "targetPath" to root.path,
                "written" to written.joinToString(","),
                "realFs" to "true",
                "status" to "Written",
                "fileCount" to written.size.toString(),
            ),
        )
    }

    private fun build(request: InvocationRequest): InvocationResult {
        val path = request.input["path"] ?: return InvocationResult.Err(request.requestId, "invalid", "path")
        val invoke = request.input["invoke"]?.equals("true", true) == true ||
            request.input["invokeGradle"]?.equals("true", true) == true
        if (!invoke) {
            // Honest: not a success exit
            return InvocationResult.Ok(
                request.requestId,
                mapOf(
                    "path" to path,
                    "status" to "NotInvoked",
                    "exitCode" to "",
                    "stub" to "true",
                    "message" to "build_not_invoked_set_invoke_true",
                ),
            )
        }
        val dir = File(path)
        if (!dir.isDirectory) {
            return InvocationResult.Err(request.requestId, "not_a_directory", path)
        }
        val gradlew = File(dir, "gradlew")
        val argv = if (gradlew.exists()) {
            listOf(gradlew.absolutePath, "tasks", "--offline")
        } else {
            listOf("gradle", "tasks", "--offline")
        }
        val result = executor.execute(
            ProcessExecRequest(
                argv = argv,
                cwd = dir.absolutePath,
                workspaceRoot = dir.absolutePath,
                shell = false,
                timeoutMs = request.input["timeoutMs"]?.toLongOrNull() ?: 45_000L,
            ),
        )
        val status = when {
            result.timedOut -> "TimedOut"
            result.exitCode == 0 -> "Succeeded"
            else -> "Failed"
        }
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "path" to path,
                "status" to status,
                "exitCode" to result.exitCode.toString(),
                "stdout" to result.stdout.take(2000),
                "stderr" to result.stderr.take(1000),
                "stub" to "false",
                "timedOut" to result.timedOut.toString(),
            ),
        )
    }

    private fun detect(request: InvocationRequest): InvocationResult {
        val kind = request.input["kind"] ?: return InvocationResult.Err(request.requestId, "invalid", "kind")
        val cmd = when (kind.lowercase()) {
            "java" -> listOf("java", "-version")
            "kotlin" -> listOf("kotlinc", "-version")
            "git" -> listOf("git", "--version")
            "python", "python3" -> listOf("python3", "--version")
            "node" -> listOf("node", "--version")
            "flutter" -> listOf("flutter", "--version")
            "dart" -> listOf("dart", "--version")
            else -> return InvocationResult.Err(request.requestId, "unknown_toolchain", kind)
        }
        val result = executor.execute(
            ProcessExecRequest(argv = cmd, shell = false, timeoutMs = 8_000),
        )
        // Many tools print version on stderr
        val text = (result.stdout + result.stderr).trim()
        val found = result.exitCode == 0 || text.contains(Regex("""\d+\.\d+"""))
        val status = if (found) "Detected" else "NotFound"
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "kind" to kind,
                "status" to status,
                "exitCode" to result.exitCode.toString(),
                "versionHint" to text.lines().firstOrNull().orEmpty().take(120),
                // Never claim Ready/Installed without proof
                "ready" to "false",
            ),
        )
    }

    private fun install(request: InvocationRequest): InvocationResult {
        val kind = request.input["kind"] ?: return InvocationResult.Err(request.requestId, "invalid", "kind")
        // Honest: we do not silently pretend install succeeded
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "kind" to kind,
                "status" to "Unavailable",
                "message" to "automated_install_not_implemented_use_detect",
                "ready" to "false",
                "stub" to "false",
            ),
        )
    }


    // --- ForgePort (deepened: lanes + honest gradle attempt) ---
    override fun listTemplates(): List<String> = templates.keys.toList()

    override fun scaffold(templateId: String, targetDir: String, projectName: String): Boolean {
        // Port API — delegates to FS write of template
        val files = templates[templateId] ?: return false
        val root = java.io.File(targetDir)
        root.mkdirs()
        for ((rel, content) in files) {
            val f = java.io.File(root, rel)
            f.parentFile?.mkdirs()
            f.writeText(content)
        }
        return true
    }

    override fun listLanes(): List<ForgeLane> = contractLanes.values.toList()

    override fun registerLane(pathPrefix: String, label: String): ForgeLane {
        val id = ForgeLaneId("lane-${laneSeq.incrementAndGet()}")
        val lane = ForgeLane(id, pathPrefix, label.ifBlank { pathPrefix })
        contractLanes[id.value] = lane
        pathLanes.setLane(pathPrefix, id.value)
        return lane
    }

    override fun build(request: ForgeBuildRequest): ForgeBuildResult = runGradleLike(request, testMode = false)

    fun test(request: ForgeBuildRequest): ForgeBuildResult = runGradleLike(request, testMode = true)

    private fun runGradleLike(request: ForgeBuildRequest, testMode: Boolean): ForgeBuildResult {
        val path = request.projectPath
        if (path.isBlank()) {
            return ForgeBuildResult(false, "invalid_path", 2)
        }
        request.laneId?.let { lane ->
            if (!pathLanes.allows(path, lane.value) && pathLanes.ownerOf(path) != "coordinator") {
                val owner = pathLanes.ownerOf(path)
                if (owner != lane.value && owner != "coordinator") {
                    return ForgeBuildResult(false, "lane_forbidden owner=$owner", 3)
                }
            }
        }
        val dir = File(path)
        if (!dir.isDirectory) {
            return ForgeBuildResult(false, "not_a_directory:$path", 4)
        }
        val gradlew = File(dir, "gradlew")
        val task = when {
            testMode && request.goals.isNotEmpty() -> request.goals
            testMode -> listOf("test")
            request.goals.isNotEmpty() -> request.goals
            else -> listOf("tasks", "--offline")
        }
        val argv = if (gradlew.exists()) {
            listOf(gradlew.absolutePath) + task
        } else {
            // Honest: no silent success without gradle
            return ForgeBuildResult(
                success = false,
                output = "gradlew_missing_and_no_auto_install path=$path goals=${task.joinToString()}",
                exitCode = 5,
            )
        }
        if (!gradlew.canExecute()) {
            gradlew.setExecutable(true)
        }
        val result = executor.execute(
            ProcessExecRequest(
                argv = argv,
                cwd = dir.absolutePath,
                shell = false,
                timeoutMs = 120_000,
                workspaceRoot = dir.absolutePath,
            ),
        )
        val ok = result.exitCode == 0 && !result.timedOut
        val out = buildString {
            appendLine("argv=${argv.joinToString(" ")}")
            appendLine(result.stdout.take(3000))
            if (result.stderr.isNotBlank()) appendLine("STDERR:" + result.stderr.take(1000))
            if (result.timedOut) appendLine("TIMED_OUT")
        }
        return ForgeBuildResult(ok, out, result.exitCode)
    }

    /** Resolve path ownership for tests/UI. */
    fun ownerOf(path: String): String = pathLanes.ownerOf(path)

    fun boardSnapshot(): Map<String, String> = mapOf(
        "lanes" to pathLanes.size().toString(),
        "contractLanes" to contractLanes.size.toString(),
        "templates" to templates.size.toString(),
        "state" to state.name,
    )
}

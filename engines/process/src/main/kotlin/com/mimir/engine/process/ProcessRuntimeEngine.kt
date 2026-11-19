package com.mimir.engine.process

import com.mimir.contracts.process.ProcessExecutorPort
import com.mimir.contracts.process.UnavailableProcessExecutor
import com.mimir.contracts.process.ProcessExecRequest
import com.mimir.contracts.process.ProcessExecResult
import com.mimir.kernel.contract.*

object ProcessRuntimeCapabilities {
    const val EXEC_ARGV = "process.exec.argv"
    const val EXEC_SHELL = "process.exec.shell"
    const val METRICS = "process.metrics"
    val ALL = listOf(EXEC_ARGV, EXEC_SHELL, METRICS)
}

/**
 * Process Runtime — argv-first execution via [ProcessExecutorPort], workspace jail + timeout.
 * Shell is a separate high-risk capability (policy/approval at Kernel).
 */
class ProcessRuntimeEngine(
    private val executor: ProcessExecutorPort = UnavailableProcessExecutor,
    private val defaultWorkspace: String = System.getProperty("java.io.tmpdir"),
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private var state = EngineState.Discovered
    private val execCount = java.util.concurrent.atomic.AtomicLong(0)
    private val failCount = java.util.concurrent.atomic.AtomicLong(0)
    private val lastExit = java.util.concurrent.atomic.AtomicInteger(0)

    private val manifest = EngineManifest(
        id = EngineId("process-runtime"),
        name = "Mimir Process Runtime",
        version = "0.5.1",
        provides = ProcessRuntimeCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("process.exec"),
        dataNamespace = "engine.process",
        priority = 86,
        platforms = listOf("desktop", "android"),
        runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x",
        publisher = "mimir",
    )

    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState {
        state = EngineState.Configured
        return state
    }
    override suspend fun start(): EngineState {
        state = EngineState.Running
        return state
    }
    override suspend fun stop(reason: String): EngineState {
        state = EngineState.Stopped
        return state
    }
    override suspend fun health() =
        HealthReport(
            manifest.id,
            state == EngineState.Running,
            state,
            "workspace=$defaultWorkspace executor=${executor::class.simpleName}",
            clockMs(),
        )

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            ProcessRuntimeCapabilities.METRICS ->
                InvocationResult.Ok(request.requestId, processMetrics())

            ProcessRuntimeCapabilities.EXEC_ARGV -> execArgv(request)
            ProcessRuntimeCapabilities.EXEC_SHELL -> execShell(request)
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun execArgv(request: InvocationRequest): InvocationResult {
        val argv = parseArgv(request.input)
        if (argv.isEmpty()) {
            return InvocationResult.Err(request.requestId, "invalid", "argv")
        }
        val workspace = request.input["workspaceRoot"] ?: defaultWorkspace
        val cwd = request.input["cwd"] ?: workspace
        val timeout = request.input["timeoutMs"]?.toLongOrNull() ?: 15_000L
        val result = executor.execute(
            ProcessExecRequest(
                argv = argv,
                cwd = cwd,
                workspaceRoot = workspace,
                timeoutMs = timeout,
                shell = false,
            ),
        )
        return toResult(request.requestId, result)
    }

    private fun isHighRisk(cmd: String): Boolean {
        val c = cmd.lowercase()
        return listOf("rm -rf", "rm -fr", "mkfs", "dd if=", ":(){", "shutdown", "reboot", "chmod 777 /", "curl | sh", "wget | sh").any { it in c }
    }

    private fun execShell(request: InvocationRequest): InvocationResult {
        val cmd = request.input["command"] ?: return InvocationResult.Err(request.requestId, "invalid", "command")
        val approval = !request.input["approvalToken"].isNullOrBlank()
        if (isHighRisk(cmd) && !approval) {
            return InvocationResult.Err(request.requestId, "risk_blocked", "approval_required")
        }
        val workspace = request.input["workspaceRoot"] ?: defaultWorkspace
        val cwd = request.input["cwd"] ?: workspace
        val timeout = request.input["timeoutMs"]?.toLongOrNull() ?: 15_000L
        val result = executor.execute(
            ProcessExecRequest(
                command = cmd,
                cwd = cwd,
                workspaceRoot = workspace,
                timeoutMs = timeout,
                shell = true,
            ),
        )
        return toResult(request.requestId, result)
    }

    private fun parseArgv(input: Map<String, String>): List<String> {
        val nul = (input["argv"] ?: "").split('\u0000').map { it.trim() }.filter { it.isNotEmpty() }
        if (nul.isNotEmpty()) return nul
        val csv = (input["argvCsv"] ?: "").split(',').map { it.trim() }.filter { it.isNotEmpty() }
        if (csv.isNotEmpty()) return csv
        // space-separated fallback for simple tests: argvLine=echo hello
        val line = (input["argvLine"] ?: "").trim()
        if (line.isNotEmpty()) return line.split(Regex("\\s+")).filter { it.isNotEmpty() }
        return emptyList()
    }

    fun processMetrics(): Map<String, String> = mapOf(
        "execCount" to execCount.get().toString(),
        "failCount" to failCount.get().toString(),
        "lastExit" to lastExit.get().toString(),
        "state" to state.name,
        "workspace" to defaultWorkspace,
    )

    private fun toResult(requestId: String, result: ProcessExecResult): InvocationResult {
        execCount.incrementAndGet()
        lastExit.set(result.exitCode)
        if (result.exitCode != 0 || result.errorCode != null) failCount.incrementAndGet()
        if (result.errorCode == "path_jail") {
            return InvocationResult.Err(requestId, "path_jail", result.stderr.ifBlank { "cwd_outside_workspace" })
        }
        if (result.errorCode == "invalid") {
            return InvocationResult.Err(requestId, "invalid", result.stderr.ifBlank { "empty_or_invalid_command" })
        }
        return InvocationResult.Ok(
            requestId,
            mapOf(
                "exitCode" to result.exitCode.toString(),
                "stdout" to result.stdout,
                "stderr" to result.stderr,
                "timedOut" to result.timedOut.toString(),
                "errorCode" to (result.errorCode ?: ""),
            ),
        )
    }
}

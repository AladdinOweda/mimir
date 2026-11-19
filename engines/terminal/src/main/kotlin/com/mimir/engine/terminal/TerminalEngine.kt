package com.mimir.engine.terminal

import com.mimir.contracts.process.ProcessExecResult

import com.mimir.contracts.process.ProcessExecRequest

import com.mimir.contracts.process.UnavailableProcessExecutor

import com.mimir.contracts.process.ProcessExecutorPort

import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.EngineId
import com.mimir.kernel.contract.EngineManifest
import com.mimir.kernel.contract.EngineState
import com.mimir.kernel.contract.HealthReport
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import com.mimir.kernel.contract.KernelErrorCodes
import com.mimir.kernel.contract.MimirEngine
import com.mimir.kernel.contract.RuntimeMode
import com.mimir.contracts.terminal.TerminalPort
import com.mimir.contracts.terminal.TerminalSessionId
import com.mimir.contracts.terminal.TerminalSessionInfo
import com.mimir.contracts.terminal.TerminalCommandRequest
import com.mimir.contracts.terminal.TerminalCommandResult
import com.mimir.contracts.terminal.TerminalCapabilityIds
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object TerminalCapabilities {
    const val SESSION_OPEN = "terminal.session.open"
    const val SESSION_CLOSE = "terminal.session.close"
    const val COMMAND_EXECUTE = "terminal.command.execute"
    const val SESSION_LIST = "terminal.session.list"
    val ALL = listOf(SESSION_OPEN, SESSION_CLOSE, COMMAND_EXECUTE, SESSION_LIST)
}

object TerminalPermissions {
    const val SESSION = "terminal.session"
    const val EXECUTE = "terminal.execute"
}

/**
 * Terminal Engine (compile-clean + real JVM process executor).
 * Risk gate: blocks destructive patterns without approvalToken (Kernel-issued).
 */
class TerminalEngine(
    private val executor: ProcessExecutorPort = UnavailableProcessExecutor,
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine, TerminalPort {

    private data class Session(val id: String, val cwd: String, val createdAt: Long)

    private val sessions = ConcurrentHashMap<String, Session>()
    private val commandHistory = ConcurrentHashMap<String, MutableList<String>>()
    private val seq = AtomicLong(0)
    private var state: EngineState = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("terminal-core"),
        name = "Mimir Terminal",
        version = "0.3.0",
        apiContract = "mimir.engine/1.x",
        publisher = "mimir",
        runtimeMode = RuntimeMode.Embedded,
        platforms = listOf("android", "desktop"),
        provides = TerminalCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf(TerminalPermissions.SESSION, TerminalPermissions.EXECUTE),
        dataNamespace = "engine.terminal",
        priority = 80,
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
    override suspend fun health() = HealthReport(
        engineId = manifest.id,
        healthy = state == EngineState.Running,
        state = state,
        detail = "terminal=0.3.0 sessions=${sessions.size} executor=${executor::class.simpleName}",
        checkedAtEpochMs = clockMs(),
    )

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            TerminalCapabilities.SESSION_OPEN -> open(request)
            TerminalCapabilities.SESSION_CLOSE -> close(request)
            TerminalCapabilities.COMMAND_EXECUTE -> exec(request)
            TerminalCapabilities.SESSION_LIST -> list(request)
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun open(request: InvocationRequest): InvocationResult {
        val id = "ts-${seq.incrementAndGet()}"
        val cwd = request.input["cwd"] ?: System.getProperty("user.dir")
        sessions[id] = Session(id, cwd, clockMs())
        commandHistory[id] = mutableListOf()
        return InvocationResult.Ok(request.requestId, mapOf("sessionId" to id, "cwd" to cwd))
    }

    private fun close(request: InvocationRequest): InvocationResult {
        val id = request.input["sessionId"] ?: return InvocationResult.Err(request.requestId, "invalid", "sessionId")
        sessions.remove(id)
        commandHistory.remove(id)
        return InvocationResult.Ok(request.requestId, mapOf("sessionId" to id, "closed" to "true"))
    }

    private fun list(request: InvocationRequest): InvocationResult {
        val all = sessions.values.toList()
        return InvocationResult.Ok(
            request.requestId,
            mapOf("count" to all.size.toString(), "ids" to all.joinToString(",") { it.id }),
        )
    }

    private fun exec(request: InvocationRequest): InvocationResult {
        val sid = request.input["sessionId"]
        val cmd = request.input["command"] ?: return InvocationResult.Err(request.requestId, "invalid", "command")
        val approval = !request.input["approvalToken"].isNullOrBlank()
        if (isHighRisk(cmd) && !approval) {
            return InvocationResult.Err(request.requestId, "risk_blocked", "approval_required")
        }
        val cwd = sid?.let { sessions[it]?.cwd } ?: request.input["cwd"]
        val timeout = request.input["timeoutMs"]?.toLongOrNull() ?: 15_000L
        val result = executor.execute(ProcessExecRequest(command = cmd, cwd = cwd, timeoutMs = timeout, shell = true))
        if (sid != null) {
            commandHistory.getOrPut(sid) { mutableListOf() }.let { hist ->
                synchronized(hist) {
                    hist.add(cmd)
                    while (hist.size > 32) hist.removeAt(0)
                }
            }
        }
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "exitCode" to result.exitCode.toString(),
                "stdout" to result.stdout.take(4000),
                "stderr" to result.stderr.take(2000),
                "timedOut" to result.timedOut.toString(),
                "errorCode" to (result.errorCode ?: ""),
            ),
            evidenceRefs = listOf("terminal.exec:${clockMs()}"),
        )
    }

    // --- TerminalPort (deepened) ---
    override fun openSession(cwd: String): TerminalSessionInfo {
        val id = "ts-${seq.incrementAndGet()}"
        val c = cwd.ifBlank { System.getProperty("user.dir") }
        sessions[id] = Session(id, c, clockMs())
        commandHistory[id] = mutableListOf()
        return TerminalSessionInfo(TerminalSessionId(id), c, sessions[id]!!.createdAt)
    }

    override fun closeSession(id: TerminalSessionId): Boolean {
        commandHistory.remove(id.value)
        return sessions.remove(id.value) != null
    }

    override fun listSessions(): List<TerminalSessionInfo> =
        sessions.values.map { TerminalSessionInfo(TerminalSessionId(it.id), it.cwd, it.createdAt) }

    override fun execute(request: TerminalCommandRequest): TerminalCommandResult {
        if (isHighRisk(request.command) && !request.approvalGranted) {
            return TerminalCommandResult(
                exitCode = 126,
                stdout = "",
                stderr = "high_risk_requires_approval",
                timedOut = false,
            )
        }
        val cwd = request.cwd
            ?: request.sessionId?.let { sessions[it.value]?.cwd }
            ?: System.getProperty("user.dir")
        val outcome = executor.execute(
            ProcessExecRequest(
                command = request.command,
                cwd = cwd,
                timeoutMs = request.timeoutMs,
                shell = true,
            ),
        )
        request.sessionId?.value?.let { sid ->
            commandHistory.getOrPut(sid) { mutableListOf() }.let { hist ->
                synchronized(hist) {
                    hist.add(request.command)
                    while (hist.size > 32) hist.removeAt(0)
                }
            }
        }
        return TerminalCommandResult(
            exitCode = outcome.exitCode,
            stdout = outcome.stdout,
            stderr = outcome.stderr,
            timedOut = outcome.timedOut,
        )
    }

    /** Recent commands for a session (Port/debug). */
    fun history(sessionId: TerminalSessionId): List<String> =
        commandHistory[sessionId.value]?.toList() ?: emptyList()

    private fun isHighRisk(cmd: String): Boolean {
        val c = cmd.lowercase().trim()
        val patterns = listOf(
            "rm -rf", "rm -fr", "mkfs", "dd if=", ":(){", "shutdown", "reboot",
            "chmod 777 /", "mkfs.", "> /dev/sd", "curl | sh", "wget | sh",
            "drop table", "fork bomb",
        )
        return patterns.any { it in c }
    }

    /** Observability for product dashboards. */
    fun sessionMetrics(): Map<String, String> {
        val totalCmds = commandHistory.values.sumOf { it.size }
        return mapOf(
            "sessions" to sessions.size.toString(),
            "historyLines" to totalCmds.toString(),
            "openSessions" to sessions.size.toString(),
        )
    }

    fun lastCommands(sessionId: String, limit: Int = 20): List<String> =
        commandHistory[sessionId]?.takeLast(limit.coerceAtLeast(1)) ?: emptyList()
}

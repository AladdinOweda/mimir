package com.mimir.contracts.terminal

/**
 * Terminal engine **contract** (G-C3).
 * Engines implement behavior; this module defines stable IDs and DTOs only.
 * Aligns with engines/terminal capabilities; catalog also has coarse `terminal.exec`.
 */
object TerminalCapabilityIds {
    const val SESSION_OPEN = "terminal.session.open"
    const val SESSION_CLOSE = "terminal.session.close"
    const val COMMAND_EXECUTE = "terminal.command.execute"
    const val SESSION_LIST = "terminal.session.list"
    /** Coarse product-level id (CapabilityCatalog.TERMINAL_EXEC). */
    const val EXEC_COARSE = "terminal.exec"

    val ALL_FINE = listOf(SESSION_OPEN, SESSION_CLOSE, COMMAND_EXECUTE, SESSION_LIST)
}

data class TerminalSessionId(val value: String)

data class TerminalSessionInfo(
    val id: TerminalSessionId,
    val cwd: String,
    val createdAtEpochMs: Long,
)

data class TerminalCommandRequest(
    val sessionId: TerminalSessionId?,
    val command: String,
    val cwd: String? = null,
    val timeoutMs: Long = 30_000,
    /** When true, high-risk patterns may run (mirrors Kernel approval for Port path). */
    val approvalGranted: Boolean = false,
)

data class TerminalCommandResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val timedOut: Boolean = false,
)

/**
 * Port engines/adapters satisfy. Not a second security boundary — KernelHost still gates invoke.
 */
interface TerminalPort {
    fun openSession(cwd: String = "."): TerminalSessionInfo
    fun closeSession(id: TerminalSessionId): Boolean
    fun listSessions(): List<TerminalSessionInfo>
    fun execute(request: TerminalCommandRequest): TerminalCommandResult
}

package com.mimir.contracts.process

/**
 * Process execution port — owned by contracts, not by Terminal.
 * Engines (Forge, Process, Terminal) depend on this interface;
 * JvmProcessExecutor lives in adapters/process or terminal as an implementation.
 */
data class ProcessExecRequest(
    val command: String = "",
    val argv: List<String> = emptyList(),
    val cwd: String? = null,
    val timeoutMs: Long = 15_000,
    val shell: Boolean = false,
    val workspaceRoot: String? = null,
    val maxStdoutBytes: Int = 32_000,
    val maxStderrBytes: Int = 8_000,
)

data class ProcessExecResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val timedOut: Boolean = false,
    val errorCode: String? = null,
)

interface ProcessExecutorPort {
    fun execute(req: ProcessExecRequest): ProcessExecResult
}

/** Explicit unavailable — never fake exitCode 0. */
object UnavailableProcessExecutor : ProcessExecutorPort {
    override fun execute(req: ProcessExecRequest) =
        ProcessExecResult(-1, "", "executor_unavailable", errorCode = "unavailable")
}

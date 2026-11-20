package com.mimir.engine.terminal

import com.mimir.contracts.process.ProcessExecRequest
import com.mimir.contracts.process.ProcessExecResult
import com.mimir.contracts.process.ProcessExecutorPort

class NoopProcessExecutor : ProcessExecutorPort {
    override fun execute(req: ProcessExecRequest) =
        ProcessExecResult(0, "NOOP:${(req.argv.ifEmpty { listOf(req.command) }).joinToString(" ").take(80)}", "")
}

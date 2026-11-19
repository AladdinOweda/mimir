package com.mimir.engine.terminal

import com.mimir.contracts.process.ProcessExecRequest
import com.mimir.contracts.process.ProcessExecResult
import com.mimir.contracts.process.ProcessExecutorPort
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class JvmProcessExecutor : ProcessExecutorPort {
    private val ioPool = Executors.newCachedThreadPool()

    override fun execute(req: ProcessExecRequest): ProcessExecResult {
        return try {
            val cmd = resolveCmd(req) ?: return ProcessExecResult(-1, "", "empty_or_invalid_command", errorCode = "invalid")
            val cwdResolved = resolveCwd(req)
            if (cwdResolved is CwdResult.Denied) {
                return ProcessExecResult(-1, "", "cwd_outside_workspace", errorCode = "path_jail")
            }
            val cwdFile = (cwdResolved as? CwdResult.Ok)?.dir
            val pb = ProcessBuilder(cmd)
            if (cwdFile != null) pb.directory(cwdFile)
            pb.redirectErrorStream(false)
            val proc = pb.start()
            val stdoutRef = AtomicReference("")
            val stderrRef = AtomicReference("")
            val outFut = ioPool.submit { stdoutRef.set(readLimited(proc.inputStream, req.maxStdoutBytes)) }
            val errFut = ioPool.submit { stderrRef.set(readLimited(proc.errorStream, req.maxStderrBytes)) }
            val finished = proc.waitFor(req.timeoutMs, TimeUnit.MILLISECONDS)
            if (!finished) {
                proc.destroyForcibly()
                outFut.get(1, TimeUnit.SECONDS)
                errFut.get(1, TimeUnit.SECONDS)
                return ProcessExecResult(
                    exitCode = -1,
                    stdout = stdoutRef.get(),
                    stderr = "timeout after ${req.timeoutMs}ms",
                    timedOut = true,
                    errorCode = "timeout",
                )
            }
            outFut.get(2, TimeUnit.SECONDS)
            errFut.get(2, TimeUnit.SECONDS)
            ProcessExecResult(proc.exitValue(), stdoutRef.get(), stderrRef.get())
        } catch (t: Throwable) {
            ProcessExecResult(-1, "", t.message ?: "exec_failed", errorCode = "exec_failed")
        }
    }

    private fun resolveCmd(req: ProcessExecRequest): List<String>? {
        if (req.argv.isNotEmpty()) return req.argv
        if (req.command.isBlank()) return null
        return if (req.shell) {
            listOf("/bin/sh", "-c", req.command)
        } else {
            // naive split only when shell=false and no argv — prefer explicit argv in production
            req.command.split(Regex("\\s+")).filter { it.isNotEmpty() }.ifEmpty { null }
        }
    }

    private sealed class CwdResult {
        data class Ok(val dir: File?) : CwdResult()
        object Denied : CwdResult()
    }

    private fun resolveCwd(req: ProcessExecRequest): CwdResult {
        if (req.cwd.isNullOrBlank()) {
            val dir = req.workspaceRoot?.let { File(it) }
            return CwdResult.Ok(dir)
        }
        val dir = File(req.cwd).canonicalFile
        if (!dir.isDirectory) return CwdResult.Denied
        val root = req.workspaceRoot?.let { File(it).canonicalFile }
        if (root != null) {
            val path = dir.path
            if (path != root.path && !path.startsWith(root.path + File.separator)) {
                return CwdResult.Denied
            }
        }
        return CwdResult.Ok(dir)
    }

    private fun readLimited(stream: InputStream, max: Int): String {
        val bytes = stream.readNBytes(max + 1)
        val slice = if (bytes.size > max) bytes.copyOf(max) else bytes
        return String(slice)
    }
}

class NoopProcessExecutor : ProcessExecutorPort {
    override fun execute(req: ProcessExecRequest) =
        ProcessExecResult(0, "NOOP:${(req.argv.ifEmpty { listOf(req.command) }).joinToString(" ").take(80)}", "")
}

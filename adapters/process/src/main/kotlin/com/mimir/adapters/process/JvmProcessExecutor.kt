package com.mimir.adapters.process

import com.mimir.contracts.process.ProcessExecRequest
import com.mimir.contracts.process.ProcessExecResult
import com.mimir.contracts.process.ProcessExecutorPort
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicBoolean

class JvmProcessExecutor : ProcessExecutorPort, AutoCloseable {
    private val ioPool = Executors.newCachedThreadPool()
    private val closed = AtomicBoolean(false)

    override fun close() {
        if (closed.compareAndSet(false, true)) {
            ioPool.shutdownNow()
        }
    }

    override fun execute(req: ProcessExecRequest): ProcessExecResult {
        if (closed.get()) {
            return ProcessExecResult(-1, "", "executor_closed", errorCode = "executor_closed")
        }
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
            val finished = try {
                proc.waitFor(req.timeoutMs, TimeUnit.MILLISECONDS)
            } catch (_: InterruptedException) {
                proc.destroyForcibly()
                outFut.cancel(true)
                errFut.cancel(true)
                Thread.currentThread().interrupt()
                return ProcessExecResult(
                    exitCode = -1,
                    stdout = stdoutRef.get(),
                    stderr = "execution_interrupted",
                    errorCode = "interrupted",
                )
            }
            if (!finished) {
                proc.destroyForcibly()
                try {
                    proc.waitFor(1, TimeUnit.SECONDS)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                }

                try {
                    outFut.get(1, TimeUnit.SECONDS)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    outFut.cancel(true)
                } catch (_: Exception) {
                    outFut.cancel(true)
                }

                try {
                    errFut.get(1, TimeUnit.SECONDS)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    errFut.cancel(true)
                } catch (_: Exception) {
                    errFut.cancel(true)
                }

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
        } catch (_: RejectedExecutionException) {
            if (closed.get()) {
                ProcessExecResult(-1, "", "executor_closed", errorCode = "executor_closed")
            } else {
                ProcessExecResult(-1, "", "exec_failed", errorCode = "exec_failed")
            }
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
        val limit = max.coerceAtLeast(0)
        val retained = ByteArrayOutputStream(minOf(limit, 8192))
        val buffer = ByteArray(8192)

        while (true) {
            val count = stream.read(buffer)
            if (count < 0) break

            val remaining = limit - retained.size()
            if (remaining > 0) {
                retained.write(buffer, 0, minOf(count, remaining))
            }
        }

        return String(retained.toByteArray(), StandardCharsets.UTF_8)
    }
}

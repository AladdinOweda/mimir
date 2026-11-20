package com.mimir.adapters.process

import com.mimir.contracts.process.ProcessExecRequest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JvmProcessExecutorTest {
    private val executor = JvmProcessExecutor()

    @Test
    fun executes_real_argv_and_captures_stdout() {
        val result = executor.execute(
            ProcessExecRequest(argv = listOf("/bin/echo", "adapter-process-ok")),
        )

        assertEquals(0, result.exitCode, result.stderr)
        assertTrue(result.stdout.contains("adapter-process-ok"), result.stdout)
        assertFalse(result.timedOut)
        assertEquals(null, result.errorCode)
    }

    @Test
    fun rejects_cwd_outside_workspace_root() {
        val root = createTempDir(prefix = "mimir-process-root-")
        val outside = createTempDir(prefix = "mimir-process-outside-")
        try {
            val result = executor.execute(
                ProcessExecRequest(
                    argv = listOf("/bin/echo", "must-not-run"),
                    cwd = outside.absolutePath,
                    workspaceRoot = root.absolutePath,
                ),
            )

            assertEquals(-1, result.exitCode)
            assertEquals("path_jail", result.errorCode)
            assertTrue(result.stderr.contains("cwd_outside_workspace"))
        } finally {
            root.deleteRecursively()
            outside.deleteRecursively()
        }
    }

    @Test
    fun reports_timeout_without_fake_success() {
        val result = executor.execute(
            ProcessExecRequest(
                argv = listOf("/bin/sleep", "2"),
                timeoutMs = 100,
            ),
        )

        assertEquals(-1, result.exitCode)
        assertTrue(result.timedOut)
        assertEquals("timeout", result.errorCode)
    }

    @Test
    fun truncates_stdout_to_configured_byte_limit() {
        val result = executor.execute(
            ProcessExecRequest(
                argv = listOf(
                    "/bin/sh", "-c",
                    "i=0; while [ \"\$i\" -lt 1000 ]; do printf '0123456789'; i=\$((i+1)); done",
                ),
                maxStdoutBytes = 32,
                timeoutMs = 5_000,
            ),
        )

        assertEquals(0, result.exitCode, result.stderr)
        assertFalse(result.timedOut)
        assertTrue(result.stdout.toByteArray().size <= 32)
    }

    @Test
    fun close_is_idempotent_and_rejects_new_execution() {
        val ownedExecutor = JvmProcessExecutor()
        ownedExecutor.close()
        ownedExecutor.close()

        val result = ownedExecutor.execute(
            ProcessExecRequest(argv = listOf("/bin/echo", "must-not-run")),
        )

        assertEquals(-1, result.exitCode)
        assertEquals("executor_closed", result.errorCode)
        assertEquals("executor_closed", result.stderr)
    }

    private fun createTempDir(prefix: String): File =
        kotlin.io.path.createTempDirectory(prefix).toFile()
}

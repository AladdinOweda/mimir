package com.mimir.integration

import com.mimir.engine.process.ProcessRuntimeCapabilities
import com.mimir.engine.process.ProcessRuntimeEngine
import com.mimir.engine.terminal.TerminalCapabilities
import com.mimir.engine.terminal.TerminalEngine
import com.mimir.engine.vault.VaultCapabilities
import com.mimir.engine.vault.VaultEngine
import com.mimir.kernel.contract.*
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Real execution on JVM — not stubs.
 * Process spawns OS process; Vault does AES-GCM; Terminal opens session + exec.
 */
class RealEngineExecutionTest {

    @Test
    fun process_real_echo_argv() = runTest {
        val eng = ProcessRuntimeEngine()
        eng.start()
        val r = eng.invoke(
            InvocationRequest(
                "1", "j",
                CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_ARGV)),
                mapOf("argvLine" to "echo REAL_PROCESS_OK"),
            ),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
        val ok = r as InvocationResult.Ok
        assertEquals("0", ok.output["exitCode"])
        assertTrue(ok.output["stdout"]!!.contains("REAL_PROCESS_OK"), ok.output.toString())
        assertTrue(eng.processMetrics()["execCount"]!!.toInt() >= 1)
    }

    @Test
    fun process_path_jail_denies_outside_workspace() = runTest {
        val eng = ProcessRuntimeEngine(defaultWorkspace = File(System.getProperty("java.io.tmpdir"), "mimir-jail").also { it.mkdirs() }.absolutePath)
        eng.start()
        val r = eng.invoke(
            InvocationRequest(
                "1", "j",
                CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_ARGV)),
                mapOf(
                    "argvLine" to "echo x",
                    "cwd" to "/",
                    "workspaceRoot" to eng.processMetrics()["workspace"]!!,
                ),
            ),
        )
        assertTrue(r is InvocationResult.Err, "expected path_jail, got $r")
        assertEquals("path_jail", (r as InvocationResult.Err).code)
    }

    @Test
    fun vault_aes_gcm_roundtrip_and_bad_passphrase() = runTest {
        val eng = VaultEngine() // in-memory, no sqlite required
        eng.start()
        val put = eng.invoke(
            InvocationRequest(
                "1", "j",
                CapabilityRef(CapabilityId(VaultCapabilities.PUT)),
                mapOf("key" to "secret1", "value" to "hello-vault", "passphrase" to "correct-horse"),
            ),
        )
        assertTrue(put is InvocationResult.Ok, put.toString())
        val getOk = eng.invoke(
            InvocationRequest(
                "2", "j",
                CapabilityRef(CapabilityId(VaultCapabilities.GET)),
                mapOf("key" to "secret1", "passphrase" to "correct-horse"),
            ),
        )
        assertTrue(getOk is InvocationResult.Ok, getOk.toString())
        assertEquals("hello-vault", (getOk as InvocationResult.Ok).output["value"])
        val getBad = eng.invoke(
            InvocationRequest(
                "3", "j",
                CapabilityRef(CapabilityId(VaultCapabilities.GET)),
                mapOf("key" to "secret1", "passphrase" to "wrong-pass"),
            ),
        )
        assertTrue(getBad is InvocationResult.Err)
        assertEquals("decrypt_failed", (getBad as InvocationResult.Err).code)
        assertTrue(eng.vaultMetrics()["keys"]!!.toInt() >= 1)
    }

    @Test
    fun terminal_session_and_echo_command() = runTest {
        val eng = TerminalEngine()
        eng.start()
        val open = eng.invoke(
            InvocationRequest(
                "1", "j",
                CapabilityRef(CapabilityId(TerminalCapabilities.SESSION_OPEN)),
                mapOf("cwd" to System.getProperty("java.io.tmpdir")),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val sid = (open as InvocationResult.Ok).output["sessionId"]
            ?: (open.output["id"] ?: error("no session id in $open"))
        val exec = eng.invoke(
            InvocationRequest(
                "2", "j",
                CapabilityRef(CapabilityId(TerminalCapabilities.COMMAND_EXECUTE)),
                mapOf("sessionId" to sid, "command" to "echo REAL_TERM_OK"),
            ),
        )
        assertTrue(exec is InvocationResult.Ok, exec.toString())
        val out = (exec as InvocationResult.Ok).output
        val blob = (out["stdout"] ?: "") + (out["output"] ?: "") + out.values.joinToString(" ")
        assertTrue(blob.contains("REAL_TERM_OK") || out["exitCode"] == "0", out.toString())
        assertTrue(eng.sessionMetrics()["sessions"]!!.toInt() >= 1)
    }

    @Test
    fun kernel_vault_put_get_with_policies() = runTest {
        val host = KernelHost()
        val eng = VaultEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("vault.read", "vault.write"))
        host.capabilitySecurity.register(CapabilityPolicy(VaultCapabilities.PUT, "vault.write", RiskLevel.High, "vault.write"))
        host.capabilitySecurity.register(CapabilityPolicy(VaultCapabilities.GET, "vault.read", RiskLevel.Medium, null))
        val p = PermissionContext("u")
        // High risk put needs token
        val denied = host.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(VaultCapabilities.PUT)), mapOf("key" to "k", "value" to "v", "passphrase" to "passphrase-ok")),
            p,
        )
        assertTrue(denied is InvocationResult.Err)
        assertEquals("approval_required", (denied as InvocationResult.Err).code)
        val tok = host.approvals.issue("u", "vault.write", VaultCapabilities.PUT, "vault.write")
        val put = host.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(VaultCapabilities.PUT)),
                mapOf("key" to "k", "value" to "v", "passphrase" to "passphrase-ok", "approvalToken" to tok.id),
            ),
            p,
        )
        assertTrue(put is InvocationResult.Ok, put.toString())
        val get = host.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(VaultCapabilities.GET)),
                mapOf("key" to "k", "passphrase" to "passphrase-ok"),
            ),
            p,
        )
        assertTrue(get is InvocationResult.Ok, get.toString())
        assertEquals("v", (get as InvocationResult.Ok).output["value"])
    }

    @Test
    fun process_through_kernel_real_echo() = runTest {
        val host = KernelHost()
        val eng = ProcessRuntimeEngine()
        val man = eng.describe()
        host.attach(eng, man)
        host.registerAndStart(man.id)
        host.permissions.grantAll("u", listOf("process.exec"))
        host.capabilitySecurity.register(
            CapabilityPolicy(ProcessRuntimeCapabilities.EXEC_ARGV, "process.exec", RiskLevel.Medium, null),
        )
        val r = host.invoke(
            InvocationRequest(
                "1", "j",
                CapabilityRef(CapabilityId(ProcessRuntimeCapabilities.EXEC_ARGV)),
                mapOf("argvLine" to "echo KERNEL_PROCESS_OK"),
            ),
            PermissionContext("u"),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
        assertTrue((r as InvocationResult.Ok).output["stdout"]!!.contains("KERNEL_PROCESS_OK"))
    }
}

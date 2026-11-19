package com.mimir.engine.vault

import com.mimir.kernel.contract.*
import java.io.File
import java.security.SecureRandom
import java.sql.DriverManager
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object VaultCapabilities {
    const val PUT = "vault.put"
    const val GET = "vault.get"
    const val DELETE = "vault.delete"
    const val LIST = "vault.list"
    const val EXISTS = "vault.exists"
    const val METRICS = "vault.metrics"
    val ALL = listOf(PUT, GET, DELETE, LIST, EXISTS, METRICS)
}

data class VaultEnvelope(
    val saltB64: String,
    val ivB64: String,
    val cipherB64: String,
)

class JdbcVaultBlobStore(dbFile: File) {
    private val conn = run {
        dbFile.parentFile?.mkdirs()
        Class.forName("org.sqlite.JDBC")
        DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").also { c ->
            c.createStatement().use {
                it.execute(
                    """
                    CREATE TABLE IF NOT EXISTS vault_blob (
                      key TEXT PRIMARY KEY,
                      salt_b64 TEXT NOT NULL,
                      iv_b64 TEXT NOT NULL,
                      cipher_b64 TEXT NOT NULL
                    )
                    """.trimIndent(),
                )
            }
        }
    }

    fun save(key: String, env: VaultEnvelope) {
        conn.prepareStatement(
            "INSERT OR REPLACE INTO vault_blob(key,salt_b64,iv_b64,cipher_b64) VALUES (?,?,?,?)",
        ).use { ps ->
            ps.setString(1, key)
            ps.setString(2, env.saltB64)
            ps.setString(3, env.ivB64)
            ps.setString(4, env.cipherB64)
            ps.executeUpdate()
        }
    }

    fun delete(key: String) {
        conn.prepareStatement("DELETE FROM vault_blob WHERE key=?").use {
            it.setString(1, key)
            it.executeUpdate()
        }
    }

    fun loadAll(): Map<String, VaultEnvelope> {
        val out = mutableMapOf<String, VaultEnvelope>()
        conn.createStatement().use { st ->
            st.executeQuery("SELECT key,salt_b64,iv_b64,cipher_b64 FROM vault_blob").use { rs ->
                while (rs.next()) {
                    out[rs.getString(1)] = VaultEnvelope(rs.getString(2), rs.getString(3), rs.getString(4))
                }
            }
        }
        return out
    }

    fun close() = conn.close()
}

/**
 * R7 Vault — AES-GCM + PBKDF2 only. No XOR. Optional SQLite durability for ciphertext.
 */
class VaultEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
    private val durableDb: File? = null,
) : MimirEngine {
    private val store = ConcurrentHashMap<String, VaultEnvelope>()
    private var state = EngineState.Discovered
    private val jdbc: JdbcVaultBlobStore? = durableDb?.let { JdbcVaultBlobStore(it) }

    private val manifest = EngineManifest(
        id = EngineId("vault-core"),
        name = "Mimir Vault",
        version = "0.5.0",
        provides = VaultCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("vault.read", "vault.write"),
        dataNamespace = "engine.vault",
        priority = 90,
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
        jdbc?.loadAll()?.forEach { (k, e) -> store[k] = e }
        state = EngineState.Running; return state
    }
    override suspend fun stop(reason: String): EngineState {
        jdbc?.close()
        state = EngineState.Stopped; return state
    }
    override suspend fun health() =
        HealthReport(manifest.id, state == EngineState.Running, state, "alg=AES-GCM-PBKDF2 keys=${store.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        request.input["cipher"]?.let {
            if (it.contains("xor", ignoreCase = true) || it.contains("stream", ignoreCase = true)) {
                return InvocationResult.Err(request.requestId, "cipher_forbidden", "AES-GCM only")
            }
        }
        return when (request.capability.id.value) {
            VaultCapabilities.PUT -> {
                val key = request.input["key"] ?: return inv(request, "key")
                if (!validKey(key)) return InvocationResult.Err(request.requestId, "invalid_key", key.take(32))
                put(request)
            }
            VaultCapabilities.GET -> get(request)
            VaultCapabilities.DELETE -> {
                val key = request.input["key"] ?: return inv(request, "key")
                store.remove(key)
                jdbc?.delete(key)
                InvocationResult.Ok(request.requestId, mapOf("deleted" to key))
            }
            VaultCapabilities.LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("keys" to store.keys.joinToString(","), "count" to store.size.toString(), "alg" to "AES-GCM"),
                )
            VaultCapabilities.EXISTS -> {
                val key = request.input["key"] ?: return inv(request, "key")
                InvocationResult.Ok(request.requestId, mapOf("key" to key, "exists" to store.containsKey(key).toString()))
            }
            VaultCapabilities.METRICS ->
                InvocationResult.Ok(request.requestId, vaultMetrics())
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun put(request: InvocationRequest): InvocationResult {
        val key = request.input["key"] ?: return inv(request, "key")
        val value = request.input["value"] ?: return inv(request, "value")
        val pass = request.input["passphrase"] ?: return inv(request, "passphrase")
        if (pass.length < 8) return InvocationResult.Err(request.requestId, "weak_passphrase", "min_8")
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val secret = derive(pass, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secret, GCMParameterSpec(128, iv))
        val enc = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val env = VaultEnvelope(
            Base64.getEncoder().encodeToString(salt),
            Base64.getEncoder().encodeToString(iv),
            Base64.getEncoder().encodeToString(enc),
        )
        store[key] = env
        jdbc?.save(key, env)
        return InvocationResult.Ok(request.requestId, mapOf("key" to key, "stored" to "true", "alg" to "AES-GCM-PBKDF2"))
    }

    private fun get(request: InvocationRequest): InvocationResult {
        val key = request.input["key"] ?: return inv(request, "key")
        val pass = request.input["passphrase"] ?: return inv(request, "passphrase")
        val env = store[key] ?: return InvocationResult.Err(request.requestId, "not_found", key)
        return try {
            val salt = Base64.getDecoder().decode(env.saltB64)
            val iv = Base64.getDecoder().decode(env.ivB64)
            val data = Base64.getDecoder().decode(env.cipherB64)
            val secret = derive(pass, salt)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, secret, GCMParameterSpec(128, iv))
            val plain = String(cipher.doFinal(data), Charsets.UTF_8)
            InvocationResult.Ok(request.requestId, mapOf("key" to key, "value" to plain))
        } catch (_: Throwable) {
            InvocationResult.Err(request.requestId, "decrypt_failed", "bad_passphrase_or_corrupt")
        }
    }

    private fun derive(pass: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = factory.generateSecret(PBEKeySpec(pass.toCharArray(), salt, 120_000, 256)).encoded
        return SecretKeySpec(key, "AES")
    }

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)

    private fun validKey(key: String): Boolean =
        key.isNotBlank() && key.length <= 256 && key.none { it in "\n\r\u0000" }

    /** Test/debug helper — ciphertext never exposed. */
    fun keyCount(): Int = store.size


    fun vaultMetrics(): Map<String, String> = mapOf(
        "keys" to store.size.toString(),
        "state" to state.name,
        "jdbc" to (jdbc != null).toString(),
    )

}

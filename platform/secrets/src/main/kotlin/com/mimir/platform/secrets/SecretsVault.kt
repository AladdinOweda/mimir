package com.mimir.platform.secrets

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.security.SecureRandom
import java.util.Base64

/**
 * Shared Secrets Vault (MIMIR-REPO-002 §9.14).
 * Not Knowledge Workspace. Not an Engine.
 */
data class SecretId(val value: String)

interface SecretsVault {
    fun put(name: String, plaintext: ByteArray): SecretId
    fun get(id: SecretId): ByteArray?
    fun delete(id: SecretId): Boolean
    fun listNames(): List<String>
}

/** Local AES-GCM vault; key held in memory for this process only. */
class LocalAesSecretsVault(
    private val masterKey: SecretKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
) : SecretsVault {
    private data class Entry(val name: String, val nonce: ByteArray, val ciphertext: ByteArray)
    private val map = linkedMapOf<String, Entry>()
    private var seq = 0L
    private val random = SecureRandom()

    override fun put(name: String, plaintext: ByteArray): SecretId {
        val nonce = ByteArray(12).also { random.nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, GCMParameterSpec(128, nonce))
        val ct = cipher.doFinal(plaintext)
        val id = SecretId("sec-${++seq}")
        map[id.value] = Entry(name, nonce, ct)
        return id
    }

    override fun get(id: SecretId): ByteArray? {
        val e = map[id.value] ?: return null
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, masterKey, GCMParameterSpec(128, e.nonce))
        return cipher.doFinal(e.ciphertext)
    }

    override fun delete(id: SecretId) = map.remove(id.value) != null
    override fun listNames() = map.values.map { it.name }
}

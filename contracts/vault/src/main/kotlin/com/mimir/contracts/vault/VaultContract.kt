package com.mimir.contracts.vault

object VaultCapabilityIds {
    const val PUT = "vault.put"
    const val GET = "vault.get"
    const val DELETE = "vault.delete"
    const val LIST = "vault.list"
    const val EXISTS = "vault.exists"
    const val METRICS = "vault.metrics"
    val ALL = listOf(PUT, GET, DELETE, LIST, EXISTS, METRICS)
}

object VaultPermissionIds {
    const val READ = "vault.read"
    const val WRITE = "vault.write"
}

data class VaultPutRequest(val key: String, val plaintext: String, val passphrase: String)
data class VaultGetRequest(val key: String, val passphrase: String)

interface VaultPort {
    fun put(request: VaultPutRequest): Boolean
    fun get(request: VaultGetRequest): String?
    fun delete(key: String): Boolean
    fun listKeys(): List<String>
}

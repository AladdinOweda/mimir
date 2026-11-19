package com.mimir.engine.provider.adapter

import com.mimir.engine.provider.contract.ProviderId

/**
 * Adapter contract — provider-specific I/O stays behind this port.
 * Kernel and engines never call vendor SDKs directly.
 */
interface ProviderAdapter {
    val adapterId: String
    val providerId: ProviderId
    suspend fun healthCheck(): AdapterHealth
    /** Optional probe — must not spend money without explicit policy */
    suspend fun probe(input: Map<String, String> = emptyMap()): AdapterProbeResult
}

data class AdapterHealth(
    val ok: Boolean,
    val detail: String? = null,
)

data class AdapterProbeResult(
    val ok: Boolean,
    val output: Map<String, String> = emptyMap(),
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

/** Placeholder until real MCP/REST adapters exist */
class NotImplementedAdapter(
    override val adapterId: String,
    override val providerId: ProviderId,
) : ProviderAdapter {
    override suspend fun healthCheck() = AdapterHealth(false, "adapter_not_implemented")
    override suspend fun probe(input: Map<String, String>) =
        AdapterProbeResult(false, errorCode = "not_implemented", errorMessage = adapterId)
}

class AdapterRegistry {
    private val byId = linkedMapOf<String, ProviderAdapter>()
    fun register(adapter: ProviderAdapter) {
        byId[adapter.adapterId] = adapter
    }
    fun get(adapterId: String) = byId[adapterId]
    fun list() = byId.values.toList()
}

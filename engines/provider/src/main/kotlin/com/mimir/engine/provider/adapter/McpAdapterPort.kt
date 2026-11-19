package com.mimir.engine.provider.adapter

import com.mimir.engine.provider.adapter.AdapterHealth
import com.mimir.engine.provider.adapter.AdapterProbeResult
import com.mimir.engine.provider.adapter.ProviderAdapter
import com.mimir.engine.provider.contract.ProviderId

/**
 * MCP transport port — real IO later (stdio/HTTP).
 * Registry binds adapters; connectivity engine may host sessions.
 */
interface McpTransportPort {
    suspend fun listTools(): List<String>
    suspend fun callTool(name: String, argsJson: String): String
}

class NoopMcpTransportPort : McpTransportPort {
    override suspend fun listTools() = emptyList<String>()
    override suspend fun callTool(name: String, argsJson: String) =
        """{"error":"mcp_transport_noop","tool":"$name"}"""
}

/**
 * Structured MCP adapter skeleton for a single provider.
 * probe() lists tools when transport available; never spends $ without policy.
 */
class McpProviderAdapter(
    override val adapterId: String,
    override val providerId: ProviderId,
    private val transport: McpTransportPort = NoopMcpTransportPort(),
) : ProviderAdapter {
    override suspend fun healthCheck(): AdapterHealth {
        return try {
            val tools = transport.listTools()
            AdapterHealth(true, "tools=${tools.size}")
        } catch (t: Throwable) {
            AdapterHealth(false, t.message)
        }
    }

    override suspend fun probe(input: Map<String, String>): AdapterProbeResult {
        val allowSpend = input["allowSpend"]?.equals("true", true) == true
        if (input["tool"] != null && !allowSpend) {
            return AdapterProbeResult(
                false,
                errorCode = "spend_blocked",
                errorMessage = "Set allowSpend=true only under policy",
            )
        }
        return try {
            if (input["tool"] != null) {
                val out = transport.callTool(input.getValue("tool"), input["argsJson"] ?: "{}")
                AdapterProbeResult(true, mapOf("result" to out.take(500)))
            } else {
                val tools = transport.listTools()
                AdapterProbeResult(true, mapOf("toolCount" to tools.size.toString(), "tools" to tools.joinToString(",")))
            }
        } catch (t: Throwable) {
            AdapterProbeResult(false, errorCode = "mcp_fail", errorMessage = t.message)
        }
    }
}

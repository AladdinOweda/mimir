package com.mimir.engine.tools

import com.mimir.kernel.contract.*

/**
 * Authorize via ToolRegistry then forward to a target engine invoke.
 * Side-effecting tools must pass Kernel approvalToken when required.
 * approvalGranted boolean is never accepted.
 */
class SecureInvoker(
    private val registry: ToolRegistryEngine,
) {
    suspend fun invoke(
        toolId: String,
        target: MimirEngine,
        capability: String,
        input: Map<String, String>,
        grantedPermissions: Set<String>,
        approvalToken: String? = null,
        jobId: String = "job",
        requestId: String = "req-${System.nanoTime()}",
    ): InvocationResult {
        val authInput = input + mapOf(
            "toolId" to toolId,
            "grantedPermissions" to grantedPermissions.joinToString(","),
        ) + (approvalToken?.let { mapOf("approvalToken" to it) } ?: emptyMap())
        val auth = registry.invoke(
            InvocationRequest(
                requestId = "$requestId-auth",
                jobId = jobId,
                capability = CapabilityRef(CapabilityId(ToolCapabilities.AUTHORIZE)),
                input = authInput,
            ),
        )
        if (auth is InvocationResult.Err) return auth
        val forwardInput = input + (approvalToken?.let { mapOf("approvalToken" to it) } ?: emptyMap())
        return target.invoke(
            InvocationRequest(
                requestId = requestId,
                jobId = jobId,
                capability = CapabilityRef(CapabilityId(capability)),
                input = forwardInput,
            ),
        )
    }
}

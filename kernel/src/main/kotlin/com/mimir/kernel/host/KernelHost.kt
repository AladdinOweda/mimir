package com.mimir.kernel.host

import com.mimir.kernel.audit.AuditEntry
import com.mimir.kernel.audit.AuditLog
import com.mimir.kernel.contract.*
import com.mimir.kernel.delivery.DurableOutbox
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.permission.PermissionPolicy
import com.mimir.kernel.persist.JdbcKernelStore
import com.mimir.kernel.security.ApprovalService
import com.mimir.kernel.security.CapabilitySecurityRegistry
import com.mimir.kernel.security.RiskLevel
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * KernelHost — single secure entry for engine invoke.
 *
 * Guarantees:
 * - Fail-closed capability policies (G-06): no policy ⇒ no_policy
 * - Permission always checked
 * - High/Critical risk requires ApprovalService token (never trust approvalGranted)
 * - Spoofable approvalGranted stripped before engine
 * - Domain-neutral (no engine names in this module)
 */
class KernelHost(
    val permissions: PermissionPolicy = PermissionPolicy(),
    val approvals: ApprovalService = ApprovalService(),
    val capabilitySecurity: CapabilitySecurityRegistry = CapabilitySecurityRegistry(),
    val audit: AuditLog = AuditLog(),
    val events: KernelLocalEventBus = KernelLocalEventBus(),
    val outbox: DurableOutbox = DurableOutbox(),
    private val clockMs: () -> Long = { System.currentTimeMillis() },
    /** Simple per-principal rate limit (0 = disabled). */
    private val maxInvokesPerWindow: Int = 0,
    private val rateWindowMs: Long = 60_000L,
) {
    private val engines = linkedMapOf<String, MimirEngine>()
    private val manifests = linkedMapOf<String, EngineManifest>()
    private val states = linkedMapOf<String, EngineState>()
    private val rateBuckets = ConcurrentHashMap<String, RateBucket>()
    private val invokeSeq = AtomicLong(0)

    private data class RateBucket(var windowStart: Long, var count: Int)

    fun attach(engine: MimirEngine, manifest: EngineManifest) {
        check(manifest.id.value !in engines) {
            "Engine '${manifest.id.value}' is already attached"
        }
        engines[manifest.id.value] = engine
        manifests[manifest.id.value] = manifest
        states[manifest.id.value] = EngineState.Discovered
        events.publish(
            EventEnvelope(
                type = "kernel.engine.attached",
                source = "KernelHost",
                payload = mapOf("engineId" to manifest.id.value, "provides" to manifest.provides.size.toString()),
            ),
        )
    }

    suspend fun registerAndStart(id: EngineId): EngineState {
        val engine = engines[id.value] ?: return EngineState.Failed
        engine.configure(emptyMap())
        val st = engine.start()
        states[id.value] = st
        events.publish(
            EventEnvelope(
                type = "kernel.engine.started",
                source = "KernelHost",
                payload = mapOf("engineId" to id.value, "state" to st.name),
            ),
        )
        return st
    }

    suspend fun invoke(
        request: InvocationRequest,
        principal: PermissionContext,
        requiredPermissionOverride: String? = null,
    ): InvocationResult {
        invokeSeq.incrementAndGet()
        val capId = request.capability.id.value

        if (maxInvokesPerWindow > 0 && !allowRate(principal.principalId)) {
            return deny(request, principal, capId, "rate_limited", null)
        }

        val policy = capabilitySecurity.policyFor(capId)
        val required = requiredPermissionOverride
            ?: policy?.requiredPermission
            ?: return deny(request, principal, capId, "no_policy", null)

        if (!permissions.check(principal.principalId, required)) {
            return deny(request, principal, capId, KernelErrorCodes.PERMISSION_DENIED, null)
        }

        val needsApproval = policy?.approvalActionId != null &&
            (policy.risk == RiskLevel.High || policy.risk == RiskLevel.Critical)
        if (needsApproval) {
            val tokenId = request.input["approvalToken"]
            if (tokenId.isNullOrBlank()) {
                return deny(request, principal, capId, "approval_required", null)
            }
            val resource = request.input["approvalResource"] ?: capId
            val ok = approvals.consumeIfValid(
                tokenId = tokenId,
                principalId = principal.principalId,
                actionId = policy!!.approvalActionId!!,
                targetResource = resource,
                requiredScope = required,
            )
            if (ok == null) {
                return deny(request, principal, capId, "approval_invalid", tokenId)
            }
        }

        val matched = engines.entries.firstOrNull { (eid, _) ->
            manifests[eid]?.provides?.any { it.id.value == capId } == true &&
                states[eid] == EngineState.Running
        }?.value
            ?: return deny(request, principal, capId, KernelErrorCodes.UNKNOWN_CAPABILITY, null)

        audit.append(
            AuditEntry(
                atEpochMs = clockMs(),
                principalId = principal.principalId,
                capabilityId = capId,
                decision = "ALLOW",
                reason = "ok",
                requestId = request.requestId,
                approvalTokenId = request.input["approvalToken"],
            ),
        )
        events.publish(
            EventEnvelope(
                type = "kernel.invoke.allow",
                source = "KernelHost",
                correlationId = request.requestId,
                payload = mapOf("capability" to capId, "principal" to principal.principalId),
            ),
        )
        val cleaned = request.copy(input = request.input - "approvalGranted")
        return matched.invoke(cleaned)
    }

    private fun allowRate(principalId: String): Boolean {
        val now = clockMs()
        val b = rateBuckets.getOrPut(principalId) { RateBucket(now, 0) }
        synchronized(b) {
            if (now - b.windowStart > rateWindowMs) {
                b.windowStart = now
                b.count = 0
            }
            if (b.count >= maxInvokesPerWindow) return false
            b.count++
            return true
        }
    }

    private fun deny(
        request: InvocationRequest,
        principal: PermissionContext,
        capId: String,
        reason: String,
        tokenId: String?,
    ): InvocationResult {
        audit.append(
            AuditEntry(
                atEpochMs = clockMs(),
                principalId = principal.principalId,
                capabilityId = capId,
                decision = "DENY",
                reason = reason,
                requestId = request.requestId,
                approvalTokenId = tokenId,
            ),
        )
        events.publish(
            EventEnvelope(
                type = "kernel.invoke.deny",
                source = "KernelHost",
                correlationId = request.requestId,
                payload = mapOf("capability" to capId, "reason" to reason, "principal" to principal.principalId),
            ),
        )
        return InvocationResult.Err(request.requestId, reason, capId)
    }

    fun listEngineIds(): List<String> = engines.keys.toList()
    fun engineCount(): Int = engines.size

    suspend fun stopEngine(
        id: EngineId,
        reason: String = "shutdown",
    ): EngineState? {
        val engine = engines[id.value] ?: return null
        val stoppedState = engine.stop(reason)
        states[id.value] = stoppedState
        events.publish(
            EventEnvelope(
                type = "kernel.engine.stopped",
                source = "KernelHost",
                payload = mapOf("engineId" to id.value, "state" to stoppedState.name),
            ),
        )
        return stoppedState
    }

    fun detach(id: String): Boolean {
        if (!engines.containsKey(id)) return false
        engines.remove(id)
        manifests.remove(id)
        states.remove(id)
        events.publish(EventEnvelope("kernel.engine.detached", "KernelHost", payload = mapOf("engineId" to id)))
        return true
    }

    fun stateOf(id: String): EngineState? = states[id]
    fun manifestOf(id: String): EngineManifest? = manifests[id]
    fun listManifests(): List<EngineManifest> = manifests.values.toList()

    fun securitySnapshot(): Map<String, String> = mapOf(
        "engines" to engines.size.toString(),
        "running" to states.values.count { it == EngineState.Running }.toString(),
        "policies" to capabilitySecurity.size().toString(),
        "auditAllow" to audit.allowCount().toString(),
        "auditDeny" to audit.denyCount().toString(),
        "auditTotal" to audit.size().toString(),
        "invokes" to invokeSeq.get().toString(),
        "outbox" to outbox.size().toString(),
        "eventHandlers" to events.handlerCount().toString(),
    )

    suspend fun stopAndDetach(id: String, reason: String = "detach"): Boolean {
        val eng = engines[id] ?: return false
        if (states[id] == EngineState.Running || states[id] == EngineState.Degraded) {
            runCatching { eng.stop(reason) }
            states[id] = EngineState.Stopped
        }
        return detach(id)
    }

    /** Resolve which running engine owns a capability (or null). */
    fun resolveEngineFor(capabilityId: String): String? =
        manifests.entries.firstOrNull { (eid, m) ->
            states[eid] == EngineState.Running && m.provides.any { it.id.value == capabilityId }
        }?.key

    companion object {
        fun durable(dataDir: File, clockMs: () -> Long = { System.currentTimeMillis() }): KernelHost {
            dataDir.mkdirs()
            val store = JdbcKernelStore(File(dataDir, "kernel.db"))
            val audit = AuditLog(store).also { it.recover() }
            val permissions = PermissionPolicy(store)
            val approvals = ApprovalService(clockMs, store)
            return KernelHost(
                permissions = permissions,
                approvals = approvals,
                audit = audit,
                clockMs = clockMs,
            )
        }

        /** Test helper with rate limit enabled. */
        fun withRateLimit(maxPerWindow: Int, windowMs: Long = 60_000L): KernelHost =
            KernelHost(maxInvokesPerWindow = maxPerWindow, rateWindowMs = windowMs)
    }
}

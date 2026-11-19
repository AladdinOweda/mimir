package com.mimir.kernel.contract

/**
 * Core Kernel contracts (Blueprint §4). Engines implement [MimirEngine];
 * UI and services never bypass [com.mimir.kernel.host.KernelHost] for capability invoke.
 */

@JvmInline value class EngineId(val value: String)
@JvmInline value class CapabilityId(val value: String)
@JvmInline value class PrincipalId(val value: String)
@JvmInline value class RequestId(val value: String)

data class CapabilityRef(
    val id: CapabilityId,
    val version: String = ContractVersions.CAPABILITY_DEFAULT
)

object ContractVersions {
    const val ENGINE_API = "mimir.engine/1.x"
    const val CAPABILITY_DEFAULT = "1.0"
    const val EVENT_ENVELOPE = "1.0"
}

enum class EngineState {
    Discovered, Configured, Starting, Running, Degraded, Stopping, Stopped, Failed
}

enum class RuntimeMode { Embedded, Isolated, External }

data class EngineManifest(
    val id: EngineId,
    val name: String,
    val version: String,
    val apiContract: String = ContractVersions.ENGINE_API,
    val publisher: String = "mimir",
    val runtimeMode: RuntimeMode = RuntimeMode.Embedded,
    val platforms: List<String> = listOf("android", "desktop"),
    val provides: List<CapabilityRef> = emptyList(),
    val requestedPermissions: List<String> = emptyList(),
    val dataNamespace: String = "",
    val priority: Int = 100,
)

data class HealthReport(
    val engineId: EngineId,
    val healthy: Boolean,
    val state: EngineState,
    val detail: String = "",
    val checkedAtEpochMs: Long = 0L,
)

data class InvocationRequest(
    val requestId: String,
    val jobId: String,
    val capability: CapabilityRef,
    val input: Map<String, String> = emptyMap(),
)

sealed class InvocationResult {
    abstract val requestId: String
    data class Ok(
        override val requestId: String,
        val output: Map<String, String> = emptyMap(),
        val evidenceRefs: List<String> = emptyList(),
    ) : InvocationResult()
    data class Err(
        override val requestId: String,
        val code: String,
        val message: String,
    ) : InvocationResult()
}

object KernelErrorCodes {
    const val ENGINE_NOT_RUNNING = "engine_not_running"
    const val PERMISSION_DENIED = "permission_denied"
    const val UNKNOWN_CAPABILITY = "unknown_capability"
    const val APPROVAL_REQUIRED = "approval_required"
    const val APPROVAL_INVALID = "approval_invalid"
    const val NO_POLICY = "no_policy"
    const val VALIDATION_FAILED = "validation_failed"
    const val CANCELLED = "cancelled"
    const val RATE_LIMITED = "rate_limited"
}

/** Platform event envelope — shared services and engines publish via kernel bus later. */
data class EventEnvelope(
    val type: String,
    val source: String,
    val correlationId: String? = null,
    val payload: Map<String, String> = emptyMap(),
    val version: String = ContractVersions.EVENT_ENVELOPE,
    val atEpochMs: Long = System.currentTimeMillis(),
)

enum class PolicyEffect { Allow, Deny, RequireApproval }

data class PolicyDecision(
    val effect: PolicyEffect,
    val reason: String,
    val requiredPermission: String? = null,
    val approvalActionId: String? = null,
)

interface MimirEngine {
    suspend fun describe(): EngineManifest
    suspend fun configure(config: Map<String, String>): EngineState
    suspend fun start(): EngineState
    suspend fun stop(reason: String): EngineState
    suspend fun health(): HealthReport
    suspend fun invoke(request: InvocationRequest): InvocationResult
}

/**
 * Optional base to reduce boilerplate; engines may implement [MimirEngine] directly.
 */
abstract class BaseMimirEngine(
    private val manifest: EngineManifest,
) : MimirEngine {
    @Volatile protected var state: EngineState = EngineState.Discovered

    override suspend fun describe(): EngineManifest = manifest

    override suspend fun configure(config: Map<String, String>): EngineState {
        state = EngineState.Configured
        return state
    }

    override suspend fun start(): EngineState {
        state = EngineState.Running
        return state
    }

    override suspend fun stop(reason: String): EngineState {
        state = EngineState.Stopped
        return state
    }

    override suspend fun health(): HealthReport =
        HealthReport(
            engineId = manifest.id,
            healthy = state == EngineState.Running || state == EngineState.Degraded,
            state = state,
            checkedAtEpochMs = System.currentTimeMillis(),
        )
}


/** In-process event fan-out — not durable; engines may publish lifecycle notes. */
fun interface KernelEventHandler {
    fun onEvent(event: EventEnvelope)
}

class KernelLocalEventBus {
    private val handlers = java.util.concurrent.CopyOnWriteArrayList<KernelEventHandler>()

    fun subscribe(handler: KernelEventHandler) {
        handlers.add(handler)
    }

    fun unsubscribe(handler: KernelEventHandler) {
        handlers.remove(handler)
    }

    fun publish(event: EventEnvelope) {
        handlers.forEach { runCatching { it.onEvent(event) } }
    }

    fun handlerCount(): Int = handlers.size
}

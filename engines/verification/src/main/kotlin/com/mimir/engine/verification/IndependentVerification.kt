package com.mimir.engine.verification

import com.mimir.contracts.process.TaskBoardPort
import com.mimir.contracts.process.TaskItemStatus
import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap

object VerifyCapabilities {
    const val SUBMIT = "verify.submit"
    const val VERDICT = "verify.verdict"
    const val CHECK_STALE = "verify.check_stale"
    val ALL = listOf(SUBMIT, VERDICT, CHECK_STALE)
}

enum class RejectReason {
    TEST_FAILURE, BEHAVIOR_MISMATCH, INSUFFICIENT_EVIDENCE, STALE_HEAD, SELF_ACCEPT, OTHER
}

data class VerdictReceipt(
    val taskId: String,
    val commitRef: String,
    val verifierId: String,
    val accepted: Boolean,
    val reason: String,
    val requirementFingerprint: String,
    val atEpochMs: Long,
)

/**
 * Independent verification — Builder cannot self-accept.
 */
class IndependentVerificationService(
    private val board: TaskBoardPort,
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val receipts = ConcurrentHashMap<String, VerdictReceipt>()
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("independent-verification"),
        name = "Mimir Independent Verification",
        version = "0.4.0",
        provides = VerifyCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("verify.write"),
        dataNamespace = "engine.verify",
        priority = 91,
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
        state = EngineState.Running; return state
    }
    override suspend fun stop(reason: String): EngineState {
        state = EngineState.Stopped; return state
    }
    override suspend fun health() =
        HealthReport(manifest.id, state == EngineState.Running, state, "receipts=${receipts.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            VerifyCapabilities.SUBMIT -> submit(request)
            VerifyCapabilities.VERDICT -> verdict(request)
            VerifyCapabilities.CHECK_STALE -> stale(request)
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun submit(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return inv(request, "id")
        val agent = request.input["agentId"] ?: return inv(request, "agentId")
        val commit = request.input["commitRef"] ?: return inv(request, "commitRef")
        val testsPassed = request.input["testsPassed"]?.equals("true", true) == true
        if (!testsPassed) return InvocationResult.Err(request.requestId, RejectReason.TEST_FAILURE.name, id)
        if (!board.markSubmitted(id, commit, agent)) {
            return InvocationResult.Err(request.requestId, "submit_denied", id)
        }
        board.markReview(id)
        return InvocationResult.Ok(request.requestId, mapOf("id" to id, "status" to "InReview", "commitRef" to commit))
    }

    private fun verdict(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return inv(request, "id")
        val verifier = request.input["verifierId"] ?: return inv(request, "verifierId")
        val item = board.getItem(id) ?: return InvocationResult.Err(request.requestId, "not_found", id)
        val builder = item.claimAgentId ?: item.activity.mapNotNull {
            if (it.startsWith("SUBMITTED:")) it.substringAfterLast('@') else null
        }.lastOrNull()
        // PB-07: builder cannot self-accept
        if (verifier == builder || verifier == item.claimAgentId) {
            return InvocationResult.Err(request.requestId, RejectReason.SELF_ACCEPT.name, verifier)
        }
        val accept = request.input["accept"]?.equals("true", true) == true
        val commit = request.input["commitRef"] ?: item.commitRef ?: return inv(request, "commitRef")
        if (item.commitRef != null && item.commitRef != commit) {
            return InvocationResult.Err(request.requestId, RejectReason.STALE_HEAD.name, commit)
        }
        val reason = request.input["reason"] ?: if (accept) "OK" else RejectReason.OTHER.name
        val receipt = VerdictReceipt(
            taskId = id,
            commitRef = commit,
            verifierId = verifier,
            accepted = accept,
            reason = reason,
            requirementFingerprint = item.frozenRequirementFingerprint ?: item.requirementIds.joinToString(","),
            atEpochMs = clockMs(),
        )
        receipts["$id:$commit"] = receipt
        if (accept) board.markAccepted(id) else board.markRejected(id, reason)
        return InvocationResult.Ok(
            request.requestId,
            mapOf(
                "id" to id,
                "accepted" to accept.toString(),
                "reason" to reason,
                "commitRef" to commit,
            ),
        )
    }

    private fun stale(request: InvocationRequest): InvocationResult {
        val id = request.input["id"] ?: return inv(request, "id")
        val commit = request.input["commitRef"] ?: return inv(request, "commitRef")
        val item = board.getItem(id) ?: return InvocationResult.Err(request.requestId, "not_found", id)
        val key = "$id:$commit"
        val receipt = receipts[key]
        val stale = receipt != null && item.commitRef != null && item.commitRef != commit
        return InvocationResult.Ok(request.requestId, mapOf("stale" to stale.toString(), "hasReceipt" to (receipt != null).toString()))
    }

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)
}

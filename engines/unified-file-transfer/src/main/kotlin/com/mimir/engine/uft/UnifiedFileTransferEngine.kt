package com.mimir.engine.uft

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object UftCapabilities {
    const val TRANSFER_CREATE = "uft.transfer.create"
    const val TRANSFER_PROGRESS = "uft.transfer.progress"
    const val TRANSFER_COMPLETE = "uft.transfer.complete"
    const val TRANSFER_CANCEL = "uft.transfer.cancel"
    const val TRANSFER_STATUS = "uft.transfer.status"
    const val TRANSFER_LIST = "uft.transfer.list"
    const val TRANSFER_CHECKSUM = "uft.transfer.checksum"
    const val TRANSFER_RESUME = "uft.transfer.resume"
    const val METRICS = "uft.metrics"
    val ALL = listOf(
        TRANSFER_CREATE, TRANSFER_PROGRESS, TRANSFER_COMPLETE,
        TRANSFER_CANCEL, TRANSFER_STATUS, TRANSFER_LIST, TRANSFER_CHECKSUM, TRANSFER_RESUME, METRICS,
    )
}

enum class UftDirection { Upload, Download, Copy }
enum class UftStatus { Queued, InProgress, Completed, Failed, Cancelled }

data class UftTransfer(
    val id: String,
    val direction: UftDirection,
    val source: String,
    val destination: String,
    var status: UftStatus = UftStatus.Queued,
    var progress: Int = 0,
    var bytesTotal: Long = 0,
    var bytesDone: Long = 0,
    var error: String = "",
    var checksum: String = "",
    var resumable: Boolean = false,
)

/** Tracking-only UFT; adapters move bytes later. */
class UnifiedFileTransferEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val transfers = ConcurrentHashMap<String, UftTransfer>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered
    private val manifest = EngineManifest(
        id = EngineId("uft-core"), name = "Mimir Unified File Transfer", version = "0.1.0",
        provides = UftCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("uft.read", "uft.write"),
        dataNamespace = "engine.uft", priority = 74,
        platforms = listOf("android", "desktop"), runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x", publisher = "mimir",
    )
    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState { state = EngineState.Configured; return state }
    override suspend fun start(): EngineState { state = EngineState.Running; return state }
    override suspend fun stop(reason: String): EngineState { state = EngineState.Stopped; return state }
    override suspend fun health() =
        HealthReport(manifest.id, state == EngineState.Running, state, "transfers=${transfers.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded)
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        return when (request.capability.id.value) {
            UftCapabilities.TRANSFER_CREATE -> {
                val dir = try { UftDirection.valueOf(request.input["direction"] ?: "Copy") }
                catch (_: Exception) { return InvocationResult.Err(request.requestId, "invalid", "direction") }
                val src = request.input["source"] ?: return inv(request, "source")
                val dst = request.input["destination"] ?: return inv(request, "destination")
                val id = "uft-${seq.incrementAndGet()}"
                val total = request.input["bytesTotal"]?.toLongOrNull() ?: 0L
                val resume = request.input["resumable"]?.equals("true", true) == true
                transfers[id] = UftTransfer(id, dir, src, dst, bytesTotal = total, status = UftStatus.InProgress, resumable = resume)
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "status" to "InProgress", "direction" to dir.name))
            }
            UftCapabilities.TRANSFER_PROGRESS -> {
                val t = get(request) ?: return notFound(request)
                if (t.status != UftStatus.InProgress) return InvocationResult.Err(request.requestId, "bad_state", t.status.name)
                request.input["bytesDone"]?.toLongOrNull()?.let {
                    t.bytesDone = it
                    if (t.bytesTotal > 0) t.progress = ((it * 100) / t.bytesTotal).toInt().coerceIn(0, 100)
                }
                request.input["progress"]?.toIntOrNull()?.let { t.progress = it.coerceIn(0, 100) }
                InvocationResult.Ok(request.requestId, mapOf("id" to t.id, "progress" to t.progress.toString(), "bytesDone" to t.bytesDone.toString()))
            }
            UftCapabilities.TRANSFER_COMPLETE -> {
                val t = get(request) ?: return notFound(request)
                val fail = request.input["failed"]?.equals("true", true) == true
                t.status = if (fail) UftStatus.Failed else UftStatus.Completed
                if (fail) t.error = request.input["error"] ?: "failed" else { t.progress = 100; if (t.bytesTotal > 0) t.bytesDone = t.bytesTotal }
                InvocationResult.Ok(request.requestId, mapOf("id" to t.id, "status" to t.status.name))
            }
            UftCapabilities.TRANSFER_CANCEL -> {
                val t = get(request) ?: return notFound(request)
                if (t.status == UftStatus.Completed) return InvocationResult.Err(request.requestId, "already_done", t.id)
                t.status = UftStatus.Cancelled
                InvocationResult.Ok(request.requestId, mapOf("id" to t.id, "status" to "Cancelled"))
            }
            UftCapabilities.TRANSFER_STATUS -> {
                val t = get(request) ?: return notFound(request)
                InvocationResult.Ok(request.requestId, mapOf(
                    "id" to t.id, "direction" to t.direction.name, "source" to t.source, "destination" to t.destination,
                    "status" to t.status.name, "progress" to t.progress.toString(), "error" to t.error,
                ))
            }
            UftCapabilities.TRANSFER_LIST ->
                InvocationResult.Ok(request.requestId, mapOf("count" to transfers.size.toString(), "ids" to transfers.keys.joinToString(",")))
            UftCapabilities.TRANSFER_CHECKSUM -> {
                val t = get(request) ?: return notFound(request)
                val sum = request.input["checksum"] ?: return inv(request, "checksum")
                t.checksum = sum
                InvocationResult.Ok(request.requestId, mapOf("id" to t.id, "checksum" to sum))
            }
            UftCapabilities.TRANSFER_RESUME -> {
                val t = get(request) ?: return notFound(request)
                if (!t.resumable) return InvocationResult.Err(request.requestId, "not_resumable", t.id)
                if (t.status == UftStatus.Completed) return InvocationResult.Err(request.requestId, "already_done", t.id)
                t.status = UftStatus.InProgress
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "id" to t.id,
                        "status" to "InProgress",
                        "bytesDone" to t.bytesDone.toString(),
                        "resumed" to "true",
                    ),
                )
            }
            UftCapabilities.METRICS ->
                InvocationResult.Ok(request.requestId, mapOf("state" to state.name, "engine" to "uft"))
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }
    private fun get(request: InvocationRequest) = request.input["id"]?.let { transfers[it] }
    private fun notFound(request: InvocationRequest) = InvocationResult.Err(request.requestId, "not_found", request.input["id"] ?: "")
    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)

    fun uftMetrics(): Map<String, String> = mapOf(
        "transfers" to transfers.size.toString(),
        "completed" to transfers.values.count { it.status.name == "Completed" }.toString(),
        "failed" to transfers.values.count { it.status.name == "Failed" }.toString(),
        "state" to state.name,
    )

}

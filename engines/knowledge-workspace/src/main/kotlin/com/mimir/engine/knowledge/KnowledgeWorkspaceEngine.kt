package com.mimir.engine.knowledge

import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object KnowledgeWorkspaceCapabilities {
    const val INDEX = "knowledge.index"
    const val SEARCH = "knowledge.search"
    const val GET = "knowledge.get"
    const val DELETE = "knowledge.delete"
    const val LIST = "knowledge.list"
    const val TAG = "knowledge.tag"
    const val METRICS = "knowledge.metrics"
    val ALL = listOf(INDEX, SEARCH, GET, DELETE, LIST, TAG, METRICS)
}

data class KnowledgeDoc(
    val id: String,
    val title: String,
    val body: String,
    val tags: MutableSet<String> = mutableSetOf(),
    val updatedAt: Long,
)

/**
 * Knowledge workspace — in-memory index + keyword search (no vendor RAG).
 */
class KnowledgeWorkspaceEngine(
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine {
    private val docs = ConcurrentHashMap<String, KnowledgeDoc>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered
    private val manifest = EngineManifest(
        id = EngineId("knowledge-workspace"),
        name = "Mimir Knowledge Workspace",
        version = "0.4.0",
        provides = KnowledgeWorkspaceCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("knowledge.read", "knowledge.write"),
        dataNamespace = "engine.knowledge",
        priority = 70,
        platforms = listOf("android", "desktop"),
        runtimeMode = RuntimeMode.Embedded,
        apiContract = "mimir.engine/1.x",
        publisher = "mimir",
    )

    override suspend fun describe() = manifest
    override suspend fun configure(config: Map<String, String>): EngineState { state = EngineState.Configured; return state }
    override suspend fun start(): EngineState { state = EngineState.Running; return state }
    override suspend fun stop(reason: String): EngineState { state = EngineState.Stopped; return state }
    override suspend fun health() =
        HealthReport(manifest.id, state == EngineState.Running, state, "docs=${docs.size}", clockMs())

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded)
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        return when (request.capability.id.value) {
            KnowledgeWorkspaceCapabilities.INDEX -> {
                val id = request.input["id"] ?: "doc-${seq.incrementAndGet()}"
                val title = request.input["title"] ?: id
                val body = request.input["body"] ?: ""
                val tags = (request.input["tags"] ?: "").split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toMutableSet()
                docs[id] = KnowledgeDoc(id, title, body, tags, clockMs())
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "chars" to body.length.toString(), "tags" to tags.size.toString()))
            }
            KnowledgeWorkspaceCapabilities.SEARCH -> {
                val q = (request.input["query"] ?: "").lowercase()
                if (q.isBlank()) return InvocationResult.Err(request.requestId, "invalid", "query")
                val limit = request.input["limit"]?.toIntOrNull() ?: 20
                val hits = docs.values.filter {
                    it.title.lowercase().contains(q) || it.body.lowercase().contains(q) || q in it.tags
                }.take(limit.coerceIn(1, 200))
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "count" to hits.size.toString(),
                        "ids" to hits.joinToString(",") { it.id },
                        "titles" to hits.joinToString("|") { it.title.take(40) },
                    ),
                )
            }
            KnowledgeWorkspaceCapabilities.GET -> {
                val id = request.input["id"] ?: return inv(request, "id")
                val d = docs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "id" to d.id,
                        "title" to d.title,
                        "body" to d.body,
                        "tags" to d.tags.joinToString(","),
                        "updatedAt" to d.updatedAt.toString(),
                    ),
                )
            }
            KnowledgeWorkspaceCapabilities.DELETE -> {
                val id = request.input["id"] ?: return inv(request, "id")
                val removed = docs.remove(id) != null
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "removed" to removed.toString()))
            }
            KnowledgeWorkspaceCapabilities.LIST ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("count" to docs.size.toString(), "ids" to docs.keys.joinToString(",")),
                )
            KnowledgeWorkspaceCapabilities.TAG -> {
                val id = request.input["id"] ?: return inv(request, "id")
                val d = docs[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val tag = request.input["tag"]?.lowercase()?.trim() ?: return inv(request, "tag")
                d.tags += tag
                InvocationResult.Ok(request.requestId, mapOf("id" to id, "tags" to d.tags.joinToString(",")))
            }
            KnowledgeWorkspaceCapabilities.METRICS ->
                InvocationResult.Ok(request.requestId, mapOf("docs" to docs.size.toString(), "state" to state.name))
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }
    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)

    fun knowledgeMetrics(): Map<String, String> = mapOf(
        "docs" to docs.size.toString(),
        "state" to state.name,
    )

}

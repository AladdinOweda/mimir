package com.mimir.engine.chat

import com.mimir.contracts.chat.ChatCapabilityIds
import com.mimir.contracts.chat.ChatMessageId
import com.mimir.contracts.chat.ChatPort
import com.mimir.contracts.chat.ChatRole
import com.mimir.contracts.chat.ChatThreadId
import com.mimir.kernel.contract.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

object ChatCapabilities {
    const val THREAD_CREATE = ChatCapabilityIds.THREAD_CREATE
    const val THREAD_LIST = ChatCapabilityIds.THREAD_LIST
    const val THREAD_GET = ChatCapabilityIds.THREAD_GET
    const val THREAD_ARCHIVE = ChatCapabilityIds.THREAD_ARCHIVE
    const val MESSAGE_SEND = ChatCapabilityIds.MESSAGE_SEND
    const val MESSAGE_LIST = ChatCapabilityIds.MESSAGE_LIST
    const val COMPLETE = ChatCapabilityIds.COMPLETE
    const val METRICS = ChatCapabilityIds.METRICS
    const val STREAM_START = "chat.stream.start"
    const val TOOL_RESULT = "chat.tool.result"
    const val ATTACH_CONTEXT = "chat.context.attach"
    val ALL = ChatCapabilityIds.ALL + listOf(STREAM_START, TOOL_RESULT, ATTACH_CONTEXT)
}

data class ChatMessage(
    val id: String,
    val threadId: String,
    val role: String,
    val text: String,
    val atEpochMs: Long,
    val modelRef: String = "",
)

data class ChatThread(
    val id: String,
    var title: String,
    var modelRef: String,
    var status: String = "open", // open | archived
    val createdAtEpochMs: Long,
    var updatedAtEpochMs: Long,
    val messageIds: MutableList<String> = mutableListOf(),
    val contextRefs: MutableList<String> = mutableListOf(),
    var sessionId: String? = null,
)

/**
 * Primary in-app Chat engine (product workspace Chat).
 * Browser chat extract is separate (multi-vendor web UIs).
 * Completion is pluggable: default local echo/stub; host can inject [CompletionPort].
 */
fun interface CompletionPort {
    fun complete(threadId: String, modelRef: String, history: List<ChatMessage>): String
}

class EchoCompletionPort : CompletionPort {
    override fun complete(threadId: String, modelRef: String, history: List<ChatMessage>): String {
        val lastUser = history.lastOrNull { it.role == "user" }?.text ?: ""
        return if (lastUser.isBlank()) "(empty)" else "echo: ${lastUser.take(500)}"
    }
}

class ChatEngine(
    private val completion: CompletionPort = EchoCompletionPort(),
    private val memory: ChatMemoryStore? = null,
    private val clockMs: () -> Long = { System.currentTimeMillis() },
) : MimirEngine, ChatPort {
    private val threads = ConcurrentHashMap<String, ChatThread>()
    private val messages = ConcurrentHashMap<String, ChatMessage>()
    private val seq = AtomicLong(0)
    private var state = EngineState.Discovered

    private val manifest = EngineManifest(
        id = EngineId("chat-core"),
        name = "Mimir Chat",
        version = "0.5.0",
        provides = ChatCapabilities.ALL.map { CapabilityRef(CapabilityId(it), "1.0") },
        requestedPermissions = listOf("chat.read", "chat.write"),
        dataNamespace = "engine.chat",
        priority = 75,
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
        HealthReport(
            manifest.id,
            state == EngineState.Running,
            state,
            "threads=${threads.size} messages=${messages.size}",
            clockMs(),
        )

    override suspend fun invoke(request: InvocationRequest): InvocationResult {
        if (state != EngineState.Running && state != EngineState.Degraded) {
            return InvocationResult.Err(request.requestId, KernelErrorCodes.ENGINE_NOT_RUNNING, state.name)
        }
        return when (request.capability.id.value) {
            ChatCapabilities.THREAD_CREATE -> {
                val title = request.input["title"] ?: "Chat"
                val model = request.input["modelRef"] ?: ""
                val id = createThread(title, model).value
                InvocationResult.Ok(request.requestId, mapOf("threadId" to id, "title" to title, "modelRef" to model))
            }
            ChatCapabilities.THREAD_LIST -> {
                val status = request.input["status"]
                val list = threads.values.filter { status == null || it.status == status }
                    .sortedByDescending { it.updatedAtEpochMs }
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "count" to list.size.toString(),
                        "ids" to list.joinToString(",") { it.id },
                        "titles" to list.joinToString(";") { "${it.id}|${it.title}|${it.status}" }.take(2_000),
                    ),
                )
            }
            ChatCapabilities.THREAD_GET -> {
                val id = request.input["threadId"] ?: return inv(request, "threadId")
                val t = threads[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "threadId" to t.id,
                        "title" to t.title,
                        "modelRef" to t.modelRef,
                        "status" to t.status,
                        "messageCount" to t.messageIds.size.toString(),
                        "updatedAt" to t.updatedAtEpochMs.toString(),
                    ),
                )
            }
            ChatCapabilities.THREAD_ARCHIVE -> {
                val id = request.input["threadId"] ?: return inv(request, "threadId")
                val t = threads[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                t.status = "archived"
                t.updatedAtEpochMs = clockMs()
                InvocationResult.Ok(request.requestId, mapOf("threadId" to id, "status" to "archived"))
            }
            ChatCapabilities.MESSAGE_SEND -> {
                val id = request.input["threadId"] ?: return inv(request, "threadId")
                val role = (request.input["role"] ?: "user").lowercase()
                val text = request.input["text"] ?: return inv(request, "text")
                if (text.isBlank()) return InvocationResult.Err(request.requestId, "invalid", "text")
                val chatRole = when (role) {
                    "assistant" -> ChatRole.Assistant
                    "system" -> ChatRole.System
                    "tool" -> ChatRole.Tool
                    else -> ChatRole.User
                }
                val mid = send(ChatThreadId(id), chatRole, text).value
                InvocationResult.Ok(
                    request.requestId,
                    mapOf("messageId" to mid, "threadId" to id, "role" to chatRole.name.lowercase()),
                )
            }
            ChatCapabilities.MESSAGE_LIST -> {
                val id = request.input["threadId"] ?: return inv(request, "threadId")
                val t = threads[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val limit = request.input["limit"]?.toIntOrNull() ?: 100
                val msgs = t.messageIds.mapNotNull { messages[it] }.takeLast(limit)
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "threadId" to id,
                        "count" to msgs.size.toString(),
                        "messages" to msgs.joinToString("\n") { "${it.role}|${it.text}" }.take(8_000),
                    ),
                )
            }
            ChatCapabilities.COMPLETE -> {
                val id = request.input["threadId"] ?: return inv(request, "threadId")
                val t = threads[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                if (t.status != "open") return InvocationResult.Err(request.requestId, "archived", id)
                val history = t.messageIds.mapNotNull { messages[it] }
                val reply = completion.complete(id, t.modelRef, history)
                val mid = send(ChatThreadId(id), ChatRole.Assistant, reply).value
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "threadId" to id,
                        "messageId" to mid,
                        "role" to "assistant",
                        "text" to reply.take(4_000),
                        "modelRef" to t.modelRef,
                        "contextCount" to t.contextRefs.size.toString(),
                    ),
                )
            }

            ChatCapabilities.STREAM_START -> {
                val id = request.input["threadId"] ?: return inv(request, "threadId")
                val th = threads[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                if (th.status != "open") return InvocationResult.Err(request.requestId, "archived", id)
                val streamId = "cstr-${seq.incrementAndGet()}"
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "threadId" to id,
                        "streamId" to streamId,
                        "status" to "started",
                        "modelRef" to th.modelRef,
                        "note" to "chunks_delivered_by_host_completion_port",
                    ),
                )
            }
            ChatCapabilities.TOOL_RESULT -> {
                val id = request.input["threadId"] ?: return inv(request, "threadId")
                val text = request.input["text"] ?: return inv(request, "text")
                val toolName = request.input["toolName"] ?: "tool"
                val mid = send(ChatThreadId(id), ChatRole.Tool, "[$toolName] $text").value
                InvocationResult.Ok(request.requestId, mapOf("messageId" to mid, "role" to "tool", "toolName" to toolName))
            }
            ChatCapabilities.ATTACH_CONTEXT -> {
                val id = request.input["threadId"] ?: return inv(request, "threadId")
                val th = threads[id] ?: return InvocationResult.Err(request.requestId, "not_found", id)
                val ref = request.input["contextRef"] ?: return inv(request, "contextRef")
                th.contextRefs += ref
                request.input["sessionId"]?.let { th.sessionId = it }
                th.updatedAtEpochMs = clockMs()
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "threadId" to id,
                        "contextCount" to th.contextRefs.size.toString(),
                        "sessionId" to (th.sessionId ?: ""),
                    ),
                )
            }

            ChatCapabilities.METRICS ->
                InvocationResult.Ok(
                    request.requestId,
                    mapOf(
                        "threads" to threads.size.toString(),
                        "open" to threads.values.count { it.status == "open" }.toString(),
                        "messages" to messages.size.toString(),
                        "state" to state.name,
                        "memoryKeys" to (memory?.size() ?: 0).toString(),
                    ),
                )
            else -> InvocationResult.Err(request.requestId, "unsupported", request.capability.id.value)
        }
    }

    private fun inv(r: InvocationRequest, f: String) = InvocationResult.Err(r.requestId, "invalid", f)

    // --- ChatPort ---
    override fun createThread(title: String, modelRef: String): ChatThreadId {
        val id = "cth-${seq.incrementAndGet()}"
        val now = clockMs()
        threads[id] = ChatThread(id, title, modelRef, createdAtEpochMs = now, updatedAtEpochMs = now)
        return ChatThreadId(id)
    }

    override fun send(threadId: ChatThreadId, role: ChatRole, text: String): ChatMessageId {
        val t = threads[threadId.value] ?: error("thread_not_found")
        val mid = "cmsg-${seq.incrementAndGet()}"
        val msg = ChatMessage(mid, t.id, role.name.lowercase(), text, clockMs(), t.modelRef)
        messages[mid] = msg
        t.messageIds += mid
        t.updatedAtEpochMs = clockMs()
        memory?.put(t.id, "title=${t.title};msgs=${t.messageIds.size};status=${t.status}")
        return ChatMessageId(mid)
    }

    override fun listMessages(threadId: ChatThreadId): List<Pair<ChatRole, String>> {
        val t = threads[threadId.value] ?: return emptyList()
        return t.messageIds.mapNotNull { messages[it] }.map { m ->
            val r = when (m.role) {
                "assistant" -> ChatRole.Assistant
                "system" -> ChatRole.System
                "tool" -> ChatRole.Tool
                else -> ChatRole.User
            }
            r to m.text
        }
    }
}

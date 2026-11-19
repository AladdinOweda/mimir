package com.mimir.contracts.chat

/** In-app Chat workspace (UX 02-chat) — not browser extract. */
object ChatCapabilityIds {
    const val THREAD_CREATE = "chat.thread.create"
    const val THREAD_LIST = "chat.thread.list"
    const val THREAD_GET = "chat.thread.get"
    const val THREAD_ARCHIVE = "chat.thread.archive"
    const val MESSAGE_SEND = "chat.message.send"
    const val MESSAGE_LIST = "chat.message.list"
    const val COMPLETE = "chat.complete"
    const val METRICS = "chat.metrics"
    const val STREAM_START = "chat.stream.start"
    const val TOOL_RESULT = "chat.tool.result"
    const val ATTACH_CONTEXT = "chat.context.attach"
    val ALL = listOf(
        THREAD_CREATE, THREAD_LIST, THREAD_GET, THREAD_ARCHIVE,
        MESSAGE_SEND, MESSAGE_LIST, COMPLETE, METRICS,
        STREAM_START, TOOL_RESULT, ATTACH_CONTEXT,
    )
}

object ChatPermissionIds {
    const val READ = "chat.read"
    const val WRITE = "chat.write"
}

data class ChatThreadId(val value: String)
data class ChatMessageId(val value: String)

enum class ChatRole { User, Assistant, System, Tool }

interface ChatPort {
    fun createThread(title: String, modelRef: String = ""): ChatThreadId
    fun send(threadId: ChatThreadId, role: ChatRole, text: String): ChatMessageId
    fun listMessages(threadId: ChatThreadId): List<Pair<ChatRole, String>>
}

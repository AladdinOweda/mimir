package com.mimir.service.notifications

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val channel: String = "general",
    val read: Boolean = false,
    val epochMs: Long = System.currentTimeMillis()
)

interface NotificationService {
    fun push(title: String, body: String, channel: String = "general"): Notification
    fun unread(): List<Notification>
    fun markRead(id: String): Boolean
}

class InMemoryNotificationService : NotificationService {
    private val items = linkedMapOf<String, Notification>()
    private var seq = 0L
    override fun push(title: String, body: String, channel: String): Notification {
        val n = Notification(id = "n-${++seq}", title = title, body = body, channel = channel)
        items[n.id] = n
        return n
    }
    override fun unread() = items.values.filter { !it.read }
    override fun markRead(id: String): Boolean {
        val n = items[id] ?: return false
        items[id] = n.copy(read = true)
        return true
    }
}

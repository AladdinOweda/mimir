package com.mimir.service.activity

/**
 * Activity / history application service (Blueprint §3).
 */
data class ActivityEvent(
    val id: String,
    val type: String,
    val summary: String,
    val resource: String? = null,
    val epochMs: Long = System.currentTimeMillis()
)

interface ActivityLog {
    fun record(type: String, summary: String, resource: String? = null): ActivityEvent
    fun recent(limit: Int = 50): List<ActivityEvent>
}

class InMemoryActivityLog : ActivityLog {
    private val events = ArrayDeque<ActivityEvent>()
    private var seq = 0L
    override fun record(type: String, summary: String, resource: String?): ActivityEvent {
        val e = ActivityEvent(id = "act-${++seq}", type = type, summary = summary, resource = resource)
        synchronized(events) {
            events.addFirst(e)
            while (events.size > 500) events.removeLast()
        }
        return e
    }
    override fun recent(limit: Int): List<ActivityEvent> = synchronized(events) { events.take(limit) }
}

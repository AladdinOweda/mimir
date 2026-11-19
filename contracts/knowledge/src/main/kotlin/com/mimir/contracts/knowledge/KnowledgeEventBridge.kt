package com.mimir.contracts.knowledge

import com.mimir.platform.events.EventBus
import com.mimir.platform.events.EventCatalog

class KnowledgeEventBridge(private val events: EventBus) {
    fun onIndexed(id: String, title: String) {
        events.publish(EventCatalog.KNOWLEDGE_INDEXED, "knowledge", mapOf("id" to id, "title" to title))
    }
}

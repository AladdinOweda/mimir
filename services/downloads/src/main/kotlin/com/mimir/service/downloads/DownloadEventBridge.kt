package com.mimir.service.downloads

import com.mimir.platform.events.EventBus
import com.mimir.platform.events.EventCatalog

/** Optional bridge: download lifecycle → EventBus (INT-010 subset). */
class DownloadEventBridge(private val events: EventBus) {
    fun onEnqueued(item: DownloadItem) {
        events.publish(
            EventCatalog.DOWNLOAD_ENQUEUED,
            "downloads",
            mapOf("id" to item.id.value, "url" to item.url, "name" to item.suggestedName),
        )
    }

    fun onCompleted(item: DownloadItem) {
        events.publish(
            EventCatalog.DOWNLOAD_COMPLETED,
            "downloads",
            mapOf("id" to item.id.value, "bytes" to item.bytesReceived.toString()),
        )
    }
}

package com.mimir.service.downloads

/**
 * INT-005 lifecycle (S2 skeleton). Acquisition only — not full browser download manager.
 */
enum class DownloadStatus {
    Queued,
    Fetching,
    Paused,
    Completed,
    Failed,
    Cancelled,
}

data class DownloadId(val value: String)

data class DownloadItem(
    val id: DownloadId,
    val url: String,
    val suggestedName: String,
    val status: DownloadStatus = DownloadStatus.Queued,
    val bytesReceived: Long = 0,
    val error: String? = null,
)

interface DownloadDirectory {
    fun list(): List<DownloadItem>
    fun enqueue(url: String, suggestedName: String = url.substringAfterLast('/')): DownloadItem
    fun get(id: DownloadId): DownloadItem?
    fun update(id: DownloadId, status: DownloadStatus, bytesReceived: Long = 0, error: String? = null): DownloadItem?
}

class InMemoryDownloadDirectory : DownloadDirectory {
    private val map = linkedMapOf<String, DownloadItem>()
    private var seq = 0L

    override fun list() = map.values.toList()

    override fun enqueue(url: String, suggestedName: String): DownloadItem {
        val id = DownloadId("dl-${++seq}")
        val item = DownloadItem(id, url, suggestedName.ifBlank { "download" })
        map[id.value] = item
        return item
    }

    override fun get(id: DownloadId) = map[id.value]

    override fun update(
        id: DownloadId,
        status: DownloadStatus,
        bytesReceived: Long,
        error: String?,
    ): DownloadItem? {
        val cur = map[id.value] ?: return null
        val next = cur.copy(status = status, bytesReceived = bytesReceived, error = error)
        map[id.value] = next
        return next
    }
}

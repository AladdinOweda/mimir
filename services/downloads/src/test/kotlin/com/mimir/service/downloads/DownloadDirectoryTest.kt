package com.mimir.service.downloads

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DownloadDirectoryTest {
    @Test
    fun enqueue_and_complete() {
        val dir = InMemoryDownloadDirectory()
        val item = dir.enqueue("https://example.com/a.bin", "a.bin")
        assertEquals(DownloadStatus.Queued, item.status)
        val done = dir.update(item.id, DownloadStatus.Completed, bytesReceived = 10)
        assertNotNull(done)
        assertEquals(DownloadStatus.Completed, done.status)
        assertEquals(10, done.bytesReceived)
        assertEquals(1, dir.list().size)
    }
}

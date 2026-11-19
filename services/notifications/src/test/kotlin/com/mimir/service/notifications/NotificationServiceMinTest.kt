package com.mimir.service.notifications

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationServiceMinTest {
    @Test
    fun markRead_missing_returns_false() {
        assertFalse(InMemoryNotificationService().markRead("nope"))
    }

    @Test
    fun push_appears_unread_then_read() {
        val s = InMemoryNotificationService()
        val n = s.push("t", "b")
        assertTrue(s.unread().any { it.id == n.id })
        assertTrue(s.markRead(n.id))
        assertTrue(s.unread().none { it.id == n.id })
    }
}

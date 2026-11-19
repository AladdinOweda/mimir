package com.mimir.platform.events

import com.mimir.kernel.contract.EventEnvelope
import kotlin.test.Test
import kotlin.test.assertEquals

class EventBusMinTest {
    @Test
    fun publish_with_no_subscribers_does_not_throw() {
        EventBus().publish("t", "src", mapOf("k" to "v"))
    }
    @Test
    fun subscriber_receives_event_and_handler_failure_is_isolated() {
        val bus = EventBus()
        var count = 0
        bus.subscribe(EventHandler { count++ })
        bus.subscribe(EventHandler { throw RuntimeException("boom") })
        bus.subscribe(EventHandler { count++ })
        bus.publish(EventEnvelope(type = "x", source = "test", payload = emptyMap()))
        assertEquals(2, count)
    }
}

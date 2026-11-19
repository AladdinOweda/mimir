package com.mimir.contracts.events
import kotlin.test.Test
import kotlin.test.assertEquals
class EventsContractTest {
    @Test fun names() { assertEquals("task.created", ProductEventNames.TASK_CREATED) }
}

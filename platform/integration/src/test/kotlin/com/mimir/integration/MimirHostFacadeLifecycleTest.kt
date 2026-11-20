package com.mimir.integration

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MimirHostFacadeLifecycleTest {

    @Test
    fun engines_start_stop_restart_and_close_cleanly() = runBlocking {
        val facade = MimirHostFacade()

        facade.startAll()

        val healthAfterStart = facade.healthLine()
        val expectedIds = listOf("tools", "terminal", "forge", "vault", "ai", "workflow")

        expectedIds.forEach { id ->
            assertTrue(
                healthAfterStart.contains("$id:Running:true"),
                "Expected $id to be running and healthy: $healthAfterStart",
            )
        }

        val firstStop = facade.stopAll()
        assertEquals(expectedIds.toSet(), firstStop.keys)
        assertTrue(firstStop.values.all { it.name == "Stopped" })

        val healthAfterStop = facade.healthLine()
        expectedIds.forEach { id ->
            assertTrue(
                healthAfterStop.contains("$id:Stopped:false"),
                "Expected $id to be stopped and unhealthy: $healthAfterStop",
            )
        }

        facade.startAll()

        val healthAfterRestart = facade.healthLine()
        expectedIds.forEach { id ->
            assertTrue(healthAfterRestart.contains("$id:Running:true"))
        }

        val secondStop = facade.stopAll()
        assertEquals(expectedIds.toSet(), secondStop.keys)
        assertTrue(secondStop.values.all { it.name == "Stopped" })

        facade.close()

        assertFailsWith<IllegalStateException> {
            facade.startAll()
        }
    }
}

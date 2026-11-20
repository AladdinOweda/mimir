package com.mimir.integration

import com.mimir.kernel.contract.EngineState
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EngineBootstrapLifecycleTest {

    @Test
    fun coreEngines_start_stop_restart_and_close_cleanly() = runBlocking {
        val bootstrap = EngineBootstrap()

        bootstrap.startAll(includeExtended = false)

        val ids = bootstrap.registeredIds()
        assertEquals(12, ids.size, "Expected the 12 core engines to be registered")

        ids.forEach { id ->
            assertEquals(
                EngineState.Running,
                bootstrap.host.stateOf(id),
                "Engine '$id' should be running",
            )
        }

        val firstStop = bootstrap.stopAll()
        assertEquals(ids.toSet(), firstStop.keys)
        assertTrue(firstStop.values.all { it == EngineState.Stopped })

        ids.forEach { id ->
            assertEquals(EngineState.Stopped, bootstrap.host.stateOf(id))
        }

        bootstrap.startAll(includeExtended = false)

        ids.forEach { id ->
            assertEquals(EngineState.Running, bootstrap.host.stateOf(id))
        }

        val secondStop = bootstrap.stopAll()
        assertEquals(ids.toSet(), secondStop.keys)
        assertTrue(secondStop.values.all { it == EngineState.Stopped })

        bootstrap.close()

        assertFailsWith<IllegalStateException> {
            bootstrap.startAll(includeExtended = false)
        }
    }
}

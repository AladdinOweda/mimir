package com.mimir.composition

import com.mimir.engine.browser.BrowserCapabilities
import com.mimir.engine.terminal.TerminalCapabilities
import com.mimir.kernel.contract.BaseMimirEngine
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.EngineId
import com.mimir.kernel.contract.EngineManifest
import com.mimir.kernel.contract.EngineState
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class ProductContextSmokeTest {
    @Test
    fun minimal_attach_start_terminal_session() = runBlocking {
        val dir = File.createTempFile("mimir-smoke", "").apply { delete(); mkdirs() }
        val ctx = ProductContext(dataDir = dir, useDurableStores = false).attachMinimalEngines()
        val states = ctx.startAll()
        assertTrue(states.isNotEmpty(), "expected engines started: $states")
        val eng = ctx.engines.values.first { it.describe().id.value.contains("terminal") }
        // open session via engine invoke
        val open = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(TerminalCapabilities.SESSION_OPEN)),
                mapOf("cwd" to dir.absolutePath),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
    }

    @Test
    fun smoke_http_browser_open_tab() = runBlocking {
        val dir = File.createTempFile("mimir-smoke-b", "").apply { delete(); mkdirs() }
        val ctx = ProductContext(dataDir = dir, useDurableStores = false).attachSmokeWithHttpBrowser()
        ctx.startAll()
        val browser = ctx.engines.values.first { runBlocking { it.describe().id.value.contains("browser") } }
        val open = browser.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)),
                mapOf("url" to "about:blank"),
            ),
        )
        assertTrue(open is InvocationResult.Ok, open.toString())
        val info = browser.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.DRIVER_INFO)),
                emptyMap(),
            ),
        )
        assertTrue(info is InvocationResult.Ok, info.toString())
    }
}


private class ProductContextLifecycleTestEngine(
    private val engineId: String,
    private val stopOrder: MutableList<String>,
    private var failuresRemaining: Int = 0,
) : BaseMimirEngine(
    EngineManifest(
        id = EngineId(engineId),
        name = engineId,
        version = "1.0",
        dataNamespace = "test.$engineId",
    ),
) {
    override suspend fun stop(reason: String): EngineState {
        stopOrder += engineId
        if (failuresRemaining > 0) {
            failuresRemaining--
            throw IllegalStateException("planned stop failure: $engineId")
        }
        return super.stop(reason)
    }

    override suspend fun invoke(request: InvocationRequest): InvocationResult =
        InvocationResult.Ok(request.requestId)
}

private suspend fun ProductContext.attachLifecycleTestEngine(
    engine: ProductContextLifecycleTestEngine,
) {
    val manifest = engine.describe()
    host.attach(engine, manifest)
    engines[manifest.id.value] = engine
}

class ProductContextLifecycleTest {
    @Test
    fun stopAll_stops_in_reverse_start_order_and_allows_close() = runBlocking {
        val ctx = ProductContext(useDurableStores = false)
        val order = mutableListOf<String>()

        ctx.attachLifecycleTestEngine(ProductContextLifecycleTestEngine("first", order))
        ctx.attachLifecycleTestEngine(ProductContextLifecycleTestEngine("second", order))

        ctx.startAll()
        val stopped = ctx.stopAll()

        assertEquals(listOf("second", "first"), order)
        assertEquals(EngineState.Stopped, stopped["first"])
        assertEquals(EngineState.Stopped, stopped["second"])

        ctx.close()
    }

    @Test
    fun stopAll_keeps_failed_engine_pending_and_retries() = runBlocking {
        val ctx = ProductContext(useDurableStores = false)
        val order = mutableListOf<String>()

        ctx.attachLifecycleTestEngine(
            ProductContextLifecycleTestEngine("retry-engine", order, failuresRemaining = 1),
        )
        ctx.startAll()

        val firstAttempt = ctx.stopAll()
        assertEquals(EngineState.Failed, firstAttempt["retry-engine"])
        assertFailsWith<IllegalStateException> { ctx.close() }

        val secondAttempt = ctx.stopAll()
        assertEquals(EngineState.Stopped, secondAttempt["retry-engine"])
        assertEquals(listOf("retry-engine", "retry-engine"), order)

        ctx.close()
    }

    @Test
    fun attaching_engine_after_stopAll_prevents_premature_close() = runBlocking {
        val ctx = ProductContext(useDurableStores = false)
        val order = mutableListOf<String>()

        ctx.attachLifecycleTestEngine(ProductContextLifecycleTestEngine("first", order))
        ctx.startAll()
        ctx.stopAll()

        ctx.attachMinimalEngines()
        assertFailsWith<IllegalStateException> { ctx.close() }

        ctx.startAll()
        val stopped = ctx.stopAll()
        assertTrue(stopped.isNotEmpty())
        assertTrue(stopped.values.all { it == EngineState.Stopped })

        ctx.close()
    }
}

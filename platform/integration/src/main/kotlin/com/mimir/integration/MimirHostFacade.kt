package com.mimir.integration

import com.mimir.engine.ai.AiModelEngine
import com.mimir.engine.forge.ForgeEngine
import com.mimir.adapters.process.JvmProcessExecutor
import com.mimir.engine.terminal.TerminalEngine
import com.mimir.engine.tools.SecureInvoker
import com.mimir.engine.tools.ToolRegistryEngine
import com.mimir.engine.vault.VaultEngine
import com.mimir.engine.workflow.WorkflowEngine
import com.mimir.engine.workflow.persist.WorkflowRunStore
import com.mimir.engine.workflow.persist.InMemoryWorkflowRunStore
import com.mimir.kernel.contract.EngineState
import com.mimir.kernel.contract.MimirEngine

/** Facade for phone-side / smoke integration without full DI graph. */
class MimirHostFacade(
    store: WorkflowRunStore = InMemoryWorkflowRunStore(),
) : AutoCloseable {
    private val processExecutor = JvmProcessExecutor()
    private val startAttemptOrder = linkedMapOf<String, MimirEngine>()
    private var shutdownPrepared = false
    private var closed = false

    private fun ensureOpen() {
        check(!closed) { "MimirHostFacade is closed" }
    }

    val tools = ToolRegistryEngine()
    val secure = SecureInvoker(tools)
    val terminal = TerminalEngine(executor = processExecutor)
    val forge = ForgeEngine(executor = processExecutor)
    val vault = VaultEngine()
    val ai = AiModelEngine()
    val workflow = WorkflowEngine(store = store)

    private fun allEngines(): List<MimirEngine> =
        listOf(tools, terminal, forge, vault, ai, workflow)

    suspend fun startAll() {
        ensureOpen()
        shutdownPrepared = false
        allEngines().forEach { engine ->
            val id = engine.describe().id.value
            if (id in startAttemptOrder) return@forEach
            startAttemptOrder[id] = engine
            engine.start()
        }
    }

    suspend fun stopAll(): Map<String, EngineState> {
        ensureOpen()
        shutdownPrepared = false
        val results = linkedMapOf<String, EngineState>()

        startAttemptOrder.entries.toList().asReversed().forEach { (id, engine) ->
            val result = try {
                engine.stop("host_facade_shutdown")
            } catch (_: Exception) {
                EngineState.Failed
            }
            results[id] = result
            if (result == EngineState.Stopped) {
                startAttemptOrder.remove(id)
            }
        }

        shutdownPrepared = startAttemptOrder.isEmpty()
        return results
    }

    suspend fun healthLine(): String {
        ensureOpen()
        val parts = allEngines().map {
            val h = it.health()
            "${h.engineId.value}:${h.state.name}:${h.healthy}"
        }
        return parts.joinToString(" | ")
    }

    override fun close() {
        ensureOpen()
        check(shutdownPrepared && startAttemptOrder.isEmpty()) {
            "MimirHostFacade.close() requires a successful stopAll() first"
        }
        processExecutor.close()
        closed = true
    }
}

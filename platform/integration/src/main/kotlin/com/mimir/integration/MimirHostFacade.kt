package com.mimir.integration

import com.mimir.engine.ai.AiModelEngine
import com.mimir.engine.forge.ForgeEngine
import com.mimir.engine.terminal.JvmProcessExecutor
import com.mimir.engine.terminal.TerminalEngine
import com.mimir.engine.tools.SecureInvoker
import com.mimir.engine.tools.ToolRegistryEngine
import com.mimir.engine.vault.VaultEngine
import com.mimir.engine.workflow.WorkflowEngine
import com.mimir.engine.workflow.persist.WorkflowRunStore
import com.mimir.engine.workflow.persist.InMemoryWorkflowRunStore
import com.mimir.kernel.contract.EngineState

/** Facade for phone-side / smoke integration without full DI graph. */
class MimirHostFacade(
    store: WorkflowRunStore = InMemoryWorkflowRunStore(),
) {
    val tools = ToolRegistryEngine()
    val secure = SecureInvoker(tools)
    val terminal = TerminalEngine(JvmProcessExecutor())
    val forge = ForgeEngine()
    val vault = VaultEngine()
    val ai = AiModelEngine()
    val workflow = WorkflowEngine(store = store)

    suspend fun startAll() {
        listOf(tools, terminal, forge, vault, ai, workflow).forEach { it.start() }
    }

    suspend fun healthLine(): String {
        val parts = listOf(tools, terminal, forge, vault, ai, workflow).map {
            val h = it.health()
            "${h.engineId.value}:${h.state.name}:${h.healthy}"
        }
        return parts.joinToString(" | ")
    }
}

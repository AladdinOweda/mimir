package com.mimir.integration

import com.mimir.engine.agent.AgentRuntimeEngine
import com.mimir.engine.ai.AiModelEngine
import com.mimir.engine.bot.profile.BotProfileService
import com.mimir.engine.bot.tasks.BackgroundTaskService
import com.mimir.engine.browser.BrowserEngine
import com.mimir.engine.computer.ComputerUseEngine
import com.mimir.engine.forge.ForgeEngine
import com.mimir.engine.orchestration.CollaborationService
import com.mimir.engine.process.ProcessRuntimeEngine
import com.mimir.engine.process.TaskBoardEngine
import com.mimir.engine.provider.registry.ProviderRegistryEngine
import com.mimir.engine.session.SessionContinuityEngine
import com.mimir.adapters.process.JvmProcessExecutor
import com.mimir.engine.terminal.TerminalEngine
import com.mimir.engine.tools.ToolRegistryEngine
import com.mimir.engine.vault.VaultEngine
import com.mimir.engine.verification.IndependentVerificationService
import com.mimir.engine.workflow.WorkflowEngine
import com.mimir.kernel.contract.MimirEngine
import com.mimir.kernel.contract.EngineState
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext

/**
 * Single bootstrap wiring official engines into KernelHost.
 * Only canonical classes from ENGINE-REGISTRY-MATRIX — no V2/V3.
 */
class EngineBootstrap(
    val host: KernelHost = KernelHost(),
) : AutoCloseable {
    private val processExecutor = JvmProcessExecutor()
    private val startAttemptOrder = linkedSetOf<String>()
    private val ownedEngineIds = linkedSetOf<String>()
    private var shutdownPrepared = false
    private var closed = false

    private fun ensureOpen() {
        check(!closed) { "EngineBootstrap is closed" }
    }

    val tools = ToolRegistryEngine()
    val terminal = TerminalEngine(executor = processExecutor)
    val forge = ForgeEngine(executor = processExecutor)
    val vault = VaultEngine()
    val ai = AiModelEngine()
    val workflow = WorkflowEngine()
    val taskBoard = TaskBoardEngine()
    val verification = IndependentVerificationService(taskBoard)
    val bots = BotProfileService()
    val bgTasks = BackgroundTaskService()
    val sessions = SessionContinuityEngine()
    val collab = CollaborationService()
    val browser = BrowserEngine()
    val computer = ComputerUseEngine()
    val processRuntime = ProcessRuntimeEngine(executor = processExecutor)
    val agents = AgentRuntimeEngine()
    val providers = ProviderRegistryEngine()

    private val core: List<MimirEngine> = listOf(
        tools, terminal, forge, vault, ai, workflow,
        taskBoard, verification, bots, bgTasks, sessions, collab,
    )

    private val extended: List<MimirEngine> = listOf(
        browser, computer, processRuntime, agents, providers,
    )

    suspend fun startAll(includeExtended: Boolean = true) {
        ensureOpen()
        shutdownPrepared = false
        val list = if (includeExtended) core + extended else core
        for (e in list) {
            val m = e.describe()
            val id = m.id.value
            if (id in startAttemptOrder) continue
            if (id !in ownedEngineIds) {
                check(id !in host.listEngineIds()) {
                    "Engine '$id' is already attached and is not owned by EngineBootstrap"
                }
                host.attach(e, m)
                ownedEngineIds.add(id)
            } else if (id !in host.listEngineIds()) {
                host.attach(e, m)
            }
            startAttemptOrder.add(id)
            host.registerAndStart(m.id)
        }
    }

    suspend fun stopAll(): Map<String, EngineState> {
        ensureOpen()
        shutdownPrepared = false
        val results = linkedMapOf<String, EngineState>()
        startAttemptOrder.toList().asReversed().forEach { id ->
            val result = try {
                host.stopEngine(
                    com.mimir.kernel.contract.EngineId(id),
                    "engine_bootstrap_shutdown",
                ) ?: EngineState.Failed
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

    override fun close() {
        ensureOpen()
        check(shutdownPrepared && startAttemptOrder.isEmpty()) {
            "EngineBootstrap.close() requires a successful stopAll() first"
        }
        processExecutor.close()
        closed = true
    }

    fun principal(userId: String = "dev") = PermissionContext(userId)

    fun grantDevPermissions(userId: String = "dev") {
        host.permissions.grantAll(userId, listOf("*"))
    }

    fun registeredIds(): List<String> = host.listEngineIds()
}

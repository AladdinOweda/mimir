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
import com.mimir.engine.terminal.JvmProcessExecutor
import com.mimir.engine.terminal.TerminalEngine
import com.mimir.engine.tools.ToolRegistryEngine
import com.mimir.engine.vault.VaultEngine
import com.mimir.engine.verification.IndependentVerificationService
import com.mimir.engine.workflow.WorkflowEngine
import com.mimir.kernel.contract.MimirEngine
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext

/**
 * Single bootstrap wiring official engines into KernelHost.
 * Only canonical classes from ENGINE-REGISTRY-MATRIX — no V2/V3.
 */
class EngineBootstrap(
    val host: KernelHost = KernelHost(),
) {
    val tools = ToolRegistryEngine()
    val terminal = TerminalEngine(JvmProcessExecutor())
    val forge = ForgeEngine()
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
    val processRuntime = ProcessRuntimeEngine()
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
        val list = if (includeExtended) core + extended else core
        for (e in list) {
            val m = e.describe()
            host.attach(e, m)
            host.registerAndStart(m.id)
        }
    }

    fun principal(userId: String = "dev") = PermissionContext(userId)

    fun grantDevPermissions(userId: String = "dev") {
        host.permissions.grantAll(userId, listOf("*"))
    }

    fun registeredIds(): List<String> = host.listEngineIds()
}

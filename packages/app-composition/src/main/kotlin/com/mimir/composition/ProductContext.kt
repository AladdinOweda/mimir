package com.mimir.composition

import com.mimir.engine.agent.AgentRuntimeEngine
import com.mimir.engine.browser.BrowserEngine
import com.mimir.engine.computer.ComputerUseEngine
import com.mimir.engine.bot.profile.BotProfileService
import com.mimir.engine.bot.tasks.BackgroundTaskService
import com.mimir.engine.collaboration.CollaborationEngine
import com.mimir.engine.knowledge.KnowledgeWorkspaceEngine
import com.mimir.engine.spe.SpeEngine
import com.mimir.engine.forge.ForgeEngine
import com.mimir.engine.ie.IntelligentExecutionEngine
import com.mimir.engine.loop.LoopRuntime
import com.mimir.engine.orchestration.CollaborationService
import com.mimir.engine.process.ProcessRuntimeEngine
import com.mimir.engine.terminal.TerminalEngine
import com.mimir.engine.vault.VaultEngine
import com.mimir.engine.workflow.WorkflowEngine
import com.mimir.kernel.contract.EngineId
import com.mimir.kernel.contract.EngineState
import com.mimir.kernel.contract.EventEnvelope
import com.mimir.kernel.contract.KernelEventHandler
import com.mimir.kernel.contract.MimirEngine
import com.mimir.kernel.host.KernelHost
import com.mimir.contracts.capability.DefaultCapabilityPolicies
import com.mimir.service.activity.ActivityLog
import com.mimir.service.activity.DurableFileActivityLog
import com.mimir.service.activity.InMemoryActivityLog
import com.mimir.service.approvals.UserApprovalFacade
import com.mimir.service.files.FileCatalog
import com.mimir.service.files.InMemoryFileCatalog
import com.mimir.service.files.LocalFileSystemCatalog
import com.mimir.service.projects.InMemoryProjectDirectory
import com.mimir.service.projects.ProjectDirectory
import com.mimir.service.tasks.InMemoryTaskDirectory
import com.mimir.service.tasks.TaskDirectory
import com.mimir.service.tasks.TaskForgeProjector
import com.mimir.service.downloads.DownloadDirectory
import com.mimir.service.downloads.InMemoryDownloadDirectory
import com.mimir.contracts.knowledge.KnowledgeFacade
import com.mimir.contracts.knowledge.InMemoryKnowledgeFacade
import com.mimir.service.retention.DefaultRetentionPolicy
import com.mimir.service.retention.RetentionPolicy
import com.mimir.service.portability.DataPortability
import com.mimir.service.portability.InMemoryDataPortability
import com.mimir.platform.memory.DurableFileMemoryService
import com.mimir.platform.memory.InMemoryMemoryService
import com.mimir.platform.memory.MemoryService
import com.mimir.platform.secrets.LocalAesSecretsVault
import com.mimir.platform.secrets.SecretsVault
import com.mimir.platform.events.EventBus
import com.mimir.ui.AppShellController
import java.io.File
import kotlinx.coroutines.runBlocking
import com.mimir.engine.provider.registry.ProviderRegistryEngine
import com.mimir.engine.uft.UnifiedFileTransferEngine
import com.mimir.engine.tools.ToolRegistryEngine
import com.mimir.engine.chat.ChatEngine
import com.mimir.engine.aihub.AiHubEngine
import com.mimir.engine.ai.AiModelEngine

/**
 * Product composition root: durable services + KernelHost with engines attached.
 */
class ProductContext(
    val dataDir: File = File(System.getProperty("user.home"), ".mimir"),
    useDurableStores: Boolean = true,
    val shell: AppShellController = AppShellController(),
    val host: KernelHost = KernelHost(),
) {
    val approvals: UserApprovalFacade = UserApprovalFacade(host.approvals)
    val activity: ActivityLog =
        if (useDurableStores) DurableFileActivityLog(File(dataDir, "activity.tsv"))
        else InMemoryActivityLog()
    val files: FileCatalog =
        if (useDurableStores) LocalFileSystemCatalog(File(dataDir, "files"))
        else InMemoryFileCatalog()
    val downloads: DownloadDirectory = InMemoryDownloadDirectory()
    val knowledge: KnowledgeFacade = InMemoryKnowledgeFacade()
    val projects: ProjectDirectory = InMemoryProjectDirectory()
    val tasks: TaskDirectory = InMemoryTaskDirectory()
    val memory: MemoryService =
        if (useDurableStores) DurableFileMemoryService(File(dataDir, "memory.tsv"))
        else InMemoryMemoryService()
    val secrets: SecretsVault = LocalAesSecretsVault()
    val events: EventBus = EventBus()
    val taskProjector: TaskForgeProjector = TaskForgeProjector(events)
    val retention: RetentionPolicy = DefaultRetentionPolicy()
    val portability: DataPortability = InMemoryDataPortability()

    val engines = linkedMapOf<String, MimirEngine>()
    var loopRuntime: LoopRuntime? = null
        private set

    /** Kernel EventBus → product EventBus + activity log (once per context). */
    private var kernelBridgeInstalled: Boolean = false

    init {
        installKernelEventBridge()
    }

    /**
     * Forwards [KernelHost.events] to product [events] and records a compact activity line.
     * Idempotent.
     */
    fun installKernelEventBridge(): ProductContext {
        if (kernelBridgeInstalled) return this
        host.events.subscribe(
            KernelEventHandler { env: EventEnvelope ->
                events.publish(env)
                val summary = env.payload.entries.joinToString(" ") { "${it.key}=${it.value}" }.take(400)
                activity.record(
                    type = env.type,
                    summary = summary.ifBlank { env.source },
                    resource = env.correlationId ?: env.source,
                )
            },
        )
        kernelBridgeInstalled = true
        events.publish(
            "product.kernel.bridge.installed",
            "ProductContext",
            mapOf("handlers" to host.events.handlerCount().toString()),
        )
        return this
    }

    fun attachDefaultEngines(
        includeBrowser: Boolean = true,
        includeComputer: Boolean = true,
        includeForge: Boolean = true,
        includeAgent: Boolean = true,
        includeOrch: Boolean = true,
        includeWorkflow: Boolean = true,
        includeIe: Boolean = true,
        includeBot: Boolean = true,
        includeKnowledge: Boolean = true,
        includeSpe: Boolean = true,
        /** noop | http-fetch | chromium — default http-fetch for real network smoke */
        browserDriverKind: String = System.getenv("MIMIR_BROWSER_DRIVER") ?: "http-fetch",
    ): ProductContext {
        dataDir.mkdirs()
        DefaultCapabilityPolicies.seed(host.capabilitySecurity)
        val list = buildList {
            add(TerminalEngine())
            add(ProcessRuntimeEngine())
            if (includeForge) add(ForgeEngine())
            if (includeBrowser) add(BrowserEngine(driverKind = browserDriverKind))
            if (includeComputer) add(ComputerUseEngine())
            add(VaultEngine())
            if (includeAgent) add(AgentRuntimeEngine())
            if (includeOrch) add(CollaborationService())
            if (includeWorkflow) add(WorkflowEngine())
            if (includeIe) add(IntelligentExecutionEngine())
            if (includeBot) {
                add(BotProfileService())
                add(BackgroundTaskService())
            }
            add(CollaborationEngine())
            if (includeKnowledge) add(KnowledgeWorkspaceEngine())
            if (includeSpe) add(SpeEngine())
            add(ProviderRegistryEngine())
            add(UnifiedFileTransferEngine())
            add(ToolRegistryEngine())
            add(ChatEngine())
            add(AiHubEngine())
            add(AiModelEngine())
        }
        list.forEach { eng ->
            val manifest = runBlocking { eng.describe() }
            host.attach(eng, manifest)
            engines[manifest.id.value] = eng
        }
        loopRuntime = LoopRuntime()
        activity.record("host.engines.attached", "count=${engines.size}", resource = dataDir.path)
        events.publish("engines.attached", "ProductContext", mapOf("count" to engines.size.toString()))
        return this
    }


    /**
     * Smoke path: terminal + process + forge only (no browser/computer/agent swarm).
     * Seeds policies and attaches a minimal engine set for local verification.
     */

    /**
     * Priority smoke: minimal engines + HTTP-fetch browser (real network navigate, no JS DOM).
     * Use for CI without Chromium/CDP.
     */
    fun attachSmokeWithHttpBrowser(): ProductContext {
        dataDir.mkdirs()
        DefaultCapabilityPolicies.seed(host.capabilitySecurity)
        val list = listOf(
            TerminalEngine(),
            ProcessRuntimeEngine(),
            ForgeEngine(),
            BrowserEngine(driverKind = "http-fetch"),
        )
        list.forEach { eng ->
            val manifest = runBlocking { eng.describe() }
            host.attach(eng, manifest)
            engines[manifest.id.value] = eng
        }
        activity.record("host.engines.attached.smoke_http", "count=${list.size}", resource = dataDir.path)
        events.publish(
            "engines.attached",
            "ProductContext",
            mapOf("count" to list.size.toString(), "mode" to "smoke_http_browser"),
        )
        return this
    }

    fun attachMinimalEngines(): ProductContext {
        dataDir.mkdirs()
        DefaultCapabilityPolicies.seed(host.capabilitySecurity)
        val list = listOf(TerminalEngine(), ProcessRuntimeEngine(), ForgeEngine())
        list.forEach { eng ->
            val manifest = runBlocking { eng.describe() }
            host.attach(eng, manifest)
            engines[manifest.id.value] = eng
        }
        activity.record("host.engines.attached.minimal", "count=${list.size}", resource = dataDir.path)
        events.publish("engines.attached", "ProductContext", mapOf("count" to list.size.toString(), "mode" to "minimal"))
        return this
    }

    suspend fun startAll(): Map<String, EngineState> {
        installKernelEventBridge()
        val out = linkedMapOf<String, EngineState>()
        engines.keys.forEach { id -> out[id] = host.registerAndStart(EngineId(id)) }
        activity.record("host.engines.started", "n=${out.size}")
        events.publish(
            "product.engines.started",
            "ProductContext",
            mapOf("count" to out.size.toString(), "running" to out.values.count { it == EngineState.Running }.toString()),
        )
        return out
    }

    /** Snapshot combining kernel security + bridge state. */
    fun observabilitySnapshot(): Map<String, String> =
        host.securitySnapshot() + mapOf(
            "kernelBridge" to kernelBridgeInstalled.toString(),
            "productEngines" to engines.size.toString(),
        )
}

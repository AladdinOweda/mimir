# ENGINE-REGISTRY-MATRIX — sole product entrypoints
**Updated:** 2026-11-05

| EngineId (manifest) | Canonical class | Module | Bootstrap |
|---------------------|-----------------|--------|-----------|
| tools-registry | ToolRegistryEngine | engine/tools | yes |
| terminal-core | TerminalEngine | engine/terminal | yes |
| forge-core | ForgeEngine | engine/forge | yes |
| vault-core | VaultEngine | engine/vault | yes |
| ai-model | AiModelEngine | engine/ai-model | yes |
| workflow-core | WorkflowEngine | engine/workflow | yes |
| task-board | TaskBoardEngine | engine/process | yes |
| independent-verification | IndependentVerificationService | engine/verification | yes |
| bot-profiles | BotProfileService | engine/bot | yes |
| background-tasks | BackgroundTaskService | engine/bot | yes |
| session-continuity | SessionContinuityEngine | engine/agent-session | yes |
| collaboration | CollaborationService | engine/orchestration | yes |
| browser-core | BrowserEngine | engine/browser | optional |
| computer-use | ComputerUseEngine | engine/computer | optional |
| process-runtime | ProcessRuntimeEngine | engine/process | optional |
| agent-runtime | AgentRuntimeEngine | engine/agent-runtime | optional |
| provider-registry | ProviderRegistryEngine | engine/provider | optional |
| task-isolation | TaskIsolationEngine (slim stub) | engine/task-isolation | optional — prefer TaskBoardEngine |
| ie | IntelligentExecutionEngine | engine/ie | optional |
| spe | SpeEngine | engine/spe | optional |

**Forbidden as product entrypoints:** any `*EngineV2` / `*EngineV3` / classes under `src/archive`.

**Approval:** only `approvalToken` from Kernel `ApprovalService`. `approvalGranted` is stripped by KernelHost and must not gate engine logic.

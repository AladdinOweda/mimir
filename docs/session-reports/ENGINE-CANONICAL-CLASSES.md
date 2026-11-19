# Canonical class versions (Phase B)

When duplicates exist outside this tree, **these in-tree paths win**.

| Concept | Canonical path | Notes |
|---------|----------------|-------|
| Kernel contracts | `shared/kernel/.../contract/Contracts.kt` | Single Types set |
| KernelHost | `shared/kernel/.../host/KernelHost.kt` | Phase A minimal host |
| PermissionPolicy | `shared/kernel/.../permission/PermissionPolicy.kt` | |
| AiModelEngine | `shared/engine/ai-model/.../ai/AiModelEngine.kt` | ADR-033 |
| ModelRuntime adapters | `.../ai/adapters/NoopAndTransformersAdapters.kt` | Transformers=stub |
| ProviderRegistryEngineV2 | `shared/engine/provider/.../registry/ProviderRegistryEngineV2.kt` | |
| TerminalEngine | `shared/engine/terminal/.../TerminalEngine.kt` | from terminal-v2 |
| ForgeEngine | `shared/engine/forge/.../ForgeEngine.kt` | from mimir-tf |
| SpeEngineV2 | `shared/engine/spe/.../SpeEngineV2.kt` | |
| ProcessRuntimeEngineV3 | `shared/engine/process/.../ProcessRuntimeEngineV3.kt` | |
| VerificationEngineV2 | `shared/engine/verification/.../VerificationEngineV2.kt` | |
| AgentConnectivityEngineV2 | `shared/engine/agent-connectivity/.../AgentConnectivityEngineV2.kt` | if present |

Historical artifacts under `/artifacts/mimir-*` are **reference only**, not Gradle source.

## Still marker-only modules
computer, orchestration, ie, vault, agent-session, agent-runtime, bot, task-isolation, browser — migrate next.

# F1 — Boundaries complete (2026-11-17)

Claude H-02 / H-03 remaining work.

## Done
| Edge | Fix |
|------|-----|
| Forge/Process → Terminal `JvmProcessExecutor` | `ProcessExecutorPort` in contracts (prior) |
| Verification → Process `TaskBoardEngine` | `TaskBoardPort` + `TaskItem` in `contracts/process` (`ProcessContract.kt`); service takes port only |
| Chat → AiHub/AiModel engines | `ChatModelPipeline(invoke)` (prior) |
| Computer → BrowserEngine | `SharedBrowserSession` moved to `packages/app-composition` |

## Tests
`EngineBoundarySourceGuardTest` — PASS (forbidden imports + contracts present)

## Gradle
- verification `implementation` → `:contracts:process` only (`:shared:engine:process` test-only)
- computer no longer depends on `:shared:engine:browser`

## Remaining (not F1)
F0 full CI green · F3 min tests · F4 migration

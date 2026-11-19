---
document_id: MIMIR-ADR-001
title: Logical repository map to KMP modules
status: Accepted
date: 2026-11-08
---

# ADR-001 — Logical structure → Kotlin Multiplatform modules

## Decision
Keep physical packaging as Gradle KMP/JVM modules under `shared/` and `apps/`.
Treat `docs/engineering/repository-structure.md` as the **logical** ownership map, not a forced folder rename.

## Mapping

| Logical area | Physical module / path |
|--------------|------------------------|
| contracts/ | `contracts/` (schemas + docs); Kotlin types in `shared/kernel` |
| kernel/ | `shared/kernel` |
| engines/* | `shared/engine/*` |
| runtimes/* | `shared/engine/{agent-runtime,orchestration,workflow,bot,ie,...}` |
| platform/* | `shared/persistence`, vault engine, future platform modules |
| services/* | application services — start in `shared/integration` + future `shared/service/*` |
| packages/design-tokens | `shared/design-tokens` |
| packages/app-shell + ui | `shared/ui` |
| packages/ui-components | `shared/ui` (components grow here) |
| apps/mobile | `apps/android`, `apps/androidApp` |
| apps/desktop | `apps/desktop` |
| docs/* | `docs/` (governance, architecture, ux, design-system) |

## Consequences
- No mass move of engine sources in this ADR.
- New code follows shell + token contracts in `shared/ui` and `shared/design-tokens`.
- Kernel remains free of Compose and engine-name branches.

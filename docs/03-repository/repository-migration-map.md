# Repository migration map (F4)

**Authority:** ADR-046 (13 roots principles only) · Claude F4 · D-7
**Rule:** one family per batch · compile/tests after each · no mass delete

## Target roots (principles)

`kernel/` · `contracts/` · `engines/` · `services/` · `platform/` · `adapters/` · `packages/` · `apps/` · `docs/` · `tests/` · `config/` · `tooling/` · `build-logic/`

Plus infrastructure: `.github/`, `gradle/`, root governance files.

## Current → target

| Current path | Target | Action | Batch | Gradle impact |
|--------------|--------|--------|-------|---------------|
| `runtimes/` | (none — code in `engines/`) | Keep **pointer README only** | **F4-1** | none |
| `integrations/` | `adapters/` (by type) | Pointer README; no duplicate code | **F4-1** | none |
| `scripts/` | `tooling/` | Move files into tooling | **F4-1** | none |
| `samples/` | `docs/examples/` + `tests/fixtures/` | Pointer + copy READMEs | **F4-1** | none |
| `benchmarks/` | `tests/performance/` | Pointer README | **F4-1** | none |
| `extensions/` | engine/module manifests | Keep until extension SDK decision | later | low |
| `shared/memory` → **`platform/memory`** | `platform/memory` or `services/memory` | Move + settings projectDir | F4-2 | yes |
| `shared/secrets` → **`platform/secrets`** | `platform/secrets` | Move + settings | F4-2 | yes |
| `shared/events` → **`platform/events`** | `platform/events` | Move + settings | F4-2 | yes |
| `shared/configuration` → **`platform/configuration`** | `platform/configuration` | Move + settings | F4-2 | yes |
| `shared/*` empty slots | delete or docs/archive | After F4-2 | F4-3 | no |
| `:shared:engine:*` coordinates | keep historic names | **Do not rename** coordinates; `projectDir` already → `engines/` | stable | n/a |
| `:shared:kernel` | keep coordinate; dir `kernel/` | stable | n/a | n/a |
| `apps/android` + `apps/androidApp` | one entry after ownership audit | later | yes |

## Batches

### F4-1 — Pointer hygiene ✅
**Done.** Pointer roots cleared; samples→docs/examples; benchmarks→tests/performance; scripts→tooling/scripts; packages renamed to `com.mimir.platform.*`.

### F4-2 — Platform services out of `shared/` ✅
**Done 2026-11-17.** Moved the four real packages (memory, secrets, events, configuration) under `platform/` with settings `projectDir` updates. Run `:shared:memory:compileKotlin` etc. (coordinates may stay `:shared:memory` temporarily).

### F4-3 — Empty shared placeholders ✅
**Done 2026-11-17.** Empty slots removed; single `shared/README` + STRUCTURE-STATUS; archive in `docs/archive/f4-3-shared-placeholders/`.

### F4-4 — Optional ✅
**Done 2026-11-17.** ADR-047 extensions · ADR-048 coordinate rename deferred · Android canonical=`apps/android` entry=`androidApp` · document registry minimal · tooling already under `tooling/`.

## Exit criteria for F4
- One migration map (this file)
- No second “target tree” document active
- Pointer roots cannot be mistaken for code homes
- Every Gradle-included dir has build.gradle.kts
- canShip still false until product gates

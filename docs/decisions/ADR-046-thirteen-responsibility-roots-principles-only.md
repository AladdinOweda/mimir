# ADR-046 — Thirteen responsibility roots (principles only)

- **Status:** Accepted
- **Date:** 2026-11-17
- **Decision ID:** D-7 (owner)
- **Related:** ADR-037, ADR-042, Claude audit 2026-11-16, MIMIR-REPO-TARGET-003 (rejected as detailed tree)

## Context

Multiple competing repository structure documents (REPO-STRUCTURE, ENG-REPO-001, REPO-002, TARGET-003) caused parallel sources of truth. TARGET-003 proposed a detailed tree including rewrite of Kernel layout and KMP paths, conflicting with ADR-042 (single Kernel Gradle module).

## Decision

1. Adopt **exactly these 13 responsibility roots as principles** (plus standard `.github/`, `gradle/`, root files — not architecture layers):

   `kernel/` · `contracts/` · `engines/` · `services/` · `platform/` · `adapters/` · `packages/` · `apps/` · `docs/` · `tests/` · `config/` · `tooling/` · `build-logic/`

2. **Reject TARGET-003 detailed tree** as binding. Use it only as optional design input.
3. No bulk migration until a **migration map** is approved; move one root/family per batch with compile + tests.
4. Extra current roots (`runtimes/`, `integrations/`, `shared/`, `tools/`, `samples/`, `benchmarks/`, `extensions/`) are absorbed gradually per Claude F4 — not deleted in one shot.
5. Kernel remains **one Gradle module** (ADR-042); do not split into proposed registry/lifecycle package trees as new modules.

## Consequences

- Single target narrative: principles + migration map, not a second full tree document.
- `REPO-STRUCTURE.md` describes **current** layout; links to this ADR for target principles.

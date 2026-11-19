# ADR-037 — Physical repository layout (supersedes ADR-001 mass-move stance)

- **Status:** Accepted
- **Date:** 2026-11-10
- **Deciders:** Owner (Aladdin) via “اعتمد التوصيات”
- **Supersedes:** ADR-001 constraint against mass physical moves where it conflicts with MIMIR-REPO-002

## Context
ADR-001 (2026-11-08) preferred logical Gradle coordinates under `:shared:engine:*` without mass filesystem moves. Subsequent work and MIMIR-REPO-002 established physical roots: `kernel/`, `engines/`, `packages/`, `adapters/`, `services/`, `shared/` (platform services only). CANONICAL and settings.gradle.kts already use `projectDir` redirects.

## Decision
1. **Physical layout** follows MIMIR-REPO-002: `kernel/`, `engines/<name>/`, `packages/`, `adapters/`, `services/`, `shared/{memory,secrets,events,configuration}/`, `apps/`, `docs/`.
2. **Gradle coordinates** may remain historical (`:shared:kernel`, `:shared:engine:browser`, …) via `projectDir` until a dedicated rename ADR.
3. No second parallel tree of engine sources under `shared/engine/` as source of truth.
4. ADR-001 remains historical for the *logical* module map; this ADR wins on *filesystem* placement.

## Consequences
- Documentation must cite REPO-002 for paths and settings.gradle.kts for coordinates.
- New engines are added under `engines/` only.

# ADR-048 — Defer `:shared:*` Gradle coordinate rename

- **Status:** Accepted (defer)
- **Date:** 2026-11-17
- **Related:** F4-2 · F4-3 · ADR-046

## Context
Physical code for memory/secrets/events/configuration lives under `platform/`, packages are `com.mimir.platform.*`, but Gradle paths remain `:shared:memory` etc. for dependency stability.

## Decision
**Do not rename** `:shared:memory|secrets|events|configuration|engine:*|kernel|…` in this release train.

Rename to `:platform:*` / `:engines:*` only when:
1. Full CI green (F0 closed)
2. Automated rewrite of all `project(":shared:…")` references
3. One coordinated PR with migration notes

Until then, `settings.gradle.kts` `projectDir` is the source of path truth.

## Consequences
- Docs must always show coordinate **and** physical path.
- `shared/README.md` remains the map.

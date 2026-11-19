# Repository structure (current map)

> **Target principles:** ADR-046 — 13 responsibility roots only.
> **Not binding:** MIMIR-REPO-TARGET-003 detailed tree.
> **Platform:** ADR-045 — JVM/Android first.
> This file describes the **current** layout, not a second architecture constitution.


```
Mimir/   (Google Drive root = repo root)
├── CANONICAL.md · STATE.md · REPO-STRUCTURE.md · DASHBOARD.md
├── BLUEPRINT-v4.4.md → docs/architecture/blueprint.md
├── settings.gradle.kts · build.gradle.kts · VERSION · LICENSE
├── kernel/
├── engines/          # all engine + runtime sources
├── packages/         # app-shell, design-tokens
├── platform/
├── shared/           # platform service slots only
├── adapters/ · contracts/ · extensions/ · tooling/
├── apps/ · services/ · integrations/ · tests/
├── config/ · samples/ · benchmarks/ · runtimes/ (pointer)
├── docs/ · build-logic/ · gradle/
└── mimir-repo-canonical.zip   # full source snapshot
```

Each major folder includes STRUCTURE-STATUS.md for audit.


## F4 migration
See [docs/03-repository/repository-migration-map.md](docs/03-repository/repository-migration-map.md). Batch **F4-1** (pointer hygiene) applied 2026-11-17.

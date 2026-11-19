# F4-3 — Empty shared placeholders (2026-11-17)

## Removed directories under `shared/`
artifacts, caching, common, configuration, context, cost-context, diagnostics, events, knowledge-access, memory, messaging, model-gateway, notifications, observability, platform-utilities, retrieval, scheduling, search, secrets, storage, synchronization

(Each had zero `.kt` sources; code for memory/secrets/events/configuration already in `platform/`.)

## Retained under `shared/`
- `README.md` — canonical coordinate map
- `STRUCTURE-STATUS.md`

## Not changed
- Gradle `include(":shared:…")` coordinates (historic stability)
- `projectDir` targets (`platform/`, `engines/`, `kernel/`, `packages/`)

## Archive
`docs/archive/f4-3-shared-placeholders/`

## F4 status
| Batch | Status |
|-------|--------|
| F4-1 | complete |
| F4-2 | complete |
| F4-3 | **complete** |
| F4-4 | optional (extensions policy, android dedupe, coordinate rename ADR) |

# Code deduplication — 2026-11-05

## Removed
- All `shared/**/src/archive/` trees (superseded Kotlin)
- `ProviderRegistryEngineV2.kt` (kept R7 `ProviderRegistryEngine.kt`)
- `TaskIsolationEngineV2` filename → `TaskIsolationEngine.kt`

## Renamed for uniqueness
- Forge `OwnershipRegistry` → `PathLaneRegistry` (process keeps `OwnershipRegistry` for agent lanes)

## Rule
- One `*Engine.kt` official name per module under `src/main` only
- No V2/V3 suffixes in product entrypoints

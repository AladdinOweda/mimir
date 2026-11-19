# F4-2 — shared → platform (2026-11-17)

## Physical home
| Coordinate | Directory |
|------------|-----------|
| `:shared:memory` | `platform/memory/` |
| `:shared:secrets` | `platform/secrets/` |
| `:shared:events` | `platform/events/` |
| `:shared:configuration` | `platform/configuration/` |

## settings.gradle.kts
`projectDir` → `platform/<name>` for all four.

## shared/
Pointer README only (no `src/`).

## Recovery note
Initial `rsync` missing on host caused a brief source gap; restored from `MIMIR-SSOT-2026-11-16.zip` + F3 min tests re-applied.

## Packages
Still `com.mimir.shared.*` (rename deferred).

## Next
F4-3 empty shared placeholders

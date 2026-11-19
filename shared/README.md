# shared/ — transitional Gradle coordinate namespace only (F4-3)

**This directory is not a code home.** Empty placeholder folders were removed in F4-3.

## Where code lives

| Gradle coordinate (historic) | Physical path | Package / notes |
|------------------------------|---------------|-----------------|
| `:shared:kernel` | `kernel/` | `com.mimir.kernel.*` |
| `:shared:memory` | `platform/memory/` | `com.mimir.platform.memory` |
| `:shared:secrets` | `platform/secrets/` | `com.mimir.platform.secrets` |
| `:shared:events` | `platform/events/` | `com.mimir.platform.events` |
| `:shared:configuration` | `platform/configuration/` | `com.mimir.platform.configuration` |
| `:shared:integration` | `platform/integration/` | integration host |
| `:shared:ui` | `packages/app-shell/` | UI shell |
| `:shared:design-tokens` | `packages/design-tokens/` | tokens |
| `:shared:engine:*` | `engines/<name>/` | specialized engines |

## Rules
1. Do **not** add new modules under `shared/`.
2. New platform primitives → `platform/`.
3. New engines → `engines/`.
4. New app services → `services/`.
5. Coordinate renames (`:shared:memory` → `:platform:memory`) require a dedicated ADR — not done in F4-3.

See: `docs/03-repository/repository-migration-map.md`

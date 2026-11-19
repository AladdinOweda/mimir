# F4-1 complete + naming fix (2026-11-17)

## F4-1 completion
| Item | Action |
|------|--------|
| samples/* code | → `docs/examples/{minimal-engine,sample-adapter,vertical-workflow}` |
| samples/* | pointer README only |
| benchmarks/startup | → `tests/performance/startup` |
| scripts/ci-local.sh | only under `tooling/scripts/` |
| STRUCTURE-STATUS on pointer roots | aligned with POINTER ONLY |
| runtimes/integrations | pointer README (already) |

## Naming (with F4-2)
| Before | After |
|--------|-------|
| `com.mimir.shared.memory` | `com.mimir.platform.memory` |
| `com.mimir.shared.secrets` | `com.mimir.platform.secrets` |
| `com.mimir.shared.events` | `com.mimir.platform.events` |
| `com.mimir.shared.configuration` | `com.mimir.platform.configuration` |

Imports updated in app-composition, contracts/knowledge, services/tasks, services/downloads, samples/examples.

Gradle coordinates remain `:shared:memory` etc. (historic); physical path `platform/`.

## Residual
- Empty `shared/*` placeholders → F4-3
- Coordinate rename `:shared:memory` → `:platform:memory` → future ADR

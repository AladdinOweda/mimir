# apps/

## Android (F4-4 policy)

| Module | Role |
|--------|------|
| **`:apps:android`** | **Canonical** Android product module — shell bridges, presenters, WebView host references, unit tests |
| **`:apps:androidApp`** | **Application entry only** — `MainActivity` / process entry; must depend on `:apps:android` for product logic |
| `:apps:desktop` | Desktop entry |
| `:apps:iosApp` | iOS entry scaffold |
| `:apps:mobile` / `:apps:shared-app` | Transitional; do not expand without ADR |

### Rules
1. New Android UI/feature code → `apps/android` (or `packages/` if shared with desktop).
2. Do not duplicate presenters/hosts in `androidApp`.
3. When feasible, `androidApp` becomes a thin Application wrapper only.

See ADR-047/048 and `docs/audit/F4-4-GAP-CLOSE-2026-11-17.md`.

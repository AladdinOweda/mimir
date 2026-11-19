# Mimir

Modular AI knowledge and execution platform (Kotlin / multi-app).

**Canonical governance:** `docs/governance/constitution.md` → `docs/architecture/blueprint.md`
**Layout:** see `REPO-STRUCTURE.md` and `docs/engineering/MOVE-MAP-2026-11-08.md`
**Status:** `STATE.md` · **canShip:** false

## Layout (physical)

- `kernel/` — engine-agnostic kernel
- `engines/` — browser, forge, terminal, vault, …
- `runtimes/` — agent, orchestration, workflow, bot, IE
- `packages/` — design-tokens, app-shell
- `platform/` — persistence, integration
- `apps/` — android, desktop, iosApp
- `docs/` — governance, architecture, ux, design-system

License: Apache-2.0

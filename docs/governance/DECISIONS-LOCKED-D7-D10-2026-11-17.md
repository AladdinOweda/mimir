# Decisions locked — D-7 … D-10

**Date:** 2026-11-17
**Owner:** approved in session
**Authority:** above competing structure proposals and draft audits

| ID | Decision | ADR / doc |
|----|----------|-----------|
| **D-7** | 13 responsibility roots as **principles only**; reject TARGET-003 detailed tree as binding | ADR-046 |
| **D-8** | Governing Constitution = package `01-MIMIR-CONSTITUTION.md` (longer); governing Blueprint = `docs/architecture/02-MIMIR-BLUEPRINT.md` (package twin). Shorter `constitution.md` / `blueprint.md` become pointers + archive | this file + CANONICAL |
| **D-9** | JVM/Android first; KMP **not** near-term | ADR-045 |
| **D-10** | No ADR number reuse; model-provider → **ADR-043**, workflow-runtime-adapter → **ADR-044**; original 022 execution-plan and 023 terminal-blocks remain | ADR-043, ADR-044 |

## D-8 paths

| Role | Path |
|------|------|
| Constitution (canonical) | `docs/governance/01-MIMIR-CONSTITUTION.md` and package copy under `docs/packages/2026-11-09-complete/` |
| Constitution (pointer) | `docs/governance/constitution.md` → points to 01- |
| Blueprint (canonical) | `docs/architecture/02-MIMIR-BLUEPRINT.md` |
| Blueprint (pointer) | `docs/architecture/blueprint.md` → points to 02- |
| Root status pointer | `BLUEPRINT-v4.4.md` (short status only) |

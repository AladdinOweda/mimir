# Document Registry — Complete Documentation Package 2026-11-09

**Adopted:** 2026-11-11
**Source zips:** Mimir-Complete-Documentation-Package-2026-11-09-REPACKAGED.zip
**UX subset zip:** identical for files 05–07 (no separate content)

| Document ID | Package file | Canonical path |
|-------------|--------------|----------------|
| MIMIR-GOV-CON-001 | 01-MIMIR-CONSTITUTION.md | docs/governance/01-MIMIR-CONSTITUTION.md |
| MIMIR-ARCH-BP-001 | 02-MIMIR-BLUEPRINT.md | docs/architecture/02-MIMIR-BLUEPRINT.md |
| MIMIR-ENG-REPO-001 | 03-MIMIR-REPOSITORY-STRUCTURE.md | docs/03-repository/03-MIMIR-REPOSITORY-STRUCTURE.md |
| MIMIR-UX-DTC-001 | 01-MIMIR-DESIGN-TOKENS-THEME-CONTRACT.md | docs/design-system/01-… |
| MIMIR-UX-CLS-001 | 02-MIMIR-COMPONENT-LIBRARY-SPECIFICATION.md | docs/design-system/02-… |
| MIMIR-UX-SHELL-001 | 03-MIMIR-APPLICATION-SHELL-SPECIFICATION.md | docs/design-system/03-… |
| MIMIR-UX-LIQUID-001 | 04-MIMIR-LIQUID-DESIGN-SYSTEM-…md | docs/design-system/04-… |
| MIMIR-UX-INT-001 | 05-MIMIR-UX-INTERACTION-SPECIFICATION.md | docs/ux/05-… |
| MIMIR-UX-SCREEN-001 | 06-MIMIR-SCREEN-BY-SCREEN-SPECIFICATION.md | docs/ux/06-… |
| MIMIR-ENG-FORGE-001 | 07-MIMIR-FORGE-SPECIFICATION.md | docs/engineering/07-… (mirror docs/ux/07-…) |

Snapshot of original package filenames: `docs/packages/2026-11-09-complete/`

## Consistency notes at adoption
- Must not contradict ADR-037…041 (layout, knowledge adapters, task lifecycle, capability policy, catalog exit).
- Cognee/Vane language in Constitution/Blueprint: **optional / adapter-bound** only (ADR-038); not kernel dependencies.
- Forge UI/spec follows ADR-039 for task vs verification columns.
- canShip remains false; package adoption ≠ release.

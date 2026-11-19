# MIMIR-UX-SCREEN-INVENTORY

**Status:** Working registry (2026-11-12)
**Purpose:** Single list of screens/areas, codes, paths, and status — avoid duplicate specs.

## Legend
- `ADOPTED` — files in repo from package/chat
- `DIRECTION` — design direction approved, detailed file may be missing
- `TODO` — needs detailed screen spec
- `N/A-merge` — may be a panel inside another screen, not a full route

## Structure root
`docs/ux/screen-specifications/`

| Area | Path | Status | Notes |
|------|------|--------|-------|
| Inventory | `00-screen-inventory/` | ADOPTED | this file |
| Home | `01-home/` | ADOPTED | section 3 of Screen-by-Screen package |
| Chat | `02-chat/` | ADOPTED | section 4 of Screen-by-Screen package |
| Forge (11 pages) | `03-forge/` | ADOPTED | 07-MIMIR-FORGE full workspace spec |
| Browser | `04-browser/` | TODO | AI control + tabs |
| Projects | `05-projects/` | TODO | |
| Tasks & Execution | `06-tasks-and-execution/` | TODO | |
| Agents & Swarm | `07-agents-and-swarm/` | TODO | |
| Activity | `08-activity-and-history/` | TODO | shared |
| Approvals | `09-approval-center/` | TODO | shared |
| Intelligence | `10-intelligence/*` | INT specs ADOPTED; **screens TODO** | Brain/Files/Downloads/Vault |
| Settings | `11-settings/` | TODO | progressive disclosure |
| **Terminal** | `12-terminal/` | **ADOPTED (4 docs)** | from Mimir terminal.zip |

## Terminal files (ADOPTED)
See `12-terminal/README.md`.

## Forge page checklist (DIRECTION → detail TODO)
1. Forge Home 2. Projects 3. Code Workspace 4. Toolchains 5. Tasks & Planning
6. Agents & Workers 7. Build & Test 8. Changes & Review 9. Artifacts 10. Sandbox 11. Settings

## Cross-cutting UX docs (not screens)
| Doc | Location (existing) | Status |
|-----|---------------------|--------|
| Design Tokens | packages / docs | ADOPTED package |
| Component Library | packages / docs | ADOPTED |
| Application Shell | packages / docs | ADOPTED |
| UX Interaction | `docs/ux/` | ADOPTED |
| Responsive | — | TODO MIMIR-UX-RESP-001 |
| A11y + RTL | — | TODO MIMIR-UX-A11Y-001 |
| Design QA | — | TODO MIMIR-UX-DQA-001 |

## Next write order (from owner plan)
1. Keep inventory current
2. Home detail
3. Chat detail
4. Forge 11 files
5. Browser
6. Intelligence screens
7. Terminal already packed — implement UI later
8. Agents/Swarm, shared centers, Settings
9. RESP / A11Y / DQA


## Package 2026-11-09 files placed
- Screen-by-Screen → inventory + home/chat copies
- Forge → 03-forge/

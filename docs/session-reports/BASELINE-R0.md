# R0 Repository Baseline — 2026-11-04

## Official source of truth (this workspace)
| Artifact | Path | Status |
|----------|------|--------|
| Blueprint | `BLUEPRINT-v4.4.md` | **Canonical architecture** |
| State | `STATE.md` | **Canonical status** |
| Checklist | `CHECKLIST-MASTER.md` | Canonical gates |
| Dashboard | `DASHBOARD.md` | Snapshot |
| Structure | `REPO-STRUCTURE.md` | Canonical layout |
| Maturity | `docs/MATURITY.md` | Scores + honesty |
| ADRs | `docs/decisions/` + `docs/ADR-INDEX.md` | Includes 034–036 |

## Explicit non-claims (from external Dropbox audit)
- Dropbox may contain auto-renamed duplicates (STATE (1)…); **not authoritative**.
- Gradle full multi-module CI green: **not proven in this environment**.
- Vault production crypto, real Browser driver, real Computer Use: **not ready**.
- `approvalGranted` boolean pattern: **known anti-pattern** → P2.
- canShip: **false**.

## Engine modules (official paths under shared/engine/*)
Listed in settings.gradle.kts; each has build.gradle.kts in this baseline.

## Remediation track
R0 (this) → R1 Gradle → R2 Kernel security → … per plan P0–P11.

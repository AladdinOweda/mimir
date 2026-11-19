# Remediation R0–R1 Record — 2026-11-04

## R0 — Baseline
- Canonical docs: BLUEPRINT-v4.4, STATE, CHECKLIST, DASHBOARD, REPO-STRUCTURE, BASELINE-R0.md
- Dropbox renames (STATE (1)…) treated as **non-authoritative**
- ADR-034/035/036 present under docs/decisions
- **No historical files deleted**

## R1 — Gradle foundation (this workspace)
| Item | Result |
|------|--------|
| settings.gradle.kts matches modules with build.gradle.kts | **Yes** |
| No `project(":shared")` aggregate | **Yes** (apps → `:shared:kernel`) |
| pluginManagement includeBuild("build-logic") | **Yes** |
| build-logic not included as subproject | **Yes** |
| Gradle Wrapper jar + properties 8.11.1 | **Present** |
| Full `./gradlew printModules` in sandbox | **FAIL** Metaspace OOM — environment limit, not proof of structural success on developer machine |

## Honest status
R0 **closed** for this workspace.
R1 **partial**: structure aligned; full clean Gradle build must be re-run on a machine with adequate heap (not this sandbox).

## Next (do not skip)
R2 Kernel security: forced permissions + Approval tokens (no `approvalGranted` boolean).

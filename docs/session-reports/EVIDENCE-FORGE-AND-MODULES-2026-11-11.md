# Evidence — Forge board + module tests + minimal attach

**Date (UTC):** 2026-11-11T17:16:27Z area

## Code delivered
1. `packages/app-shell/.../forge/ForgeBoardUi.kt` — `projectForgeBoard` / `ForgeBoardState` / column order (Compose-ready, no Compose runtime).
2. `ProductContext.attachMinimalEngines()` — terminal + process + forge only.
3. Gradle deps: `:services:tasks` → events; `:shared:ui` → tasks.

## Kotlinc + JUnitCore results
| Suite | Result |
|-------|--------|
| TaskContractsTest + TaskForgeProjectorTest | OK (5) |
| DownloadDirectoryTest | OK (1) |
| InMemoryKnowledgeFacadeTest | OK (1) |
| DefaultRetentionPolicyTest | OK (1) |
| **ForgeBoardUiTest** | **OK (1)** |
| **Total** | **9 OK** |

Raw: see session `/tmp/kotlinc-batch.log` (ALL_DONE).

## Gradle
Attempted:
```
bash ./gradlew --no-daemon :services:downloads:test :services:retention:test :contracts:knowledge:test :services:tasks:test :shared:ui:test
```
**Result:** FAILED — `Gradle build daemon disappeared unexpectedly` (sandbox memory/kill).
**Not claimed green.** Kotlinc evidence stands for module logic.

## canShip
false

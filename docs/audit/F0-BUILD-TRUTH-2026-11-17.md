# F0 — Build truth (updated 2026-11-17 later)

## Fixed this phase
1. **Module group collision** — `contracts/*` vs `engines/*` same folder names (`terminal`, `process`, …) under `group=com.mimir` caused project substitution and circular Gradle tasks.
   **Fix:** path-based `group` in root `build.gradle.kts` + path `archivesName`.
2. **Missing Gradle scripts/includes** for chat, ai-hub, collaboration, uft, integration-tests, contracts.
3. **ProcessExecutorPort / TaskBoardPort imports & overrides** for terminal/process/forge/verification.
4. **Chat** → `implementation(project(":contracts:chat"))` for `ChatRole`.
5. **gradle.properties** — higher heap/metaspace, single worker, daemon off, in-process Kotlin where possible.
6. **CI** compile list expanded (chat, process contracts, verification, ai-hub, collab, uft).
7. **Helper** `tooling/compile-engines-kotlinc.sh` for memory-constrained environments.

## Gradle evidence (this sandbox)
| Target | Result |
|--------|--------|
| `:shared:kernel:compileKotlin` | SUCCESS |
| `:contracts:capability\|process\|events\|terminal\|forge\|browser\|chat\|agent` | SUCCESS |
| `:shared:engine:terminal\|process\|forge\|verification` | SUCCESS |
| Full multi-engine Gradle in one daemon session | **Unstable** (daemon OOM / killed) |

## kotlinc evidence (earlier same day)
With kernel + contracts + adapters on classpath: chat, browser, vault, ai-hub, collaboration, uft, computer compiled **OK** (exit 0). Re-run after env resets may need the helper script.

## Remaining for F0 close
- Green `./gradlew test` (or CI) on a machine with ≥4 GB heap for Gradle.
- Fix any remaining engine compile errors exposed only under full classpath.
- `verifyModuleBuilds` task passes on CI.

## canShip
false

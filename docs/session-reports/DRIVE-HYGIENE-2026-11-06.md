# Drive hygiene — 2026-11-06

## Problem
Flat Mimir root accumulated:
- 14× `mimir-repo-canonical.zip`
- 14× `STATE.md`
- 6× `RELEASE-GATE.md`
- 3× `TEST-RESULTS-2026-11-05.md`
- 2× `build.gradle.kts` / `gradle.properties`
- Many dated session reports + loose `.kt` files at root

## Action
1. Keep newest of each critical duplicate; trash older copies (recoverable 30 days).
2. Single distributable: latest `mimir-repo-canonical.zip` only.
3. SSOT names (never date-suffix): STATE, RELEASE-GATE, CANONICAL, BLUEPRINT-v4.4, DASHBOARD, CHECKLIST-MASTER, REPO-STRUCTURE.
4. Dated reports belong in `docs/` (session / audit).
5. Source of truth for code tree: unzip of canonical zip OR synced monorepo folders (`shared/`, `apps/`), not root-scattered `.kt`.

## Policy going forward
- Upload **one** canonical zip per session end (overwrite mindset: trash previous zip first if same name stacks).
- Do not re-upload STATE unless content changed; prefer edit in place when possible.
- Kotlin sources only under `shared/**` and `apps/**` paths.

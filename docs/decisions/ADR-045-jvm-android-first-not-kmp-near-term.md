# ADR-045 — JVM / Android first; KMP is not a near-term goal

- **Status:** Accepted
- **Date:** 2026-11-17
- **Decision ID:** D-9 (owner)
- **Related:** ADR-001 (historical KMP map), ADR-037, ADR-042

## Context

ADR-001 and several docs assume Kotlin Multiplatform (`commonMain`). The repository is overwhelmingly `kotlin("jvm")` with Android/desktop apps. Claude audit (2026-11-16) found no multiplatform plugin usage. Premature KMP forces a full build-system migration.

## Decision

1. **Near-term platform target is JVM + Android** (desktop JVM as secondary).
2. **KMP is deferred** until product UI and engine boundaries are stable and an explicit ADR re-opens KMP.
3. Do **not** invent empty `commonMain` trees or claim KMP readiness.
4. ADR-001 remains historical for logical module naming only; it does not require multiplatform source sets now.

## Consequences

- Gradle modules stay JVM unless a module has a real Android-specific need (`android` source set only when required).
- Docs and TARGET trees must not prescribe `commonMain` as the default layout.
- Future KMP requires a new ADR and migration map.

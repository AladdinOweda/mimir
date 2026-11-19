# Gradle Baseline — 2026-11-05

## Fixed in this cycle (artifacts + Drive upload)
- `settings.gradle.kts`: modules only; `includeBuild("build-logic")`; **no** `:shared` aggregate
- Apps depend on `:shared:kernel` only (not `:shared`)
- Every included module has `build.gradle.kts`
- Forge → depends on `:shared:engine:terminal`
- Integration → kernel + terminal + forge + process + vault
- Root version `0.5.0` via root `build.gradle.kts` + `gradle.properties` `mimir.version`
- Wrapper: Gradle 8.11.1

## Drive note
Previous Drive `settings.gradle.kts` (binary/octet upload) must be replaced by this text file.

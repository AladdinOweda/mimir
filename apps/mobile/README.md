# apps/mobile

**MIMIR-REPO-002** Android-first entry.

Physical modules (Gradle):
- `apps/android` — JVM/bootstrap shell
- `apps/androidApp` — AGP application (when SDK present)

Do not duplicate business logic here. This directory is the **target name** alias until modules are renamed in a future ADR.

# ADR-021: Eighteen Templates Strategy

- **Status:** Accepted
- **Date:** 2026-10-30
- **Related:** ADR-017, Forge design notes

## Context

Users asked for a broad template catalog. Shipping empty shells violates quality rules.

## Decision

1. Phase 03.5 ships the **full set of 18** template *definitions* with real scaffold intent.
2. Scaffold implementations land incrementally; priority order:
   - kotlin-android-basic, compose-desktop, python-cli, node-express, rust-cli, go-cli
   - then remaining 12
3. **No-Code path:** natural-language description → template suggestion (yes)
4. **Manual path:** picker UI
5. Later: Loops may generate/refine projects from templates (path B)

## Catalog (ids)

kotlin-android-basic, kmp-shared-library, compose-desktop, compose-multiplatform-app,
android-library, gradle-java-lib, node-express, react-vite, static-site, node-cli,
python-cli, python-data-notebook, rust-cli, go-cli, cmake-cpp, maven-java,
dotnet-console, microservice-skeleton

## Consequences

- BuiltinTemplates.all has size 18
- TemplateRegistry and UI list all 18; scaffold depth may vary by id in early 03.5

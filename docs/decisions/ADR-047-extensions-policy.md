# ADR-047 — Extensions policy (F4-4)

- **Status:** Accepted
- **Date:** 2026-11-17
- **Related:** ADR-046 · F4-4 · Claude D-7

## Context
`extensions/` held manifests/templates and could be mistaken for a parallel module system.

## Decision
1. **No independently versioned extension SDK** in v0.x.
2. `extensions/manifests` and `extensions/templates` are **documentation scaffolds only**.
3. Runtime capability extension = new **engine** under `engines/` + **contract** under `contracts/` + registration via Kernel capability catalog — not a separate extension loader.
4. Third-party plugins (browser-style) are a **Browser engine** concern (extension registry already sketched there), not a repo root.

## Consequences
- Do not add Gradle modules under `extensions/`.
- Do not publish `extensions` as a product surface until a future ADR revisits.

## Links
- `extensions/README.md`
- Browser extension registry (engine-level)

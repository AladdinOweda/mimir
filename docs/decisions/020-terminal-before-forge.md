# ADR-020: Terminal Core (03.3) Before Forge (03.5)

- **Status:** Accepted
- **Date:** 2026-10-30
- **Related:** ADR-017, Notes_261030 phase reorder

## Context

Forge local builds, toolchain install, and tests all execute shell commands. Original plan placed Terminal at Phase 06, after Forge — ordering was invalid.

## Decision

- Insert **Phase 03.3 — Terminal Core** after Phase 03 (Files) and **before** Phase 03.5 (Forge).
- No splitting of layers across random phases: each capability lands in its phase.
- Forge must depend on TerminalService APIs (not reimplement process execution).

## Consequences

- Phase checklists and Blueprint phase table must list 03.3 then 03.5
- CloudExecutor may exist as skeleton without full Terminal; LocalExecutor requires Terminal

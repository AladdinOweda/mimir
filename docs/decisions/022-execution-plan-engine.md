# ADR-022: Execution Plan Engine (inspired by Termonster Prompt-to-Plan)

- **Status:** Accepted (design)
- **Date:** 2026-10-31
- **Related:** ADR-019 Terminal modes, ADR-016, Forge 017
- **License note:** Termonster source is **private**. This ADR captures *patterns only*.

## Context

Natural language → raw shell is unsafe and hard to audit. Termonster demonstrates Prompt-to-Plan: user intent becomes a structured plan before execution.

## Decision

Introduce **Execution Plan Engine** between Intelligence (model) and Terminal/Forge:

1. Intent understanding (AR/EN)
2. Context selection (budgeted)
3. Plan generation (ordered steps)
4. Risk classification per step
5. Approval policy (Assisted default)
6. Authorization / sandbox
7. Execute via TerminalService or Forge only
8. Verify results (**Evidence-Based Completion**)
9. Concise report

No feature may shell out except through TerminalService after plan approval rules.

## Evidence-Based Completion

Success requires observable checks (files exist, tests pass, artifact present) — not merely exit code 0 from a stub.

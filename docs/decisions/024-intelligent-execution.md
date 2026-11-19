# ADR-024: Intelligent Execution as cross-cutting system

- **Status:** Accepted
- **Date:** 2026-11-01
- **Context:** DeepSeek gap analysis + ChatGPT decision (full absorb, placement = cross-cutting).

## Decision
Intelligent Execution is a **cross-cutting environment** (Intent, Plan, Risk, Verification, Checkpoints) that uses Kernel jobs/permissions and Engine capabilities. It is **not** Kernel L15 and **not** solely inside Agent Runtime.

## Consequences
- Packages under `engine/execution/`, `engine/verification/`
- Docs: architecture/29-intelligent-execution.md
- Plans are first-class before high-impact Terminal/Forge/Browser actions

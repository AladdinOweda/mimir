# Mimir Engineering Doctrine (working)
Authority: below Constitution and ADRs; above ad-hoc chat instructions.

1. Only **approved** requirements may drive implementation tasks.
2. Builder never accepts own work; Verifier is independent.
3. Every task has an explicit acceptance criterion and optional test command.
4. Agents must not modify files outside their lane/ownership without coordinator grant.
5. Shared contracts (Kernel API, schemas) require coordinator-approved change.
6. Failures record reason codes; repeated reasons feed improvement proposals (human approves).
7. Git/worktree isolation is preferred; hooks are defense-in-depth, not sole security.
8. Evidence is append-only; corrections add new receipts, never erase history.

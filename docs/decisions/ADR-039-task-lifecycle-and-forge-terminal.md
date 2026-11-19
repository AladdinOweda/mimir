# ADR-039 — Task lifecycle, verification verdicts, Forge views, Terminal contract

- **Status:** Accepted
- **Date:** 2026-11-10

## Decision

### A) Execution lifecycle (single dictionary)
`Draft → Queued → Ready → Running ⇄ Paused → Blocked | WaitingApproval → Completed | Failed | Cancelled`

### B) Verification verdict (independent of execution)
`Unverified → Verified | Inconclusive | Rejected`
Verification is not owned by the executing engine. “Done” in UI must not mean Completed without Verified when verification is required.

### C) Forge Kanban is a **view**, not a second lifecycle
| Forge column | Maps to |
|--------------|---------|
| Backlog | Draft / Queued |
| Ready | Ready |
| In Progress | Running |
| Blocked | Blocked |
| Waiting for Approval | WaitingApproval |
| In Review | Completed ∧ Unverified |
| Done | Completed ∧ Verified |

### D) Terminal ↔ Forge
- Shared abstraction: **process/terminal execution port** (e.g. `ProcessExecutorPort` / TerminalService contract).
- Forge depends on that **generic execution contract**, not on embedding Terminal UI.
- Terminal engine remains a separate capability module; ADR-020 remains valid insofar as Forge uses the execution service, not a private fork of process code.

## Consequences
- Task services, Forge engine docs, and UX copy use dictionary (A)+(B) only.
- New status strings require an ADR amendment.


## Amendment G-04 (accepted 2026-11-10)
Accepted by owner 2026-11-10. Accepted: Completed+Inconclusive→InReview; Completed+Rejected→Ready; Failed/Cancelled→Backlog (not Done).

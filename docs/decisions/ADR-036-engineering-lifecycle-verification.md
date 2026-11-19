# ADR-036 — Engineering Lifecycle, Ownership & Independent Verification

Status: **Accepted (directional)** — 2026-11-04
Source: Pullboard audit (patterns only; no code import; MIT noted)

## Context
Multi-agent development of Mimir needs: approved requirements, task claims/leases, file ownership, independent verifier, evidence-bound acceptance.

## Decision
1. **No Pullboard Engine.** Capabilities live in Task/Process, Forge ownership, Verification, Evidence/Outbox.
2. **Nothing ships until a second agent verifies it** — Builder cannot self-accept.
3. Claims are atomic with lease TTL; expired leases re-queue.
4. Acceptance is bound to commit/ref + frozen requirement ids; stale verdicts invalidate on ref change.
5. Constitution > ADR > Engineering Doctrine > Approved Requirements > Tasks > Evidence.

## Consequences
Implement TaskBoard (claims), OwnershipRegistry, IndependentVerificationService, Requirement registry; tests PB-01…PB-08 minimum.

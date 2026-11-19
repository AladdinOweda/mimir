# Mimir Gap Checklist — Work Queue

**Date:** 2026-11-11
**Source of truth:** Target structure (MIMIR-REPO-002) + Blueprint package 2026-11-09 + INT-001…015
**Working style:** One gap (or small batch) at a time → implement → update this table → short maturity report
**Testing policy (owner decision):** Prefer finish code first; run the big test pass once per major batch (not micro-tests after every file). Evidence still recorded when a batch closes.

**Status legend**
- `OPEN` — not started
- `PARTIAL` — folder or stub exists, not real
- `DONE` — accepted for current phase
- `DEFER` — consciously later

**Priority**
- P0 — blocks correctness / structure honesty
- P1 — product vertical path
- P2 — polish / secondary surfaces

---

## A) Governance & root files

| ID | Gap | Priority | Status | Notes |
|----|-----|----------|--------|-------|
| G-A1 | CONTRIBUTING.md | P2 | OPEN | Target root |
| G-A2 | CODE_OF_CONDUCT.md | P2 | OPEN | Target root |
| G-A3 | SECURITY.md | P1 | OPEN | Trust surface |
| G-A4 | NOTICE (Apache) | P2 | OPEN | License companion |
| G-A5 | Single canonical Blueprint pointer (avoid dual BLUEPRINT-v4.4 vs 02-MIMIR-BLUEPRINT) | P1 | PARTIAL | Prefer package 02-… |

## B) Kernel layout vs Target

| ID | Gap | Priority | Status | Notes |
|----|-----|----------|--------|-------|
| G-B1 | Decide: multi-module kernel (api/host/…) **or** ADR “single kernel module is intentional” | P0 | DONE | ADR-042 Accepted 2026-11-11 |
| G-B2 | Kernel remains vendor-neutral (ratchet tests) | P0 | DONE | KernelNeutrality baseline 0 |
| G-B3 | ApprovalToken / fail-closed invoke | P0 | DONE | Vertical path evidence |
| G-B4 | Capability catalog outside kernel | P0 | DONE | contracts/capability |

## C) Contracts (largest structural hole)

| ID | Gap | Priority | Status | Notes |
|----|-----|----------|--------|-------|
| G-C1 | contracts/capability | P0 | DONE | catalog + policies |
| G-C2 | contracts/knowledge | P1 | PARTIAL | facade + in-memory |
| G-C3 | contracts for terminal / forge / browser / agent / workflow / events… | P0 | DONE | All 6 contract modules + engine/EventBus port binding 2026-11-11 |
| G-C4 | Typed event catalog aligned INT-010 | P1 | PARTIAL | EventCatalog subset |

## D) Shared services code depth

| ID | Gap | Priority | Status | Notes |
|----|-----|----------|--------|-------|
| G-D1 | shared/memory durable path | P1 | PARTIAL | file TSV |
| G-D2 | shared/secrets | P1 | PARTIAL | local AES stub |
| G-D3 | shared/events bus | P1 | PARTIAL | in-process |
| G-D4 | search / retrieval / sync / observability real code | P1 | OPEN | folders mostly empty |

## E) Engines depth

| ID | Gap | Priority | Status | Notes |
|----|-----|----------|--------|-------|
| G-E1 | terminal + process usable path | P1 | PARTIAL→stronger | Port risk gate, history, 3 tests OK 2026-11-12 |
| G-E2 | forge engine + task column projection | P1 | PARTIAL→stronger | PathLaneRegistry+honest build Port, 4 tests OK 2026-11-12 |
| G-E3 | browser / computer-use real driver | P1 | PARTIAL→stronger | Port+policy+tests; computer still layered |
| G-E4 | knowledge-workspace / unified-file-transfer / ai-hub | P1 | PARTIAL | knowledge-workspace engine DONE; others open |
| G-E5 | orchestration / agent-runtime depth | P1 | PARTIAL→stronger | agent cost+tests; orch collab still separate |

## F) Product services (recent progress)

| ID | Gap | Priority | Status | Notes |
|----|-----|----------|--------|-------|
| G-F1 | tasks + ForgeColumn mapping | P1 | DONE | tests OK |
| G-F2 | downloads lifecycle skeleton | P1 | DONE | skeleton |
| G-F3 | files LocalFileSystemCatalog sandbox | P1 | DONE | skeleton |
| G-F4 | retention + portability skeletons | P2 | DONE | skeleton |
| G-F5 | ProductServices integration (no engines) | P1 | DONE | ProductServicesSliceTest |
| G-F6 | ProductContext full engine attach smoke | P1 | PARTIAL | attachMinimal exists |

## G) Apps / UX

| ID | Gap | Priority | Status | Notes |
|----|-----|----------|--------|-------|
| G-G1 | App shell destinations Home/Chat/Forge | P1 | PARTIAL | model exists |
| G-G2 | ForgeBoardPresenter column lines (Android JVM) | P1 | DONE | presenter + test |
| G-G3 | Real Compose/Material screens (Home/Chat/Forge) | P1 | OPEN | design docs only |
| G-G4 | androidApp shows Forge columns in UI | P1 | OPEN | WebView MainActivity only |
| G-G5 | Desktop same shell | P2 | OPEN | bridge only |

## H) Quality / CI / ship

| ID | Gap | Priority | Status | Notes |
|----|-----|----------|--------|-------|
| G-H1 | Gradle green on service modules | P0 | OPEN | sandbox kills daemon; CI job drafted |
| G-H2 | tests/ tree formal suites | P2 | OPEN | tests live in modules |
| G-H3 | RELEASE-GATE human sign-off | P0 | OPEN | canShip false |
| G-H4 | UX acceptance | P0 | OPEN | |

## I) Intelligence product domains (INT)

| ID | Gap | Priority | Status | Notes |
|----|-----|----------|--------|-------|
| G-I1 | Specs MIMIR-INT-001…015 registered | P1 | DONE | docs only |
| G-I2 | Brain product surface | P1 | OPEN | facade only |
| G-I3 | Files / Downloads product UX | P1 | OPEN | services only |
| G-I4 | Secure Vault product UX vs engines/vault | P1 | OPEN | keep packages separate |

---

## Suggested work order (one-by-one / small batches)

1. **G-B1** — kernel layout decision (ADR)
2. **G-C3** — fill critical contracts (terminal, forge, browser)
3. **G-E1/E2** — deepen terminal + forge against contracts
4. **G-G3/G4** — one real screen (Forge columns)
5. **G-H1** — Gradle green when RAM allows
6. Then Brain/Files/Downloads product slices

---

## Batch close rule
After each batch: mark IDs DONE/PARTIAL, append date note, refresh DASHBOARD + short maturity report for the owner.


## Batch log
- **2026-11-11 G-B1:** ADR-042 single kernel Gradle module accepted. Next: G-C3 contracts (terminal/forge/browser).

- **2026-11-11 G-C3 (partial):** contracts/terminal, forge, browser + 3 unit tests OK. Remaining: agent/workflow/events contracts.

- **2026-11-11 G-C3 complete:** agent/workflow/events contracts; engines implement ports; EventBus implements EventPublisherPort; 6 contract tests OK.

- **2026-11-12 G-E1/E2 deepen:** Terminal history+Port risk; Forge lanes+gradlew-honest build; 7 engine tests OK.

- **2026-11-12 engine batch:** browser port, agent cost tests, knowledge-workspace engine. Not 100% fleet.

- **2026-11-12 CPV:** process risk, computer level gates, vault key validation — 8 tests OK.

- **2026-11-12 OWL:** loop as engine, collab close, workflow tests — 7 OK.

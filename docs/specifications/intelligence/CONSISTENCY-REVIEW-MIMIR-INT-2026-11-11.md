# Consistency Review — MIMIR-INT series vs Blueprint / ADRs

**Date:** 2026-11-11
**Scope:** MIMIR-INT-001…015 vs Blueprint, ADR-037…041, Constitution posture
**Outcome:** Series **adopted** into registry with official Document Registry IDs; status remains **Draft** (not implementation-verified; canShip unchanged false).

## 1. Alignment (consistent)

| Theme | Spec series | Governing refs |
|-------|-------------|----------------|
| Four domains: Brain, Files, Downloads, Secure Vault | INT-001…007 | Blueprint product surfaces; INT as product-layer specs |
| Kernel-first / domain ownership / provider-neutral | INT-001, INT-002 | Blueprint Kernel; ADR-041 kernel neutrality |
| Android-first, mobile-first | INT domain UX specs | D-6 / product posture |
| Knowledge/memory via adapters, not vendor in kernel | INT-002, INT-011, INT-012 | **ADR-038** |
| Security / privacy / acceptance gates | INT-008 | Blueprint security; ApprovalToken culture (ADR-040) |
| Data model, API/events, storage/sync | INT-009…011 | Complements engine contracts; not a second Kernel |
| Retention / portability / ops | INT-013…015 | Lifecycle & ops — product policy layer |

## 2. Tensions / must-not-drift (recorded)

| ID | Issue | Resolution rule |
|----|--------|-----------------|
| C-01 | INT may describe rich RAG/search; Blueprint §17 / ADR-038 keep RAG vendor **open** and adapters-first | INT-012 must not mandate Cognee/Vane/Graphify inside `kernel/`. Implementation = adapters + honest stubs first. |
| C-02 | INT Secure Vault vs engines/vault | Product **Secure Vault** domain (INT-006) is UX/policy layer; `engines/vault` remains capability engine behind KernelHost. Do not merge packages. |
| C-03 | INT Shared Services vs `shared/memory|secrets|events` | INT-002 is **contract intent**; code modules under `shared/` and `services/` implement subset. Gaps are implementation debt, not dual SoT. |
| C-04 | Task/Forge status language | Any task board language in INT must follow **ADR-039** (execution ⊥ verification). |
| C-05 | Capability invocation | Privileged actions still go **KernelHost + policy** (ADR-040/041). INT must not imply domain UI bypasses approval. |
| C-06 | Browser engine | Long-term **GeckoView** (D-6); WebView transitional. INT must not freeze a conflicting browser engine as mandatory production. |
| C-07 | canShip / acceptance | INT-008 acceptance criteria do **not** auto-flip canShip. RELEASE-GATE + human sign-off remain. |

## 3. Registry assignment

Official Document Registry IDs assigned (see DOCUMENT-REGISTRY-ENTRY-MIMIR-INT.md):

`MIMIR-DOC-REG-INT-001` … `MIMIR-DOC-REG-INT-015`

Document codes remain `MIMIR-INT-001` … `MIMIR-INT-015`.

## 4. Adoption decision

- **Adopt** series as official **Draft** product specifications under `docs/specifications/intelligence/`.
- **Not** a claim of code completeness or canShip.
- Next engineering work maps INT contracts → existing modules with gap list; no duplicate SoT trees.

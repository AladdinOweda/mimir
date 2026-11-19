# ADR-038 — Knowledge/Memory adapters and stack boundaries

- **Status:** Accepted (boundaries) · **External products (Cognee/Vane):** Proposed only
- **Date:** 2026-11-10

## Context
Blueprint §17 listed open backend/RAG choices. CONTEXT and other notes marked Kotlin/KMP as fixed. Package reviews introduced Cognee/Vane without a single adapter boundary. Drive/repo risked treating vendor names as kernel dependencies.

## Decision
### Settled
- **Client/app language:** Kotlin Multiplatform (Android-first, Desktop).
- **UI toolkit direction:** Compose Multiplatform (implementation maturity separate from this ADR).
- **Kernel:** vendor-neutral; no Cognee/Vane/Graphify imports inside `kernel/`.
- **Memory (product service):** `shared/memory` (`MemoryService`) — current durable path is file TSV; may later bind SQLDelight behind the same interface.
- **Secrets:** `shared/secrets` — not knowledge graph.

### Open (require future ADR before production lock-in)
- Primary server topology and cloud sync.
- Exact RAG stack.
- SQLDelight vs other durable engines as *mandatory* production default.

### Knowledge / Research
- All external knowledge systems (STAIR, Graphify, Cognee, Vane, “Knowledge Vault”) sit behind **adapter interfaces** in `adapters/` or `integrations/`.
- First implementation: **honest stubs** implementing the interface (non-production).
- Constitution sections naming Cognee/Vane remain **Proposed** until a dedicated adoption ADR.

## Consequences
- Product code depends on interfaces, not vendors.
- Blueprint §17 must distinguish Settled vs Open (see updated notes in STATE / compliance).

# ADR-027: Kernel-first implementation order

- **Status:** Accepted
- **Date:** 2026-11-01
- **Context:** User decision — deepen Kernel docs → Kernel code → stabilize → then Engine docs/code.

## Decision
1. Expand Universal Kernel specification to coding-grade detail.
2. Implement Kernel module (`registry`, `lifecycle`, `capability`, `permission`, `job`, `event`, `audit`) with in-memory backends and tests.
3. Prove independence with a **SampleEngine** (no product feature).
4. Only then deepen Terminal / Forge / Browser engine documents and implement those Engines against Kernel contracts.

## Consequences
- Phase 00 includes `:shared` kernel compile + unit tests as gate items.
- Product Engines must not bypass CapabilityResolver.
- IE remains outside Kernel core but uses JobManager + permissions.

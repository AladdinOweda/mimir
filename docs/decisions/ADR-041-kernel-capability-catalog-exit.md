# ADR-041 — Capability catalog and default policies leave the kernel

- **Status:** Accepted
- **Date:** 2026-11-10
- **Addresses:** Claude G-05 / Constitution §3.2

## Decision
1. `CapabilityCatalog` lives in `contracts/capability` (`com.mimir.contracts.capability`).
2. `DefaultCapabilityPolicies.seed` lives in the same module (depends on kernel security types only).
3. `KernelHost` default registry is **empty**; product composition seeds policies.
4. `EngineBootstrapNotes` lives in `packages/app-composition`, not kernel.
5. `KernelNeutralityTest` baseline is empty (zero engine-name lines in kernel).

## Consequences
- Engines/UI import catalog from contracts, not kernel.
- Kernel remains opaque-string + policy driven.

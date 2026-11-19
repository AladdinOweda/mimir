# ADR-040 — Capability policy registration (no silent Low on attach)

- **Status:** Accepted
- **Date:** 2026-11-10
- **Addresses:** Claude review G-06

## Decision
1. `KernelHost.attach` registers engine + manifest only. It must **not** auto-create `CapabilityPolicy` at RiskLevel.Low.
2. Invoke remains **fail-closed**: missing policy → deny (`no_policy`).
3. Explicit registration only via `capabilitySecurity.register` or `DefaultCapabilityPolicies.seed`.
4. Product composition (`ProductContext.attachDefaultEngines`) calls `DefaultCapabilityPolicies.seed` before attach.

## Consequences
- New engine capabilities without a seeded/registered policy cannot be invoked until policies are declared with honest risk levels.

# ADR-026: Universal Kernel and pluggable Engines

- **Status:** Accepted (target architecture)
- **Date:** 2026-11-01
- **Source:** Mimir Universal Architecture Blueprint 2026-10-31

## Decision
Kernel is engine-agnostic control plane (registry, lifecycle, capabilities, policy, jobs, events, audit). Engines are independently installable capability providers. Solutions compose capabilities; UI does not own policy.

## First proof
Minimal Kernel + one sample Engine: install → invoke → stop → remove without Kernel source edits.

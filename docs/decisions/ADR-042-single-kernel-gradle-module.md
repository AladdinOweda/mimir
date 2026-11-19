# ADR-042 — Single kernel Gradle module (logical packages inside)

- **Status:** Accepted
- **Date:** 2026-11-11
- **Gap ID:** G-B1
- **Related:** ADR-037 (layout), ADR-041 (catalog exit), MIMIR-REPO-002 target structure §kernel/

## Context

The **target repository structure** (MIMIR-REPO-002) describes a *logical* kernel decomposition:

`kernel-api`, `kernel-host`, `identity`, `authentication`, `authorization`, `capability-registry`, `lifecycle`, `policy`, `approval`, `audit`, `isolation`, `extension-trust`, `kernel-testing`, …

The **physical repo today** ships one Gradle module `:shared:kernel` / `kernel/` with internal packages:

| Package | Role |
|---------|------|
| `com.mimir.kernel.contract` | Core types, engine/capability envelopes |
| `com.mimir.kernel.host` | `KernelHost` attach/invoke |
| `com.mimir.kernel.security` | Approval + capability security |
| `com.mimir.kernel.permission` | Permission policy |
| `com.mimir.kernel.audit` | Audit log |
| `com.mimir.kernel.delivery` | Durable outbox |
| `com.mimir.kernel.persist` | Durable kernel services / JDBC store |

Creating empty Gradle subprojects for every target folder would **fake structure** without boundaries, slow CI further, and conflict with progressive implementation.

## Decision

1. **For the current phase, the kernel remains a single Gradle module.**
   This is intentional, not an accidental omission.

2. **Target structure names are logical domains**, mapped to **packages (and future optional splits)** — not mandatory one-folder-one-Gradle-module *today*.

3. **Mapping (authoritative until a future split ADR):**

   | Target logical unit | Current location |
   |---------------------|------------------|
   | kernel-api / contracts used by host | `kernel/.../contract` + `contracts/*` for external catalogs |
   | kernel-host | `kernel/.../host` |
   | authorization / policy / approval | `kernel/.../security`, `permission` |
   | audit | `kernel/.../audit` |
   | isolation / delivery durability | `kernel/.../delivery`, `persist` |
   | capability-registry (product policies) | **not in kernel** — `contracts/capability` (ADR-041) |
   | identity / authentication (full product IdP) | **deferred** — not required for invoke path |
   | kernel-testing | `kernel/src/test` |

4. **Do not create empty `kernel-api/`, `kernel-host/` Gradle projects** solely to match the tree diagram.

5. **Split is allowed later** when *all* of these hold:
   - Stable API boundaries (no circular package deps)
   - CI/memory can compile multiple kernel modules reliably
   - A dedicated ADR names the first modules to extract (likely `kernel-api` + `kernel-host`)

6. **Neutrality rules unchanged:** no engine/vendor names in kernel source; catalog stays outside (ADR-041).

## Consequences

- Gap **G-B1** closed for this phase: structure honesty > decorative modules.
- Docs/checklists must say “logical kernel domains” not “missing Gradle modules”.
- Blueprint/Target remain valid as *architecture*; physical layout follows progressive implementation.
- canShip still independent of this ADR.

## Rejected alternative

**Immediate multi-module split** of all target kernel folders — rejected as premature and harmful to build stability in constrained environments.

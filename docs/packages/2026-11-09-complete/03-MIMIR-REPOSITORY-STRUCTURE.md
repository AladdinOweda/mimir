---
document_id: MIMIR-ENG-REPO-001
title: Mimir Repository Structure
document_type: Repository Organization Standard
status: Proposed target structure — not a verified inventory of the current repository
language: en
scope: Repository layout, ownership, naming, documentation, tests, delivery
---

# Mimir Repository Structure

## 1. Purpose and status

This document proposes a maintainable target repository layout for Mimir. It is an architectural organization plan, not a claim that these folders or files already exist in the user's repository.

Before applying it to an existing repository, perform an inventory and map current files to these destinations. Reuse and reconcile existing files; do not create duplicates merely to match this proposal.

## 2. Design rules

- Keep the Kernel engine-agnostic.
- Organize by responsibility and bounded context.
- Keep contracts separate from implementations.
- Keep provider/vendor adapters at integration boundaries.
- Keep UI presentation separate from execution and policy enforcement.
- Put tests near the owning module or in a clearly mapped test tree.
- Keep generated files, build outputs, caches, secrets, and local environments out of version control.
- Use one canonical document per subject.
- Do not put version numbers in filenames; use metadata, tags, or release records.
- Preserve stable Document IDs in front matter.

## 3. Proposed top-level structure

```text
mimir/
├── README.md
├── LICENSE
├── CONTRIBUTING.md
├── SECURITY.md
├── CODE_OF_CONDUCT.md
├── CHANGELOG.md
├── .gitignore
├── .editorconfig
├── .github/
│   ├── ISSUE_TEMPLATE/
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── workflows/
├── docs/
│   ├── governance/
│   ├── architecture/
│   ├── adr/
│   ├── product/
│   ├── ux/
│   ├── design-system/
│   ├── engines/
│   ├── runtime/
│   ├── security/
│   ├── privacy/
│   ├── operations/
│   ├── testing/
│   └── delivery/
├── contracts/
│   ├── kernel/
│   ├── modules/
│   ├── capabilities/
│   ├── events/
│   ├── commands/
│   ├── permissions/
│   └── schemas/
├── kernel/
│   ├── registry/
│   ├── lifecycle/
│   ├── dispatch/
│   ├── events/
│   ├── compatibility/
│   ├── policy-integration/
│   └── health/
├── platform/
│   ├── identity/
│   ├── authorization/
│   ├── configuration/
│   ├── secrets/
│   ├── persistence/
│   ├── storage/
│   ├── observability/
│   ├── notifications/
│   └── search/
├── runtimes/
│   ├── agent-runtime/
│   ├── orchestration/
│   ├── workflow-runtime/
│   ├── bot-profiles/
│   ├── loop-runtime/
│   └── intelligent-execution/
├── services/
│   ├── projects/
│   ├── tasks/
│   ├── approvals/
│   ├── activity/
│   ├── history/
│   ├── files/
│   ├── knowledge/
│   └── preferences/
├── engines/
│   ├── chat/
│   ├── forge/
│   └── browser/
├── integrations/
│   ├── model-providers/
│   ├── tools/
│   ├── mcp/
│   ├── cloud/
│   └── external-services/
├── apps/
│   ├── mobile/
│   ├── web/
│   └── desktop/
├── packages/
│   ├── design-tokens/
│   ├── ui-components/
│   ├── app-shell/
│   ├── shared-types/
│   └── developer-sdk/
├── tests/
│   ├── contract/
│   ├── integration/
│   ├── end-to-end/
│   ├── security/
│   ├── performance/
│   └── fixtures/
├── examples/
├── scripts/
├── tools/
└── infra/
    ├── local/
    ├── containers/
    ├── deployment/
    └── monitoring/
```

This is a logical proposal. The actual language ecosystem may require different physical packaging or naming.

## 4. Ownership map

| Area | Primary ownership | Boundary |
|---|---|---|
| `contracts/` | Shared interface and schema definitions | No engine implementation |
| `kernel/` | Generic module lifecycle, registry, dispatch, events | No concrete engine names or business logic |
| `platform/` | Cross-cutting infrastructure | Provider-neutral interfaces where practical |
| `runtimes/` | Agent, orchestration, workflow, bot, loop, execution behavior | No UI rendering |
| `services/` | Product domain services | Must not duplicate runtime ownership |
| `engines/` | User-facing capability modules | Must integrate through contracts |
| `integrations/` | Vendor/provider/service adapters | Vendor-specific details stay here |
| `apps/` | Platform entry points and composition | No bypass of service policy |
| `packages/` | Reusable UI, tokens, SDK, types | Avoid circular dependencies |
| `tests/` | Cross-module and system verification | Tests must state what they actually verify |
| `docs/` | Governance and technical/product documentation | One canonical source per topic |

## 5. Dependency rules

Allowed direction, subject to explicit contracts:

```text
apps
 ├── packages (UI, shell, tokens)
 ├── services
 └── contracts / client SDK

engines ───────────────> contracts / SDK
runtimes ──────────────> contracts + platform abstractions
services ──────────────> contracts + platform abstractions
integrations ─────────> contracts + vendor SDKs
kernel ────────────────> contracts + minimal platform primitives
```

Forbidden patterns:
- `kernel` imports an engine implementation.
- A UI component directly invokes a privileged external tool while bypassing policy.
- One engine imports another engine's private implementation instead of a shared contract.
- Provider SDK types leak through the whole product.
- Runtime components depend on UI state.
- Circular dependencies between engines, services, and runtimes.

## 6. Documentation organization

Suggested canonical documents:
- `docs/governance/constitution.md`
- `docs/architecture/blueprint.md`
- `docs/architecture/kernel-architecture.md`
- `docs/architecture/module-system.md`
- `docs/adr/ADR-NNN-title.md`
- `docs/product/product-requirements.md`
- `docs/ux/ux-interaction-specification.md`
- `docs/ux/application-shell-specification.md`
- `docs/design-system/liquid-design-system.md`
- `docs/engines/<engine-name>.md`
- `docs/runtime/<runtime-name>.md`
- `docs/security/security-architecture.md`
- `docs/privacy/data-lifecycle.md`
- `docs/testing/verification-strategy.md`
- `docs/delivery/release-process.md`

Use the project's agreed naming convention if an existing canonical convention is already approved. Do not create a second copy solely because a suggested path differs.

## 7. File naming and metadata

Recommended:
- Lowercase kebab-case filenames.
- Stable IDs in front matter.
- No version suffixes in filenames.
- Version/status/date in metadata or release notes.
- ADR numbers remain stable and are never reused.
- Avoid vague names such as `final2`, `new-blueprint`, or `latest-fixed`.

Example front matter:

```yaml
---
document_id: MIMIR-ARCH-KERNEL-001
title: Kernel Architecture
status: Proposed
owner: Architecture
---
```

## 8. Tests and quality layout

- Contract tests verify compatibility and required semantics.
- Unit tests verify local behavior.
- Integration tests verify interactions across real module boundaries.
- End-to-end tests verify user-visible workflows.
- Security tests verify authorization, isolation, and abuse cases.
- Performance tests measure startup, rendering, memory, battery-sensitive work, and execution latency.
- Fixtures must be synthetic and must not contain real credentials or private user data.

A green test suite is evidence only for the tests that were run, in the environment and commit recorded.

## 9. Configuration and secrets

- Commit safe examples such as `.env.example`, never real `.env` secrets.
- Validate configuration at startup and fail clearly on invalid required values.
- Separate user preferences from deployment configuration and secrets.
- Document defaults, scopes, and precedence.
- Avoid embedding credentials in mobile bundles or client-side source.

## 10. What must not be committed

Typical exclusions:
- Build outputs and caches.
- Dependency directories generated by package managers.
- Local IDE state unless explicitly shared by policy.
- Logs, crash dumps, temporary exports.
- Secrets, tokens, private keys, session cookies.
- User databases and real private datasets.
- Large generated assets that belong in controlled artifact storage.

## 11. Migration procedure for an existing repository

1. Inventory all current files and folders.
2. Record file path, purpose, owner, status, and references.
3. Identify canonical documents and duplicate/conflicting copies.
4. Map existing content to the target structure.
5. Mark each item: keep, move, merge, update, archive, or delete.
6. Check inbound links, imports, build configuration, and CI references.
7. Move in small batches with a manifest.
8. Run relevant tests after each code move.
9. Update documentation links and ownership records.
10. Produce a migration report; do not claim completion until verified.

## 12. Initial implementation warning

Do not create every proposed directory as empty scaffolding before the architecture and toolchain are settled. Start with the smallest structure needed for the first vertical slice, then expand when a real module or contract requires it.

---

## 13. Optional external integration organization

Review record date: 2026-11-09.

The following is a target ownership map only. Do not create these folders until the current repository inventory and existing ADRs are reconciled.

Suggested logical locations:
- `contracts/knowledge/` — provider-neutral Knowledge contracts.
- `contracts/memory/` — provider-neutral Memory contracts.
- `contracts/research/` — provider-neutral external Search/Research contracts.
- `integrations/knowledge-memory/cognee/` — optional Cognee adapter, capability manifest, version pin, and mapping tests.
- `integrations/research-search/vane/` — optional Vane Search adapter, API protocol parser, capability manifest, and mapping tests.
- `services/knowledge-vault/` — Mimir-owned Vault behavior and persistence abstractions, if not already present.
- `services/secrets-vault/` — isolated secret storage and secret-reference resolution, if not already present.
- `tests/contracts/knowledge/`, `tests/contracts/memory/`, `tests/contracts/research/` — provider conformance tests.
- `tests/integration/cognee/`, `tests/integration/vane/` — opt-in integration tests requiring explicit service configuration.

Rules:
- Never put Cognee or Vane imports in Kernel packages.
- Avoid vendor names in core contract types and domain models.
- Keep provider-specific configuration in adapter/deployment configuration, not in shared user-facing domain records.
- Store only safe configuration templates in the repository. Never commit API keys, SearXNG credentials, user datasets, uploaded documents, graph databases, vector indexes, SQLite user data, or local model files.
- Pin reviewed provider versions and transitive dependencies; maintain license and software-bill-of-materials records.
- Every adapter must be removable while preserving canonical Mimir records or providing an export/migration path.
- Keep integration tests opt-in and isolated from production user data.
- Do not create duplicate search, memory, graph, or vault services when an equivalent canonical module already exists; map and reuse existing responsibilities.

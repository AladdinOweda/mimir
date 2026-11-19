# Mimir Documentation Package Manifest

Review date: 2026-11-09

## Package contents

This package contains 10 specification documents:
- 7 previously created documents, of which 6 were updated in this delivery.
- 3 newly created documents.

## Existing documents

| File | Document ID | Delivery action |
|---|---|---|
| 01-MIMIR-DESIGN-TOKENS-THEME-CONTRACT.md | MIMIR-UX-DTC-001 | Preserved unchanged; no integration-specific token changes required |
| 02-MIMIR-COMPONENT-LIBRARY-SPECIFICATION.md | MIMIR-UX-CLS-001 | Updated: search/research UI components |
| 03-MIMIR-APPLICATION-SHELL-SPECIFICATION.md | MIMIR-UX-SHELL-001 | Updated: global search/research entry point |
| 01-MIMIR-CONSTITUTION.md | MIMIR-GOV-CON-001 | Updated: Cognee accepted optional; Vane candidate boundaries |
| 02-MIMIR-BLUEPRINT.md | MIMIR-ARCH-BP-001 | Updated: provider-neutral integration architecture and repository reviews |
| 03-MIMIR-REPOSITORY-STRUCTURE.md | MIMIR-ENG-REPO-001 | Updated: adapter/contracts/test ownership guidance |
| 04-MIMIR-LIQUID-DESIGN-SYSTEM-COMPREHENSIVE-UX-SPECIFICATION.md | MIMIR-UX-LIQUID-001 | Updated: search/research experience |

## New documents

| File | Document ID | Scope |
|---|---|---|
| 05-MIMIR-UX-INTERACTION-SPECIFICATION.md | MIMIR-UX-INT-001 | Cross-platform interaction contract |
| 06-MIMIR-SCREEN-BY-SCREEN-SPECIFICATION.md | MIMIR-UX-SCREEN-001 | Approved Home, Chat, Forge screens; pending screen families marked |
| 07-MIMIR-FORGE-SPECIFICATION.md | MIMIR-ENG-FORGE-001 | All eleven approved Forge pages |

## External repository review records

### Cognee — accepted optional integration
- Repository: https://github.com/topoteretes/cognee
- Reviewed branch: `main`
- Reviewed files: README.md, pyproject.toml, LICENSE, and selected remember/recall/improve/forget API implementation.
- Reviewed pyproject metadata at review time: version 1.6.7, Python >=3.10,<3.15, Apache-2.0.
- Decision: accepted as an optional provider behind Mimir-owned Knowledge & Memory contracts.
- Not verified: Android/Termux ARM64 runtime, production deployment, complete security posture, performance, or Mimir adapter implementation.

### Vane Search candidate — not yet adopted
- Supplied repository URL: https://github.com/BunsDev/perplexica-search-engine-ai
- Reviewed branch: `master`
- Repository README/package currently identify the application as Vane, version 1.12.2, MIT.
- Reviewed files: README.md, package.json, LICENSE, architecture/working docs, Search API docs, chat/search routes, search agent/researcher/actions, model registry, upload manager, SQLite schema, SearXNG integration, Dockerfile, and config/provider routes.
- Decision: candidate for an optional Search/Research adapter prototype; not approved as a production dependency.
- Not verified: complete authentication/middleware posture, production hardening, full dependency audit, runtime tests, or Android native compatibility.

## Important architecture rules

- Kernel remains provider-neutral and engine-agnostic.
- Cognee is optional Knowledge & Memory infrastructure; it is not the Knowledge Vault product and never replaces Secrets Vault.
- Vane is an optional external research/search candidate; it is not Memory, Knowledge Vault, or a new Mimir engine.
- Search results do not become permanent Memory/Knowledge automatically.
- No build, test, security audit, or device compatibility certification is claimed.

## Status note

The source Markdown files were read from the two user-provided ZIP packages. Existing Document IDs were preserved. New Document IDs were assigned to the three missing documents.

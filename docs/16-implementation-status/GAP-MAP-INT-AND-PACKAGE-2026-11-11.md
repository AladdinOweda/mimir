# Gap Map — MIMIR-INT + Documentation Package → Code

**Date:** 2026-11-11
**Inputs:** MIMIR-INT-001…015 (registered), Package 2026-11-09 (Constitution/Blueprint/UX/Forge), ADR-037…041
**Purpose:** Single backlog for the next engineering cycles. Status is evidence-based, not aspirational %.

Legend: **P** = present skeleton/partial · **S** = solid for current phase · **G** = gap vs spec · **N** = not started as product surface

## A) Platform core

| Capability | Spec | Code locus | Status | Next action |
|------------|------|------------|--------|-------------|
| KernelHost invoke + audit + ApprovalToken | Blueprint, ADR-040/041 | `kernel/` | **S** | Keep tests green; expand policies via seed only |
| Capability catalog | ADR-041 | `contracts/capability` | **S** | Grow IDs as engines register |
| Engine attach composition | Product | `packages/app-composition` | **P/S** | Wire only engines with real ports |
| Task status + Forge view mapping | ADR-039, Forge package | `services/tasks` | **S** contracts | Wire Forge engine + UI columns |
| Memory / secrets durable | INT-002, ADR-038 | `shared/memory`, `shared/secrets` | **P** (file/TSV, local AES) | Adapter boundary for optional RAG later |
| Activity log | INT-002/015 | `services/activity` | **P** | Keep durable path; add retention hooks (INT-014) |
| Gradle CI full graph | RELEASE-GATE | `.github/workflows/ci.yml` | **G** | Need Actions green log on real runner |

## B) Intelligence domains (INT)

| Domain | Spec | Code locus | Status | Next action |
|--------|------|------------|--------|-------------|
| **Brain** | INT-003, 012 | `engines/knowledge-workspace`, `shared/retrieval`, `shared/search` | **P/N** product | Define Brain facade service + 1 search path; no vendor in kernel |
| **Files** | INT-004 | `services/files`, `engines/unified-file-transfer` | **P** | FileCatalog API → real FS adapter; UX list screen later |
| **Downloads** | INT-005 | (thin / missing dedicated engine) | **G** | New `services/downloads` or engine + lifecycle states |
| **Secure Vault (product)** | INT-006, 008 | UX vs `engines/vault` | **P** engine / **G** product UX | Keep engine; product screens + policy UX separate |
| Integration | INT-007 | ProductContext + events | **P** | EventBus topics named in INT-010 |
| Data model | INT-009 | scattered models | **G** | Single `contracts/data` or shared models module |
| API/Events | INT-010 | partial EventBus | **G** | Publish typed event catalog |
| Storage/Sync/Offline | INT-011 | `shared/storage`, `shared/synchronization` | **P/G** | Spec vs thin modules — prioritize local-first |
| Import/Export | INT-013 | **G** | Portability job interface |
| Retention | INT-014 | **G** | Policy service + activity hooks |
| Ops monitoring | INT-015 | `shared/observability`, diagnostics | **P** | Health endpoint/stub |

## C) UX package (2026-11-09)

| Spec | Code locus | Status | Next action |
|------|------------|--------|-------------|
| Design tokens | `packages/design-tokens` | **P** | Align names to MIMIR-UX-DTC-001 |
| Component library | `packages/ui-components` | **P** | Map CLS components to Compose stubs |
| Application shell | `packages/app-shell` (+ ShellCompose) | **P** | One shell with nav slots for Home/Chat/Forge |
| Liquid UX | docs only | **G** | Visual system after shell nav works |
| UX interaction | docs | **G** | Encode gestures/shortcuts as test checklist |
| Screen-by-screen | docs | **G** | Implement **Home + Chat + Forge** only first |
| Forge specification | `engines/forge` + docs | **P** | Board view bound to TaskStatus/ForgeColumn |

## D) Apps

| App | Status | Next |
|-----|--------|------|
| `apps/android` | **P** bridge | Host shell + WebView transitional |
| `apps/desktop` | **P** bridge | Same shell |
| `apps/iosApp` | structure | Defer |

## E) Explicit non-goals this cycle
- canShip true
- Full GeckoView production browser
- Full RAG vendor lock-in
- Replacing Kernel with domain-specific security bypasses


## Update 2026-11-11 S2
- `services/downloads` InMemoryDownloadDirectory + lifecycle enum (INT-005 skeleton)
- `LocalFileSystemCatalog` sandboxed (INT-004)
- `contracts/knowledge` KnowledgeFacade + InMemoryKnowledgeFacade (INT-003 / ADR-038)
- Evidence: EVIDENCE-S2-downloads-files-knowledge-2026-11-11.md

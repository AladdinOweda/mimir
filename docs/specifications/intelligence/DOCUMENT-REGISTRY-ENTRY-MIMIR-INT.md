# Document Registry — MIMIR-INT series

**Registry version:** 1.0
**Assigned:** 2026-11-11
**Authority:** Owner adoption + consistency review

| Document ID | Code | Title | Path | Status |
|-------------|------|-------|------|--------|
| MIMIR-DOC-REG-INT-001 | MIMIR-INT-001 | Architecture & Domain Boundaries | docs/specifications/intelligence/MIMIR-INT-001-ARCHITECTURE-AND-DOMAIN-BOUNDARIES.md | Draft — Registered |
| MIMIR-DOC-REG-INT-002 | MIMIR-INT-002 | Shared Services Contracts | …/MIMIR-INT-002-SHARED-SERVICES-CONTRACTS.md | Draft — Registered |
| MIMIR-DOC-REG-INT-003 | MIMIR-INT-003 | Brain Functional & UX | …/MIMIR-INT-003-BRAIN-FUNCTIONAL-AND-UX-SPECIFICATION.md | Draft — Registered |
| MIMIR-DOC-REG-INT-004 | MIMIR-INT-004 | Files Functional & UX | …/MIMIR-INT-004-FILES-FUNCTIONAL-AND-UX-SPECIFICATION.md | Draft — Registered |
| MIMIR-DOC-REG-INT-005 | MIMIR-INT-005 | Downloads Functional & UX | …/MIMIR-INT-005-DOWNLOADS-FUNCTIONAL-AND-UX-SPECIFICATION.md | Draft — Registered |
| MIMIR-DOC-REG-INT-006 | MIMIR-INT-006 | Secure Vault Functional UX & Security | …/MIMIR-INT-006-SECURE-VAULT-FUNCTIONAL-UX-AND-SECURITY-SPECIFICATION.md | Draft — Registered |
| MIMIR-DOC-REG-INT-007 | MIMIR-INT-007 | Integration | …/MIMIR-INT-007-INTEGRATION-SPECIFICATION.md | Draft — Registered |
| MIMIR-DOC-REG-INT-008 | MIMIR-INT-008 | Security Privacy & Acceptance | …/MIMIR-INT-008-SECURITY-PRIVACY-AND-ACCEPTANCE-SPECIFICATIONS.md | Draft — Registered |
| MIMIR-DOC-REG-INT-009 | MIMIR-INT-009 | Data Model & Entity Relationships | …/MIMIR-INT-009-DATA-MODEL-AND-ENTITY-RELATIONSHIPS.md | Draft — Registered |
| MIMIR-DOC-REG-INT-010 | MIMIR-INT-010 | API & Event Contracts | …/MIMIR-INT-010-API-AND-EVENT-CONTRACTS.md | Draft — Registered |
| MIMIR-DOC-REG-INT-011 | MIMIR-INT-011 | Storage Sync & Offline | …/MIMIR-INT-011-STORAGE-SYNC-AND-OFFLINE-ARCHITECTURE.md | Draft — Registered |
| MIMIR-DOC-REG-INT-012 | MIMIR-INT-012 | Search & Retrieval | …/MIMIR-INT-012-SEARCH-AND-RETRIEVAL-ARCHITECTURE.md | Draft — Registered |
| MIMIR-DOC-REG-INT-013 | MIMIR-INT-013 | Import Export & Portability | …/MIMIR-INT-013-IMPORT-EXPORT-AND-DATA-PORTABILITY.md | Draft — Registered |
| MIMIR-DOC-REG-INT-014 | MIMIR-INT-014 | Retention Deletion & Lifecycle | …/MIMIR-INT-014-RETENTION-DELETION-AND-DATA-LIFECYCLE-POLICY.md | Draft — Registered |
| MIMIR-DOC-REG-INT-015 | MIMIR-INT-015 | Operational Monitoring & Recovery | …/MIMIR-INT-015-OPERATIONAL-MONITORING-AND-RECOVERY.md | Draft — Registered |

## Dependency sketch
001 → 002 → (003|004|005|006) → 007
001+Constitution → 008
001+002 → 009 → 010|011|012
009+domains → 013|014
007+008+011 → 015

## Cross-refs (mandatory)
- ADR-037 layout · ADR-038 knowledge adapters · ADR-039 task/verify · ADR-040/041 capability policy & catalog exit
- Blueprint §17 open decisions remain open unless a later ADR closes them

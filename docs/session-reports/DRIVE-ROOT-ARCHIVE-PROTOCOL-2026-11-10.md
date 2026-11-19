# Drive root archive protocol

**Do not bulk-delete without listing.** Goal: one STATE, one DASHBOARD, one RELEASE-GATE, one ENGINE-REGISTRY-MATRIX, one canonical zip.

## Steps
1. List root files named STATE.md, DASHBOARD.md, RELEASE-GATE.md, ENGINE-REGISTRY-MATRIX.md, mimir-repo-canonical.zip.
2. Keep the newest by modified time **after** confirming content includes ADR-037+ and EVIDENCE link (or re-upload from this session).
3. Move older copies to `archive/2026-11/` (create folder) — trash only after copy confirmed.
4. Before archiving ENGINE-REGISTRY-MATRIX versions: open each; if an older file has more engine rows, merge into the kept file first (F-09).
5. After cleanup: single STATE points at ADR-037/035/036 and EVIDENCE path.

## Connector note
Drive upload may create a new file ID each time (create vs update). Prefer trash of superseded IDs after archive copy.

# Drive / Dropbox audit acceptance — 2026-11-05

Accepted findings from external review of `/Mimir`:
1. REPO-STRUCTURE vs actual tree naming mismatches historically
2. `:shared` aggregate dependency problem on Drive settings/apps
3. Multiple engine versions (V2/V3) still present on Drive tree
4. ApprovalToken / JdbcKernelStore not found as claimed on Drive tree
5. TaskIsolation approveMerge accepts approvalGranted — policy violation
6. InMemory defaults for audit/tasks/vault
7. UX AppShell still engine-centric vs product destinations
8. Duplicate root files and dual canonical zips on Drive
9. VERSION / MATURITY / ADR-INDEX missing from some Drive listings
10. Prior smoke reports not binding evidence for current Drive snapshot

Response this cycle: honest STATE/DASHBOARD/CHECKLIST; CANONICAL SSOT; UX spec checked in; trash older Drive duplicates of STATE/LICENSE/PRIVACY/RELEASE-GATE/zip where identifiable.

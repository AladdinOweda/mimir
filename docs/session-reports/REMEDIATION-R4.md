# R4 Persistence & Recovery — 2026-11-04

## Delivered
- `KernelPersist.sq` schema (audit, approval_token, permission_grant)
- `JdbcKernelStore` SQLite durable store
- `DurablePermissionPolicy` / `DurableApprovalService` / `PersistingAuditLog`
- Recovery after process restart: permissions, token consume, audit reload
- Smoke **10/10 PASS**

## Not closed
- Full KernelHost constructor injection of durable services (host still default in-memory; durable APIs ready)
- Task/Process/Verification durable stores product-wide
- SQLDelight codegen in Gradle CI

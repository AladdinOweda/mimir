# R2 Kernel Security — 2026-11-04

## Delivered
- `ApprovalService` — tokens bound to principal/action/resource/scope, TTL, single-use, revoke
- `CapabilitySecurityRegistry` + default high-risk policies (terminal.execute, vault.write, computer.act, …)
- `KernelHost.invoke` always resolves permission from policy (no null permission path)
- `approvalGranted` **stripped and ignored**; high/critical requires `approvalToken`
- `AuditLog` append-only ALLOW/DENY

## Tests (8/8 PASS)
R2_deny_no_perm · deny_no_token_despite_boolean · allow_with_token · token_single_use · wrong_principal · audit deny/allow · no_policy

## Remaining (not closed)
- Wire all production engines exclusively through KernelHost
- Persist audit (P4)
- Full RBAC/ABAC roles
- Engine-side residual approvalGranted reads in legacy archived code

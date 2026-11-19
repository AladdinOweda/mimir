# Product-path durable storage — 2026-11-05

## Layout under data root
```
<dataRoot>/
  kernel/kernel.db     # audit_entry, approval_token, permission_grant
  tasks/tasks.db       # task_item (+ evidence if configured)
  vault/vault.db       # ciphertext blobs only
```

## API
- `KernelHost.durable(dataDir)` — AuditLog + ApprovalService + PermissionPolicy on SQLite
- `ProductHosts.kernel / taskBoard / vault` — integration factory
- `TaskBoardEngine.start()` reloads items from `JdbcTaskStore`
- `VaultEngine(durableDb=…)` reloads envelopes on start

## Tests (this cycle)
10/10 PASS: audit/token/perm recover · task recover · vault recover

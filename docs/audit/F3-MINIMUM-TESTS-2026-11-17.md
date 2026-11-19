# F3 — Minimum tests (2026-11-17)

Claude H-09: modules with code and no tests receive at least one negative/smoke test.

## Modules covered this batch
| Module | Test class | Cases |
|--------|------------|-------|
| shared/memory | MemoryServiceMinTest | missing get/delete + roundtrip |
| shared/secrets | SecretsVaultMinTest | missing get/delete + AES roundtrip |
| shared/events | EventBusMinTest | no subscribers; handler isolation |
| shared/configuration | AppConfigMinTest | defaults; fromMap |
| services/approvals | UserApprovalFacadeMinTest | grant/deny unknown; one-shot grant |
| services/activity | ActivityLogMinTest | empty recent; record |
| services/projects | ProjectDirectoryMinTest | get missing; empty list |
| services/notifications | NotificationServiceMinTest | markRead missing; unread |
| services/portability | DataPortabilityMinTest | status missing; export job |
| contracts/capability | CapabilityCatalogMinTest | stable IDs; seed policies |

## Execution
- Environment: kotlinc 2.0.21 + JUnit 4 (sandbox)
- Result: **OK (23 tests)**
- Not a substitute for full `./gradlew test` / CI green (F0 rest)

## Still light / deferred
- apps/desktop (UI entry; covered partially by host tests elsewhere)
- apps/android (has 1 existing test)
- Full CI matrix

## canShip
false

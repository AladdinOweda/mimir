# Kernel completion checklist — designed core surface

Status: **COMPLETE for designed core (2026-11-15)**

| Capability | Status |
|------------|--------|
| Engine attach / start / stop / detach | ✅ |
| Fail-closed capability policies (G-06) | ✅ |
| Permission grant/revoke/deny/check | ✅ |
| High/Critical approval tokens (no spoof) | ✅ |
| Audit ALLOW/DENY + query | ✅ |
| securitySnapshot metrics | ✅ |
| KernelLocalEventBus lifecycle + invoke | ✅ |
| DurableOutbox enqueue/retry/hold/purge | ✅ |
| Optional rate limit | ✅ |
| resolveEngineFor capability | ✅ |
| durable() JDBC factory | ✅ |
| Domain neutrality (no engine names in kernel) | ✅ |
| Automated tests (CompleteSuite + Deepen) | ✅ 9 OK |

Out of scope for "kernel 100%": real WebView drivers, Compose UI, full Gradle CI matrix, production multi-node consensus.
Those are product layers above the kernel.

# Evidence — Batch S1/S2 + retention + ProductContext wiring

DATE_UTC=2026-11-11T16:49:13Z (kotlinc batch)

## Kotlinc + JUnitCore
```
DATE_UTC=2026-11-11T16:49:13Z
kernel_ok
events_ok
tasks_ok
dl_ok
files_ok
know_ok
ret_ok
port_ok
JUnit version 4.13.2
.....
Time: 0.03

OK (5 tests)

JUnit version 4.13.2
.
Time: 0.032

OK (1 test)

JUnit version 4.13.2
.
Time: 0.06

OK (1 test)

JUnit version 4.13.2
.
Time: 0.039

OK (1 test)

JUnit version 4.13.2
.
Time: 0.032

OK (1 test)
```

| Suite | Result |
|-------|--------|
| TaskContracts + TaskForgeProjector | OK (5) |
| DownloadDirectory | OK (1) |
| LocalFileSystemCatalog | OK (1) |
| InMemoryKnowledgeFacade | OK (1) |
| DefaultRetentionPolicy | OK (1) |
| **Total** | **9 tests OK** |

## ProductContext
Wired: , , , durable  when useDurableStores=true.

## Gradle
- Wrapper **Gradle 8.11.1** runs via .
- First multi-project test attempt failed on missing  (fixed to ); full  not confirmed green in session time budget.
- **Do not claim CI green.**

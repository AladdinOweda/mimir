# Evidence — ProductServices integration + Android Forge presenter

## Code
- `ProductServices` — engine-free graph
- `ProductServicesSliceTest` — integration without engines
- `apps/android/ForgeBoardPresenter` + `ForgeBoardPresenterTest`
- `AndroidShellBridge.forgeLines` / `forgeBoard`
- `androidApp/ForgeBoardListModel` placeholder for UI list rows
- CI job `services-unit-tests` with GRADLE_OPTS -Xmx2g (for hosts with RAM)

## Kotlinc results
| Suite | Result |
|-------|--------|
| ProductServicesSliceTest | OK (1) |
| ForgeBoardPresenterTest | OK (1) |
| TaskContracts + Projector | OK (5) |
| ForgeBoardUiTest | OK (1) |
| **Total** | **8 OK** |

## Gradle in this sandbox
Still not reliable (daemon killed). CI job targets ubuntu-latest with 2g heap.

## canShip
false

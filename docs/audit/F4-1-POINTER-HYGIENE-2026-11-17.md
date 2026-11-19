# F4-1 — Pointer hygiene (2026-11-17)

## Done
- Authored `docs/03-repository/repository-migration-map.md`
- `runtimes/`, `integrations/`, `benchmarks/`, `scripts/`, `samples/` marked pointer-only
- `tooling/scripts/ci-local.sh` receives scripts
- `tests/performance/` and `docs/examples/` stubs created
- No Gradle `projectDir` changes (safe batch)

## Not done
- F4-2 move of shared/{memory,secrets,events,configuration}
- Empty shared slot cleanup
- extensions policy

## Verification
- No source deletions of Kotlin modules
- canShip: false

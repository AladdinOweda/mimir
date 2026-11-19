# Next slice plan — Vertical “Shell + Tasks + Forge columns”

**Goal:** One demonstrable path on JVM tests (then Android shell), without claiming canShip.

## Slice S1 (this cycle)
1. **Gap map published** (done in GAP-MAP-INT-AND-PACKAGE-2026-11-11.md).
2. **Typed event names** minimal set in `shared/events` matching INT-010 subset: `task.created`, `task.status_changed`, `approval.requested`, `approval.granted`.
3. **TaskDirectory** emits status_changed; ProductContext optional hook to EventBus.
4. **ForgeColumn projection test** already exists — add integration test: create task → Running → Completed+Verified → Done column via EventBus listener (in-memory).
5. **AppShellController** exposes destinations: Home, Chat, Forge (enum only + unit test).
6. **CI workflow** includes `:services:tasks`, `:packages:app-shell`, `:shared:events` if not already.

## Slice S2 (follow-up)
- `services/downloads` skeleton + lifecycle enum (Queued/Fetching/Completed/Failed).
- Files: one `LocalFileSystemCatalog` implementing FileCatalog for user home sandbox.
- Brain: `KnowledgeFacade` interface in contracts + no-op / memory-backed impl.

## Exit criteria for S1
- Unit tests pass for shell destinations + task/event projection (kotlinc or Gradle evidence file).
- No kernel neutrality regression.
- STATE updated with evidence paths.
- canShip remains false.

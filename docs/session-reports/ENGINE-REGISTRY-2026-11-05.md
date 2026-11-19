# Engine Registry cycle — 2026-11-05

## Changes
- Removed `approvalGranted` **gates** from terminal, tools, workflow, IE, task-isolation
- SecureInvoker now takes `approvalToken` only
- KernelHost continues to strip `approvalGranted`
- ENGINE-REGISTRY-MATRIX.md lists sole product entry classes
- EngineBootstrap wires core + extended canonical engines only
- TaskIsolationEngine: incomplete isolation backends moved to src/scratch; slim stub requires token and does not fake Git merge

## Tests
- terminal/tools/workflow/ie/computer: kotlinc PASS
- TaskIsolation: deny without token PASS; token → AcceptedNotMerged PASS

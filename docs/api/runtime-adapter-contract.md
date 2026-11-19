# Runtime Adapter Contract (Mimir-owned)

## Rules
1. Adapters translate **Mimir** Agent/Workflow/Run contracts to external runtimes.
2. No external type may appear in Mimir public schemas.
3. Kernel never imports LangGraph/CrewAI/LangChain/AutoGPT/Dify.
4. Optional: process/service isolation only; not `commonMain`.
5. Failure/removal of adapter must leave native Flows operational.
6. Permissions, secrets, sandbox, audit stay Mimir-owned.

## Required adapter surface
- `adapterId`, `version`, `supportedContractVersions`
- `capabilities()` discovery
- `health()`
- `startRun(definition, runContext) -> runId`
- `resume(runId, approval?)`
- `cancel(runId)`
- normalized events: node_started | node_finished | paused | failed | checkpoint

## First POC candidate
LangGraph (optional). CrewAI comparative only after native path works.
AutoGen: reference only. AutoGPT platform code: no copy (Polyform Shield).

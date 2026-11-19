---
document_id: MIMIR-ARCH-RECON-001
title: Mimir Blueprint Reconciliation — Seven Agent Framework Repositories
project: Mimir
version: 1.0
status: PROPOSED — Review Artifact, Not Yet Approved
created: 2026-11-03
source_baseline:
  repository_archive: Mimir.zip (2026-10-30)
  canonical_blueprint: BLUEPRINT-v4.1-FULL.md (inside archive)
  architecture_proposal: Mimir_Universal_Architecture_Blueprint_2026-10-31.md
  adr_range: ADR-001 through ADR-021
---

# Mimir Blueprint Reconciliation — Seven Agent Framework Repositories

## 0. Purpose and decision status

This document reconciles the earlier reviews of AutoGPT, LangChain, Dify, MetaGPT, AutoGen, CrewAI, and LangGraph against the actual Mimir source snapshot and the current documented architecture. It identifies what already exists, what is missing, what should be amended, and what should not be introduced.

**This is a reconciliation proposal, not an approved architecture amendment.** It does not modify the Constitution, existing ADRs, source code, or the canonical Blueprint. Proposed ADR numbers 022 and 023 are reserved recommendations only; they have not been created or approved.

The evidence baseline is the uploaded `Mimir.zip` dated 2026-10-30, containing 247 files, plus the generated `Mimir_Universal_Architecture_Blueprint_2026-10-31.md`. The archive is a snapshot, not proof of the current remote repository state or of a successful build.

## 1. Executive decision

**Do not import any of the seven projects wholesale and do not create seven new Engines.** Mimir already has documented or partially implemented equivalents for the major responsibilities:

- Model layer and `ModelRouter` for model selection/invocation.
- Agent Runtime, Tools, Skills, MCP, and Connector Gateway in the architecture/package map.
- Swarm/Council/OK Computer for multi-agent coordination.
- Loop Engineering with an independent Evaluator, Sandbox, Schedule, State, budgets, and Kill Switch.
- Mimir Flows / `FlowEngine` / `TriggerManager` as the planned lightweight automation layer.
- STAIR + Graphify + Mimir Vault for knowledge and retrieval.
- TerminalService + OpenShell for controlled shell execution.
- Forge for project scaffolding/build/test/artifacts.

The actual gaps are primarily **contracts and lifecycle semantics**, not missing top-level Engines:

1. A formal Agent Definition contract is missing.
2. A portable Workflow Definition / Block / Node / Edge contract is missing.
3. A durable Workflow Run / Checkpoint / Pause / Resume / Approval contract is missing.
4. The model abstraction is currently too small for interchangeable providers: `ModelRouter` exposes only `complete()` and its only implementation in the archive is a placeholder.
5. The Tool Registry describes risk as informational and does not yet define a complete enforceable permission, idempotency, timeout, retry, or output-schema contract.
6. The documentation does not consistently distinguish Agent Runtime, Swarm, Flows, Loops, Bots, Skills, Tools, and external framework adapters.

### Recommended disposition

| Repository | Mimir disposition | New Engine? | Code adoption now? |
|---|---|---:|---:|
| AutoGPT | Learn Blocks, graph design, visual workflow UX | No | No; platform code has a separate restrictive license |
| LangChain | Learn provider/model/tool abstractions and conformance tests | No | No direct KMP dependency; evaluate narrow adapters only |
| Dify | UX and RAG workflow reference | No | No; do not transplant platform code under current license conditions |
| MetaGPT | Apply SOP, role, handoff, and artifact patterns to existing Swarm/Flows | No | No framework dependency initially |
| AutoGen | Historical reference for event-driven agent messaging | No | No; archived and no longer maintained |
| CrewAI | Optional comparative runtime adapter experiment | No | Not before Mimir contracts and POC gates |
| LangGraph | First candidate for a durable graph-runtime adapter POC | No | Optional isolated adapter only, not a core dependency |

## 2. Authority and source reconciliation

### 2.1 Current source hierarchy in the archive

The archive's `CONSTITUTION.md` states that it is the highest project authority. The current `BLUEPRINT-v4.1-FULL.md` identifies `CONSTITUTION.md` as highest authority and `REPO-STRUCTURE.md` as the structure contract. The practical hierarchy to preserve is:

1. `CONSTITUTION.md` — non-negotiable principles and approved project constraints.
2. `BLUEPRINT-v4.1-FULL.md` — current product/technical blueprint, subordinate to the Constitution.
3. `REPO-STRUCTURE.md` — repository layout contract.
4. `docs/architecture/*` and `docs/api/*` — detailed architecture and machine-readable contracts.
5. `docs/decisions/001–021` — engineering decisions and their rationale.
6. Phase `SPEC.md`, `CHECKLIST.md`, and `ACCEPTANCE.md` — implementation scope and phase gates.

`Mimir_Universal_Architecture_Blueprint_2026-10-31.md` is a newer proposed target architecture. Its Universal Kernel / Shared Services / Engines / Solutions separation is useful and aligns with the extensibility principle, but its own status is Proposed. It must be reconciled into the canonical Blueprint rather than silently treated as an approved replacement.

### 2.2 Current documented architecture already covers the domains

| Existing Mimir area | Source evidence in archive | Meaning for this review |
|---|---|---|
| Model layer | `shared/.../engine/model/ModelRouter.kt`, `PACKAGE_MAP.md`, Blueprint layers L4–L5 | Do not add a LangChain Engine; strengthen the existing model contract. |
| Agent Runtime | `agents/runtime`, `agents/tools`, `agents/skills`, `agents/mcp`, `agents/connectors` in package map / repository structure | External agent frameworks must integrate through this boundary. |
| Swarm | ADR-004, `docs/architecture/09-swarm.md` | CrewAI and MetaGPT patterns belong here where they concern team coordination. |
| Loops | ADR-011, `docs/architecture/18-loop-engineering.md`, `loop-schema.json`, `LoopRuntime.kt` | LangGraph must not create a second Loop Engine. It may provide an optional execution runtime. |
| Flows / Automation | `docs/architecture/27-automation.md` defines `FlowEngine`, `TriggerManager`, visual/declarative flows, and promotion of a Flow to a Loop | AutoGPT/Dify/CrewAI workflow ideas extend Mimir Flows; they do not justify another Automation Engine. |
| Bots | `bot-schema.json`, architecture/10-bots, ADR-004's orchestration context | Bot scheduling remains distinct from general workflow definitions. |
| Knowledge / RAG | ADR-012, ADR-018, architecture/12-memory-rag, STAIR, Graphify | Dify does not justify replacing Mimir Vault or creating another RAG Engine. |
| Terminal / Sandbox | ADR-002, ADR-013, ADR-016, ADR-019; `TerminalService`; `terminal-schema.json` | All framework tool execution that reaches the shell must remain behind TerminalService and sandbox policy. |
| Forge | ADR-017, ADR-020, ADR-021; `forge-schema.json` | Do not import AutoGPT Classic Forge or create a second project-build Engine. |
| Tools / Skills | `tool-registry.yaml`, `skill-schema.json`, architecture/05-connectors-skills | Reuse and strengthen these registries; do not conflate a Skill with an Agent or Workflow. |

### 2.3 Documentation inconsistencies to fix

1. `BLUEPRINT-v4.1-FULL.md` section 10 says ADRs `001–018`, while the archive contains ADR-019, ADR-020, and ADR-021 and `REPO-STRUCTURE.md` already lists all 21.
2. `docs/api/README.md` lists the project/library/bot/connector/skill/loop schemas but omits the already-present `forge-schema.json` and `terminal-schema.json`.
3. `CONSTITUTION.md` Article 4 says the initial Forge release starts with 5–6 templates and expands to 18; ADR-021 says Phase 03.5 must include 18 template definitions, with scaffold implementations prioritized in stages. These can be reconciled as **18 catalog definitions, 5–6 production-depth scaffold implementations initially**, but because the Constitution is highest authority, its wording requires an approved amendment rather than silent reinterpretation.
4. `docs/architecture/27-automation.md` describes `FlowEngine` and `TriggerManager`, but the archive contains no `workflow-schema.json` and no workflow run/checkpoint schema. The concept is documented, not contract-complete.
5. `ModelRouter.kt` has only a `complete()` interface and `PlaceholderModelRouter`; it is not evidence of production provider routing, streaming, tool calling, or model capability discovery.
6. `LoopRuntime.kt` is a stub. `loop-schema.json` has an opaque `state` object but does not define durable checkpoints, run identity, approvals, idempotency, or resume semantics.
7. The Universal Architecture proposal enumerates Agent Runtime, Swarm, Bots, Loop Engineering, and Workflow/Automation as separate Engines. The current v4.1 project structure instead groups many of these as layers/packages/features. The merged Blueprint must classify **Engine vs runtime service vs feature/capability** by independent lifecycle and contract ownership, not by product label alone.

## 3. Repository-by-repository reconciliation

## 3.1 AutoGPT — Blocks, graphs, builder, and execution visibility

**Useful patterns:** typed Blocks with input/output definitions, graph validation, explicit node connections, branching, visual construction, run inspection, workflow templates, triggers, and API/MCP invocation.

**Already present in Mimir:**
- `docs/architecture/27-automation.md`: declarative/visual Mimir Flows, triggers (Cron/Event/Webhook/Notification), actions from Tool Registry, `FlowEngine`, `TriggerManager`.
- `docs/api/tool-registry.yaml`: existing reusable action/tool catalogue.
- `docs/api/skill-schema.json`: reusable Skills that bind to tool IDs.
- `docs/architecture/18-loop-engineering.md` and ADR-011: evaluated long-running loops.

**Missing:** a machine-readable workflow graph; typed node ports; edges; conditions/branches; validation rules; versioning; run history; checkpoint/resume; per-node permissions; and artifact/evidence references.

**Decision:** extend Mimir Flows. A Block is a typed Workflow Node backed by an approved Mimir capability or adapter. The builder edits a Mimir-owned Workflow Definition; it does not execute the workflow itself. Execution belongs to the Mimir runtime boundary.

**Do not:** add an AutoGPT Engine, import the AutoGPT Platform runtime, or use AutoGPT Classic Forge as a second Forge.

**License boundary:** the repository uses MIT outside `autogpt_platform/`, while `autogpt_platform/` is Polyform Shield. Do not copy Platform code into Mimir without a separate legal review. The repository README also describes the current visual builder, marketplace, integrations, and API/MCP surface.

Sources: [AutoGPT repository](https://github.com/Significant-Gravitas/AutoGPT), [repository license](https://github.com/Significant-Gravitas/AutoGPT/blob/master/LICENSE), [Platform Polyform Shield license](https://github.com/Significant-Gravitas/AutoGPT/blob/master/autogpt_platform/LICENSE.md).

## 3.2 LangChain — model/provider and tool abstractions

**Useful patterns:** common model interfaces, normalized messages, tool invocation, structured output, streaming, provider capability metadata, middleware, and integration conformance tests.

**Already present in Mimir:**
- Model layer and `ModelRouter` in `shared/.../engine/model/`.
- Local and cloud inference packages in `PACKAGE_MAP.md`.
- `CostTracker` and SearchAggregator.
- Tool Registry, Skills, MCP, and Connector Gateway.
- ADR-001 requires KMP-first; ADR-008 requires SQLDelight; ADR-010 requires a FOSS-clean distribution path.

**Missing:** a formal provider adapter contract; provider/model capability description; streaming and tool-call result types; normalized error categories; usage/cost reporting; context/modality metadata; and provider conformance tests. The archived Kotlin `ModelRouter` is a placeholder, so the current abstraction is not yet operational.

**Decision:** strengthen the existing Model layer and ModelRouter. LangChain is a design/reference source and may be used behind a separately isolated adapter only if a concrete integration justifies it. Do not make LangChain a Kernel dependency or introduce its Python runtime into `commonMain`.

**Do not:** create a LangChain Engine or a second model router. Provider selection remains Mimir-owned and policy-aware.

**License:** the LangChain library package is MIT. Each selected integration and transitive dependency still requires its own license and security review.

Sources: [LangChain repository](https://github.com/langchain-ai/langchain), [LangChain package license](https://github.com/langchain-ai/langchain/blob/master/libs/langchain/LICENSE).

## 3.3 Dify — application UX, workflows, and RAG

**Useful patterns:** visual Chatflow/Workflow design, node debugging, run history, app publishing, knowledge-source management, and configurable RAG pipelines.

**Already present in Mimir:**
- Mimir Flows in architecture/27.
- Mimir Vault and Libraries in ADR-018 and phase 02.
- STAIR + Graphify in ADR-012 and architecture/12/19/23.
- Model layer and Tool/Connector registries.

**Decision:** use Dify as a product/UX reference for workflow editing, RAG observability, and app lifecycle. Do not create a Dify-like RAG Engine or replace STAIR/Graphify/Vault. Any useful concepts should be expressed through Mimir's own contracts and UI.

**Do not:** transplant Dify's platform/frontend code under the assumption that it is unmodified Apache-2.0. Its repository license is Apache-2.0 with additional conditions, including multi-tenant and frontend logo/copyright restrictions. Those conditions require legal review before any code reuse.

Sources: [Dify repository](https://github.com/langgenius/dify), [Dify license](https://github.com/langgenius/dify/blob/main/LICENSE).

## 3.4 MetaGPT — SOP, role definitions, and artifact handoffs

**Useful patterns:** role-specific responsibilities, SOPs, ordered production stages, structured artifacts, explicit handoffs, and review gates. The key transferable idea is that collaboration quality comes from a defined process, not merely from adding more agents.

**Already present in Mimir:**
- ADR-004 and architecture/09 for Swarm/Council/multiple agents with roles.
- ADR-011 and architecture/18 for evaluated iterative work.
- Skills and Tool Registry for reusable capabilities.
- Mimir Flows for declarative steps and triggers.

**Missing:** a standardized Agent Definition (role, objective, allowed capabilities, model policy, context scope, permissions, budget, output schema, verification requirements, stop conditions) and reusable SOP/role-template metadata.

**Decision:** represent MetaGPT-style roles and SOPs as Agent Definitions, Skills, and Workflow templates consumed by the existing Agent Runtime, Swarm, and Flows. A separate SPE Engine is **not justified by this repository review alone**. Reconsider only if specification engineering becomes an independently scoped product with its own lifecycle and contracts.

**Do not:** create a MetaGPT Engine or duplicate Swarm. Keep the independent evaluator rule from the Constitution and ADR-011.

**License/runtime note:** the repository declares MIT, but its README describes Python `<3.12` as a prerequisite in the reviewed setup. This makes direct embedding into the KMP application inappropriate; any later experiment must be isolated and version-pinned.

Sources: [MetaGPT repository](https://github.com/FoundationAgents/MetaGPT), [MetaGPT license](https://github.com/FoundationAgents/MetaGPT/blob/main/LICENSE).

## 3.5 AutoGen — event-driven multi-agent reference

**Useful patterns:** layered runtime APIs, event/message passing, group-chat patterns, and local/distributed agent runtime concepts.

**Already present in Mimir:** ADR-004 Swarm, Agent Runtime packages, MCP/Connectors, and the proposed Universal Blueprint's event/job/contract boundaries.

**Decision:** reference only. Do not adopt AutoGen as a production runtime because Microsoft marks the project archived and no longer maintained, and directs new users toward Microsoft Agent Framework. Any future Microsoft Agent Framework review must be a separate assessment; it is not an automatic replacement decision.

**License precision:** the repository distinguishes MIT for code from CC-BY-4.0 for documentation/content. Review the specific files and bundled assets before reuse; do not label the entire repository with a single license.

Sources: [AutoGen repository](https://github.com/microsoft/autogen), [official archive warning and migration guidance](https://microsoft.github.io/autogen/dev/), [repository README/license notice](https://github.com/microsoft/autogen/blob/main/README.md).

## 3.6 CrewAI — team composition and Flow patterns

**Useful patterns:** role/task-based Crews, sequential and hierarchical collaboration, Flows, event/state handling, human feedback, and execution observability.

**Already present in Mimir:**
- Swarm/Council in ADR-004 and architecture/09.
- Mimir Flows in architecture/27.
- Loops and independent Evaluator in ADR-011 / architecture/18.
- Bot scheduling in bot-schema and architecture/10.

**Decision:** use CrewAI as a comparative reference and, if justified, a temporary optional Runtime Adapter experiment after Mimir's Agent/Workflow contracts exist. The experiment must prove that the same Mimir Agent/Workflow definitions can be run without leaking CrewAI-specific types into Mimir's public contracts.

**Do not:** create CrewAI Engine, Crew Engine, or another Flow Engine. CrewAI's team semantics map to Swarm; its Flow semantics map to Mimir Flows/Loop runtime depending on whether the work is a lightweight automation or a long-running evaluated cycle.

**License:** CrewAI's repository declares MIT. Enterprise/hosted offerings and optional telemetry/data-sharing settings must still be reviewed separately; do not assume their service terms are identical to the core library license.

Sources: [CrewAI repository](https://github.com/crewAIInc/crewAI), [CrewAI README](https://github.com/crewAIInc/crewAI/blob/main/README.md).

## 3.7 LangGraph — stateful graph runtime and checkpointing

**Useful patterns:** graph-based execution, state transitions, checkpointing, durable execution, pause/interrupt, human approval, and resumption of long-running agent workflows.

**Already present in Mimir:**
- `LoopRuntime` and `loop-schema.json` (but currently stub/opaque state).
- `FlowEngine` and `TriggerManager` as architecture concepts.
- ADR-008 SQLDelight-only persistence.
- ADR-013 OpenShell sandbox policy.
- The proposed Universal Blueprint's Task/Job Manager, checkpoint, cancellation, and recovery requirements.

**Missing:** first-class Workflow Run identity and status; node execution records; checkpoint metadata; resume tokens/state references; approval records; idempotency keys; retry classification; cancellation semantics; and durable state integration.

**Decision:** LangGraph is the first candidate for an optional durable graph-runtime POC, not a replacement for Mimir Flows, Loops, Swarm, or Agent Runtime. Mimir owns the workflow definition, run identity, permissions, state ownership, audit, and persistence contract. LangGraph may execute a compatible graph behind an adapter and return normalized events/results.

**KMP boundary:** do not add Python/LangGraph dependencies to `commonMain`, Android APK base, or the core runtime. Use an isolated optional process/service boundary only where supported, with explicit resource and network policy. It must not silently add a second primary database in conflict with ADR-008. Checkpoint storage must be mediated by an approved Mimir persistence adapter or a formally approved exception.

**Do not:** create LangGraph Engine or replace every Flow/Loop with a LangGraph graph. Keep a native lightweight path so the app remains useful when the optional runtime is absent.

**License:** LangGraph repository declares MIT. Review runtime dependencies and deployment requirements separately.

Sources: [LangGraph repository](https://github.com/langchain-ai/langgraph), [LangGraph documentation](https://docs.langchain.com/oss/python/langgraph).

## 4. Existing ADR reconciliation

No existing ADR should be renumbered, overwritten, or marked superseded because of these seven repositories.

| Existing ADR | Current decision | Reconciliation action |
|---|---|---|
| ADR-001 KMP First | KMP + Compose is the only UI/logic stack | **Preserve.** External Python runtimes must stay outside `commonMain`; adapters cross a stable platform boundary. |
| ADR-002 Terminal Hybrid | Hybrid terminal and TerminalService backbone | **Preserve.** Framework tools must not bypass TerminalService for shell work. |
| ADR-003 Design System | MDS / Material 3 | No change. |
| ADR-004 Swarm Architecture | Swarm, Council, OK Computer, role/departments | **Amend documentation, not the core decision.** Add role definitions, SOP templates, task handoff, sequential/parallel execution, independent verification, and framework-neutral strategy selection. No CrewAI/MetaGPT Engine. |
| ADR-005 Auth Strategy | KMP Auth + Supabase | No change; runtime adapter credentials must use Mimir-scoped secret references. |
| ADR-006 Web Search & Citations | Multi-provider SearchAggregator | No change; LangChain/Dify do not replace SearchAggregator. |
| ADR-007 Screen Share | WebRTC-KMP | No change. |
| ADR-008 SQLDelight vs Room | SQLDelight 2.x exclusively | **Preserve.** Do not let LangGraph/CrewAI introduce a second app database by default. External runtime state must be mediated through Mimir persistence or separately approved. |
| ADR-009 Crash Reporting | Firebase/Sentry split | No change. |
| ADR-010 F-Droid Compatibility | FOSS-clean flavor | **Preserve.** No mandatory Python/closed service dependency in F-Droid build; audit all transitive licenses. |
| ADR-011 Loop Engineering | Five Moves, independent Evaluator, budgets, Kill Switch | **Extend details.** Define durable checkpoint, pause/resume, retry/idempotency, run history, and approval semantics without weakening Five Moves or evaluator independence. |
| ADR-012 STAIR RAG | STAIR + Graphify | **Preserve.** Dify is a reference only; do not duplicate or replace Mimir knowledge architecture. |
| ADR-013 OpenShell Sandbox | OpenShell for agent/terminal/forge sandboxing | **Clarify enforcement.** Optional framework adapters and subprocesses must inherit sandbox/policy boundaries; no direct unsandboxed shell or unrestricted secrets. |
| ADR-014 GeckoView Browser | GeckoView Dynamic Feature + external Playwright MCP | No change; AutoGPT/Dify do not replace Browser Engine. |
| ADR-015 Adaptive UX | Unified UX + DeviceCapability defaults | No change; optional runtime availability must be reflected in capability-aware UX. |
| ADR-016 AI Terminal | Chat/Classic/Split, safety pipeline, auto-invoke | **Preserve.** Agent framework tools may propose terminal actions but must use TerminalService and its approvals. |
| ADR-017 Mimir Forge | Independent Forge, depends on TerminalService | **Preserve.** Do not import AutoGPT Classic Forge or create another build Engine. |
| ADR-018 Mimir Vault | Local-first Obsidian-grade Second Brain | **Preserve.** Dify knowledge features do not replace Vault/STAIR/Graphify. |
| ADR-019 AI Terminal Modes + Fallback | Three modes and graduated fallback | No change. |
| ADR-020 Terminal before Forge | Phase 03.3 before 03.5 | No change. |
| ADR-021 Eighteen Templates | 18 definitions; scaffold depth staged | **Preserve, but reconcile wording with Constitution Article 4** as a separately approved documentation amendment. |

## 5. What to add

The additions below are deliberately contract-first. They do not create new Engines.

### 5.1 New contract files (proposed)

| Proposed file | Owns | Why it is needed |
|---|---|---|
| `docs/api/agent-schema.json` | Agent identity, role, instructions reference, capability allow-list, model policy, context scope, permission profile, budgets, output contract, verifier, stop conditions | Skill is not an Agent; Mimir currently lacks a formal Agent Definition schema. |
| `docs/api/workflow-schema.json` | Workflow metadata/version, typed inputs/outputs, nodes/Blocks, ports, edges, conditions, triggers, policy references, bounded loops, runtime requirements | AutoGPT/Dify/CrewAI graph concepts need one portable Mimir-owned definition. |
| `docs/api/workflow-run-schema.json` | Run ID, workflow/version, status, node states, checkpoints, state references, approvals, retries, idempotency keys, artifacts, costs, timestamps, cancellation/resume metadata | LangGraph's durable execution ideas need an engine-neutral run contract. |
| `docs/api/runtime-adapter-contract.md` (or equivalent schema) | Adapter identity/version, supported contract versions, capabilities, transport, lifecycle, health, isolation, permission propagation, event/result mapping | Prevent framework-specific types and APIs leaking into Mimir. |

These are proposed names, not existing files. The exact schemas should be designed only after reviewing the full Bridge API and persistence model.

### 5.2 Strengthen existing contracts

**`tool-registry.yaml`** should evolve from a descriptive catalogue into a validated capability/tool contract. Add, where applicable:
- `inputSchema` and `outputSchema` references;
- capability ID/version and provider/adapter identity;
- required permission scopes and workspace/path/domain constraints;
- risk class and whether human approval is mandatory;
- idempotency/retry semantics and side-effect classification;
- timeout, cancellation, and resource-budget limits;
- sandbox profile and network policy;
- audit/evidence requirements;
- secret references (never literal secret values).

The existing comment that risk policy is informational must not be mistaken for enforcement. Runtime authorization must be authoritative and deny by default for sensitive operations.

**`loop-schema.json`** should reference the shared Run/Checkpoint model or define a compatible LoopRun extension. Keep the existing required Five Moves: Skill + Schedule + independent Evaluator + Sandbox + State.

**`bot-schema.json`** should reference workflow/agent definitions where needed, but Bot schedule/status should remain Bot-specific. Do not merge Bot and Workflow into one overloaded entity.

**`ModelRouter` contract** should be expanded to cover normalized message/content parts, streaming, tool calls/results, structured output, model capabilities, provider errors, usage/cost, cancellation, and privacy/data handling metadata. The public interface must remain provider-neutral.

**`docs/api/README.md`** should list `forge-schema.json` and `terminal-schema.json` now, and the proposed schemas only after they are created and validated.

### 5.3 Add conformance tests

A shared runtime/provider conformance suite should test:
- contract-version compatibility;
- capability discovery and invocation;
- permission propagation and denial;
- cancellation and timeout;
- structured input/output validation;
- state persistence and recovery;
- idempotent retry and duplicate-side-effect prevention;
- event ordering/correlation;
- secret redaction;
- resource limits and sandbox isolation;
- adapter failure isolation;
- deterministic disable/uninstall behavior.

## 6. What to modify in existing Blueprint and architecture documents

| File | Required modification | Do not change |
|---|---|---|
| `BLUEPRINT-v4.1-FULL.md` | Update docs map to ADR-001–021; add Agent/Workflow/Runtime Adapter boundaries; clarify Flows vs Loops vs Swarm vs Bots; add provider contract and durable run/checkpoint gap | Do not reorder approved phases or weaken Constitution constraints. |
| `docs/architecture/02-layers.md` | Clarify L9 Agent Runtime as execution capability/service boundary; L10 Swarm/Orchestration as coordination; Flows and Loops as distinct workflow types/capabilities, not duplicate Engines | Do not add a LangGraph/CrewAI/Dify layer. |
| `docs/architecture/05-connectors-skills.md` | Define Tool vs Skill vs Connector vs Agent vs Runtime Adapter; state that Skills cannot grant permissions | Do not treat every integration as an Engine. |
| `docs/architecture/09-swarm.md` | Add role contracts, SOP templates, task graph/handoff, parallel/sequential modes, independent reviewer, runtime strategy interface | Do not bind Swarm to CrewAI or MetaGPT. |
| `docs/architecture/18-loop-engineering.md` | Add durable run lifecycle, checkpoints, approvals, resume, idempotency, retry classes, event/evidence trail | Do not remove Five Moves, separate Evaluator, budgets, or Kill Switch. |
| `docs/architecture/27-automation.md` | Define portable Flow graph, typed Blocks/ports, validation, versioning, execution history; explain when a Flow is promoted to a Loop | Do not embed full AutoGPT, Dify, n8n, or CrewAI runtime. |
| `docs/architecture/12-memory-rag.md` and `19-stair.md` | Add a short boundary note: Dify is a UX/reference, Mimir Vault + STAIR + Graphify remain authoritative | Do not create a second RAG/Knowledge Engine. |
| `docs/architecture/16-security-testing.md` and `22-openshell.md` | Specify adapter sandbox, egress controls, secret scoping, audit, package trust, runtime disable/failure behavior | Do not allow framework adapters to bypass OpenShell/TerminalService/policy. |
| `docs/api/README.md` | Correct schema inventory | Do not list proposed schemas as implemented before they exist. |
| `REPO-STRUCTURE.md` | Add the contract files only after approval; update ADR list and architecture docs map | Do not add framework-specific top-level Engine folders. |
| `STATE.md` | Track contract design, POCs, and verification evidence as not started/in progress/completed accurately | Do not increase completion percentage for documents or proposed designs alone. |

## 7. What not to add

1. **No AutoGPT Engine.** Mimir Flows and Tool Registry already own the relevant product responsibilities.
2. **No LangChain Engine.** Strengthen ModelRouter and provider adapters.
3. **No Dify Engine or second RAG Engine.** Mimir Vault, STAIR, Graphify, and Flows already cover those domains.
4. **No MetaGPT/SPE Engine at this stage.** Apply SOP and role templates to existing Swarm, Skills, and Flows. A separate SPE product requires a separate scope decision.
5. **No AutoGen Engine or production dependency.** It is archived and unmaintained.
6. **No CrewAI Engine.** Its useful team patterns belong to Swarm and Flows; an adapter is optional and experimental only.
7. **No LangGraph Engine.** It may be an optional runtime adapter for selected durable graph workloads.
8. **No second Flow Engine, Loop Engine, Swarm Engine, Model Router, or Forge.** Do not create parallel implementations with overlapping ownership.
9. **No mandatory Python runtime in KMP `commonMain`, base APK, or F-Droid build.** Any Python framework must be optional and isolated.
10. **No additional primary database just to satisfy a framework's persistence default.** ADR-008 remains binding unless formally amended.

## 8. Proposed new ADRs — minimum set

Existing ADR numbers 001–021 remain untouched. The following are proposed next IDs only; they must be reviewed and approved before files are created.

### Proposed ADR-022 — Model Provider and Capability Adapter Contract

**Context:** `ModelRouter` is currently a minimal `complete()` interface with a placeholder implementation. LangChain demonstrates useful normalized provider abstractions and conformance patterns, but Mimir is KMP-first and must not depend on a Python framework in `commonMain`.

**Decision proposal:** Mimir owns a provider-neutral Model/Provider contract. Provider adapters declare capabilities, modalities, context limits where known, streaming, tool calling, structured output, usage, pricing metadata, rate limits, authentication reference, and data handling. The router chooses based on policy, privacy, capability, cost, latency, availability, and user preference. Frameworks may implement adapters but cannot own routing policy or credentials.

**Alternatives rejected:** LangChain as the universal model layer; provider-specific branches in Kernel; direct provider calls from Agents/Skills.

**Consequences:** extend ModelRouter and add provider conformance tests; no new Engine.

### Proposed ADR-023 — Portable Workflow and Agent Runtime Adapter Contract

**Context:** Mimir already defines Swarm, Loops, Bots, Skills, Tools, and Mimir Flows, but has no portable Workflow schema or durable Workflow Run/Checkpoint contract. AutoGPT, Dify, MetaGPT, CrewAI, and LangGraph provide useful but overlapping implementations.

**Decision proposal:** Mimir owns Agent Definition, Workflow Definition, Workflow Run, Checkpoint, Approval, and event/result contracts. Swarm owns multi-agent coordination; Flows own lightweight automation; Loops own evaluated recurring/iterative work; Bots own scheduled/event-driven assistant identity. External frameworks are optional adapters behind these contracts. LangGraph is the first candidate for a bounded POC; CrewAI is a comparison candidate; AutoGen is reference-only.

**Alternatives rejected:** framework-specific public schemas; a separate Engine for every framework; one universal runtime dependency; using the UI graph as the execution source of truth.

**Consequences:** add the proposed Agent/Workflow/Run schemas; extend ADR-004 and ADR-011 documentation; retain native Mimir behavior when optional adapters are absent.

## 9. Implementation and validation sequence

This work must not jump ahead of the project's current Phase 00 gate. The archive's STATE.md still identifies Phase 00 as open, with GitHub push, green CI APK, and acceptance sign-off remaining. The following sequence is an architecture/engineering work plan, not a claim that phases have been completed.

### R0 — Freeze and reconcile the source baseline

- Confirm the current canonical archive/revision; the uploaded archive is dated 2026-10-30.
- Reconcile Constitution Article 4 vs ADR-021 template wording.
- Correct Blueprint ADR map (001–021) and API schema inventory.
- Mark existing code stubs accurately; do not count docs as implementation.

**Exit:** one agreed source baseline and no contradictory architecture indexes.

### R1 — Define Mimir-owned contracts

- Draft `agent-schema.json`.
- Draft `workflow-schema.json` with typed nodes/ports/edges/branches/triggers.
- Draft `workflow-run-schema.json` with run state, checkpoints, approvals, idempotency, cancellation, retry, artifacts, and usage.
- Strengthen Tool Registry and ModelRouter contract.
- Map persistence to SQLDelight and runtime adapter state boundaries.

**Exit:** schemas validate; versioning and migration policy documented; no framework-specific type in public Mimir contracts.

### R2 — Implement or complete native Mimir primitives first

- Complete Agent Runtime contract and Tool authorization.
- Implement minimal native Flow validation and execution path.
- Implement durable Run/Checkpoint persistence and resume.
- Keep Loops and Bots as distinct domain concepts using shared run primitives where appropriate.

**Exit:** a native Flow can validate, execute, pause, resume, cancel, and produce audit/evidence without any external framework.

### R3 — LangGraph adapter proof of concept

Run only in an isolated optional process/service. Test one bounded workflow end-to-end:

1. Mimir creates a workflow and run.
2. Mimir passes a versioned definition and scoped context to the adapter.
3. Adapter executes a graph and emits normalized progress events.
4. Mimir persists run state/checkpoint through the approved persistence interface.
5. Workflow pauses for human approval and resumes after approval.
6. Process/service restart recovers without repeating completed side effects.
7. User can cancel; sandbox and network limits are enforced.
8. Removing/disabling the adapter leaves native Flows and Loops operational.

**Exit:** adapter passes the conformance suite and provides measurable value beyond native Mimir execution.

### R4 — CrewAI comparative POC (only if R3 exposes a real gap)

Compare role/team execution, handoffs, parallelism, human feedback, persistence, and observability using the same Mimir Agent/Workflow contracts. Do not run both frameworks as permanent dependencies by default.

**Exit:** documented evidence that CrewAI adds a capability Mimir cannot meet economically with its own Swarm/Flow primitives; otherwise reject adoption.

### R5 — LangChain and MetaGPT patterns

- Test provider adapter patterns and conformance suite against direct Ktor-based provider adapters first.
- Only add a LangChain adapter if a concrete integration is unavailable or materially more costly to implement natively.
- Encode MetaGPT SOP/role/handoff patterns as Mimir templates and Agent Definitions.

**Exit:** no framework-specific dependency is exposed in Mimir public contracts.

### R6 — Dify/AutoGPT UX study and final license gate

- Prototype workflow editor interactions from observed product behavior, not copied frontend code.
- Review Dify and AutoGPT component-specific licenses before any source reuse.
- Generate dependency inventory/SBOM and attribution notices for every adopted package.

**Exit:** legal and technical review signed before code reuse or distribution.

## 10. Acceptance criteria

The reconciliation is complete only when all applicable tests pass:

- [ ] Existing ADR IDs 001–021 are unchanged and linked from the canonical Blueprint.
- [ ] `BLUEPRINT-v4.1-FULL.md` documents the boundaries among Agent Runtime, Swarm, Flows, Loops, Bots, Skills, Tools, and Runtime Adapters.
- [ ] `agent-schema.json`, `workflow-schema.json`, and `workflow-run-schema.json` validate against examples and invalid cases.
- [ ] A Workflow Definition is portable and does not contain LangGraph/CrewAI/AutoGPT-specific types.
- [ ] Tool permissions are enforced by Mimir, not merely described in YAML.
- [ ] A Flow can be paused, resumed, cancelled, and recovered after restart.
- [ ] Retrying a node cannot duplicate a non-idempotent external side effect without reconciliation/approval.
- [ ] A Loop still requires all Five Moves and an independent Evaluator.
- [ ] Swarm can assign role-based tasks and record handoffs without requiring CrewAI or MetaGPT.
- [ ] Model adapters pass common streaming/tool-call/error/usage/privacy tests.
- [ ] Adapter failure or removal does not break native Mimir Flows, Loops, Bots, or model access.
- [ ] Optional runtimes cannot bypass TerminalService, OpenShell, permission checks, or secret scoping.
- [ ] No new primary persistence engine is introduced without an approved ADR amendment to ADR-008.
- [ ] Android/F-Droid builds do not gain a mandatory Python runtime or non-FOSS dependency.
- [ ] License and transitive dependency inventories are reviewed and recorded.

## 11. Final decision register

| Decision | Status | Owner area |
|---|---|---|
| Preserve existing 21 ADR IDs | Required | Architecture governance |
| Correct Blueprint ADR index to 001–021 | Required documentation fix | Blueprint |
| Keep Mimir Flows as the only lightweight visual automation concept | Recommended | Automation |
| Keep Swarm as the multi-agent coordination capability | Preserve + document extension | Swarm |
| Keep Loops as Five-Move evaluated cycles | Preserve + add durability details | Loop Engineering |
| Add Agent Definition contract | Proposed | Agent Runtime |
| Add portable Workflow Definition contract | Proposed | Flows |
| Add Workflow Run/Checkpoint contract | Proposed | Shared execution/persistence |
| Strengthen Model Provider contract | Proposed | Model layer |
| LangGraph optional POC | Candidate, not approved dependency | Runtime Adapter |
| CrewAI optional comparison POC | Conditional candidate | Swarm/Runtime Adapter |
| MetaGPT SOP patterns | Adopt as design patterns/templates | Swarm/Flows |
| LangChain direct runtime adoption | Not planned | Model layer |
| Dify source reuse | Not planned pending legal review | UX/Knowledge |
| AutoGPT Platform source reuse | Not planned due license restriction | Workflow UX |
| AutoGen runtime adoption | Rejected for production | Agent Runtime |
| New framework-specific Engines | Rejected | Platform architecture |

## 12. Final architectural statement

Mimir must own the stable abstractions and delegate execution strategies to replaceable adapters. The frameworks are implementation options and sources of patterns, not architectural authorities.

- **Mimir owns:** Agent/Workflow/Run definitions, permissions, capability resolution, identity, persistence, audit, cost budgets, evidence, and user approvals.
- **Swarm owns:** agent coordination, role assignment, delegation, parallel/sequential collaboration, handoffs, synthesis, and independent verification.
- **Flows own:** lightweight declarative automation and triggers.
- **Loops own:** bounded iterative/recurring work with Five Moves, independent Evaluator, budgets, sandbox, state, and Kill Switch.
- **Bots own:** persistent scheduled/event-driven assistant identity and inbox/notification behavior.
- **Model layer owns:** model/provider selection and normalized provider capabilities.
- **Adapters own:** translating Mimir contracts to LangGraph, CrewAI, provider APIs, or other approved runtimes.

No external framework may define Mimir's public data model, bypass Mimir governance, or become a mandatory dependency of the Kernel.

**Core must remain framework-agnostic. Frameworks must adapt to Mimir, not the other way around.**

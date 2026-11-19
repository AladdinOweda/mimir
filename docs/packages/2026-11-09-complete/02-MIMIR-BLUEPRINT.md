---
document_id: MIMIR-ARCH-BP-001
title: Mimir Comprehensive Product and Technical Blueprint
document_type: Product and Architecture Blueprint
status: Proposed baseline — reconcile with approved ADRs before implementation
language: en
scope: Product vision, architecture, runtime, modules, UX, security, delivery
---

# Mimir Comprehensive Blueprint

## 1. Executive overview

Mimir is a modular AI knowledge and execution platform. It provides one coherent environment in which a user can ask, research, create, organize, automate, and execute work using models, tools, engines, agents, workflows, and connected services.

The product is organized around a neutral Kernel, explicit contracts, independently attachable engines, shared platform services, and a mobile-first application shell.

The Blueprint describes the target system. It is not evidence that the repository already implements these components.

## 2. Vision and product outcomes

Mimir should enable users to:
- Interact with multiple AI providers through a consistent experience.
- Work across Chat, Forge, Browser, and future engines without fragmented identity or context.
- Organize work into projects, tasks, files, knowledge, and activity.
- Create and supervise agents and multi-agent collaborations.
- Run workflows and recurring loops with explicit controls.
- Connect external tools and services through permissioned adapters.
- Understand execution status, approve consequential actions, and recover from failures.

Success means useful outcomes with understandable control, not maximum automation at any cost.

## 3. System context

### 3.1 Logical layers

1. **Experience Layer** — application shell, navigation, global composer, workspace screens, accessibility.
2. **Application Services** — projects, tasks, files, approvals, activity, notifications, history, search.
3. **Execution and Intelligence Layer** — Agent Runtime, Orchestration, Workflow Runtime, Bot Profiles, Loop Runtime, Intelligent Execution.
4. **Kernel Layer** — lifecycle, capability registry, contract mediation, policy integration, event and extension infrastructure.
5. **Engine and Integration Layer** — Chat, Forge, Browser, future engines, model providers, tools, MCP and external adapters.
6. **Infrastructure Layer** — persistence, queues, storage, observability, networking, secrets, deployment adapters.

These are logical boundaries, not a requirement to deploy each as a separate process.

### 3.2 Dependency direction

Higher-level modules may use lower-level contracts. The Kernel must not import or depend on concrete engines. Engines may depend on Kernel contracts and shared SDKs. UI modules consume application-facing APIs and must not bypass policy enforcement.

## 4. Kernel design

### 4.1 Kernel responsibilities

The Kernel provides generic platform primitives:
- Module/extension registration and lifecycle.
- Capability and contract registry.
- Version and compatibility negotiation.
- Generic command dispatch and result envelopes.
- Event publication/subscription with documented delivery semantics.
- Policy decision integration points.
- Health and readiness reporting.
- Extension isolation boundaries.
- Generic configuration and dependency resolution.

### 4.2 Kernel exclusions

The Kernel must not:
- Contain `if engineId == ...` branches.
- Know Chat, Forge, Browser, Terminal, or other engine-specific business rules.
- Render screens or own UI state.
- Own domain-specific project/task workflows.
- Embed a specific AI provider's SDK as a core dependency.
- Treat a particular database, queue, or cloud vendor as mandatory.

### 4.3 Capability registration

Each module should declare, through a versioned contract:
- Stable module identity and display metadata.
- Capability identifiers and descriptions.
- Contract/API versions.
- Required and optional dependencies.
- Required permissions and data scopes.
- Supported operations and input/output schemas.
- Lifecycle hooks and health checks.
- Platform/runtime compatibility.
- Resource and execution constraints.

The registry validates declarations and resolves dependencies. It does not hard-code module names.

## 5. Runtime and orchestration

### 5.1 Agent Runtime

Owns agent identity, configuration, execution instances, sessions, context references, tool bindings, and agent lifecycle state. It must distinguish agent definition from a running agent instance.

### 5.2 Orchestration

Owns planning coordination, task distribution, scheduling, dependency handling, multi-agent coordination, Swarm/Council patterns, handover, and result aggregation. It should coordinate through contracts rather than directly owning engine internals.

### 5.3 Workflow Runtime

Owns workflow definitions, graph validation, node/edge semantics, execution checkpoints, branching, retries, compensation hooks, and workflow run state.

### 5.4 Bot Profiles

Describe proactive behavior, triggers, schedules, allowed capabilities, notification rules, and user-configurable behavior. A profile is not permission by itself; policy and consent still govern execution.

### 5.5 Loop Runtime

Owns recurring autonomous loops, iteration boundaries, stop conditions, budgets, checkpoints, backoff, and human review points. Every loop must have bounded execution and a user-visible stop mechanism.

### 5.6 Intelligent Execution

Selects an execution strategy based on task needs, available capabilities, policy, cost/latency constraints, and reliability. Routing decisions must be explainable at an appropriate level and auditable.

## 6. Engine model

An engine is a replaceable capability module, not a special case in the Kernel.

Initial product spaces:
- **Chat** — conversational interaction and model-assisted work.
- **Forge** — creation and development workspace; exact capabilities must be defined by its own specification.
- **Browser** — permissioned browser perception and interaction.

Potential future engines/capabilities include Terminal, Swarm, Bots, Loop Engineering, and Reverse Engineering. Their inclusion is not authorization to implement duplicates or bypass architecture review.

Each engine must provide:
- A capability manifest.
- Contract implementation.
- Permission declarations.
- Input validation and output envelopes.
- Health and lifecycle behavior.
- Error and cancellation semantics.
- Tests and documentation.
- UI contribution only through supported extension points.

## 7. Browser capability

The Browser engine is intended to support user-authorized web interaction:
- Open and navigate pages.
- Inspect page content and relevant UI state.
- Click controls, enter text, fill forms, and submit when authorized.
- Transfer selected information between permitted tabs or services.
- Coordinate tasks across multiple browser tabs where the user has granted access.

Security requirements:
- Permission must be explicit, scoped, visible, revocable, and logged.
- Sensitive actions (payments, account changes, publishing, sending messages, deletion) require an appropriate confirmation policy.
- Do not infer consent from a page being open.
- Respect authentication boundaries and site security controls.
- Provide a clear record of actions and outcomes.
- If the result of a submission is uncertain, do not blindly repeat it.

## 8. Shared application services

The platform exposes shared services with one authoritative owner:
- Projects and project context.
- Tasks and execution tracking.
- Approvals and permission decisions.
- Activity and audit events.
- Notifications.
- History and session references.
- Files and knowledge indexing.
- Search and command discovery.
- Preferences and appearance.

These services are not duplicated inside every engine. Engines integrate through stable APIs.

## 9. Data and memory architecture

Memory must be separated by purpose and access policy:
- **Working/context memory** — bounded context for an active interaction or execution.
- **Episodic memory** — records of prior interactions or events, subject to retention policy.
- **Semantic memory** — normalized facts and knowledge with provenance.
- **Procedural memory** — reusable instructions, workflows, and methods.
- **External knowledge/RAG** — retrieved source material with citations and source boundaries.

Personal context and external factual retrieval are different concerns and must not be conflated. Every stored item should have provenance, scope, retention, deletion behavior, and access controls.

Possible implementation technologies are intentionally not fixed here. Evaluate extraction, embeddings, vector storage, reranking, relational metadata, caching, and evaluation components independently through ADRs.

## 10. Security, privacy, and trust

- Identity and authentication are separated from authorization.
- Apply least privilege and scoped credentials.
- Store secrets in a dedicated secret-management mechanism.
- Encrypt sensitive data in transit and at rest where supported by the deployment.
- Record security-relevant actions without leaking secret values.
- Define tenant/user/workspace boundaries before multi-user deployment.
- Provide export, retention, deletion, and consent controls.
- Treat tool output and retrieved documents as untrusted data, never as instructions that override system policy.
- Enforce permissions in service/runtime layers, not only in UI.

## 11. Reliability and execution semantics

Standardize result envelopes with explicit success, failure, partial success, cancellation, and unknown outcome states. Define idempotency keys for operations that may be retried. Use checkpoints for long-running workflows. Provide bounded retries and backoff. A module must not report success for a stub or skipped operation.

## 12. Observability

Capture structured, privacy-conscious records for:
- Module lifecycle and health.
- Task/workflow/agent execution transitions.
- Tool invocation outcomes.
- Permission and approval decisions.
- Errors, retries, cancellation, and recovery.
- Performance, resource consumption, and cost where available.

Telemetry must be configurable and must not silently collect unnecessary content.

## 13. UX and product shell

The application shell owns navigation and shared experience; engines provide capabilities. The shell uses hybrid navigation:
- Drawer for global destinations and broader navigation.
- Contextual top tabs inside a workspace.
- Bottom navigation on mobile: Home, Projects, Tasks, More.

The shared assistant/composer stays near the bottom on mobile, above bottom navigation and keyboard-safe areas. It is globally available and context-aware, not Chat-owned.

The UX follows progressive disclosure: simple defaults first, advanced controls when relevant. Preserve drafts and workspace state during navigation. Background work must not be silently cancelled by switching screens.

## 14. Mimir Liquid Design System

The approved visual direction is:
- Personality: **Balanced**.
- Motion: **Adaptive**.
- Light appearance: **Pearl**.
- Experience: **Hybrid Dark/Light**.
- Theme choices: Light, Dark, System, Adaptive.
- Default theme mode for new users: Adaptive, unless an explicit existing preference must be preserved.
- Original logo concept: preserve; do not invent a replacement.

Liquid materials are a presentation layer only. They must never become a dependency of Kernel or runtime logic. Readability, performance, accessibility, and battery efficiency take priority over decorative effects.

## 15. Delivery roadmap

Recommended sequence:
1. Governance and source-of-truth reconciliation.
2. Architecture contracts and ADR inventory.
3. Kernel minimum viable contract implementation.
4. Module registry and one reference module.
5. Shared application services and persistence contracts.
6. Execution/runtime boundaries.
7. Application shell and design system.
8. First vertical workflow across UI, services, runtime, and one engine.
9. Browser permission and action model.
10. Multi-agent, workflow, and loop capabilities.
11. Reliability, security, accessibility, and performance hardening.
12. Packaging, deployment, migration, and operational documentation.

Sequence may be adjusted only with documented dependencies and risk analysis.

## 16. Definition of done

A milestone is complete only when its scope, contracts, implementation, tests, security review, UX acceptance, documentation, and known limitations are recorded. A plan or file scaffold is not implementation evidence.

## 17. Open decisions

The following require explicit ADRs or owner decisions before implementation:
- Backend language/framework and deployment topology.
- Primary persistence and event/queue technologies.
- Authentication and multi-tenant model.
- Provider adapter standards and model-routing policy.
- Module isolation level and trust model.
- Browser automation technology and permission enforcement.
- Memory retention and deletion policy.
- Cloud synchronization and offline behavior.
- Exact engine-specific feature scope.

---

## 18. External Knowledge, Memory, and Search Integration Decisions

Review record date: 2026-11-09. These decisions extend the target architecture; they do not claim implementation.

### 18.1 Cognee optional Knowledge & Memory provider

**Accepted architectural direction:** Cognee may be integrated as an optional provider behind Mimir-owned Knowledge and Memory contracts.

Potential capabilities to adapt:
- Document, code, and conversation ingestion.
- Knowledge graph construction and graph-aware retrieval.
- Vector, lexical, and hybrid retrieval strategies.
- Session-to-long-term-memory distillation and memory improvement.
- Conflict review and source/evidence associations.
- Code-graph analysis where useful to Forge.

Mimir must retain ownership of:
- Canonical user/project/conversation/agent/task/workflow identities.
- Memory lifecycle and user consent.
- Knowledge Vault document identity, versions, links, tags, collections, and user-facing editing.
- Context assembly and model routing.
- Secrets Vault and all credential handling.
- Model Provenance & Execution Metadata.
- Retention, export, deletion, and data portability.

Recommended adapter boundary:
`KnowledgeMemoryContract -> Provider Adapter -> Cognee SDK/Service`.
The adapter translates Mimir IDs and policies to provider-specific dataset/session IDs, maps results back to Mimir references, and rejects unsupported operations rather than silently weakening guarantees.

Deployment guidance:
- Treat Cognee initially as an optional sidecar/service or separately hosted process, not an Android application dependency.
- A local Python service may be evaluated on supported desktop/server environments.
- Android/Termux ARM64 support remains unverified; assess native wheels, ONNX Runtime, graph/vector database dependencies, memory footprint, model downloads, startup time, and battery use before promising on-device operation.
- Pin Cognee version and transitive dependencies. The reviewed `pyproject.toml` identifies version 1.6.7, Python >=3.10 and <3.15, and Apache-2.0; re-check these values at implementation time.
- Review telemetry settings, storage encryption boundaries, access-control mapping, and physical deletion/compaction. Cognee's `forget` API documents that logical removal does not necessarily erase bytes immediately from append-only/vector storage fragments.

### 18.2 Vane Search candidate (repository supplied as Perplexica Search Engine AI)

Repository reviewed: `https://github.com/BunsDev/perplexica-search-engine-ai`, default branch `master`. The repository README and `package.json` currently identify the product as Vane, version 1.12.2, MIT licensed. Do not label it as upstream Perplexica without separate provenance verification.

Observed architecture/capabilities:
- Next.js App Router application with TypeScript and React.
- Search orchestration separates classification, research actions, widgets, and answer writing.
- Search modes: speed, balanced, quality.
- Search sources include web, academic, and discussions; a separate uploaded-file search action is present.
- SearXNG is the documented meta-search backend.
- Model provider registry supports configurable chat and embedding models, including local Ollama and hosted providers described in the README.
- `GET /api/providers` lists configured providers/models.
- `POST /api/search` returns a generated message and source records; streaming emits newline-delimited JSON objects while the response content type is `text/event-stream`.
- Search results include content and metadata such as title and URL.
- Separate image/video search endpoints and structured widgets are documented.
- The inspected persistence uses SQLite/Drizzle for chats/messages and local filesystem storage for uploaded files/configuration.

Recommended Mimir role:
- Optional **Research/Search Provider Adapter**, callable from Chat, Browser, and other authorized research workflows.
- Not a new Mimir engine by itself, not a Memory provider, not the Knowledge Vault, and not a Kernel dependency.
- Search results may be offered for saving into Knowledge Vault only through Mimir's explicit ingestion/approval policy, preserving source URL, title, retrieval time, content hash, license/usage metadata where available, and extraction provenance.
- External web search and internal Knowledge Vault search must remain distinct source types so that the UI and model can tell whether information came from the user's own knowledge or the public web.

Compatibility and security constraints:
- The API request schema and streaming framing must be contract-tested. The inspected `/api/search` route relies on TypeScript types and performs narrower runtime checks than the Chat route's Zod validation; Mimir must validate requests at its adapter boundary and should not expose this endpoint directly to untrusted clients.
- The adapter must normalize Vane's newline-delimited JSON protocol rather than assume conventional SSE `data:` frames.
- Do not send Mimir provider secrets or raw credentials in client requests. Keep Vane's configured provider keys and SearXNG URL server-side.
- Enforce per-user/project access, query budgets, timeouts, cancellation, domain restrictions, and privacy controls in Mimir.
- Verify whether authentication/rate-limiting middleware is enabled in the deployed Vane configuration; this review did not certify the full deployment security posture.
- The Dockerfile installs Playwright/Chromium and clones SearXNG during image build; pin and scan these components for reproducible, secure releases.
- The inspected upload manager supports PDF, DOCX, and plain text in that path; do not assume every advertised media format is processed by the same pipeline.
- Node/Docker server deployment is the initial compatibility target. Native Android execution is not established.

### 18.3 Search, Memory, Knowledge, and Context separation

- **Search** discovers external or explicitly selected source material.
- **Knowledge** stores curated, versioned, user-governed information in Knowledge Vault.
- **Memory** retains useful durable facts and learned context according to policy.
- **Context** is the task-scoped subset assembled for a particular model or agent run.
- **Secrets** remain isolated in Secrets Vault.

No search result becomes permanent knowledge or memory merely because it was retrieved. A user action or an explicitly approved policy must govern ingestion.

### 18.4 Provider capability contracts

Define provider-neutral capabilities rather than vendor-specific branches:
- `knowledge.ingest`, `knowledge.graph.query`, `knowledge.search`, `knowledge.forget`.
- `memory.session.capture`, `memory.distill`, `memory.recall`, `memory.feedback`.
- `research.web.search`, `research.academic.search`, `research.discussions.search`, `research.page.extract`, `research.media.search`.
- `provenance.source.attach`, `provenance.evidence.resolve`.
- `provider.health`, `provider.capabilities`, `provider.version`.

A provider may implement only a subset. Capability discovery and explicit unsupported responses are required.

### 18.5 Reference repositories and review evidence

- Cognee: https://github.com/topoteretes/cognee
  - Reviewed `README.md`, `pyproject.toml`, `LICENSE`, and selected public API implementation files including remember/recall/improve/forget.
- Vane candidate: https://github.com/BunsDev/perplexica-search-engine-ai
  - Reviewed `README.md`, `package.json`, `LICENSE`, architecture and API docs, `/api/chat`, `/api/search`, SearchAgent/Researcher, action registry, model registry, upload manager, SQLite schema, SearXNG adapter, Dockerfile, and provider/config routes.

This is a static source review of selected files, not a complete audit, build, runtime test, penetration test, or Android compatibility certification.

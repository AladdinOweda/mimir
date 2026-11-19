---
document_id: MIMIR-GOV-CON-001
title: Mimir Constitution
document_type: Governing Constitution
status: Accepted — owner D-8 2026-11-17
language: en
scope: Product, architecture, engineering, security, UX, governance
---

# Mimir Constitution

## 1. Purpose

This Constitution establishes the enduring principles, boundaries, and decision rules governing Mimir. It is the highest-level project rulebook in this document set. It defines what Mimir is, what it must never become, and how product, architecture, implementation, and governance decisions are evaluated.

This document is a proposed canonical baseline. It must not be represented as formally ratified until the project owner explicitly approves it.

## 2. Product identity

Mimir is a modular, AI-powered knowledge and execution platform. It brings together conversational assistance, tools, agents, workflows, projects, files, browser capabilities, and coordinated execution through a coherent user experience.

Mimir is not merely a chat application, a single AI model wrapper, or a fixed collection of hard-coded engines.

The name and visual metaphor evoke a well of knowledge: information can enter from many sources, be understood and organized, and be used to produce useful outcomes.

## 3. Supreme principles

### 3.1 User authority

- The user remains the authority over their data, permissions, connected services, and consequential actions.
- No capability may silently expand its permissions.
- Viewing an approval request is not approval.
- External or irreversible actions require an explicit authorization path appropriate to their risk.
- The user must be able to understand what an agent is doing, why it is doing it, and what happened.

### 3.2 Kernel neutrality

- The Kernel must remain generic, engine-agnostic, and business-agnostic.
- The Kernel must not contain engine-name conditionals, engine-specific workflows, or assumptions about a particular provider.
- The Kernel must not know identifiers such as `terminal`, `forge`, or `browser` as special cases.
- Capabilities are described and registered through stable contracts, metadata, and extension points.
- Adding, replacing, disabling, or removing an engine must not require modifying the Kernel's core logic.

### 3.3 Modular composition

- Engines and features are attachable modules.
- A module must declare its capabilities, dependencies, permissions, compatibility, and lifecycle requirements.
- Optional modules must not become hidden mandatory dependencies of the Kernel.
- Shared infrastructure must have one authoritative implementation; do not create parallel services that compete for ownership.

### 3.4 Honest execution

- A stub is not a working implementation.
- An unavailable or unimplemented operation must fail explicitly and truthfully.
- Never report success, completion, or exit code zero when the operation did not actually succeed.
- Unknown outcomes must remain unknown; do not convert uncertainty into success.
- Progress indicators must be based on observable execution evidence, not invented percentages.

### 3.5 Security and privacy by design

- Least privilege is the default.
- Secrets must not be placed in source code, logs, screenshots, sample data, or ordinary configuration files.
- Data access, tool use, browser interaction, and external integrations must be permission-scoped.
- Sensitive information must be minimized in notifications, telemetry, and retained history.
- User data must not be used for unrelated purposes without a clear policy and valid authorization.

### 3.6 Accessibility and inclusion

- Accessibility is a product requirement, not a later polish phase.
- Support keyboard, touch, screen readers, scalable text, contrast, reduced motion, and RTL/LTR layouts.
- Respect platform accessibility preferences.
- Do not sacrifice readability for decorative glass effects.

### 3.7 Recoverability

- Preserve user input and useful context through recoverable failures.
- Distinguish retry, resume, cancel, stop, and abort.
- Do not automatically repeat a potentially consequential external side effect when its outcome is uncertain.
- Provide clear error explanations and a safe recovery path.

## 4. Architectural laws

1. **Core does not know engines.** The Kernel depends on contracts, not concrete engine identities.
2. **Engines describe themselves.** Each engine publishes capabilities, requirements, permissions, and supported operations through registration contracts.
3. **Contracts precede implementations.** Public interfaces and lifecycle semantics must be defined before multiple modules depend on them.
4. **One owner per responsibility.** Each service, state type, and lifecycle has a clear authoritative owner.
5. **No hidden coupling.** Modules communicate through explicit contracts or documented events.
6. **Optional means removable.** A truly optional module can be removed without corrupting core behavior or unrelated modules.
7. **State is typed by responsibility.** Agent, task, workflow, session, tool, and workspace state are not interchangeable.
8. **Policy is enforced below the UI.** Hiding a button is not a security boundary.
9. **Adapters isolate vendors.** Provider-specific behavior stays behind adapter boundaries.
10. **Compatibility is explicit.** API and schema changes have documented migration and compatibility rules.

## 5. Runtime responsibility boundaries

The following responsibilities are distinct and integrated. They must not be collapsed into one vague "agent manager".

| Responsibility | Owns | Must not absorb |
|---|---|---|
| Agent Runtime | Agent identity, configuration, execution instance, sessions, context, tool bindings, agent state | Global task distribution or workflow graph ownership |
| Orchestration | Planning coordination, task distribution, scheduling, dependencies, Swarm/Council coordination, handover | Low-level engine implementation or UI state |
| Workflow Runtime | Workflow graph definitions and graph execution semantics | Agent identity or engine-specific UI |
| Bot Profiles | Proactive behavior profiles, triggers, schedules, user-facing behavior policy | General agent execution lifecycle |
| Loop Runtime | Recurring autonomous loops, iteration policy, stop conditions, checkpoints | All-purpose scheduling for every task |
| Intelligent Execution | Execution strategy selection, capability matching, safe routing and runtime decisions | Engine-specific business logic in the Kernel |

These components may share infrastructure through contracts, but their ownership boundaries must remain explicit.

## 6. Product and UX laws

- The application shell owns the overall experience; engines provide capabilities.
- The shared assistant/composer is global and is not owned by Chat alone.
- Mobile-first design is mandatory, with Android as the first-class initial target.
- On mobile, the primary assistant/composer belongs near the bottom for one-handed reach, above bottom navigation and keyboard-safe areas.
- Navigation uses a hybrid model: Drawer, contextual top tabs, and bottom navigation.
- Advanced controls are progressively disclosed when relevant.
- Workspace switching must preserve state and must not silently cancel background work.
- Dark and Light appearances are both supported.
- The approved visual direction is Mimir Liquid Design System: Balanced personality, Adaptive motion, Pearl light appearance, and a Hybrid experience.
- The original Mimir logo concept must be preserved. Do not substitute an invented logo; use an explicitly marked placeholder until the approved source asset is supplied.

## 7. Governance and change control

### 7.1 Document authority

When documents conflict, use this order unless a later approved governance decision explicitly changes it:

1. This Constitution.
2. Approved Architecture Decision Records (ADRs).
3. Approved Blueprint.
4. Approved subsystem and interface specifications.
5. Implementation plans and repository conventions.
6. Code and tests as evidence of actual behavior.

A lower-level document cannot silently override a higher-level rule.

### 7.2 Decision records

Material decisions must record:
- Decision ID and title.
- Status: proposed, accepted, superseded, or rejected.
- Context and problem.
- Decision and alternatives considered.
- Consequences, risks, and migration impact.
- Date and approving authority.

### 7.3 Change procedure

A proposed change must identify:
1. The requirement or defect.
2. The affected contracts, modules, documents, and data.
3. Compatibility and migration implications.
4. Security, privacy, and accessibility impact.
5. Tests and acceptance evidence.
6. Rollback or recovery strategy.

Do not rewrite an accepted architectural principle through an incidental implementation change.

## 8. Quality gates

A feature is not complete merely because code exists. Completion requires evidence appropriate to the feature:

- Requirements and acceptance criteria are traceable.
- Contracts and error behavior are defined.
- Unit and integration tests pass where applicable.
- Security and permission boundaries are tested.
- Failure and recovery paths are tested.
- Accessibility and responsive behavior are reviewed.
- Documentation and migration notes are updated.
- Known limitations are disclosed.
- No test result is claimed unless it was actually run and observed.

## 9. Repository and delivery rules

- Keep one canonical copy of each authoritative document.
- Avoid duplicate documents with competing names or contradictory status.
- Use stable document IDs; do not put version numbers in filenames.
- Use descriptive filenames and metadata for version/status.
- Separate source code, documentation, tests, examples, generated artifacts, and build outputs.
- Do not delete or overwrite project material without a traceable reason and a safe review path.
- When producing a delivery package, include a manifest and clearly distinguish verified files from proposed scaffolding.

## 10. Explicit non-goals

This Constitution does not:
- Claim that any implementation already exists.
- Certify the current repository structure or code.
- Select a specific backend, database, model provider, or deployment platform without a separate decision.
- Grant an agent permission to act on the user's behalf.
- Make visual effects more important than performance, accessibility, or clarity.

## 11. Ratification checklist

- [ ] Project owner has reviewed the principles.
- [ ] Conflicts with existing approved ADRs have been reconciled.
- [ ] Document hierarchy and canonical location have been agreed.
- [ ] Status changed from Proposed only after explicit approval.
- [ ] Related Blueprint and repository governance documents reference this ID.

---

## 12. Approved integration decisions and external-project boundaries

Review record date: 2026-11-09.

### 12.1 Cognee — accepted optional Knowledge & Memory integration

**Decision:** Cognee is accepted as an optional, replaceable Knowledge & Memory provider candidate compatible with Mimir's architecture through explicit adapters and contracts. This is an architectural integration decision, not approval to embed Cognee into the Kernel, make it mandatory, or claim that runtime compatibility has been proven.

Non-negotiable boundaries:
- Kernel remains engine-agnostic and provider-agnostic. It must not import Cognee, depend on Cognee identifiers, or branch on a Cognee provider name.
- Mimir owns canonical Memory, Knowledge, Context, identity, access policy, retention, consent, and provenance contracts.
- Cognee may provide graph construction, document/session ingestion, retrieval, memory improvement, and knowledge-graph capabilities behind an adapter.
- Cognee datasets and internal identifiers are implementation details. Mimir project, user, conversation, agent, task, and workflow identities remain canonical.
- Secrets Vault is never replaced by Cognee. Credentials must be referenced through a secret handle and must not be ingested into Memory or Knowledge.
- Cognee is optional. Mimir must continue to operate with a native provider or another compatible provider when Cognee is absent, disabled, unavailable, or unsuitable.
- Logical deletion exposed by a provider is not automatically proof of physical erasure from underlying storage. Mimir must apply its own deletion and retention policy and verify provider-specific erase/compaction behavior.
- Local execution, Python compatibility, and a no-LLM quickstart do not prove Android/Termux/ARM64 compatibility. Platform support requires a measured compatibility test.

### 12.2 Vane search project — evaluated optional search integration candidate

The supplied repository URL is `https://github.com/BunsDev/perplexica-search-engine-ai`. Its current README and package metadata identify the application as **Vane** (package version shown as 1.12.2), rather than branding it as Perplexica. The repository is therefore referred to as the **Vane Search candidate** in Mimir documentation to avoid a false identity claim.

**Decision status:** evaluated and recommended for an optional Search/Research adapter prototype; not yet accepted as a production dependency and not implemented.

Boundaries:
- Search/research is distinct from Memory and Knowledge persistence.
- Vane may be integrated through a server-side adapter for web, academic, discussion, and supported file-search workflows.
- Search results must preserve URL, title, retrieved content/snippet, retrieval time when available, and source attribution.
- Mimir must enforce authentication, authorization, rate limits, source allowlists, privacy policy, and request validation before exposing any Vane API to clients.
- Provider credentials and Vane configuration remain server-side and must be accessed through Secrets Vault or deployment secret references.
- Mimir must not assume Vane's API response framing is standard SSE merely because it uses `text/event-stream`; the inspected Search API emits newline-delimited JSON objects and requires a tested adapter parser.
- The repository's API and Docker deployment must receive a separate security, dependency, and operational review before production use.

### 12.3 Integration governance

External projects are implementation options, not authorities over Mimir architecture. Every integration must:
1. Implement a Mimir-owned contract through an adapter.
2. Declare capabilities, version, health, deployment mode, data residency, and limitations.
3. Pass contract, isolation, privacy, deletion, provenance, failure, and performance tests.
4. Be removable without Kernel source changes or loss of canonical Mimir data.
5. Be pinned to a reviewed revision and license/dependency inventory before implementation.

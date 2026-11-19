<!-- SUPERSEDED as binding target — F2 2026-11-17 / D-7 ADR-046. Keep for history. -->

نكمل بالملف الثاني، مع الالتزام بالبلوپرنت الأول كمرجع معماري مباشر.

الملف ده مش مجرد أسماء مجلدات؛ هو هيكل الريبو المستهدف (Target Repository Structure)، وبيحدد وظيفة كل مجلد وموديول، وحدود المسؤولية والاعتماد بينها، ومكان الاختبارات والتوثيق والأدوات.

راعيت فيه:

• عدم تحويل كل Feature إلى Engine مستقل.

• الفصل بين الـKernel والـShared Services والـEngines والـAdapters.

• استقلال Agent Runtime عن Orchestration وWorkflow Runtime وLoop Runtime.

• فصل Knowledge Workspace عن Memory وعن Secrets Vault.

• فصل Terminal / Process Runtime عن Forge.

• الحفاظ على اتجاه Android-first مع مسار التوسع إلى Desktop ثم المنصات المستقبلية.

• عدم فرض Microservices أو توزيع النظام على عمليات متعددة من البداية؛ البداية Modular Monolith بعقود وحدود واضحة.

احفظ البلوك التالي باسم 02-MIMIR-TARGET-REPOSITORY-STRUCTURE.md.

markdown
---
document_id: MIMIR-REPO-002
title: Mimir Target Repository Structure
document_type: Repository Architecture Specification
project: Mimir Universal Architecture
version: 1.0
status: Target Repository Baseline
authority: Repository Structure Reference
language: English
created: 2026-11-06
parent_document: 01-MIMIR-FINAL-ARCHITECTURE-BLUEPRINT.md
---

# MIMIR — TARGET REPOSITORY STRUCTURE

## 1. Document Purpose

This document defines the target repository structure for the Mimir project.

It establishes:

- The canonical repository layout.
- Module and directory responsibilities.
- Architectural ownership boundaries.
- Dependency direction.
- Shared contracts.
- Engine organization.
- Platform-specific implementation boundaries.
- Test organization.
- Documentation organization.
- Build and development infrastructure.
- Extension and integration locations.
- Repository governance requirements.

The purpose is to ensure that the repository reflects the approved Mimir architecture rather than becoming a collection of unrelated features, duplicate modules, or tightly coupled implementations.

This document is subordinate to:

`01-MIMIR-FINAL-ARCHITECTURE-BLUEPRINT.md`

If a conflict exists between this document and the approved architecture, the architecture blueprint takes precedence.

This document describes the target structure.

It does not assert that every listed directory, module, source file, or capability already exists in the current repository.

---

# 2. Repository Design Principles

The repository must follow these principles.

## 2.1 Architecture Before Folder Names

A folder or module must represent a real responsibility.

Creating a directory does not create an architectural capability.

A module must have:

- A defined owner.
- A clear responsibility.
- Defined dependencies.
- Explicit interfaces where needed.
- A test strategy.
- A lifecycle where applicable.

## 2.2 One Authoritative Location

Every major responsibility must have one authoritative implementation location.

Avoid:

- Duplicate Kernel implementations.
- Duplicate repositories for the same Engine.
- Multiple competing architecture documents.
- Multiple independent versions of the same contract.
- Parallel implementations with unclear ownership.

## 2.3 Modular Monolith First

The initial implementation should favor a modular monolith.

This means:

- Clear module boundaries.
- Explicit contracts.
- Independent testing.
- Controlled dependencies.
- Shared application runtime where appropriate.
- No unnecessary distributed infrastructure.

Modules may be extracted into separate processes or services later when justified by security, performance, scaling, or deployment requirements.

## 2.4 No Business Logic in the Kernel

The Kernel must remain domain-independent.

No Engine-specific workflows, provider-specific implementation, or product-specific business logic should be placed inside Kernel modules.

## 2.5 Shared Services Are Not Engines

Shared platform services must not be converted into duplicate Engines merely because multiple Engines consume them.

Examples:

- Memory is a shared platform capability.
- Secrets management is a shared platform service.
- Persistence is a platform service.
- Event infrastructure is a shared service.
- Cost and Context Management is a shared capability.

## 2.6 Platform Independence

Shared domain logic must not depend directly on Android, Desktop, or other platform APIs.

Platform-specific behavior belongs in platform adapters and platform application modules.

## 2.7 Explicit Dependencies

Dependencies must be declared.

Undocumented access to another module's internal implementation is prohibited.

## 2.8 Testability

Every module must have an appropriate testing location.

Tests must validate behavior and contracts, not merely the existence of classes.

## 2.9 Progressive Implementation

The repository may contain planned modules that are not yet implemented.

Their status must be explicit.

An empty directory, interface, stub, mock, or placeholder must not be reported as a completed capability.

---

# 3. Repository Technology Direction

The current implementation direction is:

- Kotlin Multiplatform.
- Compose Multiplatform.
- Android-first.
- Desktop expansion.
- Future iOS and Web support.
- SQLDelight as a persistence technology candidate.
- Gradle-based build management.
- Modular repository organization.

These technologies are the current direction, subject to technical validation.

The repository must not depend on one specific database, AI provider, cloud provider, or external execution tool when an adapter can isolate that dependency.

---

# 4. Canonical Root Structure

The following is the target root structure.

```text
mimir/
│
├── README.md
├── LICENSE
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
├── SECURITY.md
├── CHANGELOG.md
├── NOTICE
│
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/
│   └── wrapper/
│
├── gradlew
├── gradlew.bat
│
├──

The root structure is logical.
Some directories may be introduced progressively.
A directory should not be created merely to make the repository look complete.
4.1 Root-Level Responsibilities

Path
Responsibility

README.md
Project introduction and developer entry point

LICENSE
Repository license

CONTRIBUTING.md
Contribution workflow

CODE_OF_CONDUCT.md
Community behavior

SECURITY.md
Security reporting and policy

CHANGELOG.md
User-visible change history

NOTICE
Third-party notices where applicable

settings.gradle.kts
Gradle module inclusion

build.gradle.kts
Root build configuration

gradle.properties
Shared Gradle settings

gradle/libs.versions.toml
Dependency and plugin version catalog

build-logic/
Reusable Gradle build conventions

apps/
Product applications

kernel/
Universal Kernel

platform/
Platform-specific application and runtime support

shared/
Cross-cutting platform services

engines/
Specialized capability implementations

adapters/
Platform and provider adapters

extensions/
Optional installable extensions and manifests

contracts/
Shared architectural contracts

integrations/
External system integrations

tooling/
Development and maintenance tools

tests/
Cross-module and system-level tests

docs/
Architecture, specifications, decisions, and guides

config/
Non-secret project configuration

scripts/
Automation and maintenance scripts

samples/
Reference implementations and examples

benchmarks/
Performance and resource benchmarks

.github/
Repository automation and CI workflows


5. Gradle Module Organization
5.1 General Rules
Every Gradle module must be explicitly registered in settings.gradle.kts.
The repository must avoid accidental module inclusion.
A module must not be included before its build configuration and ownership are ready.
The build should use centralized dependency versions where practical.
5.2 Naming Rules
Use:
• Lowercase directory names.
• Hyphen-separated names for Gradle modules.
• Clear domain names.
• Stable module identifiers.
Examples:
• :kernel:identity
• :shared:memory
• :engines:agent-runtime
• :engines:browser
• :adapters:ai:openai
Avoid:
• Temporary names.
• Duplicate module names.
• Names that imply unimplemented capabilities are complete.
• Ambiguous abbreviations.
• Provider-specific names outside adapter or integration boundaries.
5.3 Module Inclusion Policy
A module may be included when:
• Its responsibility is defined.
• Its dependencies are known.
• Its build configuration is valid.
• It has a documented implementation status.
• Its tests are identified.
Planned modules may be documented without being included in the active build.
6. Applications — apps/
Applications are product entry points.
They compose platform services, Kernel capabilities, Engines, and user interfaces.
They must not contain the authoritative business implementation of Engines.
text
apps/
│
├── mobile/
│   ├── build.gradle.kts
│   └── src/
│       └── commonMain/
│       ├── androidMain/
│       └── commonTest/
│
├── desktop/
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/
│       ├── desktopMain/
│       └── commonTest/
│
└── shared-app/
    ├── build.gradle.kts
    └── src/
        ├── commonMain/
        └── commonTest/

6.1 apps/mobile
Owns the Android-first mobile application entry point.
Responsibilities:
• Application startup.
• Mobile navigation composition.
• Mobile lifecycle integration.
• Mobile UI composition.
• Platform permissions.
• Application-level dependency composition.
• Integration with mobile-specific adapters.
It must not own:
• Authorization logic.
• Agent execution logic.
• Workflow execution.
• File storage business logic.
• Memory policy.
• Terminal process policy.
6.2 apps/desktop
Owns the desktop application entry point.
Responsibilities:
• Desktop startup.
• Desktop window management.
• Desktop navigation.
• Desktop application lifecycle.
• Desktop-specific composition.
• Desktop-specific adapter integration.
Shared functionality should be consumed from shared modules rather than duplicated.
6.3 apps/shared-app
Contains shared application composition where justified.
Responsibilities:
• Application-level navigation contracts.
• Shared presentation coordination.
• Shared screen state models.
• Cross-platform application composition.
• Common dependency wiring abstractions.
It must not become a second Kernel or an alternative location for Engine logic.
7. Kernel — kernel/
The Kernel is the universal governance and lifecycle foundation.
It must remain independent of specialized Engine implementations.
text
kernel/
│
├── kernel-api/
│
├── kernel-host/
│
├── identity/
│
├── authentication/
│
├── authorization/
│
├── capability-registry/
│
├── lifecycle/
│
├── policy/
│
├── approval/
│
├── audit/
│
├── isolation/
│
├── extension-trust/
│
└── kernel-testing/

7.1 kernel/kernel-api
Defines universal Kernel-facing interfaces and types.
Examples:
• Principal.
• IdentityReference.
• Permission.
• CapabilityDescriptor.
• CapabilityStatus.
• PolicyDecision.
• ApprovalReference.
• AuditReference.
• ScopeReference.
It must not contain specialized Engine implementation.
7.2 kernel/kernel-host
Owns Kernel initialization and runtime composition.
Responsibilities:
• Kernel startup.
• Service composition.
• Registry initialization.
• Lifecycle coordination.
• Kernel health.
• Shutdown.
It must not become a dependency container for arbitrary business logic.
7.3 kernel/identity
Owns:
• User identity.
• System identity.
• Service identity.
• Principal references.
• Identity lifecycle.
• Identity relationships.
Agent execution behavior remains in Agent Runtime.
7.4 kernel/authentication
Owns authentication mechanisms and session validation.
7.5 kernel/authorization
Owns:
• RBAC.
• ABAC.
• Permission evaluation.
• Resource access decisions.
• Delegated access validation.
• Authorization policies.
It must not trust caller-supplied authorization booleans.
7.6 kernel/capability-registry
Owns:
• Capability registration.
• Capability discovery.
• Capability metadata.
• Capability lookup.
• Compatibility information.
It does not contain the capability implementation.
7.7 kernel/lifecycle
Owns universal lifecycle states and transitions for registered platform components.
7.8 kernel/policy
Owns platform-level policy evaluation and enforcement contracts.
7.9 kernel/approval
Owns trusted approval requests, decisions, tokens, and validation.
7.10 kernel/audit
Owns security and governance audit contracts and implementation.
7.11 kernel/isolation
Owns universal scope and isolation primitives.
Examples:
• User scope.
• Project scope.
• Organization scope.
• Session scope.
• Incognito scope.
Engine-specific isolation logic remains with the relevant Engine where required.
7.12 kernel/extension-trust
Owns extension trust metadata and platform-level validation.
7.13 kernel/kernel-testing
Contains shared Kernel test utilities and Kernel-specific test suites.
8. Contracts — contracts/
Contracts define the interfaces through which independent modules communicate.
They are architectural boundaries, not implementation repositories.
text
contracts/
│
├── common/
│
├── identity/
│
├── authorization/
│
├── capability/
│
├── lifecycle/
│
├── approval/
│
├── audit/
│
├── execution/
│
├── agent/
│
├── orchestration/
│
├── workflow/
│
├── loop/
│
├── memory/
│
├── knowledge/
│
├── files/
│
├── storage/
│
├── secrets/
│
├── events/
│
├── messaging/
│
├── models/
│
├── tools/
│
├── browser/
│
├── terminal/
│
├── forge/
│
├── artifacts/
│
├── synchronization/
│
└── observability/

8.1 Contract Ownership
Contracts should have one authoritative definition.
A contract must not be independently redefined by multiple consuming modules.
The owning module should maintain the authoritative contract or explicitly delegate its maintenance to a shared contract module.
8.2 Contract Contents
A contract should define:
• Interface purpose.
• Data types.
• Inputs.
• Outputs.
• Validation.
• Error types.
• Authorization requirements.
• Version.
• Compatibility rules.
• Lifecycle.
• Idempotency.
• Observability.
8.3 Contract Versioning
Contract changes must be classified as:
• Compatible additions.
• Compatible optional changes.
• Breaking changes.
• Deprecated elements.
Breaking changes require a controlled migration strategy.
8.4 Contract Dependencies
Contracts should depend on minimal common types.
They must not import complete Engine implementations merely to reference a data type.
9. Shared Platform Services — shared/
Shared services provide reusable infrastructure and cross-cutting capabilities.
They are not duplicate Engines.
text
shared/
│
├── common/
│
├── configuration/
│
├── persistence/
│
├── storage/
│
├── events/
│
├── messaging/
│
├── scheduling/
│
├── notifications/
│
├── memory/
│
├── context/
│
├── knowledge-access/
│
├── search/
│
├── retrieval/
│
├── secrets/
│
├── model-gateway/
│
├── cost-context/
│
├── synchronization/
│
├── caching/
│
├── artifacts/
│
├── observability/
│
├── diagnostics/
│
└── platform-utilities/

9.1 shared/common
Contains minimal cross-platform foundational types and utilities.
It must not become a dumping ground for unrelated code.
9.2 shared/configuration
Owns:
• Configuration models.
• Configuration loading.
• Validation.
• Defaults.
• Environment-specific configuration.
• Configuration versioning.
Secrets must not be stored in ordinary configuration files.
9.3 shared/persistence
Owns persistence abstractions and reusable infrastructure.
Responsibilities:
• Database interfaces.
• Transaction abstractions.
• Migration support.
• Repository infrastructure where genuinely shared.
• Persistence error handling.
It must not own every Engine's domain repositories.
9.4 shared/storage
Provides generic storage abstractions.
It must not duplicate the Unified File & Transfer Engine.
The File Engine owns user-facing file operations and storage provider orchestration.
9.5 shared/events
Owns the common event infrastructure.
Responsibilities:
• Event envelopes.
• Event publication.
• Subscription contracts.
• Delivery infrastructure.
• Correlation.
• Retry and error handling.
9.6 shared/messaging
Owns common messaging infrastructure.
It supports synchronous and asynchronous communication patterns where appropriate.
9.7 shared/scheduling
Provides reusable scheduling infrastructure.
Loop Runtime and Workflow Runtime use it through contracts.
Scheduling infrastructure does not own loop or workflow business logic.
9.8 shared/notifications
Provides reusable notification delivery and routing.
It does not own user-specific notification presentation.
9.9 shared/memory
Owns the shared Memory capability.
Internal responsibilities:
• Capture.
• Classification.
• Policy integration.
• Provenance.
• Persistence coordination.
• Indexing coordination.
• Retrieval.
• Consolidation.
• Expiration.
• Correction.
• Deletion.
• Memory audit.
It must not become a duplicate Knowledge Engine or vector database wrapper.
9.10 shared/context
Owns context assembly and context lifecycle.
Responsibilities:
• Context selection.
• Scope validation.
• Context budgeting.
• Context compaction.
• Source tracking.
• Prompt context preparation.
• Context references.
It must not own Agent execution.
9.11 shared/knowledge-access
Provides reusable access contracts to authorized knowledge sources.
Knowledge Workspace owns explicit knowledge management.
9.12 shared/search
Provides common search infrastructure and indexing coordination.
9.13 shared/retrieval
Provides reusable retrieval orchestration.
It may combine lexical, vector, graph, and structural retrieval methods.
It must enforce permissions and preserve provenance.
9.14 shared/secrets
Owns the shared Secrets Vault service.
It must not be confused with Knowledge Workspace.
9.15 shared/model-gateway
Provides normalized model access contracts and request routing infrastructure.
Provider-specific implementation belongs in adapters.
9.16 shared/cost-context
Owns shared usage and budget management.
It must not own provider-specific pricing assumptions as permanent facts.
9.17 shared/synchronization
Provides synchronization infrastructure.
Provider-specific synchronization belongs in adapters.
9.18 shared/caching
Provides reusable caching infrastructure with explicit invalidation and isolation rules.
9.19 shared/artifacts
Provides generic artifact references, metadata, and lifecycle contracts.
Forge and other producers register their outputs through these contracts.
9.20 shared/observability
Provides:
• Structured logging.
• Metrics.
• Tracing.
• Correlation.
• Health reporting.
9.21 shared/diagnostics
Provides diagnostic collection and safe diagnostic export.
10. Engines — engines/
Engines own specialized capabilities.
The target organization is:
text
engines/
│
├── ai-hub/
│
├── agent-runtime/
│
├── orchestration/
│
├── workflow-runtime/
│
├── loop-runtime/
│
├── browser/
│
├── terminal/
│
├── forge/
│
├── unified-file-transfer/
│
├── knowledge-workspace/
│
└── collaboration/

The family grouping is logical.
It does not require introducing an additional parent Engine for every family.
10.1 AI Hub — engines/ai-hub
Owns the user-facing AI model management capability.
Responsibilities:
• Model discovery experience.
• Model selection.
• Provider configuration experience.
• Model capability presentation.
• Connection test interaction.
• Model availability presentation.
The shared Model Gateway owns normalized model access infrastructure.
Provider implementations belong in adapters.
10.2 Agent Runtime — engines/agent-runtime
Owns individual Agent execution.
Suggested internal organization:
text
agent-runtime/
│
├── api/
├── domain/
├── application/
├── execution/
├── sessions/
├── context/
├── tools/
├── state/
├── persistence/
└── tests/

Responsibilities:
• Agent execution lifecycle.
• Sessions.
• Context assignment.
• Tool invocation coordination.
• Agent state.
• Execution limits.
• Runtime integration.
It must not own Swarm scheduling or global task distribution.
10.3 Orchestration — engines/orchestration
Suggested internal organization:
text
orchestration/
│
├── api/
├── domain/
├── application/
├── planning/
├── task-distribution/
├── dependency-management/
├── scheduling/
├── swarm/
├── council/
├── delegation/
├── handover/
├── aggregation/
└── tests/

Responsibilities:
• Task decomposition.
• Task assignment.
• Coordination.
• Swarm.
• Council.
• Scheduling coordination.
• Dependencies.
• Delegation.
• Handover.
• Result aggregation.
10.4 Workflow Runtime — engines/workflow-runtime
Suggested internal organization:
text
workflow-runtime/
│
├── api/
├── domain/
├── definitions/
├── graph/
├── nodes/
├── triggers/
├── conditions/
├── execution/
├── checkpoints/
├── history/
├── versioning/
└── tests/

Responsibilities:
• Workflow definitions.
• Visual workflow execution model.
• Graph validation.
• Node execution.
• Branching.
• State transitions.
• Checkpoints.
• History.
• Retry behavior.
The visual editor is a product experience, not a separate workflow execution Engine.
10.5 Loop Runtime — engines/loop-runtime
Suggested internal organization:
text
loop-runtime/
│
├── api/
├── domain/
├── definitions/
├── cycle-management/
├── checkpoints/
├── persistence/
├── scheduling/
├── budgets/
├── termination/
├── recovery/
└── tests/

Responsibilities:
• Loop definitions.
• Recurrence.
• Checkpoint persistence.
• Budgets.
• Iteration limits.
• Stop conditions.
• Scheduling.
• Recovery.
It consumes Agent Runtime, Orchestration, Workflow Runtime, and shared scheduling services.
10.6 Browser — engines/browser
Suggested internal organization:
text
browser/
│
├── api/
├── domain/
├── runtime/
├── tabs/
├── navigation/
├── profiles/
├── sessions/
├── permissions/
├── inspection/
├── extraction/
├── automation/
├── downloads/
├── history/
├── bookmarks/
├── ai-assistance/
└── tests/

Responsibilities:
• Browser sessions.
• Tabs.
• Navigation.
• Page inspection.
• Content extraction.
• AI-assisted operation.
• Permission-controlled automation.
• Downloads.
• Browser state.
Rendering-engine implementations belong in adapters.
10.7 Terminal — engines/terminal
Suggested internal organization:
text
terminal/
│
├── api/
├── domain/
├── process-runtime/
├── execution-profiles/
├── command-policy/
├── isolation/
├── environment/
├── output-streaming/
├── process-monitoring/
├── cancellation/
├── history/
└── tests/

Responsibilities:
• Controlled process execution.
• Command validation.
• Execution profiles.
• Working directories.
• Environment handling.
• Output streaming.
• Cancellation.
• Process lifecycle.
• Execution history.
It must not own Forge project management.
10.8 Forge — engines/forge
Suggested internal organization:
text
forge/
│
├── api/
├── domain/
├── projects/
├── toolchain-manager/
├── templates/
├── build-systems/
├── dependency-manager/
├── build-executor/
├── test-runner/
├── artifact-manager/
├── signing/
├── provenance/
└── tests/

The seven primary Forge subsystems are:
• Toolchain Manager.
• Template Manager.
• Build System Adapters.
• Dependency Manager.
• Build Executor.
• Test Runner.
• Artifact Manager.
Signing and provenance are internal Artifact Manager capabilities or supporting modules, not independent Engines.
Forge invokes Terminal / Process Runtime through contracts.
10.9 Unified File & Transfer — engines/unified-file-transfer
Suggested internal organization:
text
unified-file-transfer/
│
├── api/
├── domain/
├── catalog/
├── metadata/
├── operations/
├── downloads/
├── transfers/
├── format-detection/
├── parsing/
├── archives/
├── conversion/
├── search-integration/
├── artifact-integration/
└── tests/

Responsibilities:
• Unified file references.
• File catalog.
• File operations.
• Downloads.
• Transfers.
• Format detection.
• Parsing coordination.
• Archive handling.
• Conversion.
• Storage provider coordination.
Provider implementations belong in adapters/storage/.
10.10 Knowledge Workspace — engines/knowledge-workspace
Suggested internal organization:
text
knowledge-workspace/
│
├── api/
├── domain/
├── documents/
├── markdown/
├── wikilinks/
├── backlinks/
├── tags/
├── folders/
├── graph/
├── templates/
├── daily-notes/
├── queries/
├── canvas/
├── attachments/
├── versioning/
└── tests/

Responsibilities:
• Knowledge documents.
• WikiLinks.
• Backlinks.
• Graph organization.
• Tags.
• Folders.
• Daily notes.
• Templates.
• Queries.
• Knowledge workspace experience.
It integrates with shared Memory and Search services but does not replace them.
10.11 Collaboration — engines/collaboration
Owns collaborative user-facing capabilities where they represent a coherent responsibility.
Potential modules:
• Conversations.
• Shared sessions.
• Meeting integration.
• Collaborative workspaces.
• Communication coordination.
External communication providers belong in adapters.
Do not create duplicate messaging infrastructure here.
11. Platform — platform/
Platform contains operating-system and application runtime support.
text
platform/
│
├── common/
│
├── android/
│
├── desktop/
│
├── ios/
│
└── web/

11.1 platform/common
Contains shared platform abstractions.
11.2 platform/android
Contains Android-specific implementations:
• Application lifecycle integration.
• Android permissions.
• Android secure storage integration.
• Android notifications.
• Android document provider integration.
• Android process/runtime restrictions.
• Android background execution integration.
• Android browser rendering integration.
11.3 platform/desktop
Contains desktop-specific implementations:
• Window management.
• Desktop filesystem access.
• Desktop notifications.
• Desktop process integration.
• Desktop secure storage.
• Desktop application lifecycle.
11.4 platform/ios
Reserved for future iOS-specific implementation.
It must not be treated as operational merely because the directory exists.
11.5 platform/web
Reserved for future web-specific implementation.
It must not be treated as operational merely because the directory exists.
12. Adapters — adapters/
Adapters isolate platform and provider-specific implementations.
text
adapters/
│
├── ai/
│
├── storage/
│
├── databases/
│
├── browser/
│
├── process/
│
├── build/
│
├── communication/
│
├── identity/
│
├── synchronization/
│
└── external-apis/

12.1 AI Adapters
text
adapters/ai/
│
├── openai/
├── anthropic/
├── google/
├── xai/
├── openai-compatible/
└── local-models/

Each provider adapter implements the Model Gateway contract.
Provider availability and supported capabilities must be validated.
12.2 Storage Adapters
text
adapters/storage/
│
├── local-filesystem/
├── android-document-provider/
├── dropbox/
├── google-drive/
├── onedrive/
├── webdav/
└── s3-compatible/

The File Engine consumes these through normalized provider contracts.
12.3 Database Adapters
text
adapters/databases/
│
├── sqldelight/
├── sqlite/
└── future-database-adapters/

Avoid duplicate implementations for the same backend.
The actual adapter set should reflect the selected persistence architecture.
12.4 Browser Adapters
text
adapters/browser/
│
├── geckoview/
├── desktop-browser/
└── future-browser-runtimes/

12.5 Process Adapters
text
adapters/process/
│
├── android-process/
├── desktop-process/
└── isolated-execution/

Process execution must follow Terminal security contracts.
12.6 Build Adapters
text
adapters/build/
│
├── gradle/
├── maven/
├── cmake/
└── other-build-systems/

Only validated adapters should be enabled.
12.7 Communication Adapters
text
adapters/communication/
│
├── email/
├── messaging/
├── calendar/
└── meeting-providers/

12.8 Identity Adapters
Contains external authentication and identity integrations.
12.9 Synchronization Adapters
Contains provider-specific synchronization implementations.
12.10 External API Adapters
Contains integrations that do not fit the preceding categories.
It must not become an uncontrolled collection of direct API calls.
13. Extensions — extensions/
Extensions are optional installable capabilities.
text
extensions/
│
├── manifests/
│
├── official/
│
├── community/
│
├── development/
│
└── templates/

13.1 Manifests
Contains extension manifests and schemas.
13.2 Official Extensions
Contains approved first-party extensions where independent extension packaging is appropriate.
13.3 Community Extensions
Contains external extension references or packaging infrastructure as appropriate.
Community extensions must not automatically receive trusted status.
13.4 Development Extensions
Contains local development extensions.
13.5 Templates
Contains extension development templates.
13.6 Extension Rules
An extension must declare:
• Identity.
• Version.
• Capabilities.
• Permissions.
• Dependencies.
• Compatibility.
• Entry points.
• Trust metadata.
An extension must not directly modify Kernel internals.
14. Integrations — integrations/
Integrations represent packaged connections to external systems when they require more than a low-level adapter.
text
integrations/
│
├── developer-platforms/
│
├── cloud-services/
│
├── productivity/
│
├── communication/
│
└── enterprise/

Examples:
• GitHub workflows.
• Git provider integration.
• Cloud application integration.
• Productivity services.
• Enterprise identity integration.
An integration may consume one or more adapters and expose a higher-level capability.
It must not duplicate a complete Engine.
15. Tooling — tooling/
Contains developer and repository maintenance tools.
text
tooling/
│
├── architecture-validator/
│
├── dependency-auditor/
│
├── module-graph/
│
├── contract-validator/
│
├── migration-tools/
│
├── test-utilities/
│
├── release-tools/
│
└── developer-cli/

15.1 Architecture Validator
Checks repository structure and dependency rules.
15.2 Dependency Auditor
Checks:
• Dependency versions.
• License metadata.
• Known vulnerabilities.
• Duplicate dependencies.
• Unsupported dependencies.
15.3 Module Graph
Generates and validates module dependency graphs.
15.4 Contract Validator
Checks contract compatibility and schema consistency.
15.5 Migration Tools
Provides controlled migration utilities.
15.6 Test Utilities
Provides reusable testing support.
15.7 Release Tools
Provides build, packaging, and release automation.
15.8 Developer CLI
Provides development utilities where appropriate.
It must not duplicate the user-facing Terminal Engine.
16. Tests — tests/
Testing is organized at both module and system levels.
Individual modules should contain their own unit and module integration tests.
The root tests/ directory contains cross-module, system, security, and acceptance suites.
text
tests/
│
├── architecture/
│
├── contracts/
│
├── integration/
│
├── system/
│
├── security/
│
├── privacy/
│
├── isolation/
│
├── performance/
│
├── reliability/
│
├── recovery/
│
├── platform/
│
├── accessibility/
│
├── localization/
│
├── acceptance/
│
├── fixtures/
│
├── test-data/
│
└── reports/

16.1 Architecture Tests
Validate:
• Dependency direction.
• Module ownership.
• Forbidden dependencies.
• Kernel independence.
• Duplicate responsibility prevention.
16.2 Contract Tests
Validate published interfaces and compatibility.
16.3 Integration Tests
Validate interactions between selected modules.
16.4 System Tests
Validate end-to-end system behavior.
16.5 Security Tests
Validate authentication, authorization, approval, secrets, and execution boundaries.
16.6 Privacy Tests
Validate data handling and retention policies.
16.7 Isolation Tests
Validate user, project, organization, session, and Incognito isolation.
16.8 Performance Tests
Measure representative workloads and resource consumption.
16.9 Reliability Tests
Validate failure handling, retries, and degradation.
16.10 Recovery Tests
Validate restart, interrupted operations, checkpoints, and restoration.
16.11 Platform Tests
Validate platform-specific behavior.
16.12 Accessibility Tests
Validate accessibility requirements.
16.13 Localization Tests
Validate Arabic/English and RTL/LTR behavior.
16.14 Acceptance Tests
Validate complete user-facing capabilities against explicit acceptance criteria.
16.15 Test Evidence
Test reports must identify:
• Repository revision or source snapshot.
• Build configuration.
• Test command.
• Environment.
• Test results.
• Failures.
• Skipped tests.
• Known limitations.
A test report without identifiable source revision is insufficient for release certification.
17. Documentation — docs/
Documentation must be organized by purpose and must avoid competing authoritative documents.
text
docs/
│
├── 00-governance/
│
├── 01-architecture/
│
├── 02-decisions/
│
├── 03-repository/
│
├── 04-contracts/
│
├── 05-engines/
│
├── 06-platforms/
│
├── 07-security/
│
├── 08-data-and-memory/
│
├── 09-user-experience/
│
├── 10-testing/
│
├── 11-development/
│
├── 12-operations/
│
├── 13-releases/
│
├── 14-integrations/
│
├── 15-research/
│
├── 16-implementation-status/
│
└── 17-archive/

17.1 00-governance
Contains:
• Documentation standards.
• Repository governance.
• Contribution rules.
• Change control.
• Ownership rules.
17.2 01-architecture
Contains:
• Final Architecture Blueprint.
• Architecture overview.
• Layer diagrams.
• System boundaries.
• Dependency principles.
The final blueprint must have one authoritative location.
17.3 02-decisions
Contains Architecture Decision Records (ADRs).
Each ADR should include:
• Decision ID.
• Title.
• Status.
• Context.
• Decision.
• Consequences.
• Alternatives.
• Related decisions.
• Date.
17.4 03-repository
Contains:
• Target repository structure.
• Module catalog.
• Module ownership.
• Dependency rules.
• Build organization.
17.5 04-contracts
Contains contract specifications and versioning rules.
17.6 05-engines
Contains one documentation section per Engine.
17.7 06-platforms
Contains platform-specific architecture and implementation guides.
17.8 07-security
Contains:
• Threat model.
• Security architecture.
• Authorization model.
• Secrets policy.
• Secure execution.
• Privacy boundaries.
17.9 08-data-and-memory
Contains:
• Data architecture.
• Memory architecture.
• Knowledge architecture.
• Retrieval strategy.
• Retention.
• Deletion.
• Synchronization.
17.10 09-user-experience
Contains:
• UX architecture.
• Navigation.
• Interaction specifications.
• Accessibility.
• Localization.
17.11 10-testing
Contains:
• Test strategy.
• Test plans.
• Acceptance criteria.
• Test evidence.
• Release gates.
17.12 11-development
Contains development guides and implementation standards.
17.13 12-operations
Contains operational procedures and diagnostics.
17.14 13-releases
Contains release processes, compatibility, packaging, and release evidence.
17.15 14-integrations
Contains integration specifications.
17.16 15-research
Contains research references and evaluated external findings.
Research documents must not silently override approved architecture.
17.17 16-implementation-status
Contains current implementation status and verified progress.
It must distinguish:
• Planned.
• Specified.
• In Development.
• Implemented.
• Tested.
• Verified.
• Blocked.
• Deprecated.
17.18 17-archive
Contains superseded historical documents.
Archived documents must not be treated as active implementation requirements unless explicitly referenced by an approved decision.
18. Configuration — config/
Contains non-secret configuration and schemas.
text
config/
│
├── application/
│
├── capabilities/
│
├── policies/
│
├── providers/
│
├── environments/
│
├── schemas/
│
└── examples/

Rules:
• No credentials.
• No private keys.
• No real access tokens.
• No user-specific production data.
• No hidden runtime secrets.
Environment-specific sensitive configuration must use an approved secure mechanism.
19. Scripts — scripts/
Contains repository automation.
text
scripts/
│
├── setup/
│
├── build/
│
├── test/
│
├── lint/
│
├── audit/
│
├── migration/
│
├── release/
│
└── maintenance/

Scripts must:
• Validate required parameters.
• Fail safely.
• Avoid destructive defaults.
• Provide clear output.
• Return meaningful exit codes.
• Avoid embedding secrets.
• Support reproducible execution where practical.
Destructive scripts must require explicit confirmation or safe dry-run behavior.
20. Samples — samples/
Contains minimal reference implementations.
text
samples/
│
├── minimal-engine/
│
├── sample-adapter/
│
├── sample-extension/
│
├── sample-workflow/
│
├── sample-agent/
│
└── sample-integration/

Samples must demonstrate the correct architectural patterns.
They must not be mistaken for production implementations.
21. Benchmarks — benchmarks/
Contains performance and resource evaluation.
text
benchmarks/
│
├── startup/
│
├── memory/
│
├── persistence/
│
├── retrie

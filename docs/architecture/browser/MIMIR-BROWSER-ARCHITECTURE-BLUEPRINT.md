Mimir Browser

Complete Architecture, Contracts & Integration Blueprint

Scope: Browser Engine, Browser AI Controller, Kernel Integration, Model Integration, Cross-Engine Collaboration, Security, Lifecycle, Data Contracts, UI Integration and Verification.

Product: Mimir Intelligence
Primary Platform: Android
Architecture: Universal Kernel + Independent Engines + Shared Services
Browser Platform Direction: GeckoView on Android first, behind a replaceable adapter
AI Control: Browser AI Controller, independent from the rendering engine
Page Understanding: DOM + Accessibility Tree + optional Screenshot/OCR
UI Direction: Mimir Liquid Design, Hybrid Navigation, Android-first
Languages: Arabic RTL / English LTR
Status: Architecture and contract specification for reconciliation and implementation; not evidence of completed runtime integration.

───

PART I — GOVERNANCE AND ARCHITECTURAL BOUNDARIES

1. Authority and compatibility

This document extends the Browser UX and Screen Specification. It does not replace the Mimir Constitution, approved ADRs, Universal Kernel architecture, or canonical contracts.

The implementation must preserve the following authority principles:

1. The Constitution and approved ADRs govern architectural decisions.
2. The canonical Blueprint describes the consolidated platform.
3. Engine Specifications define individual engine responsibilities.
4. Contracts define communication between engines and shared services.
5. Implementation must conform to those contracts.
6. Tests and runtime evidence determine actual completion.
7. State and Dashboard documents report implementation truth.

If a proposed Browser detail conflicts with an approved ADR, the ADR wins. The conflict must be documented rather than silently changing the architecture.

Do not create duplicate versions of existing contracts. Inspect the current canonical repository before adding or modifying files.

2. Non-negotiable architecture principles

2.1 Universal Kernel

The Kernel must never contain Browser-specific business logic.

Forbidden patterns include:

• "if engineId == "browser""
• Browser-specific routing inside Kernel core.
• Browser-specific permission rules embedded as Kernel business logic.
• Direct dependencies from Kernel core to GeckoView, Playwright, CDP, or browser vendor libraries.
• Browser-specific model selection inside Kernel core.

The Kernel provides universal mechanisms:

• Engine Registry.
• Engine Lifecycle.
• Capability Registry.
• Capability Resolver.
• Permission Policy.
• Risk Classification.
• Approval Service.
• Approval Tokens.
• Audit Log.
• Job and Task primitives.
• Event infrastructure.
• Shared identity and scope controls.
• Shared storage and evidence interfaces.

Browser declares its requirements and capabilities through manifests and contracts.

2.2 Independent Engines

Browser, Forge, Terminal, Vault, AI Model, Provider Registry, Agent Runtime, Swarm, Workflow, Computer Use, and other engines remain independent.

An engine may request another engine's capability through KernelHost, but must not directly call another engine's implementation.

Examples:

• Browser must not import Forge implementation classes.
• Browser AI must not directly call Terminal internals.
• Forge must not depend on Browser UI.
• Provider Registry must not execute Browser actions.
• AI Hub must not become a Browser submodule.

2.3 Shared Services

Shared services provide reusable capabilities without turning into domain-specific engines.

Relevant services:

• Identity and Session.
• Workspace and Context.
• Secrets and Vault integration.
• Artifact Storage.
• Evidence Collection.
• Provider Registry.
• Adapter Registry.
• Process Runtime.
• Scheduling.
• Notifications.
• Durable Event Outbox.
• Independent Verification.
• Search and Memory selection.

Browser consumes these services through their public interfaces.

2.4 Browser Engine versus Browser AI Controller

These are separate responsibilities.

Browser Engine owns:

• Browser runtime and rendering.
• Tabs and windows.
• Navigation.
• Profiles.
• Cookies and site storage.
• Downloads and uploads.
• Browser history.
• Bookmarks.
• Web permissions.
• Browser lifecycle.
• Page observation ports.
• Browser action ports.

Browser AI Controller owns:

• User request interpretation.
• Browser task planning.
• Context selection.
• Page understanding coordination.
• Action planning.
• Action sequencing.
• Multi-tab task coordination.
• Model selection through approved model services.
• Approval requests.
• Execution monitoring.
• Result aggregation.
• Handover and evidence references.

The Controller must not become a second Browser Engine.

2.5 Computer Use boundary

Computer Use is a separate capability and adapter surface.

Preferred Browser interaction order:

1. Browser-native navigation and tab APIs.
2. Browser DOM and accessibility interfaces.
3. Browser-supported interaction APIs.
4. Computer Use adapter when the previous interfaces cannot safely perform the required operation.
5. Human handoff when automation is unsupported or unsafe.

Computer Use must not bypass Browser permissions, Kernel policy, approval requirements, or audit.

───

PART II — COMPLETE SYSTEM ARCHITECTURE

3. Logical architecture

The intended architecture is:

Mimir Applications
└── Mimir App Shell
├── Browser UI
├── Shared Composer
├── Navigation
└── Notifications

KernelHost
├── Engine Registry
├── Lifecycle Manager
├── Capability Registry
├── Capability Resolver
├── Permission Policy
├── Risk Analyzer
├── Approval Service
├── Audit Log
├── Job / Task Coordination
└── Event Infrastructure

Shared Services
├── Workspace / Context
├── Provider Registry
├── Adapter Registry
├── Model Runtime Services
├── Secrets / Vault
├── Artifact Storage
├── Evidence Collection
├── Scheduling
├── Notifications
├── Durable Outbox
└── Independent Verification

Independent Engines
├── Browser Engine
│ ├── Browser Runtime Adapter
│ ├── Tabs / Windows
│ ├── Profiles / Sessions
│ ├── Navigation
│ ├── Downloads / Uploads
│ ├── Site Permissions
│ ├── Page Observation
│ └── Browser Action Port
│
├── Browser AI Controller
│ ├── Intent Interpretation
│ ├── Context Selection
│ ├── Page Understanding
│ ├── Task Planning
│ ├── Action Planner
│ ├── Multi-Tab Coordination
│ ├── Model Session Coordination
│ ├── Approval Coordination
│ └── Result Assembly
│
├── Computer Use Engine
├── AI Model Engine
├── Provider Registry Engine
├── Agent Runtime
├── Swarm
├── Workflow / Process Runtime
├── Terminal
├── Forge
└── Vault

This is a logical architecture, not a requirement that every box become a separate deployable module.

4. Browser module boundaries

The Browser implementation should be organized by responsibility.

Suggested logical modules:

Module| Responsibility
"browser-api"| Public Browser contracts
"browser-core"| Browser engine orchestration
"browser-runtime-api"| Replaceable browser runtime interface
"browser-runtime-gecko"| GeckoView Android adapter
"browser-tabs"| Tabs, windows and groups
"browser-sessions"| Sessions and restoration
"browser-navigation"| Navigation and address bar
"browser-storage"| Bookmarks, history and saved pages
"browser-downloads"| Download lifecycle
"browser-permissions"| Site permission state
"browser-page-understanding"| DOM, accessibility and visual observation
"browser-actions"| Supported browser actions
"browser-ai-contracts"| Controller integration contracts
"browser-ai-controller"| Planning and task coordination
"browser-ai-ui"| Browser AI user interfaces
"browser-security"| Browser-specific security enforcement
"browser-testing"| Contract and integration tests

These are proposed logical boundaries. Reuse existing modules if equivalent modules already exist.

Do not create modules solely to match these names.

5. Replaceable runtime architecture

Android's initial runtime direction is GeckoView.

However, Browser Engine must not depend on GeckoView classes outside the Gecko adapter.

Required abstraction:

BrowserEngine
|
v
BrowserRuntimePort
|
+-- GeckoBrowserRuntimeAdapter
|
+-- FutureBrowserRuntimeAdapter

The adapter exposes supported browser operations through a stable contract.

A future runtime must be replaceable without changing:

• Universal Kernel.
• Browser AI Controller.
• Provider Registry.
• AI Model Engine.
• Browser UI public state contracts.
• Cross-engine orchestration.

Platform-specific behavior belongs inside the adapter.

WebExtensions, Developer Console, and other advanced features must be implemented only when the selected runtime provides the required support or a compatible adapter exists.

───

PART III — ENGINE MANIFEST AND CAPABILITY CONTRACTS

6. Browser Engine Manifest

Browser must publish a versioned manifest describing its identity, lifecycle requirements, supported capabilities, dependencies, and compatibility.

Conceptual structure:

{
"engineId": "browser-core",
"contractVersion": "1.0",
"engineVersion": "0.1.0",
"displayName": "Mimir Browser",
"category": "browser",
"lifecycle": {
"supportsStart": true,
"supportsStop": true,
"supportsHealthCheck": true,
"supportsGracefulShutdown": true
},
"capabilities": [],
"requiredServices": [],
"optionalServices": [],
"platformAdapters": [],
"securityProfile": {
"defaultPermission": "DENY",
"supportsScopedPermissions": true,
"supportsAudit": true
}
}

The example is a conceptual contract shape, not a declaration that the current implementation already provides these features.

Manifest requirements:

• Unique engine identity.
• Manifest version.
• Engine version.
• Contract compatibility range.
• Capability declarations.
• Required and optional dependencies.
• Platform support.
• Security requirements.
• Lifecycle support.
• Health check support.
• Runtime adapter identity.
• Declared limitations.

The Kernel validates the manifest before registration.

An invalid or incompatible manifest must not be activated.

7. Capability catalogue

Capabilities must be granular and versioned.

7.1 Core browsing

browser.navigation.read
browser.navigation.navigate
browser.navigation.back
browser.navigation.forward
browser.navigation.reload

browser.tabs.list
browser.tabs.create
browser.tabs.activate
browser.tabs.close
browser.tabs.reorder
browser.tabs.duplicate

browser.windows.list
browser.windows.create
browser.windows.close

browser.sessions.save
browser.sessions.restore

browser.profiles.list
browser.profiles.create
browser.profiles.switch

7.2 Page understanding

browser.page.metadata.read
browser.page.dom.read
browser.page.accessibility.read
browser.page.selection.read
browser.page.screenshot.capture
browser.page.ocr.read
browser.page.content.extract
browser.page.links.read
browser.page.forms.describe

7.3 Page actions

browser.page.scroll
browser.page.click
browser.page.type
browser.page.select
browser.page.find
browser.page.copy
browser.page.fill
browser.page.submit
browser.page.upload
browser.page.download
browser.page.open_menu

7.4 Browser data

browser.history.read
browser.history.delete

browser.bookmarks.read
browser.bookmarks.create
browser.bookmarks.update
browser.bookmarks.delete

browser.downloads.list
browser.downloads.start
browser.downloads.pause
browser.downloads.resume
browser.downloads.cancel

browser.saved_pages.read
browser.saved_pages.create
browser.saved_pages.delete

7.5 Security and configuration

browser.permissions.read
browser.permissions.request
browser.permissions.revoke

browser.site_data.read
browser.site_data.clear

browser.settings.read
browser.settings.update

browser.private_session.create
browser.private_session.close

7.6 Browser AI capabilities

The Controller publishes its own capabilities independently:

browser.ai.task.create
browser.ai.task.inspect
browser.ai.task.pause
browser.ai.task.resume
browser.ai.task.cancel

browser.ai.context.select
browser.ai.page.summarize
browser.ai.page.analyze
browser.ai.action.propose
browser.ai.action.execute
browser.ai.multi_tab.coordinate
browser.ai.multi_model.coordinate
browser.ai.result.aggregate

The Controller does not gain permission to invoke Browser actions merely because these capabilities exist.

Every requested operation must pass capability resolution and policy evaluation.

8. Capability metadata

Every capability declaration must include:

• Capability identifier.
• Capability version.
• Input schema.
• Output schema.
• Required permission scopes.
• Risk category.
• Approval requirement.
• Idempotency behavior.
• Timeout behavior.
• Cancellation behavior.
• Evidence requirements.
• Platform availability.
• Provider or adapter constraints.

Example:

{
"id": "browser.page.click",
"version": "1.0.0",
"inputSchema": "browser-click-request",
"outputSchema": "browser-action-result",
"risk": "MEDIUM",
"permissionScopes": [
"browser.page.interact"
 ],
"approval": {
"required": "policy_decision"
},
"idempotency": "operation_scoped",
"cancellable": true,
"evidenceRequired": true
}

This metadata must be validated by the Capability Registry.

9. Capability resolution

The Kernel resolves a capability based on:

1. Requested capability ID.
2. Requested contract version.
3. Registered providers.
4. Provider compatibility.
5. Platform availability.
6. Engine lifecycle state.
7. Permission scope.
8. Policy decision.
9. Runtime health.
10. User and workspace context.

Resolution outcomes:

• Resolved.
• Unavailable.
• Disabled.
• Incompatible Version.
• Permission Denied.
• Policy Denied.
• Approval Required.
• Engine Not Ready.
• Provider Ambiguous.

The Resolver must not silently choose an incompatible implementation.

Version negotiation must use explicit compatibility rules. A major contract version mismatch is not automatically compatible.

───

PART IV — COMMUNICATION CONTRACTS

10. Universal request envelope

Every cross-engine request must use the platform's canonical request envelope.

Required information:

• "requestId"
• "correlationId"
• "traceContext"
• "idempotencyKey" where applicable
• "source"
• "targetCapability"
• "contractVersion"
• "principal"
• "workspaceId"
• "sessionId" where applicable
• "taskId" where applicable
• "contextPackageRef" where applicable
• "permissionContext"
• "deadline"
• "payload"

Do not create a Browser-only envelope that duplicates the platform contract.

The Browser-specific payload belongs inside the canonical envelope.

11. Universal result envelope

Every invocation returns a structured result.

Required concepts:

• Request identity.
• Correlation identity.
• Execution status.
• Typed result or error.
• Evidence references.
• Timing information.
• Provider identity.
• Contract version.

Supported execution states:

NotInvoked
Queued
Running
WaitingApproval
Succeeded
Failed
Cancelled
TimedOut
Unavailable
Denied

Important:

• "NotInvoked" is not success.
• "Unavailable" is not success.
• A stub must not return fake successful results.
• A timeout must not be represented as a successful empty response.
• A policy denial must not be retried as if it were a network error.

12. Error classification

The shared error taxonomy must distinguish:

Error| Meaning| Retry
"UNAVAILABLE"| Service not available| Conditional
"NOT_READY"| Engine not initialized| Conditional
"CAPABILITY_NOT_FOUND"| Unsupported capability| No
"VERSION_INCOMPATIBLE"| Contract mismatch| No
"PERMISSION_DENIED"| Permission absent| No
"POLICY_DENIED"| Kernel policy blocked action| No
"APPROVAL_REQUIRED"| User approval needed| After approval
"APPROVAL_EXPIRED"| Approval expired| Request again
"INVALID_INPUT"| Invalid request| No
"CONTEXT_EXPIRED"| Context no longer valid| Rebuild context
"STALE_PAGE_STATE"| Page changed after observation| Observe again
"NAVIGATION_CHANGED"| Page navigated unexpectedly| Re-observe
"ELEMENT_NOT_FOUND"| Target element missing| Conditional
"SITE_BLOCKED"| Site prevents operation| Human handoff
"NETWORK_ERROR"| Network failure| Conditional
"TIMEOUT"| Operation exceeded deadline| Conditional
"CANCELLED"| User or system cancelled| No
"RUNTIME_ERROR"| Adapter/runtime failure| Conditional
"VERIFICATION_FAILED"| Result not verified| Re-plan or escalate

Every error must carry safe diagnostic information.

Never include secrets, passwords, access tokens, or sensitive form values in error details.

13. Idempotency and retry

Each operation must define whether it is safe to repeat.

Examples:

• Read page metadata: generally safe to retry.
• Navigate: may be repeatable, but page state must be checked.
• Create tab: requires operation-scoped idempotency.
• Download: must avoid duplicate files.
• Submit form: must not automatically repeat.
• Send message: must not automatically repeat.
• Purchase: must never be retried without a new explicit approval and state review.

An idempotency key must be scoped to the intended operation and retained for a defined duration.

The system must distinguish:

• Request retry.
• Adapter retry.
• Task retry.
• User-requested retry.

These are not interchangeable.

14. Trace and correlation

Every multi-engine task must propagate:

• "correlationId"
• "traceId"
• "spanId"
• "parentSpanId"
• "taskId"
• "sessionId"
• "workspaceId"

Example flow:

User Request
|
v
Intelligent Execution
|
+-- Browser AI Controller
| |
| +-- Browser Engine
| |
| +-- AI Model Engine
|
+-- Forge Engine
|
+-- Terminal Engine
|
v
Independent Verification
|
v
Evidence + Final Result

All steps retain the same task correlation while using separate execution spans.

───

PART V — BROWSER RUNTIME CONTRACTS

15. BrowserRuntimePort

The Browser runtime adapter must expose a stable interface.

Logical operations:

initialize()
shutdown()
healthCheck()

createTab()
closeTab()
activateTab()
listTabs()

navigate()
goBack()
goForward()
reload()
stopLoading()

getPageMetadata()
observePage()
captureScreenshot()

performAction()
getRuntimeCapabilities()

Actual method signatures must conform to the project's existing language and contract conventions.

The runtime adapter owns platform-specific implementation details.

The Browser Engine owns lifecycle and domain coordination.

16. Tab contract

Each tab must maintain:

• Stable "tabId".
• "windowId".
• "profileId".
• Current URL.
• Display title.
• Favicon reference.
• Navigation state.
• Loading state.
• Security state.
• Group reference.
• Pinned state.
• Mute state.
• Last active time.
• Session restoration metadata.

Tab states:

CREATED
LOADING
READY
NAVIGATING
SUSPENDED
CRASHED
CLOSING
CLOSED

Transitions must be validated.

A closed tab must not accept new actions.

A suspended tab must be resumed before operations that require a live runtime.

17. Page observation contract

Observation is not a single screenshot.

The Page Understanding service should assemble a structured observation using available sources:

1. Page metadata.
2. DOM snapshot.
3. Accessibility Tree.
4. Current selection.
5. Visible interactive elements.
6. Form descriptions.
7. Links and buttons.
8. Screenshot when necessary.
9. OCR when necessary.

Each observation must include:

• "observationId"
• "tabId"
• "navigationId"
• "capturedAt"
• "pageUrl"
• "pageOrigin"
• "pageTitle"
• "sourceTypes"
• "contentVersion" or state fingerprint where available
• "sensitivityMarkers"
• "completeness"
• "warnings"

The observation must be tied to the exact tab and page state from which it was captured.

18. DOM and Accessibility Tree

DOM and Accessibility Tree are complementary.

Use DOM for:

• Structural relationships.
• Element attributes.
• Form structure.
• Link destinations.
• Supported interaction targets.

Use Accessibility Tree for:

• Accessible names.
• Roles.
• Labels.
• State descriptions.
• User-perceivable relationships.

Do not expose unrestricted page DOM to every model by default.

Apply:

• Scope filtering.
• Size limits.
• Sensitive field redaction.
• Password exclusion.
• Hidden-field exclusion where appropriate.
• Script and prompt-injection isolation.
• Source references for extracted facts.

19. Screenshot and OCR

Screenshot/OCR is a fallback or complementary observation method.

Rules:

• Capture only the requested tab and permitted visible area.
• Do not capture other applications or private content outside the authorized scope.
• Redact sensitive regions when required.
• Preserve capture time and page reference.
• Label OCR output as extracted visual text, not verified DOM content.
• Do not infer hidden form values from a screenshot.

20. Page action contract

Every action must specify:

• Action ID.
• Task ID.
• Target tab.
• Expected navigation state.
• Target element reference.
• Action type.
• Input data reference.
• Permission scope.
• Risk level.
• Approval reference if required.
• Timeout.
• Expected postcondition.

Action types:

CLICK
TYPE
SELECT
SCROLL
FIND
COPY
FILL
OPEN_MENU
NAVIGATE
UPLOAD
DOWNLOAD
SUBMIT

The action result must include:

• Execution status.
• Target reference.
• Actual operation performed.
• Resulting page state.
• Navigation change.
• Evidence reference.
• Error if failed.

Do not return "Succeeded" simply because a click event was dispatched.

A successful click must be followed by a suitable postcondition check.

21. Stale observation protection

A page may change between observation and execution.

Before executing an action:

1. Validate the tab still exists.
2. Validate navigation identity.
3. Validate the target reference.
4. Validate the element still matches.
5. Validate permission and approval are still valid.
6. Re-observe if the page state changed.

If the target is stale, return "STALE_PAGE_STATE".

Never silently execute an action against a different element.

───

PART VI — BROWSER AI CONTROLLER

22. Controller responsibilities

Browser AI Controller is responsible for the following pipeline:

User Goal
|
v
Intent Interpretation
|
v
Context Selection
|
v
Page Understanding
|
v
Task Planning
|
v
Plan Validation
|
v
Risk Classification
|
v
Kernel Permission / Approval
|
v
Capability Resolution
|
v
Browser Action Execution
|
v
Observation and Monitoring
|
v
Independent Verification
|
v
Result Assembly

The Controller must not bypass this pipeline for convenience.

23. Controller task contract

A Browser task must contain:

• "taskId"
• "requestId"
• "userGoal"
• "workspaceId"
• "sessionId"
• "profileId"
• "selectedTabIds"
• "contextPackageRef"
• "executionMode"
• "allowedCapabilities"
• "forbiddenCapabilities"
• "riskPolicy"
• "approvalPolicy"
• "modelSelectionPolicy"
• "deadline"
• "acceptanceCriteria"

The Controller must not assume all open tabs are authorized context.

24. Execution modes

The user-facing modes are:

Observe

• Read permitted page information.
• Analyze page content.
• No clicking or typing.
• No form submission.
• No cross-site transfer.

Assist

• Analyze and suggest.
• Prepare actions.
• Execute only individually authorized actions.
• Ask for approval when required.

Autonomous

• Execute a bounded plan within explicitly granted scope.
• Respect action-level restrictions.
• Pause for sensitive or out-of-scope actions.
• Stop when conditions change.

Full Control

This is not unrestricted control.

It remains bounded by:

• Kernel policy.
• User permissions.
• Risk restrictions.
• Site limitations.
• Secrets protection.
• Mandatory approval for designated actions.
• Emergency stop.

The UI must explain these boundaries before the user enables a mode.

25. Task state machine

CREATED
|
v
VALIDATING
|
v
PLANNING
|
v
WAITING_PERMISSION
|
v
WAITING_APPROVAL
|
v
READY
|
v
RUNNING
|
+----> PAUSED
| |
| v
| RUNNING
|
+----> WAITING_HUMAN
|
+----> VERIFYING
|
+--> COMPLETED
|
+--> FAILED
|
+--> NEEDS_REVIEW
|
+--> RECOVERY

Additional terminal states:

CANCELLED
TIMED_OUT
BLOCKED

Every transition must be validated and recorded.

26. Pause, resume, cancel and Kill Switch

Pause:

• Stop scheduling new actions.
• Let a safe in-progress operation reach a controlled boundary.
• Preserve the task state.
• Report what has already happened.

Resume:

• Revalidate permissions.
• Revalidate approvals.
• Re-observe relevant pages.
• Revalidate task context.
• Continue only if the plan is still valid.

Cancel:

• Stop future actions.
• Cancel cancellable operations.
• Record irreversible actions already completed.
• Preserve evidence.

Kill Switch:

• Immediately block new AI-initiated Browser actions.
• Revoke or invalidate active action authorization as supported.
• Stop active automation at the safest possible boundary.
• Show the user the current execution state.
• Record the event in audit.
• Require an explicit restart decision.

The Kill Switch must remain accessible while an autonomous task is running.

───

PART VII — MODEL AND PROVIDER INTEGRATION

27. Provider discovery versus enablement

Provider Discovery and Provider Enablement are separate.

The platform must distinguish:

1. Discovered.
2. Registered.
3. Evaluated.
4. Approved.
5. Enabled.
6. Available.
7. Selected for a task.
8. Invoked.
9. Completed or failed.

A discovered model must not automatically become usable.

Browser AI may consume only models that pass the required provider and model gates.

28. Model resolution flow

Browser AI Controller
|
v
Model Request
|
v
Provider Registry
|
v
Enabled Model Candidates
|
v
Capability / Policy / License Checks
|
v
Model Runtime Adapter
|
v
AI Model Engine
|
v
Model Result + Attribution

Browser AI must not directly implement provider-specific HTTP calls.

Provider-specific integration belongs to Provider Registry and Model Runtime adapters.

29. Model selection policy

The Controller may request a model based on:

• Required capabilities.
• Context window.
• Tool support.
• Multimodal support.
• Privacy classification.
• Local or cloud execution.
• Cost budget.
• Latency budget.
• Provider availability.
• User preference.
• License and policy eligibility.

The system must not assume that every model supports:

• Image input.
• DOM interpretation.
• Tool calling.
• Structured output.
• Long context.
• Parallel tasks.

Unsupported requirements must be detected before invocation.

30. Multi-model sessions

Multi-model orchestration is an acceptance-critical Browser feature.

Example:

Browser Task
|
+-- Tab A: Claude
|
+-- Tab B: ChatGPT
|
+-- Tab C: Grok
|
v
Browser AI Controller
|
+-- Assign Research Task
+-- Collect Response
+-- Validate Response
+-- Transfer Approved Context
+-- Request Comparison
|
v
Aggregated Result

The example illustrates the intended workflow. It does not guarantee that every external website allows automation or that every provider is available.

Required behavior

• Each tab remains an independent browser context.
• Each model interaction is tracked separately.
• The Controller records which model produced each response.
• Results are associated with their originating tab and task.
• Cross-tab transfer must be explicit and traceable.
• Sensitive data must be filtered before transfer.
• The Controller must not expose credentials to models.
• External website content is treated as untrusted input.
• The user can inspect and stop the task.

31. Model Attribution

Every AI-generated result must preserve:

• Provider ID.
• Model ID.
• Model display name.
• Execution type: Local / Cloud.
• Model role.
• Task ID.
• Contribution reference.
• Start time.
• Completion time.
• Status.
• Input context reference where permitted.
• Output reference.
• Verification status.

The UI may display a compact attribution indicator, but full provenance must remain accessible.

If multiple models contribute to one answer, do not attribute the final answer to a single model unless that is factually accurate.

32. Context and secret isolation

Never send the following to a model by default:

• Passwords.
• Authentication tokens.
• Private keys.
• Payment card values.
• Sensitive form values.
• Vault secrets.
• Unrequested cookies.
• Browser profile secrets.

When a task requires interaction with a login form:

• The user performs the sensitive authentication step.
• Browser keeps credentials within its protected runtime where possible.
• Models receive only the minimum non-secret state required.
• The Controller must not ask the model to reconstruct or retain the password.

───

PART VIII — CROSS-ENGINE INTEGRATION

33. General rule

Browser communicates with other engines only through KernelHost and approved shared contracts.

No direct engine-to-engine dependencies.

34. Browser and AI Model Engine

Purpose:

• Request model inference.
• Receive structured output.
• Maintain model attribution.
• Apply cost and privacy policies.
• Enforce provider/model enablement.

Browser does not own model provider credentials or provider-specific runtime code.

35. Browser and Provider Registry

Purpose:

• Discover available providers.
• Retrieve enabled model candidates.
• Check supported capabilities.
• Check provider availability.
• Resolve compatible adapters.

Browser must not activate providers itself.

36. Browser and Agent Runtime

Purpose:

• Delegate a bounded task to an agent.
• Receive a structured handover.
• Track agent identity and execution.
• Apply budgets and scopes.

Delegation requires a ContextPackage.

The ContextPackage must explicitly list the information allowed to be shared.

Default Browser sharing is nothing.

37. Browser and Swarm

Purpose:

• Divide a research or analysis task among specialized agents.
• Coordinate results.
• Resolve disagreements.
• Aggregate findings.

Swarm decides who performs work; Browser provides browsing capabilities through its declared contracts.

Swarm must not directly manipulate Browser runtime internals.

38. Browser and Workflow / Process Runtime

Purpose:

• Execute a user-approved multi-step process.
• Preserve step ordering.
• Resume interrupted tasks.
• Track step results.
• Collect evidence.

The Workflow Runtime owns workflow sequencing.

Browser executes Browser-related steps.

A Browser task, a Workflow, and an Intelligent Execution Plan are distinct objects and must not be merged into one contract.

39. Browser and Terminal

Examples:

• Download a permitted file and pass its artifact reference to a Terminal task.
• Request metadata about a file.
• Send a user-approved artifact to a processing step.

Rules:

• Browser does not run shell commands.
• Terminal does not read Browser cookies.
• Cross-engine transfer uses Artifact References and ContextPackage.
• Terminal execution passes through its own permission and risk controls.
• File paths must not be treated as unrestricted shared authority.

40. Browser and Forge

Examples:

• Retrieve documentation.
• Inspect public API references.
• Download permitted project artifacts.
• Provide a source URL to a Forge task.
• Transfer a validated artifact reference.

Rules:

• Forge owns project and code execution behavior.
• Browser owns browsing and retrieval behavior.
• Browser does not become a code build system.
• Forge does not depend on Browser UI.
• Cross-engine operations are mediated by KernelHost.

41. Browser and Vault

Purpose:

• Protect sensitive references.
• Store permitted secrets.
• Resolve secrets through scoped access.
• Keep authentication information out of AI contexts.

Rules:

• Browser must not store plaintext secrets in ordinary history or task logs.
• Vault returns scoped secret access only to authorized consumers.
• Secret references must not be logged as secret values.
• Revocation must invalidate future access.
• AI models never receive Vault secrets by default.

42. Browser and Independent Verification

Browser execution is not its own proof of correctness.

Examples:

• A click is not proof that a form was submitted.
• A download request is not proof that the file was fully downloaded.
• A message typed into a website is not proof that it was sent.
• A navigation request is not proof that the destination loaded.
• A search result is not proof that its content is accurate.

Verification must use independent evidence appropriate to the task.

The executor must not be the sole judge of its own success.

───

PART IX — CONTEXT, HANDOVER AND EVIDENCE

43. ContextPackage

Every delegated Browser task must have an explicit ContextPackage.

Suggested contents:

• Context ID.
• Source task.
• Allowed tab references.
• Allowed page references.
• User goal.
• Relevant extracted facts.
• Relevant URLs.
• Allowed artifacts.
• Sensitivity classification.
• Excluded content.
• Permissions and scope.
• Expiration.
• Source provenance.

Do not copy an entire browser profile or all open tabs into an agent context.

44. Handover contract

A Browser task handover must include:

• Task ID.
• Previous owner.
• Next owner.
• User goal.
• Completed steps.
• Pending steps.
• Current state.
• Decisions.
• Files and artifact references.
• Evidence references.
• Known issues.
• Approval state.
• Acceptance status.
• Required next action.

A handover must preserve context without exposing unauthorized data.

45. Evidence model

Each action or task should reference evidence as appropriate:

• Page observation.
• Navigation result.
• Download completion.
• File metadata.
• Form state.
• Confirmation page.
• User approval.
• Model output.
• Independent verification result.

Evidence metadata must include:

• Evidence ID.
• Evidence type.
• Source.
• Timestamp.
• Task ID.
• Action ID.
• Tab reference if applicable.
• Integrity metadata where supported.
• Sensitivity level.
• Retention policy.

Sensitive evidence must be protected and must not be copied into ordinary logs.

───

PART X — SECURITY AND PRIVACY

46. Security execution pipeline

Every Browser action follows:

Requested Action
|
v
Capability Resolution
|
v
Permission Check
|
v
Risk Classification
|
v
Kernel Policy
|
v
Approval if Required
|
v
Sandbox / Runtime Scope
|
v
Execution
|
v
Evidence Collection
|
v
Independent Verification
|
v
Audit

The sequence must not be bypassed by the Controller or a model.

47. Risk classification

Suggested initial classification:

Operation| Default risk
Read public page metadata| Low
Read permitted page content| Low–Medium
Open a new tab| Low
Navigate to a new site| Low–Medium
Scroll or find text| Low
Click a normal navigation link| Medium
Type into a non-sensitive field| Medium
Fill a form| Medium–High
Upload a file| High
Download a sensitive file| High
Send a message| High
Submit a public post| High
Delete user data| Critical
Purchase or payment| Critical
Change account security| Critical

Risk is contextual. These are starting defaults, not universal guarantees.

Kernel policy may raise or lower a classification according to context, but a Browser Controller or model cannot override it.

48. Approval tokens

High and Critical operations require a Kernel-issued ApprovalToken.

The token must be:

• Bound to the exact action or bounded action set.
• Bound to the relevant task.
• Bound to the authorized principal.
• Time-limited.
• Scope-limited.
• Single-use where appropriate.
• Revocable.
• Auditable.

Callers must not send "approvalGranted=true" as a substitute for an ApprovalToken.

An expired or mismatched token must be rejected.

49. Prompt injection defense

All website content is untrusted.

This includes:

• Page text.
• Hidden DOM content.
• Comments.
• Images.
• OCR text.
• Downloaded documents.
• Embedded scripts.
• Third-party content.
• Model-generated summaries of web content.

The system must distinguish:

• User instructions.
• Platform policy.
• Agent instructions.
• Website content.
• Model output.

Website content must never override system or user policy.

If a page says "ignore previous instructions" or asks the model to reveal secrets, treat it as untrusted page content.

50. Incognito and strict privacy

Incognito must use strict isolation.

Required behavior:

• Separate private session context.
• No normal History entries.
• No normal Bookmarks changes unless the user explicitly saves them.
• No normal session restoration.
• No cross-profile cookie sharing.
• No persistent AI context by default.
• No automatic memory ingestion.
• Clear private task context on session closure according to policy.
• Explicit warning before transferring data out of private context.

The exact persistence behavior of browser runtime storage must be verified on the selected platform adapter.

51. Kill Switch and security indicators

While AI controls Browser:

• Display a visible activity indicator.
• Display the current task.
• Display the current mode.
• Display the active tab scope.
• Display whether the system is waiting for approval.
• Provide a persistent stop action.

The indicator must not be hidden behind a menu during autonomous operation.

───

PART XI — DATA AND PERSISTENCE

52. Browser data ownership

Browser owns Browser-specific records.

Shared stores provide persistence mechanisms, not Browser domain logic.

Data| Owner
Tabs| Browser Engine
Windows| Browser Engine
Browser profiles| Browser Engine
Browser history| Browser Engine
Bookmarks| Browser Engine
Browser sessions| Browser Engine
Downloads metadata| Browser Engine
Site permissions| Browser Engine + Kernel policy
Browser AI tasks| Browser AI Controller / Task service
Approval tokens| Kernel
Audit events| Kernel
Model registry| Provider / AI services
Evidence artifacts| Shared Evidence / Artifact services
Secrets| Vault

53. Persistence requirements

• Durable data must survive application restart where policy requires it.
• Session restoration must not restore expired approvals.
• Private sessions must not leak into normal persistence.
• Browser task state must be recoverable.
• Event delivery must survive process interruption.
• Audit events must be append-only.
• Sensitive records must follow encryption and retention policy.
• Deleting a UI record must not accidentally delete unrelated underlying artifacts.

54. Durable Outbox

Cross-engine events must use the platform's durable delivery mechanism where required.

Required behavior:

1. Write event to durable outbox.
2. Commit the associated state change.
3. Dispatch event.
4. Record delivery state.
5. Retry transient failures.
6. Deduplicate delivery.
7. Dead-letter permanent failures.
8. Preserve event identity.

Do not rely on in-memory events alone for critical cross-engine state changes.

───

PART XII — EVENT CONTRACTS

55. Browser events

Suggested event catalogue:

browser.engine.started
browser.engine.stopped
browser.engine.health_changed

browser.tab.created
browser.tab.activated
browser.tab.navigated
browser.tab.loading
browser.tab.loaded
browser.tab.crashed
browser.tab.closed

browser.session.saved
browser.session.restored

browser.download.started
browser.download.progress
browser.download.completed
browser.download.failed

browser.permission.requested
browser.permission.granted
browser.permission.denied
browser.permission.revoked

browser.ai.task.created
browser.ai.task.started
browser.ai.task.paused
browser.ai.task.waiting_approval
browser.ai.task.completed
browser.ai.task.failed
browser.ai.task.cancelled

browser.ai.action.proposed
browser.ai.action.approved
browser.ai.action.denied
browser.ai.action.executed
browser.ai.action.verified

browser.security.kill_switch_activated
browser.security.policy_blocked

Every event must have:

• Event ID.
• Event type.
• Schema version.
• Timestamp.
• Producer identity.
• Correlation ID.
• Task ID when applicable.
• Payload.
• Sensitivity classification.

Events must not contain plaintext secrets.

56. Event consumers

Consumers subscribe only to events relevant to their responsibilities.

Examples:

• UI consumes tab and task state changes.
• Notification service consumes user-relevant task events.
• Audit consumes security and approval events.
• Task service consumes task lifecycle events.
• Independent Verification consumes evidence-ready events.
• Workflow Runtime consumes declared workflow-relevant events.

Do not make every engine subscribe to every Browser event.

───

PART XIII — COMPLETE USER EXPERIENCE AND SCREEN COVERAGE

57. Browser screens

The original 17 screens remain required.

The following additions complete the missing Browser functions:

Screen 18 — Private Browsing / Incognito

Purpose:

• Start a private session.
• Explain isolation.
• Display active private tabs.
• Close the private session.
• Explain what will and will not be retained.

UI:

• Distinct but consistent private-mode indicator.
• Clear session boundary.
• No normal history or profile mixing.
• Persistent warning when AI context is transferred outside the private session.

Screen 19 — Extensions Manager

Purpose:

• View installed extensions.
• Enable or disable supported extensions.
• Review permissions.
• Remove extensions.
• Inspect compatibility.

UI:

• Extension list.
• Publisher and version.
• Permission summary.
• Status.
• Update availability.
• Install source.
• Details view.

Rules:

• Do not promise WebExtensions support until the runtime adapter proves compatibility.
• Extension installation must pass source and permission checks.
• Extensions must not receive unrestricted Browser or Kernel privileges.

Screen 20 — Developer Tools

Purpose:

• Inspect pages and diagnose Browser behavior for advanced users.

Potential sections:

• Console.
• Network.
• Page inspection.
• Accessibility inspection.
• Storage inspection.
• Performance diagnostics.

Rules:

• This is an advanced feature.
• It must be capability-gated.
• It must not expose secrets in logs.
• It must not provide unrestricted script execution.
• Runtime support must be verified before showing controls.

Screen 21 — Browser Diagnostics

Purpose:

• Inspect Browser Engine health and adapter capabilities.
• Identify unsupported features.
• Review runtime errors.
• Export safe diagnostic reports.

UI:

• Runtime adapter identity.
• Engine version.
• Contract compatibility.
• Supported capabilities.
• Health state.
• Last error.
• Diagnostic export.

Do not display internal secrets, cookies, or authentication data.

58. Screen-to-service mapping

Screen| Main service / owner
Browser Home| Browser Navigation
Browser Workspace| Browser Runtime
Tabs Manager| Tabs Service
Tab Groups & Sessions| Session Service
Browser AI Home| Browser AI Controller
AI Page Assistant| Page Understanding + Controller
Multi-Model Workspace| Model Runtime + Controller
AI Task Monitor| Task Service
Permissions & Security| Kernel Policy + Browser Permissions
History| Browser Storage
Bookmarks & Reading List| Browser Storage
Downloads| Download Service
Saved Pages & Captures| Artifact / Browser Storage
Site Information| Browser Runtime + Permissions
Browser Profiles| Profile Service
Browser Settings| Settings Service
Command Palette| Browser Command Registry
Private Browsing| Private Session Service
Extensions Manager| Extension Adapter
Developer Tools| Runtime Diagnostics
Browser Diagnostics| Health / Diagnostics

Every screen must read from the owning service, not duplicate its data in local UI state as a second source of truth.

───

PART XIV — NAVIGATION AND STATE COHERENCE

59. Navigation ownership

Navigation must be separated into:

• Mimir global navigation.
• Browser internal navigation.
• Website navigation.
• Browser AI task navigation.

These are four distinct navigation contexts.

Back behavior must follow this order:

1. Close the active modal or Bottom Sheet.
2. Close the expanded contextual panel.
3. Return to the previous Browser internal screen.
4. Navigate backward in the active website if appropriate.
5. Return to the previous Mimir workspace.
6. Exit Browser only when no higher-priority navigation state remains.

The exact order must be reconciled with the Mimir Shell's approved navigation contract.

60. State preservation

When a user switches to Forge or Terminal and returns:

• Preserve active Browser tab.
• Preserve scroll position where supported.
• Preserve address bar state where appropriate.
• Preserve tab groups.
• Preserve AI task status.
• Preserve pending approval references without treating them as granted.
• Refresh stale page observations.
• Do not rerun completed actions automatically.

61. UI state versus domain state

UI may cache presentation state.

It must not become the authority for:

• Permissions.
• Approval.
• Task completion.
• Download completion.
• Tab lifecycle.
• Provider enablement.
• Model availability.
• Audit state.

The authoritative service must provide these states.

───

PART XV — CONTRACT FILES AND DOCUMENTATION COMPLETION

62. Canonical file reconciliation

Before adding files, inspect the repository and existing contract schemas.

The following are required contract areas. Existing files must be updated rather than duplicated.

Contract area| Required content
Browser Engine Manifest| Identity, lifecycle, capabilities, adapters
Browser Runtime Contract| Rendering and navigation adapter interface
Browser Tab Schema| Tab lifecycle and state
Browser Session Schema| Session persistence and restoration
Browser Action Schema| Action request and result
Browser Observation Schema| DOM, accessibility and visual observation
Browser Permission Schema| Scope, status, expiry and origin
Browser AI Task Schema| Goal, state, plan and acceptance criteria
Browser AI Action Schema| Proposed action and approval binding
Model Contribution Schema| Provider/model provenance
ContextPackage Contract| Explicit delegated context
Handover Contract| Task ownership transfer
Evidence Reference Schema| Evidence metadata and integrity
Event Envelope| Durable event identity and delivery
Error Schema| Error taxonomy and retry classification

63. Existing platform contracts to reuse

Browser must reuse or align with:

• Engine Manifest.
• Capability Manifest.
• Capability Registry.
• Universal Request / Result Envelope.
• Permission Policy.
• ApprovalToken.
• Audit Event.
• Task Item.
• AgentContract.
• ContextPackage.
• Handover.
• ModelRuntimeContract.
• Provider Registry.
• Workflow and Workflow Run contracts.
• Evidence and Verification contracts.
• Durable Outbox event contract.

Do not create Browser copies of these shared platform contracts.

64. Browser documentation set

Suggested canonical documentation areas:

docs/
architecture/
browser-engine-architecture.md
browser-ai-controller-architecture.md
browser-kernel-integration.md
browser-security-model.md
browser-cross-engine-integration.md

engines/
browser/
browser-engine-spec.md
browser-runtime-adapter-spec.md
browser-page-understanding-spec.md
browser-actions-spec.md
browser-ai-controller-spec.md
browser-multi-model-spec.md
browser-private-session-spec.md
browser-extension-spec.md
browser-diagnostics-spec.md

api/
browser/
browser-manifest.schema.json
browser-tab.schema.json
browser-session.schema.json
browser-action.schema.json
browser-observation.schema.json
browser-permission.schema.json
browser-ai-task.schema.json
browser-ai-action.schema.json
browser-model-contribution.schema.json

ux/
browser/
browser-screen-spec.md
browser-interaction-spec.md
browser-responsive-spec.md
browser-accessibility-spec.md

testing/
browser-contract-test-plan.md
browser-security-test-plan.md
browser-runtime-conformance.md
browser-cross-engine-test-plan.md

These are proposed destinations, not a command to create duplicates. Reconcile with the actual repository structure and naming conventions first.

───

PART XVI — TESTING AND VERIFICATION

65. Contract tests

Required:

• Manifest validation.
• Schema validation.
• Capability version compatibility.
• Request envelope validation.
• Result envelope validation.
• Error classification.
• Idempotency behavior.
• Trace propagation.
• ApprovalToken validation.
• ContextPackage validation.
• Event schema validation.

66. Browser Runtime tests

Required:

• Runtime initialization.
• Runtime shutdown.
• Health check.
• Create and close tabs.
• Navigation.
• Back and forward.
• Reload.
• Page loading state.
• Runtime crash recovery.
• Session restoration.
• Profile separation.
• Download lifecycle.
• Site permission handling.

A Noop adapter must report that the feature is unavailable or not invoked. It must never simulate a real browser success.

67. Page Understanding tests

Required:

• DOM extraction.
• Accessibility Tree extraction.
• Screenshot capture.
• OCR fallback.
• Large page handling.
• Dynamic page updates.
• Stale observation detection.
• Password field exclusion.
• Sensitive field redaction.
• Prompt-injection isolation.
• Cross-tab context isolation.

68. Action tests

Required:

• Valid click.
• Missing element.
• Stale element.
• Navigation during action.
• Permission denial.
• Approval required.
• Expired approval.
• User cancellation.
• Runtime timeout.
• Site blocking.
• Successful postcondition verification.
• Repeated action prevention.

69. Multi-model tests

Required:

• Provider discovery.
• Provider enablement.
• Model availability.
• Model capability matching.
• Model invocation.
• Provider failure.
• Model timeout.
• Local/cloud attribution.
• Parallel model execution where supported.
• Result aggregation.
• Conflicting model responses.
• Missing provenance.
• Budget enforcement.
• Context privacy enforcement.

70. Cross-engine tests

At minimum:

1. Browser downloads a permitted artifact.
2. Kernel records the artifact reference.
3. A Forge or Terminal task requests access through the approved contract.
4. Kernel checks permissions.
5. The receiving engine receives only the authorized artifact.
6. Verification checks the receiving operation.
7. Audit records the transfer.

Additional scenarios:

• Browser asks AI Model Engine to summarize a page.
• Browser delegates research to Agent Runtime.
• Swarm coordinates multiple Browser tasks.
• Workflow invokes Browser actions.
• Browser requests a scoped Vault secret reference.
• Browser pauses and resumes after application restart.

No direct engine-to-engine call is permitted.

71. Security tests

Required:

• Prompt injection.
• Malicious page content.
• Cross-origin context isolation.
• Secret exfiltration attempts.
• Unauthorized form submission.
• Approval token reuse.
• Approval token scope mismatch.
• Incognito leakage.
• Cross-profile leakage.
• Malicious extension permissions.
• Kill Switch behavior.
• Audit integrity.
• Unauthorized downloads and uploads.

72. UI tests

All 21 screens must be tested for:

• Android Phone.
• Tablet.
• Desktop.
• Arabic RTL.
• English LTR.
• Light Theme.
• Dark Theme.
• Large Font.
• Empty State.
• Loading State.
• Error State.
• Offline State.
• Permission Denied.
• Feature Unavailable.

Test real navigation and interaction. A screen that only renders correctly is not considered complete.

───

PART XVII — IMPLEMENTATION MATURITY AND RELEASE GATES

73. Maturity states

Every Browser capability must have an honest maturity status:

• "PLANNED"
• "CONTRACT_DEFINED"
• "STUB"
• "ADAPTER_IMPLEMENTED"
• "INTEGRATION_TESTED"
• "VERIFIED"
• "PRODUCTION_READY"
• "UNAVAILABLE"
• "BLOCKED"

A feature must not move to a higher state without supporting evidence.

74. Browser release gates

Browser cannot be declared complete until:

Gate A — Architecture

[  ]  Browser Engine and Controller are separated.
[  ]  Kernel remains engine-agnostic.
[  ]  Public contracts are defined.
[  ]  Capability declarations are valid.
[  ]  Runtime adapter is replaceable.

Gate B — Runtime

[  ]  Real browser runtime is connected.
[  ]  Navigation works.
[  ]  Tabs work.
[  ]  Sessions work.
[  ]  Downloads work.
[  ]  Profiles and permissions work.
[  ]  Runtime health is observable.

Gate C — AI

[  ]  Controller works.
[  ]  Page understanding works.
[  ]  Actions are permissioned.
[  ]  Multi-model sessions work.
[  ]  Provider enablement is respected.
[  ]  Model provenance is preserved.

Gate D — Security

[  ]  Kernel policy is enforced.
[  ]  Approval tokens are validated.
[  ]  Prompt injection is isolated.
[  ]  Secrets are protected.
[  ]  Incognito is isolated.
[  ]  Kill Switch is tested.
[  ]  Audit is durable.

Gate E — Integration

[  ]  Browser integrates through KernelHost.
[  ]  Cross-engine transfers use contracts.
[  ]  Workflow integration works.
[  ]  Agent delegation works.
[  ]  Independent verification works.
[  ]  Durable events work.

Gate F — UX

[  ]  All screens are implemented.
[  ]  All transitions work.
[  ]  Responsive layouts are verified.
[  ]  RTL/LTR is verified.
[  ]  Accessibility is verified.
[  ]  No decorative or fake controls remain.

Gate G — Release

[  ]  CI passes.
[  ]  Runtime tests pass.
[  ]  Security tests pass.
[  ]  Product UI gates close.
[  ]  Evidence is attached.
[  ]  Release checklist is complete.

"canShip = false" while any mandatory release gate remains open.

───

PART XVIII — IMPLEMENTATION ORDER

75. Recommended implementation sequence

Phase B0 — Canonical reconciliation

• Inspect existing ADRs.
• Inspect current Blueprint.
• Inspect KernelHost contracts.
• Inspect capability schemas.
• Inspect Browser stubs and runtime ports.
• Inspect Provider Registry and ModelRuntimeContract.
• Inspect existing UX and screen files.
• Produce a gap matrix.
• Reuse existing files wherever possible.

Deliverable: Browser Architecture Reconciliation Report.

Phase B1 — Contract foundation

• Browser manifest.
• Runtime adapter contract.
• Browser action contract.
• Page observation contract.
• Permission contract.
• Task and event contracts.
• Error and result compatibility.
• Contract tests.

Deliverable: validated Browser contracts.

Phase B2 — Browser Engine foundation

• Runtime initialization.
• Navigation.
• Tabs.
• Windows.
• Sessions.
• Profiles.
• History and bookmarks.
• Downloads.
• Runtime health.

Deliverable: working non-AI Browser Engine.

Phase B3 — Kernel integration

• Engine registration.
• Lifecycle.
• Capability publication.
• Capability resolution.
• Permission enforcement.
• Risk and approval.
• Audit.
• Durable events.

Deliverable: Browser connected through KernelHost.

Phase B4 — Page Understanding and actions

• DOM.
• Accessibility Tree.
• Screenshot/OCR.
• Action targets.
• Click and type.
• Form descriptions.
• Stale state protection.
• Postcondition checks.

Deliverable: permissioned Browser interaction.

Phase B5 — Browser AI Controller

• Intent interpretation.
• Context selection.
• Planning.
• Action sequencing.
• Monitoring.
• Pause/resume/cancel.
• Kill Switch.
• Evidence collection.

Deliverable: single-model Browser AI workflow.

Phase B6 — Model integration

• Provider resolution.
• Model capability matching.
• Model invocation.
• Attribution.
• Multi-model sessions.
• Result aggregation.
• Cost and privacy policy.

Deliverable: multi-model Browser AI.

Phase B7 — Cross-engine integration

• Agent Runtime.
• Swarm.
• Workflow.
• Terminal.
• Forge.
• Vault.
• Independent Verification.

Deliverable: controlled multi-engine Browser workflows.

Phase B8 — Complete UX

• All 21 screens.
• Responsive layouts.
• Contextual menus.
• Settings.
• Accessibility.
• RTL/LTR.
• State preservation.

Deliverable: complete Browser experience.

Phase B9 — Hardening and release

• Security tests.
• Runtime conformance.
• Performance.
• Recovery.
• CI.
• Evidence.
• Release gates.

Deliverable: verified release candidate.

───

PART XIX — DIRECT IMPLEMENTATION INSTRUCTIONS

76. Instructions for the development agent

You are implementing Mimir Browser as an independent Engine integrated with the Universal Kernel.

This document is a required design and architecture specification.

You must not:

• Modify Kernel core to add Browser-specific conditions.
• Create direct Browser-to-Forge or Browser-to-Terminal dependencies.
• Make Browser AI Controller responsible for rendering web pages.
• Make Browser Engine responsible for model orchestration.
• Bind AI Hub to GeckoView internals.
• Treat model discovery as model enablement.
• Treat UI completion as runtime completion.
• Treat a stub as a successful implementation.
• Invent missing contract fields or silently change existing contract versions.
• Create duplicate files when canonical equivalents already exist.
• Remove existing decisions without an approved change.
• Claim integration without integration tests and evidence.

You must:

1. Inspect the actual repository before editing.
2. Reconcile this document against approved ADRs.
3. Identify existing files and their authority.
4. Build a modify/reuse/merge/add/delete matrix.
5. Identify unresolved contract conflicts.
6. Implement contracts before dependent runtime features.
7. Keep Kernel independent.
8. Keep Browser Engine replaceable.
9. Keep Browser AI Controller independent from the browser vendor.
10. Integrate models through Provider Registry and ModelRuntimeContract.
11. Route all sensitive actions through Kernel policy.
12. Preserve provenance and evidence.
13. Implement and test all required screen transitions.
14. Report unavailable capabilities honestly.
15. Provide a complete verification report.

77. Required delivery report

The final report must include:

• Architecture files inspected.
• ADRs checked.
• Contracts reused.
• Contracts updated.
• New contracts added.
• Duplicate files avoided.
• Engine boundaries verified.
• Runtime adapter status.
• Browser UI status.
• Model integration status.
• Cross-engine integration status.
• Security status.
• Test results.
• Evidence references.
• Remaining blockers.
• Release readiness.

Every status must be supported by repository evidence or test output.

───

FINAL ACCEPTANCE PRINCIPLE

Mimir Browser is complete only when it is simultaneously:

1. A real browser experience.
2. An independent and replaceable Engine.
3. A correctly registered Kernel capability provider.
4. A secure and permission-aware execution surface.
5. A reliable context and page-understanding system.
6. A functional Browser AI Controller.
7. A multi-model coordination surface.
8. A controlled participant in Mimir's cross-engine workflows.
9. A fully integrated UX with correct navigation and state.
10. Independently verified through tests and observable evidence.

The Kernel governs. Engines execute. Controllers coordinate. Providers supply models. Verification proves results. The UI exposes the truth.

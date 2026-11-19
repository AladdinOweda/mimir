---
document_id: MIMIR-DOC-REG-INT-007
document_code: MIMIR-INT-007
filename: MIMIR-INT-007-INTEGRATION-SPECIFICATION.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

Mimir Intelligence — Integration Specification
Document ID: MIMIR-DOC-REG-INT-007
Status: Draft — Pending Architectural Review
Document Type: Functional & Technical Integration Specification
Product: Mimir Intelligence
Scope: Brain, Files, Downloads, Secure Vault, Shared Services, and Platform Integrations

───

1. Purpose
This document defines how Mimir Intelligence domains collaborate with one another and with the wider Mimir platform.
Its purpose is to establish predictable, secure, traceable, and recoverable integration behavior while preserving the independence of each domain.
The specification covers:
• Domain-to-domain integration.
• Integration with Chat, Forge, Browser, Terminal, Agents, and Workflows.
• User-initiated and autonomous operations.
• Cross-domain handoffs and references.
• Authorization and approval requirements.
• Provenance preservation.
• Synchronization and offline behavior.
• Failure handling and recovery.
• Activity, audit, and notification behavior.
This document defines integration contracts and expected behavior. It does not claim that any integration has already been implemented or verified.
2. Scope
2.1 Included domains

Domain
Primary responsibility

Mimir Brain
Knowledge Vault, Memory Center, Intelligent Retrieval

Mimir Files
File Manager, Storage Explorer, File Operations

Mimir Downloads
Download Manager, Queue, Media Capture

Mimir Secure Vault
Passwords, Passkeys, Authenticator, Secure Notes, Secure Documents & IDs, Cards, Digital Wallet, Autofill, Security Center


2.2 Platform integration points
• Chat and shared Composer.
• Forge.
• Browser.
• Terminal.
• Agent Runtime.
• Orchestration.
• Workflow Runtime.
• Bot Profiles.
• Loop Runtime.
• Intelligent Execution.
• Shared platform services.
2.3 Out of scope
This document does not redefine:
• The global Mimir Kernel architecture.
• The internal implementation of individual domains.
• The complete shared-services contracts.
• The internal behavior of individual Engines.
• Provider-specific storage implementations.
• Model-provider APIs.
These remain governed by their respective architecture and technical specifications.
3. Integration Principles
All integrations MUST follow these principles.
3.1 Domain ownership
Each domain remains the authoritative owner of its own records, operations, and lifecycle.
A domain may expose controlled capabilities to another domain, but it does not transfer ownership merely by sharing a reference.
3.2 Reference is not authorization
A reference to a file, note, download, or secure item does not grant permission to read or modify it.
Every operation must pass through the relevant authorization boundary.
3.3 Explicit capability contracts
Cross-domain communication must use documented contracts rather than direct access to another domain's internal database or implementation.
3.4 Least privilege
An integration receives only the permissions and data necessary for its specific operation.
Permissions must be scoped by:
• Resource.
• Operation.
• Requesting identity.
• Purpose.
• Duration, where applicable.
• User approval requirements.
3.5 Traceability
Every cross-domain operation must preserve its origin, requesting actor, authorization decision, and execution outcome.
3.6 No silent destructive operations
Deletion, replacement, overwriting, credential disclosure, and other sensitive actions must follow explicit policies and approval requirements.
3.7 No hidden AI access
Agents and models must not gain access to private or protected data merely because the data exists within Mimir.
3.8 Engine and Kernel independence
Intelligence integrations must not introduce domain-specific logic into the Kernel or require special Kernel conditions for individual Engines.
4. Domain Integration Matrix

Source
Destination
Integration purpose

Files
Brain
Process files into knowledge

Brain
Files
Open source files and referenced documents

Downloads
Files
Register completed downloads and manage destinations

Files
Downloads
Select destinations and inspect downloaded assets

Chat
Files
Attach, preview, save, and reference files

Chat
Brain
Retrieve knowledge and save approved outputs

Forge
Files
Access authorized project files and artifacts

Browser
Files
Save authorized downloads and captured content

Secure Vault
Browser
Controlled credential and Autofill operations

Secure Vault
Chat/Agents
Explicitly authorized, minimal secret operations only

Agents/Workflows
Brain
Search and process authorized knowledge

Agents/Workflows
Files
Execute permitted file operations

Agents/Workflows
Downloads
Create and monitor permitted download tasks

Unified Search
All eligible domains
Search authorized content across domains


The matrix describes possible integration paths, not unconditional access.
5. Integration Architecture
5.1 Integration flow
A standard integration operation follows this logical sequence:
1. An actor requests an operation.
2. The requesting domain or platform component creates an operation request.
3. The authorization service evaluates identity, resource, operation, scope, and policy.
4. The destination domain validates the request against its own rules.
5. The destination performs or rejects the operation.
6. The result is returned through a documented response contract.
7. Activity and audit records are generated according to policy.
8. Notifications are emitted when appropriate.
9. Relevant references and provenance metadata are preserved.
5.2 Integration patterns
The platform may support several integration patterns.
A. Synchronous request
Used when the requesting interface needs an immediate response, such as opening a file reference or checking a resource's metadata.
B. Asynchronous operation
Used for longer operations such as document processing, large file transfers, indexing, or export generation.
C. Event-driven integration
Used when one domain needs to react to a lifecycle event from another domain.
Examples:
• Download completed.
• File moved.
• Knowledge processing completed.
• Source became unavailable.
• Synchronization conflict detected.
Events are notifications of state changes, not automatic grants of permission.
D. User-mediated handoff
Used when an operation requires the user to review, confirm, select, or approve an action.
E. Agent or Workflow handoff
Used when an Agent or Workflow requests a domain capability under an explicit execution context and authorization scope.
5.3 Integration boundaries
A domain MUST NOT:
• Directly modify another domain's private records.
• Bypass another domain's authorization checks.
• Assume that an event guarantees successful completion.
• Treat a cached reference as proof of current access.
• Convert a failed operation into a successful result.
• Expose secrets through ordinary integration payloads.
6. Common Integration Request Contract
The following is a conceptual contract. Actual implementation schemas must be defined in the relevant technical specifications.
json
{
  "request_id": "unique-operation-id",
  "idempotency_key": "stable-operation-key",
  "source_domain": "files",
  "target_domain": "brain",
  "operation": "process_source",
  "actor": {
    "type": "user",
    "id": "actor-id"
  },
  "resource": {
    "type": "file",
    "id": "resource-id"
  },
  "purpose": "knowledge_processing",
  "authorization": {
    "scope": ["read"],
    "approval_reference": null
  },
  "execution_context": {
    "task_id": null,
    "agent_run_id": null,
    "workflow_run_id": null
  },
  "provenance": {
    "origin": "user_selected_file",
    "source_reference": "source-reference-id"
  }
}

6.1 Required fields
Every integration request must identify:
• Request ID.
• Source and destination.
• Operation.
• Requesting actor.
• Resource or target.
• Purpose.
• Authorization context.
• Execution context, when applicable.
6.2 Optional fields
Depending on the operation:
• Idempotency key.
• Approval reference.
• Deadline or expiry.
• User-visible description.
• Source references.
• Provenance information.
• Expected resource version.
• Correlation ID.
6.3 Response contract
A response must distinguish:
• Accepted.
• Running.
• Completed.
• Partially completed.
• Rejected.
• Failed.
• Cancelled.
• Requires approval.
• Waiting for external dependency.
It must also provide an operation reference when processing continues asynchronously.
A response must never report completion when the underlying operation has not completed.
7. Detailed Integration Scenarios
7.1 Files → Brain: Process a selected file
Goal: Convert an authorized file into useful knowledge.
Flow:
1. The user selects a file in Mimir Files.
2. The user chooses an available Brain action, such as summarize, extract concepts, create a note, or index for retrieval.
3. Files verifies that the selected resource is accessible.
4. The user confirms the intended processing action where required.
5. Files issues a controlled read capability or equivalent authorized handoff.
6. Brain obtains the permitted content.
7. Brain processes the content using available tools or models.
8. Brain creates the requested knowledge records or processing result.
9. Brain records the source reference and processing provenance.
10. The result is presented to the user.
Required behavior:
• Original files remain owned by Files or their storage provider.
• Brain stores knowledge records and source references, not an assumed replacement for the original file.
• Extracted claims must not automatically be treated as verified facts.
• Processing errors must be visible.
• The user can inspect the source of generated knowledge.
• A revoked permission must prevent subsequent unauthorized reads.
7.2 Downloads → Files: Register a completed download
Goal: Make completed downloads available through the file-management experience.
Flow:
1. Downloads completes a transfer.
2. Downloads verifies the actual destination and final transfer state.
3. Downloads emits a completion event.
4. Files receives the event and validates the resource reference.
5. Files registers the file in its index or presents it through the relevant storage view.
6. The user can open or organize the file through Files.
Required behavior:
• A completed transfer must not be reported if the file is incomplete.
• A failed or interrupted transfer must retain its accurate status.
• Files must not assume that every completed download is locally available.
• Provider-owned files must retain their storage-provider identity.
• Duplicate registration must be handled idempotently.
• Moving or deleting a file must follow the actual storage ownership rules.
7.3 Chat → Files and Brain: Save conversation outputs
Goal: Allow users to turn conversation content into durable resources.
Possible actions:
• Save an attachment to Files.
• Save an approved answer as a Brain note.
• Save generated documents to an authorized location.
• Create a source-linked knowledge record.
Flow:
1. The user selects a conversation output.
2. The user chooses Save to Files or Save to Brain.
3. The destination and resulting object type are displayed.
4. Any required authorization or confirmation is requested.
5. The destination domain creates its own record.
6. The saved item receives an appropriate source reference.
7. The conversation retains a reference to the saved result.
Required behavior:
• Saving a message does not automatically save the entire conversation.
• User-authored content must not be attributed to a model.
• Model-generated content must preserve available model provenance.
• The user must be able to distinguish a saved copy from a linked original.
• A failed save must not be presented as successful.
7.4 Forge → Files: Project resource access
Goal: Allow Forge to use authorized files without becoming the owner of the Files domain.
Examples:
• Browse project resources.
• Read source files.
• Save generated artifacts.
• Reference documentation.
• Export build results.
Flow:
1. Forge identifies the project resource required.
2. The user or authorized execution policy grants the required scope.
3. Files validates the request.
4. Files provides a controlled resource capability.
5. Forge performs the permitted operation.
6. Any output is stored through the appropriate destination contract.
7. Activity and provenance records are preserved.
Restrictions:
• Project access does not grant access to unrelated user files.
• Write and delete permissions must be separate from read permissions.
• Build artifacts and source files must remain distinguishable.
• Forge must not bypass storage-provider restrictions.
• Generated code is not considered correct merely because it was saved.
7.5 Browser → Files and Downloads
Goal: Support authorized saving and downloading of web resources.
Possible operations:
• Download a user-selected resource.
• Save an authorized document.
• Capture a permitted page or media resource.
• Organize completed downloads.
The Browser must respect:
• User-granted permissions.
• Website authentication boundaries.
• DRM and access restrictions.
• Copyright and applicable legal restrictions.
• Website restrictions and technical limitations.
A visible page does not automatically imply permission to extract or redistribute all its content.
7.6 Secure Vault → Browser: Autofill
Goal: Provide secure and controlled credential entry.
Flow:
1. The user visits an eligible authentication page.
2. The Browser identifies the relevant site context.
3. Secure Vault evaluates whether a matching credential exists.
4. The user confirms Autofill when required by policy.
5. Secure Vault provides only the necessary credential material through a protected channel.
6. Browser performs the authorized fill operation.
7. The operation is recorded without storing the secret value in ordinary activity logs.
Security requirements:
• Verify the target origin.
• Prevent unauthorized cross-origin filling.
• Never expose passwords or tokens to page scripts unnecessarily.
• Never place secrets in normal chat context.
• Never include secrets in general search indexing.
• Do not silently submit forms unless a separately authorized policy explicitly permits it.
• Do not expose a secret merely because an Agent can control the Browser.
• Respect user cancellation and vault locking.
7.7 Agent or Workflow → Intelligence domains
Goal: Allow autonomous execution to use domain capabilities safely.
Examples:
• Search Brain for project documentation.
• Read a user-approved file.
• Save an execution report.
• Monitor a download.
• Organize files according to an approved plan.
An Agent or Workflow must provide:
• Its identity.
• Its run or execution reference.
• The requesting task.
• The intended operation.
• The target resource.
• The required scope.
• The applicable approval or policy reference.
Execution rules:
1. The Agent requests a capability.
2. Authorization evaluates the request.
3. The destination domain validates its own rules.
4. The operation is performed only within the granted scope.
5. The result is returned to the Agent.
6. The operation is recorded against the Agent run and originating task.
An Agent's general autonomy level does not override resource-level permissions.
A Workflow's successful execution of one step does not automatically authorize later steps with broader privileges.
7.8 Unified Search across domains
Unified Search provides a common discovery experience while preserving domain ownership.
Searchable content may include:
• Brain knowledge records.
• Eligible Files metadata and content.
• Eligible Downloads metadata.
• Other explicitly permitted indexed resources.
Secure Vault secrets are excluded from general search by default.
Search behavior:
1. The user submits a query.
2. The search service identifies eligible sources.
3. Each source applies its own access and visibility policies.
4. Results are aggregated.
5. Each result retains its domain identity and source reference.
6. Opening a result triggers fresh authorization where required.
Required behavior:
• Search results must not reveal inaccessible content.
• Cached results must not bypass current authorization.
• Search snippets must follow the source's privacy policy.
• Stale or unavailable sources must be identifiable.
• Results must preserve their source and ownership information.
7.9 Brain deletion and dependent references
Goal: Prevent inconsistent deletion across linked domains.
Example: A source file in Files has been processed into several Brain notes.
When the original file is deleted:
1. Files processes the deletion according to its ownership and retention rules.
2. Files emits a source-lifecycle event.
3. Brain evaluates affected references.
4. Brain marks affected sources as unavailable, or applies the configured retention policy.
5. The user may be offered actions for dependent knowledge records.
6. Any permitted cleanup is recorded.
Deleting the original file must not silently delete all derived knowledge.
Likewise, deleting a Brain note must not delete its original file.
The system must distinguish:
• Original source.
• Derived content.
• Reference metadata.
• Cached content.
• Search index entries.
• Backup copies.
Permanent deletion must not be claimed until the applicable storage and backup policies have been verified.
7.10 Offline and synchronization integration
Mimir Intelligence must support appropriate offline behavior on Android and other supported clients.
Examples:
• Browsing locally available files.
• Reading permitted cached knowledge.
• Inspecting download history.
• Preparing operations for later synchronization.
Each operation must distinguish:
• Completed locally.
• Pending synchronization.
• Waiting for network.
• Conflicted.
• Failed.
• Rejected by a remote policy.
Offline availability does not imply offline authorization for every action.
Sensitive Secure Vault operations must follow their dedicated local security and key-availability requirements.
7.11 Import and Export
Import and Export may involve one or more domains.
Examples:
• Importing documents into Brain.
• Importing files into Files.
• Exporting selected knowledge.
• Exporting permitted download records.
• Exporting Secure Vault data through its dedicated secure process.
Requirements:
• Explicit destination selection.
• Permission validation.
• Format validation.
• Integrity verification.
• Provenance preservation where applicable.
• Clear reporting of partial success.
• Secure handling of exported sensitive data.
• No automatic inclusion of secrets in general exports.
Secure Vault export must use a separate security workflow and must never be treated as an ordinary Files export.
8. Authorization and Approval Model
8.1 Actor types
Supported conceptual actor types include:
• User.
• Platform service.
• Engine.
• Agent.
• Workflow.
• External provider.
Every operation must identify the effective actor and preserve the initiating user or authority context where applicable.
8.2 Permission dimensions
Authorization must consider:

Dimension
Example

Resource
Specific file or knowledge record

Operation
Read, write, move, delete, export

Purpose
Summarization, project build, backup

Actor
User, Agent, Workflow

Scope
One resource, one folder, one project

Duration
One-time or bounded access

Approval
Required, granted, denied, expired


8.3 Approval requirements
Approval may be required for:
• Deleting or overwriting data.
• Sharing or exporting sensitive data.
• Accessing protected files.
• Disclosing credentials.
• Submitting authentication forms.
• Performing external actions.
• Expanding an Agent's permissions.
• Accessing data beyond the original task scope.
Approvals must be specific and traceable.
A general approval must not silently authorize unrelated future operations.
8.4 Revocation
The platform must support revoking permissions and invalidating outstanding capabilities where technically possible.
After revocation:
• New operations must be rejected.
• Existing operations must be evaluated according to their execution state and cancellation policy.
• Cached credentials or capabilities must not remain usable beyond their permitted lifetime.
• The user must receive an accurate status.
9. Model Provenance and Execution Transparency
All model-produced outputs and AI-assisted operations must preserve the platform-wide provenance contract.
Where available, record:
• Model name and version.
• Provider.
• Local or Cloud execution.
• Model role and contribution.
• Generation and execution start/completion timestamps.
• Execution status.
• Participating models.
• Final composing model, if any.
• Source references.
• Agent and Workflow handoffs.
• Relevant tool operations.
9.1 Provenance propagation
Provenance must survive:
• Domain handoffs.
• File processing.
• Knowledge creation.
• Synchronization.
• Storage and restoration.
• Export and import.
• Copying and derived outputs.
9.2 Attribution rules
• Do not attribute user-authored content to a model.
• Do not invent missing provider or model details.
• Do not discard known model contributions during aggregation.
• Distinguish original content from AI-generated derivatives.
• Keep the default interface concise, with detailed provenance available on demand.
10. Activity, Audit and Notifications
10.1 Activity events
Relevant events may include:
• File selected for processing.
• Knowledge record created.
• Download completed.
• File saved or moved.
• Agent requested access.
• Approval granted or denied.
• Autofill performed.
• Export completed.
• Source deleted or unavailable.
• Synchronization conflict detected.
10.2 Audit records
Security-sensitive operations must generate appropriate audit records.
Audit data should identify:
• Actor.
• Operation.
• Target reference.
• Timestamp.
• Authorization decision.
• Outcome.
• Correlation or request ID.
• Approval reference, if applicable.
Audit records must not contain raw passwords, tokens, recovery secrets, or other protected secret values.
10.3 Notifications
Notifications should be generated for meaningful user-facing events.
Examples:
• A long-running processing operation completed.
• A download failed.
• An Agent needs approval.
• An export requires user action.
• A synchronization conflict needs attention.
Avoid duplicate or noisy notifications.
Opening a notification must not automatically dismiss an unresolved issue.
11. Reliability and Failure Handling
11.1 Failure categories
Integration operations must distinguish:
• Invalid request.
• Unauthorized request.
• Approval required.
• Resource unavailable.
• Resource changed.
• Network failure.
• Provider failure.
• Processing failure.
• Storage failure.
• Conflict.
• Timeout.
• Cancellation.
• Partial completion.
11.2 Idempotency
Operations that may be retried must support idempotency where appropriate.
Repeated requests with the same idempotency key must not create unintended duplicate records or repeat destructive operations.
Idempotency behavior must be defined per operation.
11.3 Retry policy
Retries must be bounded and appropriate to the failure type.
Examples:
• Temporary network errors may be retried.
• Authorization failures must not be retried as if they were network errors.
• Invalid requests require correction.
• Expired approvals require renewed authorization.
• Destructive actions require safeguards against accidental repetition.
11.4 Partial success
The platform must report partial completion accurately.
Example:
A batch import of ten files may complete for eight files and fail for two.
The result must identify:
• Successfully imported items.
• Failed items.
• Failure reasons.
• Retry eligibility.
• Any required user action.
11.5 Compensation and recovery
Where a multi-step operation cannot be completed atomically, the system must define its recovery behavior.
Examples:
• Remove an incomplete temporary artifact.
• Preserve an already completed download.
• Mark a knowledge-processing job as incomplete.
• Retain an audit trail after a failed export.
• Restore a previous version where supported.
Compensation must not destroy valid user data merely to make a multi-step operation appear atomic.
11.6 Cancellation
Cancellation must be supported where technically possible.
The user must be able to distinguish:
• Cancellation requested.
• Cancellation completed.
• Cancellation not possible.
• Operation already completed.
12. Android-First and Lifecycle Requirements
12.1 Mobile-first operation
The integration experience must support:
• File selection through Android Storage Access Framework where appropriate.
• Scoped storage restrictions.
• Cloud-provider file references.
• Background and foreground task transitions.
• Application process termination and recovery.
• Interrupted network operations.
• Permission changes.
• Device lock and unlock.
• Limited memory and processing resources.
12.2 Background execution
Long-running operations must not depend on an uninterrupted UI session.
The system should use suitable platform-supported background execution mechanisms.
The UI must display the actual operation state when the user returns.
12.3 Temporary resources
Temporary files and intermediate processing artifacts must have:
• Ownership.
• Expiry or cleanup policy.
• Storage location.
• Security classification.
• Recovery behavior.
Temporary data must not be treated as durable user content unless explicitly committed.
12.4 Connectivity and synchronization
Connectivity state and synchronization state are separate from task orchestration.
A network connection does not prove that a remote operation has completed.
A disconnected client must not fabricate remote success.
13. Privacy and Security Requirements
1. Every domain enforces its own data access boundaries.
2. Secure Vault remains isolated from general knowledge and search flows.
3. Secrets must never be inserted into general model context by default.
4. Agent access must be scoped and revocable.
5. Cross-domain references must not expose private metadata without authorization.
6. Sensitive operations must use protected communication and storage mechanisms.
7. Logs and notifications must avoid sensitive payload disclosure.
8. External providers must receive only necessary data.
9. Export and backup flows must respect domain-specific security policies.
10. Deletion and retention must follow each domain's lifecycle rules.
11. Incognito and privacy-restricted modes must not silently create persistent records.
12. User permissions and approvals must be understandable and reviewable.
14. User Experience Requirements
14.1 Consistency
Cross-domain actions must use consistent terminology and feedback.
The user should understand:
• Which domain owns the resource.
• Which domain will perform the operation.
• What information will be shared.
• What permissions are required.
• Whether the operation is local or remote.
• Whether the operation is complete or still running.
14.2 Context preservation
When moving between domains, preserve relevant navigation context where appropriate:
• Originating conversation.
• Selected file or resource.
• Project context.
• Current task.
• Relevant filters and location.
• Return destination.
Do not preserve sensitive content in an unsafe manner.
14.3 Mobile interaction
On Android:
• Keep primary actions accessible.
• Use native file selection when appropriate.
• Avoid unnecessary full-screen transitions.
• Show progress for long-running operations.
• Provide clear retry and cancellation actions.
• Keep approval requests understandable and focused.
14.4 Error presentation
Errors must explain what happened and what the user can do next.
Do not display internal stack traces or sensitive implementation details in ordinary user-facing messages.
15. Integration Sequence Examples
15.1 File processing sequence
mermaid
sequenceDiagram
    actor User
    participant Files
    participant Auth as Authorization
    participant Brain
    participant Audit

    User->>Files: Select file and request processing
    Files->>Auth: Validate read permission
    Auth-->>Files: Authorization result
    Files->>Brain: Submit authorized source reference
    Brain->>Brain: Process source
    Brain->>Audit: Record processing outcome
    Brain-->>Files: Return result reference
    Files-->>User: Present processing result

15.2 Secure Autofill sequence
mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Auth as Authorization
    participant Vault as Secure Vault
    participant Site as Web Page

    User->>Browser: Request Autofill
    Browser->>Auth: Validate origin and request
    Auth->>Vault: Request eligible credential
    Vault-->>User: Request confirmation if required
    User-->>Vault: Approve
    Vault-->>Browser: Protected credential handoff
    Browser->>Site: Fill authorized fields
    Browser->>Vault: Return operation outcome

15.3 Agent file operation
mermaid
sequenceDiagram
    participant Agent
    participant Auth as Authorization
    participant Files
    participant Audit

    Agent->>Auth: Request scoped capability
    Auth-->>Agent: Grant, reject, or require approval
    Agent->>Files: Submit authorized operation
    Files->>Files: Validate resource and scope
    Files->>Audit: Record outcome
    Files-->>Agent: Return operation status

16. Acceptance Criteria
The integration architecture is acceptable when the following requirements can be demonstrated through implementation and verification.
Domain boundaries
• [ ] Each domain retains ownership of its records and lifecycle.
• [ ] Cross-domain access uses explicit contracts.
• [ ] References do not grant authorization.
• [ ] No domain bypasses another domain's access controls.
• [ ] No domain-specific business logic is added to the Kernel.
Integration behavior
• [ ] Files can provide authorized source references to Brain.
• [ ] Downloads can report completed transfers to Files.
• [ ] Chat can save approved outputs to the appropriate destination.
• [ ] Forge and Browser can use authorized Files capabilities.
• [ ] Secure Vault Autofill follows origin and authorization controls.
• [ ] Agents and Workflows cannot bypass domain policies.
• [ ] Unified Search respects source visibility rules.
Reliability
• [ ] Retry behavior is defined.
• [ ] Idempotency is implemented where required.
• [ ] Partial success is represented accurately.
• [ ] Cancellation reports actual outcomes.
• [ ] Recovery behavior is documented and tested.
• [ ] Offline operations do not fabricate remote completion.
Security
• [ ] Secure Vault secrets are excluded from general search and memory by default.
• [ ] Approval and revocation behavior is testable.
• [ ] Sensitive information is excluded from ordinary logs.
• [ ] Cross-origin Autofill protections are verified.
• [ ] Export and deletion policies are domain-aware.
Provenance and observability
• [ ] Model provenance survives cross-domain handoffs.
• [ ] Activity events identify their source and outcome.
• [ ] Audit records preserve required security evidence.
• [ ] User-facing status matches actual execution state.
UX
• [ ] The user can identify the source and destination of an operation.
• [ ] Permissions and approvals are understandable.
• [ ] Errors provide actionable information.
• [ ] Context is preserved appropriately across domain transitions.
• [ ] Android lifecycle interruptions do not silently corrupt operation state.
17. Required Test Scenarios
The implementation team must prepare tests for at least these scenarios:
1. Processing a readable local file.
2. Processing a cloud-backed file.
3. Processing a file whose permission has been revoked.
4. Completing and registering a download.
5. Registering the same download event more than once.
6. Saving a Chat output to Brain.
7. Saving an attachment to Files.
8. Forge reading a permitted project file.
9. Forge attempting an unauthorized write.
10. Browser saving a permitted resource.
11. Browser attempting a restricted capture.
12. Secure Vault Autofill on a matching origin.
13. Secure Vault refusing an unauthorized origin.
14. Agent requesting a permitted file operation.
15. Agent requesting an operation outside its scope.
16. Workflow continuing after a recoverable failure.
17. Unified Search hiding inaccessible resources.
18. Deleting a source that has Brain references.
19. Offline operation followed by synchronization.
20. Conflicting updates from multiple clients.
21. Interrupted background processing.
22. Partial import and export.
23. Provenance preservation across a multi-model handoff.
24. Revoking an active capability.
25. Restoring data while preserving ownership and references.
18. Open Architectural Decisions
The following decisions require implementation-level research or explicit approval before they become final contracts:
1. Exact inter-domain API transport and messaging mechanism.
2. Event delivery guarantees and event retention.
3. Capability token format, lifetime, and revocation mechanism.
4. Whether individual operations use synchronous APIs, queues, or both.
5. Cross-domain transaction and recovery strategy.
6. Conflict resolution for shared references and derived records.
7. Search indexing strategy and privacy-aware cache invalidation.
8. Secure Vault integration mechanism with Android Autofill Framework.
9. Provider-specific file capability handling.
10. Offline authorization and key availability behavior.
11. Retention of derived content after source deletion.
12. Cross-device synchronization of operation state.
These decisions must be resolved without violating the ownership and security principles in this document.
19. Implementation and Verification Boundary
This document defines intended behavior and architectural requirements.
It does not establish that:
• All four domains have been implemented.
• Cross-domain APIs already exist.
• Secure Autofill is operational.
• Background processing is reliable.
• Cloud-provider integration has been verified.
• Offline synchronization is complete.
• Deletion is physically permanent.
• Agent and Workflow permissions are enforced in production.
Each claim of implementation or readiness requires corresponding source-code inspection, tests, and runtime evidence.
20. Relationship to Other Specifications
This document must be read together with:
• Mimir Intelligence — Architecture & Domain Boundaries.
• Mimir Intelligence — Shared Services Contracts.
• Mimir Brain — Functional & UX Specification.
• Mimir Files — Functional & UX Specification.
• Mimir Downloads — Functional & UX Specification.
• Mimir Secure Vault — Functional, UX & Security Specification.
• Mimir Platform Architecture and Kernel Contracts.
• Platform-wide Model Provenance and Execution Transparency Specification.
In case of conflict, the governing architecture and approved ADRs take precedence. Any proposed change must be documented and reviewed rather than silently changing an established contract.

───

End of Document
Status: Draft — Pending Architectural Review
Document ID: MIMIR-DOC-REG-INT-007
Implementation Status: Not established by this specification
Verification Status: Pending implementation and evidence

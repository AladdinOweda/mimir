---
document_id: MIMIR-DOC-REG-INT-010
document_code: MIMIR-INT-010
filename: MIMIR-INT-010-API-AND-EVENT-CONTRACTS.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

Mimir Intelligence — API & Event Contracts Specification

Document Metadata
Value

Document ID
MIMIR-DOC-REG-INT-010

Document Title
Mimir Intelligence — API & Event Contracts Specification

Domain
Mimir Intelligence

Document Type
Technical Architecture / Interface Contracts

Status
Draft — Pending Architectural & API Review

Authority
Subject to Mimir Architecture Blueprint, ADRs, Architecture & Domain Boundaries, Shared Services Contracts, Integration Specification, Security & Privacy Specification, and Data Model & Entity Relationships Specification

Version
Pending Document Registry

Implementation Status
Not verified

Approval Status
Not approved



───

1. Purpose
This document defines the conceptual API and event contract architecture for Mimir Intelligence.
It establishes how the four Intelligence domains and shared platform services expose capabilities, exchange information, report operations, and communicate state changes without violating domain ownership or security boundaries.
The specification aims to provide:
• Stable and explicit interfaces between domains.
• Consistent request, response, and error structures.
• Well-defined asynchronous operation contracts.
• Reliable event publication and consumption.
• Traceability across multi-domain operations.
• Secure, scoped access for users, Agents, and Workflows.
• Compatibility with Android-first and offline-capable operation.
• Extensibility without introducing unnecessary coupling.
This document defines interface requirements, not a claim that the APIs or event infrastructure already exist.
2. Scope
2.1 Included
• API design principles and interface ownership.
• Common API envelopes.
• Identity, authorization, capabilities, and approval references.
• Idempotency and request correlation.
• Error taxonomy.
• API versioning and compatibility.
• Filtering, sorting, pagination, and limits.
• Asynchronous operations.
• Event envelope and delivery requirements.
• Domain API and event catalogs.
• Cross-domain interactions.
• Agent and Workflow access.
• Offline queues and synchronization behavior.
• Security and privacy constraints.
• Acceptance criteria and test scenarios.
2.2 Excluded
This document does not:
• Select REST, GraphQL, gRPC, message queues, or a specific transport.
• Define concrete database schemas or storage technology.
• Replace the Shared Services Contracts Specification.
• Redefine the domain data model.
• Introduce new Mimir Engines.
• Move business logic into the Kernel.
• Authorize Agents to bypass user permissions.
• Claim implementation, deployment, or runtime verification.
3. Architectural Principles
3.1 Domain-owned interfaces
Every domain owns its public capabilities, API semantics, validation rules, and domain events.
A domain must not directly modify another domain's private records.
3.2 Contract-first design
Interfaces must be defined and reviewed before implementation.
Implementations must conform to the approved contracts rather than silently redefining them.
3.3 Kernel neutrality
The Kernel provides generic execution and infrastructure capabilities.
It must not contain:
• Intelligence-specific endpoints.
• Brain, Files, Downloads, or Secure Vault business rules.
• Conditions based on domain names.
• Domain-specific event handling logic.
• Domain-specific authorization exceptions.
3.4 Explicit authorization
A reference to a resource does not grant access to it.
Every operation must be authorized against the resource owner, requested action, actor, scope, and applicable policies.
3.5 Transport independence
The logical contract must remain independent of the eventual communication technology.
Transport adapters may map the same logical contract to different protocols.
3.6 Secure by default
Sensitive data must not be exposed through generic APIs, logs, events, search indexes, or diagnostic output.
3.7 Offline awareness
The contract must distinguish between:
• Accepted locally.
• Queued for synchronization.
• Accepted by the authoritative service.
• Successfully completed.
• Rejected.
• Conflicted.
• Cancelled.
Local acceptance must never be represented as authoritative remote completion.
4. Domain API Ownership

Domain
API Ownership
Main Responsibilities

Mimir Brain
Brain Domain
Knowledge, memory, notes, collections, references, retrieval

Mimir Files
Files Domain
File metadata, organization, operations, storage references

Mimir Downloads
Downloads Domain
Download requests, queues, jobs, progress, captured media metadata

Mimir Secure Vault
Secure Vault Domain
Credentials, passkeys, authenticator data, secure notes, cards, autofill

Shared Services
Individual service owners
Search, permissions, provenance, synchronization, audit, notifications, import/export, encryption and retention

Kernel
Kernel
Generic infrastructure and execution primitives only


The ownership of an API follows the ownership of the underlying resource and business operation.
Cross-domain operations must use explicit contracts between the owning domains.
5. Common API Contract
5.1 Request envelope
The following is a conceptual example, not an approved wire schema:
json
{
  "contractVersion": "1",
  "requestId": "request-identifier",
  "correlationId": "correlation-identifier",
  "idempotencyKey": "operation-specific-key",
  "actor": {
    "type": "user",
    "id": "actor-identifier"
  },
  "authorizationContext": {
    "scopeReference": "scope-reference",
    "approvalReference": null
  },
  "clientContext": {
    "platform": "android",
    "executionMode": "online"
  },
  "payload": {}
}

Requirements:
• requestId uniquely identifies the request.
• correlationId connects related operations.
• idempotencyKey is supplied when the operation requires retry protection.
• Actor identity must be authenticated or established through a trusted internal identity mechanism.
• Authorization context is descriptive; it does not replace authorization enforcement.
• Client context must not be trusted as proof of identity or privilege.
• Payload must contain only fields needed for the requested operation.
5.2 Response envelope
Conceptual example:
json
{
  "contractVersion": "1",
  "requestId": "request-identifier",
  "correlationId": "correlation-identifier",
  "status": "accepted",
  "data": {},
  "operationReference": null,
  "errors": [],
  "metadata": {
    "serverTimestamp": "2026-11-11T10:00:00Z"
  }
}

The response status must describe the actual state of the request.
An accepted response must not be interpreted as completed.
5.3 Request context
Where applicable, requests should support:
• Actor identity.
• Resource owner.
• Resource identifier.
• Requested action.
• Capability scope.
• Approval reference.
• Correlation identifier.
• Causation identifier.
• Idempotency key.
• Client and execution context.
• Classification and sensitivity metadata.
The server or authoritative domain must independently validate these values.
6. Identity, Authorization, and Capabilities
6.1 Actor types
The logical contract should support:
• User.
• Agent.
• Workflow.
• System service.
• Authorized integration.
Each actor must have a stable identity and a defined authorization context.
6.2 Capability-based access
Agents and Workflows must receive explicit, scoped capabilities.
A capability should define:
• Subject or actor.
• Allowed action.
• Resource or resource scope.
• Applicable constraints.
• Expiration or validity conditions.
• Delegation restrictions.
• Approval requirements, where applicable.
A capability must not automatically grant access to all resources within a domain.
6.3 Approval references
Operations requiring user approval must carry a verifiable approval reference.
The reference should identify:
• The approved operation.
• The intended resource or scope.
• The approving actor.
• The approval timestamp.
• The validity conditions.
• Any restrictions attached to the approval.
An approval for one operation must not be reused to authorize a materially different operation.
6.4 Authorization failure
Authorization failures must be explicit and auditable.
The system must not silently downgrade a prohibited operation into a broader operation.
7. Idempotency, Correlation, and Tracing
7.1 Idempotency
Operations that may be retried must define idempotency behavior.
Examples include:
• Creating a note.
• Starting a download.
• Importing a document.
• Updating metadata.
• Initiating an export.
• Creating a Vault item.
The contract must specify:
• Idempotency key scope.
• Key retention period.
• Payload consistency requirements.
• Behavior when the same key is reused with different content.
• How the original operation result is returned.
Idempotency must not be confused with exactly-once execution.
7.2 Correlation
A correlation identifier must connect related operations across domains and services.
A causation identifier should identify the immediate operation or event that caused a subsequent operation.
7.3 Traceability
Where available, trace context should be propagated across:
• API calls.
• Asynchronous operations.
• Event publication.
• Event consumption.
• Agent handoffs.
• Workflow steps.
• Synchronization.
• Import and export.
Tracing metadata must not contain secrets or unnecessary personal information.
8. Error Contract
8.1 Error envelope
Conceptual example:
json
{
  "code": "RESOURCE_ACCESS_DENIED",
  "category": "authorization",
  "message": "The requested operation is not permitted.",
  "retryable": false,
  "requestId": "request-identifier",
  "correlationId": "correlation-identifier",
  "details": {},
  "occurredAt": "2026-11-11T10:00:00Z"
}

Messages intended for users must not expose internal secrets, infrastructure details, or sensitive resource information.
8.2 Error categories

Category
Meaning

Validation
Invalid or incomplete input

Authentication
Actor identity could not be established

Authorization
Actor is not permitted to perform the operation

Not Found
Resource does not exist or is not visible to the actor

Conflict
Requested operation conflicts with current state

Rate Limit
Request exceeds applicable limits

Dependency
Required service or provider is unavailable

Timeout
Operation exceeded its allowed execution time

Unsupported
Requested capability is not supported

Integrity
Data or relationship integrity validation failed

Security
Security policy prevented the operation

Internal
Unexpected internal failure


8.3 Stable error codes
Error codes must be:
• Machine-readable.
• Stable across compatible releases.
• Documented.
• Independent of user-facing wording.
• Specific enough for reliable client handling.
Clients must not depend on parsing error message text.
8.4 Retry behavior
A retryable error does not mean every operation is safe to retry.
The client must consider:
• Idempotency support.
• Operation state.
• Retry limits.
• Backoff policy.
• Whether the previous request may already have succeeded.
9. Versioning and Compatibility
9.1 Contract versioning
Every externally consumed contract must have an identifiable version.
Versioning must cover:
• API contracts.
• Event schemas.
• Shared data structures.
• Error codes and semantics.
• Operation status definitions.
9.2 Compatibility rules
Compatible changes may include:
• Adding optional fields.
• Adding new optional metadata.
• Adding new documented error codes.
• Adding new optional capabilities.
Potentially breaking changes include:
• Removing fields.
• Changing field meaning.
• Changing required-field rules.
• Changing authorization semantics.
• Changing operation status semantics.
• Reinterpreting event payloads.
9.3 Deprecation
Deprecated contracts must have:
• A documented replacement.
• A deprecation notice.
• A migration path.
• A defined support period.
• A removal approval.
Old clients must not fail unexpectedly because of undocumented contract changes.
10. Pagination, Filtering, Sorting, and Limits
Collection APIs should support consistent query behavior where appropriate.
10.1 Pagination
The contract should support a pagination strategy appropriate to the data source.
Potential approaches include:
• Cursor-based pagination.
• Offset-based pagination.
• Continuation tokens.
The final choice remains pending technical review.
10.2 Filtering
Filters must be explicitly defined and validated.
Unsupported filter fields must return a clear error rather than being silently ignored.
10.3 Sorting
Sorting must specify:
• Supported fields.
• Direction.
• Default ordering.
• Stable tie-breaking behavior.
10.4 Limits
Every collection and operation must have appropriate limits.
Limits should consider:
• Android memory constraints.
• Network bandwidth.
• Response size.
• Processing cost.
• Sensitive data exposure.
• Resource consumption.
Secure Vault queries must not expose unrestricted bulk retrieval of secrets.
11. Asynchronous Operations
Long-running operations must use an explicit operation lifecycle.
11.1 Operation states
The conceptual state model is:
text
Requested
   |
   v
Accepted
   |
   v
Queued
   |
   v
Running
   |
   +----> WaitingForApproval
   |
   +----> WaitingForDependency
   |
   +----> Completed
   |
   +----> Failed
   |
   +----> Cancelled
   |
   +----> Conflicted

Not every operation uses every state.
11.2 Operation reference
An asynchronous response should provide an operation reference containing:
• Operation ID.
• Current status.
• Creation timestamp.
• Last update timestamp.
• Progress information, where meaningful.
• Result reference, when available.
• Error reference, when applicable.
• Cancellation capability, if supported.
11.3 Status retrieval
Clients should be able to retrieve the current operation state through an authorized status interface.
The operation owner remains responsible for determining the authoritative status.
11.4 Cancellation
Cancellation is a request, not proof that execution has stopped.
The final status must confirm whether cancellation succeeded, failed, or was no longer possible.
11.5 Progress
Progress values must be honest.
Unknown progress must be represented as unknown rather than fabricated percentages.
12. Event Contract Architecture
12.1 Purpose
Events communicate meaningful state changes and domain facts between authorized components.
Events are not a substitute for domain APIs.
An event consumer must not assume that receiving an event grants permission to access the referenced resource.
12.2 Event envelope
Conceptual example:
json
{
  "eventId": "unique-event-identifier",
  "eventType": "brain.note.updated",
  "schemaVersion": "1",
  "producer": {
    "domain": "brain",
    "service": "brain-service"
  },
  "subject": {
    "resourceType": "note",
    "resourceId": "resource-identifier"
  },
  "occurredAt": "2026-11-11T10:00:00Z",
  "publishedAt": "2026-11-11T10:00:02Z",
  "correlationId": "correlation-identifier",
  "causationId": "causation-identifier",
  "actor": {
    "type": "user",
    "id": "actor-identifier"
  },
  "classification": "internal",
  "provenanceReference": "provenance-reference",
  "payloadReference": null,
  "payload": {}
}

The envelope is conceptual and does not establish a final wire format.
12.3 Required event metadata
Events should include, where applicable:
• Globally unique event ID.
• Event type.
• Schema version.
• Producer identity.
• Subject resource type and identifier.
• Event occurrence timestamp.
• Publication timestamp.
• Correlation ID.
• Causation ID.
• Actor identity or authorized system identity.
• Classification.
• Provenance reference.
• Payload or protected payload reference.
12.4 Event payload rules
Payloads must contain only information needed by authorized consumers.
Large content should generally be referenced rather than embedded.
Sensitive data must not be placed in an event merely because the event system supports it.
Secure Vault secrets must never appear in generic event payloads.
12.5 Delivery semantics
The system must explicitly account for duplicate delivery.
The baseline design should tolerate at-least-once delivery where the selected infrastructure provides that guarantee.
Consumers must be idempotent.
The system must not claim exactly-once delivery or exactly-once processing without a demonstrated end-to-end guarantee.
12.6 Ordering
Ordering guarantees must be defined within a documented scope.
Possible scopes include:
• Per resource.
• Per aggregate.
• Per operation.
• Per synchronization stream.
Global ordering across all events must not be assumed.
Consumers must handle out-of-order events through version checks, sequence metadata, reconciliation, or authoritative state retrieval.
12.7 Retries and dead-letter handling
Failed event delivery or consumption must support:
• Bounded retries.
• Backoff.
• Failure classification.
• Dead-letter or equivalent quarantine.
• Authorized inspection.
• Safe replay.
• Duplicate detection.
• Auditability.
Replaying an event must not bypass authorization or execute unsafe side effects.
12.8 Retention and replay
Retention policies must consider:
• Data classification.
• Privacy requirements.
• Legal and operational retention.
• Storage limits.
• Replay requirements.
• Deletion obligations.
Events containing personal or sensitive information must not be retained indefinitely by default.
13. Domain API Catalogs
The following catalogs define logical capabilities. They are not final endpoint paths or approved transport-specific schemas.
13.1 Mimir Brain
Brain owns knowledge, memory, notes, collections, and retrieval-related domain records.

Logical API
Purpose

Create Knowledge Item
Create a knowledge record

Retrieve Knowledge Item
Retrieve an authorized record

Update Knowledge Item
Modify an owned record

Archive Knowledge Item
Change lifecycle state

Restore Knowledge Item
Restore where policy permits

Create Note
Create a note

Update Note
Update note content or metadata

Link Knowledge
Create an explicit knowledge relationship

Manage Collections
Create and organize collections

Manage Tags
Assign and remove tags

Retrieve Backlinks
Find authorized incoming references

Query Knowledge
Search within permitted knowledge scope

Retrieve Memory
Retrieve permitted memory records

Update Memory
Update memory under applicable memory policies

Process Knowledge
Request an AI-assisted processing operation

Retrieve Provenance
Retrieve source and processing lineage


Brain must distinguish between:
• User-provided content.
• Imported content.
• AI-generated content.
• AI-inferred content.
• Verified or reviewed information.
AI-derived information must not automatically become verified knowledge.
Brain events
Illustrative event types:
• brain.knowledge.created
• brain.knowledge.updated
• brain.knowledge.archived
• brain.note.created
• brain.note.updated
• brain.note.deleted
• brain.relationship.created
• brain.collection.updated
• brain.memory.updated
• brain.processing.requested
• brain.processing.completed
• brain.provenance.updated
Events must preserve provenance and ownership information where applicable.
13.2 Mimir Files
Files owns file-management metadata, organization, and file operations within its authorized scope.

Logical API
Purpose

List Files
List authorized file records

Retrieve File Metadata
Retrieve file information

Register File Reference
Register a file or storage reference

Update File Metadata
Update metadata

Move File
Move within permitted storage scope

Copy File
Copy where supported

Rename File
Rename where supported

Delete File Reference
Remove the Files-owned reference

Request File Operation
Request an asynchronous operation

Retrieve File Operation
Retrieve operation state

Manage Folders
Create, update, or organize folders

Resolve Storage Reference
Resolve an authorized storage reference

Retrieve File History
Retrieve available version or activity history


The API must distinguish:
• File record deletion.
• Storage-object deletion.
• Removal of a reference.
• Deletion of a derived artifact.
Deleting a Files record must not automatically delete a Brain record or Secure Vault record that references it.
Physical deletion must only be reported when confirmed by the relevant storage provider or owner.
Files events
Illustrative event types:
• files.file.registered
• files.file.metadata_updated
• files.file.moved
• files.file.copied
• files.file.renamed
• files.file.deletion_requested
• files.file.deleted
• files.folder.created
• files.folder.updated
• files.operation.started
• files.operation.completed
• files.operation.failed
13.3 Mimir Downloads
Downloads owns download requests, queue entries, jobs, and download-specific metadata.

Logical API
Purpose

Create Download Request
Submit a download request

Retrieve Download
Retrieve authorized download metadata

List Downloads
List authorized downloads

Pause Download
Request pause where supported

Resume Download
Request resume where supported

Cancel Download
Request cancellation

Retry Download
Retry a failed operation where permitted

Retrieve Queue
Retrieve queue state

Reorder Queue
Change queue ordering where supported

Retrieve Progress
Retrieve available progress

Retrieve Download Result
Retrieve the completed result

Request Media Capture
Request supported media capture

Retrieve Capture Metadata
Retrieve capture metadata


The contract must not promise:
• Resume support for every provider.
• Unlimited concurrent connections.
• Successful capture of protected content.
• DRM circumvention.
• Availability of content requiring unauthorized access.
Media Capture must comply with provider restrictions, authentication requirements, DRM, copyright, and applicable user permissions.
Downloads events
Illustrative event types:
• downloads.request.created
• downloads.request.accepted
• downloads.queue.updated
• downloads.job.started
• downloads.job.progressed
• downloads.job.paused
• downloads.job.resumed
• downloads.job.completed
• downloads.job.failed
• downloads.job.cancelled
• downloads.capture.requested
• downloads.capture.completed
• downloads.capture.rejected
Progress events should be rate-limited or aggregated where appropriate.
13.4 Mimir Secure Vault
Secure Vault owns secrets and sensitive identity-related records.
Its API surface must be isolated from generic domain interfaces.

Logical API
Purpose

Create Secure Item
Create a protected item

Retrieve Secure Item
Retrieve an authorized item

Update Secure Item
Update an item

Delete Secure Item
Request deletion under policy

Manage Secure Categories
Manage supported secure-item categories

Create Passkey
Request supported passkey creation

Use Passkey
Request an authorized passkey operation

Manage Authenticator Entry
Manage authenticator records

Generate Authentication Code
Generate a code through an authorized protected operation

Manage Secure Notes
Create, retrieve, and update secure notes

Manage Secure Documents
Manage protected document records

Manage Cards
Manage protected card records

Request Autofill
Request scoped autofill assistance

Review Security Status
Retrieve permitted security findings

Request Security Action
Initiate a protected security operation


13.4.1 Secure Vault restrictions
• Secrets must never be included in generic event payloads.
• Secrets must never be written to ordinary application logs.
• Secrets must never be indexed by Unified Search.
• Secrets must not be inserted into generic AI context or memory by default.
• References to Secure Vault records do not grant access.
• Agents must not receive unrestricted secret retrieval capabilities.
• Autofill must use narrowly scoped, user-authorized access.
• Secret retrieval must be separately authorized and auditable.
• Sensitive values must be protected in transit and at rest.
• Clipboard and temporary exposure must be handled under explicit security policies.
• Authentication and recovery flows must follow dedicated security contracts.
Secure Vault events
Events should describe state changes without disclosing secret values.
Illustrative event types:
• vault.item.created
• vault.item.updated
• vault.item.deletion_requested
• vault.item.deleted
• vault.passkey.registered
• vault.passkey.removed
• vault.authenticator.updated
• vault.autofill.requested
• vault.autofill.completed
• vault.security.alert.created
• vault.security.action.completed
Event payloads must contain identifiers and minimal metadata only.
14. Cross-Domain API and Event Contracts
14.1 General rule
Cross-domain integration must use explicit requests, references, or events.
A domain may request an operation from another domain, but the owning domain remains responsible for validation, authorization, and execution.
14.2 Example: Brain referencing Files
1. Brain receives a request to create a knowledge record from a file.
2. Brain requests authorized file metadata or content access from Files.
3. Files validates the request.
4. Files returns the permitted result or an authorized reference.
5. Brain creates its own knowledge record and provenance links.
6. Brain emits an appropriate knowledge event.
Brain does not become the owner of the original file.
14.3 Example: Downloads to Files
1. Downloads completes a download operation.
2. Downloads reports completion and the result reference.
3. Files registers or imports the file according to its contract.
4. Files confirms the result.
5. Downloads updates its own record with the permitted relationship.
A failed Files registration must not be hidden by reporting the entire workflow as successfully completed.
14.4 Example: Secure Vault Autofill
1. An authorized client requests autofill.
2. Secure Vault validates actor, context, resource, and authorization.
3. Secure Vault obtains the required approval or verifies an existing valid approval.
4. Secure Vault supplies only the minimum authorized value through a protected channel.
5. The operation is audited without recording the secret itself.
6. The temporary authorization expires according to policy.
No generic event or search index receives the secret.
15. Shared Services Interface References
The following services must expose their own approved contracts.
This specification defines their integration points but does not duplicate their complete service specifications.

Shared Service
Required Contract Responsibilities

Unified Search
Authorized query, filters, ranking, result references

Permissions & Authorization
Actor validation, policy evaluation, capability checks

Provenance & Source References
Source lineage, model contribution, transformation history

Sync & Conflict Resolution
Change exchange, version checks, conflict reporting

Activity & Audit Trail
Authorized activity recording and retrieval

Notifications
Notification creation, delivery status, user preferences

Import & Export
Job creation, progress, validation, completion

Encryption & Key Management
Protected cryptographic operations and key lifecycle

Retention & Deletion
Retention evaluation, deletion requests, verification


Shared services must not silently take ownership of domain records.
16. Model Provenance and AI Execution Contracts
All model-produced results must preserve available execution provenance across APIs, events, storage, synchronization, handoffs, and exports.
The provenance contract should preserve, where available:
• Model name and version.
• Provider.
• Local or cloud execution.
• Model role and contribution.
• Generation or execution start and completion timestamps.
• Execution status.
• Participating models.
• Final composing model, where applicable.
• Source references.
• Handoffs and intermediate contributions.
• Applicable Agent or Workflow references.
16.1 Attribution rules
• User-authored content must not be attributed to a model.
• Unknown model identity must remain unknown.
• The system must not fabricate provider or model information.
• A final composed response must not erase contributions from participating models.
• Aggregation must preserve available source and lineage information.
• Provenance must remain queryable under the applicable access policy.
16.2 API and event propagation
Where an API or event carries model-produced output, it must include or reference its provenance record.
Provenance must not be lost during:
• Domain-to-domain handoffs.
• Agent execution.
• Workflow execution.
• Synchronization.
• Storage.
• Restore.
• Export.
• Copy operations.
17. Search and Retrieval Contracts
Search APIs must respect the owning domain's authorization and classification rules.
Search results should return:
• Resource reference.
• Resource type.
• Authorized title or display metadata.
• Relevant snippet, if permitted.
• Source reference.
• Provenance reference, where available.
• Result classification.
• Version or freshness metadata, where applicable.
Search must not reveal the existence or content of inaccessible resources through snippets, counts, autocomplete, facets, or error messages.
Secure Vault secrets are excluded from general Unified Search by default.
Any future Secure Vault-specific search capability must remain isolated and explicitly authorized.
18. Synchronization and Conflict Contracts
Synchronization contracts must distinguish local changes from authoritative changes.
A synchronization request should identify:
• Client or device identity.
• Local operation identifier.
• Resource identifier.
• Base version or revision.
• Local change reference.
• Correlation identifier.
• Last known synchronization checkpoint, where applicable.
Conflict responses should identify:
• Conflicted resource.
• Conflicting versions.
• Conflict category.
• Resolution status.
• Permitted resolution actions.
The client must not silently overwrite authoritative changes.
Domain-specific merge rules belong to the owning domain.
Secure Vault synchronization requires additional security and key-management controls.
19. Audit and Notification Contracts
19.1 Audit
Operations requiring audit records must emit or submit structured audit information.
Audit records should include:
• Actor.
• Action.
• Resource reference.
• Timestamp.
• Outcome.
• Authorization context reference.
• Correlation ID.
• Source or client context.
• Applicable approval reference.
Audit records must not include passwords, passkeys, authentication codes, or other secret values.
19.2 Notifications
Notifications should communicate relevant state changes without unnecessarily disclosing sensitive content.
Notification payloads should prefer resource references and minimal display metadata.
Secure Vault notifications must not expose secret values on lock screens, system notifications, or other uncontrolled surfaces.
20. Import and Export Contracts
Import and export operations should use asynchronous operation contracts when processing may be lengthy.
The contract should support:
• Request validation.
• Authorization.
• Scope selection.
• Format identification.
• Progress reporting.
• Partial failure reporting.
• Integrity checks.
• Provenance preservation.
• Completion status.
• Cancellation where supported.
Exporting Secure Vault data requires a separate, stronger authorization and approval flow.
Importing untrusted files or structured content must not automatically execute embedded instructions or grant permissions.
21. Agent and Workflow Integration
Agents and Workflows are consumers of domain APIs and events.
They must not become alternate owners of domain data.
21.1 Agent requests
Agent operations must carry:
• Agent identity.
• Delegating actor, where applicable.
• Capability scope.
• Requested operation.
• Resource reference.
• Approval reference, where required.
• Correlation and causation identifiers.
21.2 Workflow requests
Workflow operations must preserve:
• Workflow identity.
• Workflow run identity.
• Step identity.
• Parent operation reference.
• Delegated capability.
• Approval requirements.
• Execution provenance.
21.3 Prohibited behavior
Agents and Workflows must not:
• Bypass domain authorization.
• Access private records through internal storage shortcuts.
• Reuse expired capabilities.
• Treat event delivery as authorization.
• Convert an unapproved action into an approved one.
• Hide failures or conflicts.
• Write directly into another domain's private data store.
21.4 Human approval
Where approval is required, the workflow must pause and obtain the required approval.
The operation must not proceed merely because an Agent recommends it.
22. Android, Offline, and Mobile Execution
22.1 Offline request behavior
When connectivity is unavailable, a client may queue an operation only if the operation is explicitly eligible for offline execution.
The client must identify the operation as locally queued.
It must not display a remote success state before synchronization confirms it.
22.2 Local operation records
An offline-capable operation should preserve:
• Local operation ID.
• Resource reference.
• Local timestamp.
• Required authorization context.
• Idempotency key.
• Base version.
• Synchronization state.
• Retry information.
Sensitive values must not be stored in an unprotected local queue.
22.3 Process termination
The system must recover safely after:
• Application process termination.
• Device restart.
• Network loss.
• Partial synchronization.
• Interrupted upload or download.
• Interrupted asynchronous execution.
The client must reconcile local state with authoritative domain state before assuming completion.
22.4 Offline restrictions
Offline execution must not bypass security policies.
Sensitive Secure Vault operations may require an online authorization or a specifically approved offline security model.
That model remains subject to separate security review.
23. Security and Privacy Requirements
All API and event implementations must enforce:
1. Authentication before protected operations.
2. Authorization at the owning domain.
3. Least-privilege capability scopes.
4. Payload minimization.
5. Secure transport and storage.
6. Classification-aware access.
7. Input validation and output encoding.
8. Rate limiting and abuse protection.
9. Audit of sensitive actions.
10. Protection against replay and request tampering.
11. Safe handling of untrusted content.
12. Appropriate retention and deletion.
13. Secure error responses.
14. Protection against cross-domain privilege escalation.
23.1 Untrusted content
Files, imported documents, web content, downloaded media metadata, and externally supplied text must be treated as untrusted input.
Embedded instructions must not be interpreted as trusted system instructions.
Prompt injection must not grant access to resources, change capabilities, or bypass approval.
23.2 Logging
Logs must exclude:
• Passwords.
• Passkeys and private key material.
• Authentication codes.
• Recovery secrets.
• Full payment credentials.
• Unnecessary personal data.
• Sensitive document contents.
Redaction must be applied before data reaches ordinary logging systems.
23.3 Data deletion
API responses must distinguish between:
• Deletion requested.
• Deletion accepted.
• Logical deletion completed.
• Physical deletion verified.
• Backup or retention cleanup pending.
The system must not report permanent deletion without sufficient evidence.
24. Illustrative Sequence Diagrams
These diagrams illustrate logical flows only. They do not prescribe a final transport or deployment architecture.
24.1 Brain requests a file-derived knowledge record
mermaid
sequenceDiagram
    actor User
    participant Brain
    participant Auth as Authorization
    participant Files
    participant Provenance

    User->>Brain: Request knowledge creation from file
    Brain->>Auth: Validate actor and capability
    Auth-->>Brain: Authorized scope
    Brain->>Files: Request authorized file access
    Files->>Auth: Validate file access
    Auth-->>Files: Access decision
    Files-->>Brain: Authorized file reference/content
    Brain->>Brain: Process and create knowledge record
    Brain->>Provenance: Record source lineage
    Provenance-->>Brain: Provenance reference
    Brain-->>User: Return operation/result

24.2 Asynchronous download
mermaid
sequenceDiagram
    actor User
    participant Client
    participant Downloads
    participant Events

    User->>Client: Submit download request
    Client->>Downloads: Create request with idempotency key
    Downloads-->>Client: Accepted + operation reference
    Downloads->>Events: Publish job started
    Downloads->>Downloads: Execute download
    Downloads->>Events: Publish progress
    Downloads->>Events: Publish completed or failed
    Client->>Downloads: Retrieve operation status
    Downloads-->>Client: Authoritative operation status

24.3 Secure Vault autofill
mermaid
sequenceDiagram
    actor User
    participant Client
    participant Vault
    participant Auth as Authorization
    participant Audit

    User->>Client: Request autofill
    Client->>Vault: Submit scoped request
    Vault->>Auth: Validate capability and approval
    Auth-->>Vault: Authorization decision
    Vault->>Vault: Retrieve protected value
    Vault->>Audit: Record metadata-only audit
    Vault-->>Client: Protected scoped response
    Client-->>User: Complete authorized autofill

No secret value is included in the audit or generic event stream.
25. Observability and Diagnostics
API and event infrastructure should expose operational metrics without exposing sensitive payloads.
Relevant metrics include:
• Request volume.
• Request latency.
• Error rates by stable error code.
• Authorization denials.
• Operation completion rates.
• Queue depth.
• Event publication failures.
• Consumer processing failures.
• Duplicate event detection.
• Retry counts.
• Dead-letter volume.
• Synchronization lag.
• Conflict frequency.
Metrics must be aggregated and access-controlled.
Diagnostic traces must preserve correlation while excluding secrets.
26. Acceptance Criteria
The specification is ready for implementation review when:
• [ ] Every API has an identified owning domain or service.
• [ ] No domain writes directly into another domain's private records.
• [ ] Common request and response semantics are defined.
• [ ] Identity and authorization are explicit.
• [ ] Capability scopes are enforced.
• [ ] Approval references are verifiable.
• [ ] Idempotency behavior is documented for retryable operations.
• [ ] Stable error codes are defined.
• [ ] API and event versioning rules are established.
• [ ] Asynchronous operation states are consistent.
• [ ] Event envelopes preserve correlation and causation.
• [ ] Duplicate delivery is handled safely.
• [ ] Ordering guarantees are explicitly scoped.
• [ ] Retry, quarantine, and replay policies are documented.
• [ ] Domain event catalogs are reviewed by domain owners.
• [ ] Secure Vault contracts are isolated.
• [ ] Secrets are excluded from generic logs, events, and search.
• [ ] Model provenance is preserved across handoffs.
• [ ] Offline behavior is distinguishable from remote completion.
• [ ] Cross-domain authorization is independently validated.
• [ ] API and event schemas pass contract validation.
• [ ] Security and privacy reviews are complete.
27. Test Scenarios
27.1 API contract tests
• Valid request returns the documented response envelope.
• Invalid payload returns a stable validation error.
• Unknown fields follow the documented compatibility policy.
• Unauthorized actors cannot retrieve protected resources.
• An expired capability is rejected.
• A reused idempotency key does not create duplicate operations.
• Reusing a key with conflicting payload content is handled explicitly.
• Pagination does not expose unauthorized resources.
27.2 Event contract tests
• Event IDs are unique.
• Required metadata is present.
• Schema versions are validated.
• Duplicate events do not duplicate side effects.
• Out-of-order events do not corrupt resource state.
• Failed consumers follow the documented retry policy.
• Replay does not bypass authorization.
• Dead-letter records are inspectable only by authorized operators.
27.3 Cross-domain tests
• Brain cannot directly modify Files-owned records.
• Files deletion does not silently delete Brain-owned knowledge.
• Downloads completion is not falsely reported as successful Files registration.
• Search does not reveal inaccessible records.
• Provenance survives domain handoffs.
• Event references do not grant access automatically.
27.4 Secure Vault tests
• Generic APIs cannot retrieve Vault secrets.
• Generic events contain no secret values.
• Logs contain no credentials.
• Unauthorized autofill is rejected.
• Expired approval cannot authorize autofill.
• Agent access is limited to the granted scope.
• Secret references do not reveal secret content.
• Sensitive deletion status is accurately reported.
27.5 Offline tests
• Queued operations remain visibly pending.
• Process termination does not lose recoverable operation state.
• Repeated synchronization does not create duplicate resources.
• Version conflicts are reported.
• A remote failure does not appear as local success.
• Sensitive queued data remains protected.
28. Open Architectural Decisions
The following decisions remain pending and must not be assumed to be approved:
1. API transport strategy.
2. Internal versus external API boundary.
3. API schema and interface-definition format.
4. Event transport and broker strategy.
5. Event persistence and replay architecture.
6. Event ordering scope.
7. Idempotency key storage and retention.
8. Operation status storage and lifecycle retention.
9. Pagination strategy.
10. Offline queue persistence architecture.
11. Event schema registry and compatibility enforcement.
12. Authentication token and service identity model.
13. Capability issuance, delegation, and revocation.
14. Secure Vault protected API channel.
15. Event encryption and payload-reference strategy.
16. Cross-device synchronization protocol.
17. API rate-limit policies.
18. Observability and tracing implementation.
19. Contract testing and CI enforcement strategy.
20. Formal ownership and approval process for shared contracts.
Each decision must be reconciled with the governing architecture documents and relevant ADRs before implementation.
29. Implementation and Verification Boundary
This document is a proposed architectural contract specification.
It does not establish that:
• APIs have been implemented.
• Event infrastructure exists.
• Any domain supports every listed operation.
• Offline synchronization is operational.
• Event delivery guarantees have been verified.
• Secure Vault isolation has been implemented.
• Model provenance is preserved in current code.
• Contract tests have passed.
Implementation evidence must be collected separately.
Each implemented contract should be linked to:
• Its approved schema.
• Owning module.
• Implementation reference.
• Contract tests.
• Security review.
• Compatibility policy.
• Verification results.
Stubs must fail honestly when functionality is unavailable. They must not return fabricated successful results.
30. Governance and Change Control
Changes to this specification must be reviewed for their effects on:
• Domain ownership.
• Shared service contracts.
• Data model relationships.
• Authorization boundaries.
• Security and privacy.
• Provenance.
• Offline synchronization.
• Existing clients and integrations.
Breaking changes require explicit review and migration planning.
No domain may independently redefine a shared contract in a way that creates incompatible interpretations across Mimir.
31. Final Status
Status: Draft — Pending Architectural & API Review
This document establishes the proposed logical API and event contract architecture for Mimir Intelligence.
It is not an implementation report, approved transport specification, or verification record.
Official Document ID, version, and approval status must be assigned through the Mimir document registry and governance process.

───

End of Document

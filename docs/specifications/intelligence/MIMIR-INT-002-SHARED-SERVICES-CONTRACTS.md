---
document_id: MIMIR-DOC-REG-INT-002
document_code: MIMIR-INT-002
filename: MIMIR-INT-002-SHARED-SERVICES-CONTRACTS.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

Mimir Intelligence — Shared Services Contracts
Document ID: MIMIR-DOC-REG-INT-002
Status: Draft — For Architectural Review
Domain: Mimir Intelligence
Document Type: Shared Services & Contracts Specification
Architecture Alignment: Kernel-first, Domain Ownership, Provider-neutral
Platform Priority: Android-first, Mobile-first

───

1. Purpose
This document defines the shared platform services and integration contracts used by the four Mimir Intelligence domains:
1. Mimir Brain.
2. Mimir Files.
3. Mimir Downloads.
4. Mimir Secure Vault.
The purpose is to provide consistent infrastructure behavior without merging domain ownership or introducing duplicated implementations.
Shared services provide reusable capabilities for:
• Search.
• Permissions and authorization.
• Provenance and source references.
• Synchronization and conflict resolution.
• Activity and audit trail.
• Notifications.
• Import and export coordination.
• Encryption and key management.
• Retention and deletion policies.
These capabilities are shared contracts and services. They are not additional Intelligence domains and are not new Engines.
2. Architectural Principles
2.1 Domain Ownership Remains Independent
Each domain owns its data, lifecycle, validation rules, and business semantics.
A shared service may coordinate an operation, but must not become the owner of domain records.
2.2 Contract-first Integration
Domains communicate through explicit, versioned contracts.
No domain should directly depend on another domain's internal database schema or private implementation.
2.3 Least Privilege
Every cross-domain operation must use an explicit authorization context with the minimum required scope.
2.4 Provider-neutral Infrastructure
Shared services must not depend on a specific storage, database, vector engine, cloud provider, encryption vendor, or search engine.
Providers are replaceable behind stable contracts.
2.5 Kernel Neutrality
The Mimir Kernel must remain generic and engine-agnostic.
It must not contain Intelligence-specific search rules, file semantics, Vault policies, or domain-specific conditionals.
2.6 Failure Transparency
Every contract must define:
• Success.
• Failure.
• Partial success.
• Pending execution.
• Retry eligibility.
• Idempotency behavior.
• Recovery expectations.
A service must never convert an unknown result into a success claim.
3. Service Architecture
3.1 Shared Services

Service
Primary Responsibility

Unified Search
Coordinate authorized search across domains

Permissions & Authorization
Evaluate and enforce access decisions

Provenance & Source References
Preserve origin and relationship information

Sync & Conflict Resolution
Coordinate synchronization and conflicts

Activity & Audit Trail
Record events and security-relevant actions

Notifications
Deliver user-facing alerts

Import & Export
Coordinate controlled data movement

Encryption & Key Management
Provide cryptographic infrastructure contracts

Retention & Deletion
Coordinate lifecycle and deletion policies


3.2 Service Boundaries
A shared service must not:
• Own domain-specific content.
• Make domain-specific lifecycle decisions without the domain owner.
• Expose protected data to unauthorized consumers.
• Bypass a domain's security policies.
• Assume all providers support identical capabilities.
• Become an independent Engine merely because multiple domains use it.
3.3 Logical vs. Physical Deployment
The contracts define logical responsibilities.
Implementation may use:
• Shared libraries.
• Internal services.
• Platform APIs.
• Local databases.
• Background workers.
• Provider adapters.
The physical deployment model is a separate technical decision.
4. Common Contract Conventions
All shared contracts should follow consistent conventions.
4.1 Identifiers
Use stable identifiers for:
• Domain.
• Record.
• Operation.
• Actor.
• Provider.
• Resource.
• Authorization grant.
• Event.
• Correlation.
• Idempotency.
Identifiers must not be derived from sensitive values.
4.2 Timestamps
Timestamp fields must use a consistent machine-readable representation, preferably UTC timestamps.
The UI converts them to the user's local timezone.
A timestamp must distinguish between:
• Event occurrence.
• Request creation.
• Processing start.
• Processing completion.
• Last observed state.
Unknown timestamps must remain unknown.
4.3 Contract Versioning
Every externally consumed contract must have a version.
Changes must follow compatibility rules:
• Additive optional fields may be backward-compatible.
• Removing or changing field meaning requires a breaking version.
• Enum expansion must be handled safely by consumers.
• Unknown fields should not cause unnecessary failures.
• Consumers must not assume that an unknown status means success.
4.4 Common Operation Result
All operations should use a consistent result envelope.
Conceptual structure:
• operation_id
• status
• result
• error
• partial_result
• retry
• timestamps
• correlation_id
Possible statuses:
• accepted
• pending
• running
• succeeded
• partially_succeeded
• failed
• cancelled
• blocked
• unknown
A result may be successful for one stage and pending or failed for another.
4.5 Idempotency
Operations that may be retried must define idempotency behavior.
An idempotency key should identify the logical request, not merely the network attempt.
Examples:
• Import request.
• File transfer.
• Cross-domain registration.
• Export request.
• Deletion request.
• Notification dispatch.
A repeated request must not silently create duplicate records or destructive side effects.
5. Unified Search Contract
5.1 Purpose
Unified Search provides one search entry point across authorized Mimir Intelligence domains.
It coordinates search without transferring ownership of domain indexes or records.
5.2 Searchable Domains
Potential sources:
• Brain knowledge records.
• Files metadata.
• Downloads history metadata.
• Secure Vault safe metadata, subject to strict policy.
Secure Vault secret values must never enter general search.
5.3 Search Request
Conceptual fields:
• query
• requester
• scope
• domain_filters
• type_filters
• time_range
• sort
• pagination
• authorization_context
• correlation_id
5.4 Search Result
Each result should contain:
• Stable result reference.
• Owning domain.
• Record reference.
• Display title.
• Safe preview, if authorized.
• Result type.
• Matching metadata.
• Source reference.
• Relevance information, if available.
• Access status.
• Provenance summary.
A search result must not expose protected content simply because its metadata matched.
5.5 Search Pipeline
A conceptual pipeline:
1. Receive query.
2. Authenticate requester.
3. Determine permitted search scope.
4. Resolve domain availability.
5. Dispatch domain-specific search requests.
6. Normalize authorized results.
7. Deduplicate according to explicit identity rules.
8. Rank or group results.
9. Return partial or complete results.
10. Preserve source references.
5.6 Search Strategies
The architecture may support:
• Exact matching.
• Lexical search.
• Metadata search.
• Full-text search.
• Semantic search.
• Hybrid search.
• Graph-assisted retrieval.
• Reranking.
Each strategy is optional and provider-independent.
Search must not require one specific vector database or search engine.
5.7 Search Boundaries
• Brain owns knowledge search semantics.
• Files owns file metadata search.
• Downloads owns download-history search.
• Secure Vault owns protected record search.
• Unified Search coordinates requests and results.
5.8 Partial Search
If a domain is unavailable, return available results with an explicit partial-search indicator.
The UI must not imply that all domains were searched successfully.
5.9 Search Privacy
Search logs must not unnecessarily retain sensitive query content.
Queries that may contain secrets must be handled according to the applicable privacy policy.
6. Permissions & Authorization Contract
6.1 Purpose
Provide a common authorization framework for user actions, domain access, agents, workflows, and integrations.
Authorization infrastructure is shared, while each domain retains responsibility for its own resource policies.
6.2 Authorization Subjects
Possible subjects:
• User.
• Device.
• Application component.
• Agent.
• Workflow.
• External integration.
• System process.
Every subject must have a stable identity and applicable trust context.
6.3 Authorization Resources
Resources may include:
• Domain records.
• Files and folders.
• Knowledge objects.
• Memory records.
• Download tasks.
• Vault records.
• Storage locations.
• Export destinations.
6.4 Actions
Examples:
• discover
• read_metadata
• read_content
• create
• update
• delete
• restore
• export
• share
• autofill
• execute
• process_with_ai
Actions must be defined with clear semantics and must not be treated as interchangeable.
6.5 Authorization Context
A request should include:
• Subject identity.
• Resource identity.
• Requested action.
• Scope.
• Purpose.
• Execution environment.
• User approval reference, if required.
• Expiration.
• Correlation ID.
6.6 Authorization Decisions
Possible decisions:
• Allow.
• Deny.
• Require user approval.
• Require re-authentication.
• Require additional permission.
• Temporarily unavailable.
Unknown authorization outcomes must fail closed for sensitive operations.
6.7 Scoped Grants
A grant must specify:
• Subject.
• Resource or resource scope.
• Allowed actions.
• Purpose.
• Validity period.
• Delegation policy.
• Revocation state.
A grant for one file must not grant access to its entire folder or provider.
6.8 Revocation
Revocation must prevent future authorized access.
Where immediate termination of an active operation cannot be guaranteed, the system must disclose that limitation and terminate or constrain the operation as soon as safely possible.
6.9 Agent and Workflow Authorization
Agents and Workflows must use delegated, scoped permissions.
They must not inherit unrestricted user authority.
A workflow step must not silently expand its permissions beyond the approved execution scope.
6.10 Domain Enforcement
A shared authorization decision does not replace domain-specific validation.
The owning domain must validate its own rules before performing the operation.
7. Provenance & Source References Contract
7.1 Purpose
Preserve the origin, lineage, and relationships of information and artifacts across Mimir.
7.2 Provenance Record
Potential fields:
• Provenance ID.
• Origin type.
• Origin reference.
• Origin provider.
• Source location.
• Creating actor.
• Acquisition method.
• Creation timestamp.
• Processing steps.
• Transformation references.
• Related operation IDs.
• Parent provenance references.
• Integrity information, if available.
7.3 Source Reference
A source reference should identify:
• Owning domain.
• Resource ID.
• Resource type.
• Provider reference, when applicable.
• Access requirements.
• Availability state.
• Provenance reference.
A reference is not a copy of the resource and does not grant access.
7.4 Lineage
The system should preserve meaningful transformations.
Example:
Original file → extracted text → knowledge record → AI summary.
Each stage must identify its source and transformation where known.
7.5 Provenance Integrity
The system must not:
• Invent missing origins.
• Attribute user-written content to a model.
• Claim verification without evidence.
• Remove source lineage during export or synchronization when preservation is required.
• Treat a derived summary as the original source.
7.6 Model and Execution Provenance
Model-produced content must preserve the platform-wide Model Provenance & Execution Metadata Contract.
Where available, metadata includes:
• Model identity and version.
• Provider.
• Local or cloud execution.
• Role and contribution.
• Generation or execution timestamps.
• Completion status.
• Participating models.
• Final synthesizing model.
• Source references.
• Handoffs.
Shared provenance infrastructure must preserve this information through storage, synchronization, restoration, and export.
8. Sync & Conflict Resolution Contract
8.1 Purpose
Provide consistent synchronization primitives for domains and supported storage providers.
8.2 Ownership
Shared Sync manages coordination.
Each domain defines:
• Its synchronization unit.
• Its consistency requirements.
• Its merge rules.
• Its conflict semantics.
• Its deletion behavior.
8.3 Sync State
Common conceptual states:
• Local Only.
• Pending Upload.
• Pending Download.
• Syncing.
• Synchronized.
• Conflict.
• Failed.
• Offline.
• Permission Required.
• Provider Unavailable.
8.4 Sync Operation
A sync request may include:
• Sync operation ID.
• Domain.
• Resource scope.
• Source and destination.
• Local revision.
• Remote revision.
• Last synchronization checkpoint.
• Conflict policy.
• Authorization context.
• Idempotency key.
8.5 Conflict Types
Examples:
• Concurrent updates.
• Delete vs. update.
• Rename collision.
• Provider revision mismatch.
• Stale cached state.
• Permission revocation.
• Duplicate creation.
• Incompatible schema versions.
8.6 Conflict Resolution
Possible strategies:
• Keep local.
• Keep remote.
• Merge.
• Create a new copy.
• Preserve both versions.
• Ask the user.
• Reject unsafe automatic resolution.
The domain owner defines which strategies are valid.
8.7 Offline-first
Offline capability depends on the domain and provider.
The UI must distinguish locally available data from remote-only data.
An offline operation must not be presented as synchronized merely because it was queued.
8.8 Connectivity Separation
Connectivity status and synchronization status are separate concepts.
A device may be online while a particular provider is unavailable.
A device may be offline while local operations continue.
Neither state should be used as a substitute for the other.
9. Activity & Audit Trail Contract
9.1 Purpose
Provide a consistent event model for user-visible activity and security-relevant audit records.
9.2 Event Categories
• User actions.
• Domain operations.
• Provider operations.
• Synchronization events.
• Authorization decisions.
• Security events.
• Agent and workflow actions.
• Import and export events.
• Recovery and deletion events.
9.3 Event Structure
Potential fields:
• Event ID.
• Event type.
• Owning domain.
• Actor.
• Target reference.
• Operation ID.
• Correlation ID.
• Timestamp.
• Outcome.
• Safe summary.
• Provenance reference.
• Security classification.
9.4 Activity vs. Audit
Activity is the user-facing history of relevant work.
Audit is the security and accountability record.
They may share event identifiers but must have separate visibility and retention policies.
9.5 Sensitive Data
Audit events must not contain:
• Password values.
• Secret seeds.
• Private keys.
• Card security codes.
• Plaintext protected documents.
• Authentication tokens.
• Other unnecessary sensitive payloads.
9.6 Event Ordering
Events must preserve their actual timestamps and correlation relationships.
The UI may group events by operation but must not invent chronological order when timestamps are missing or ambiguous.
9.7 Audit Integrity
Security-sensitive audit records should be protected against unauthorized modification and deletion.
The implementation must define the appropriate integrity and retention mechanism.
10. Notifications Contract
10.1 Purpose
Deliver relevant user-facing information about domain operations and security conditions.
10.2 Notification Types
• Informational.
• Progress.
• Completion.
• Warning.
• Failure.
• Security Alert.
• User Action Required.
10.3 Notification Payload
Potential fields:
• Notification ID.
• Event reference.
• Category.
• Priority.
• Safe title.
• Safe description.
• Related domain.
• Related resource reference.
• Creation timestamp.
• Expiration policy.
• Action references.
• Read state.
10.4 Privacy
Notification content must respect the resource's security classification.
Sensitive values must not appear in:
• Lock-screen notifications.
• Ordinary notification previews.
• Wearable or external notification surfaces.
• Notification analytics.
10.5 Delivery Semantics
The system must distinguish:
• Created.
• Queued.
• Delivered to platform.
• Displayed, if known.
• Read, if known.
• Dismissed.
A notification being submitted to the operating system does not prove that the user saw it.
10.6 User Preferences
Users may configure categories, priority, and presentation behavior.
Security-critical notifications may have separate mandatory delivery policies, subject to platform capabilities.
11. Import & Export Contract
11.1 Purpose
Coordinate controlled data movement across Mimir domains and supported external providers.
11.2 Import Request
Potential fields:
• Import operation ID.
• Source reference.
• Source format.
• Target domain.
• Target collection or destination.
• Requested operation type.
• Authorization context.
• Validation requirements.
• Idempotency key.
11.3 Import Modes
• Register a reference.
• Copy original content.
• Import structured records.
• Import a domain archive.
• Transform content into a domain-owned representation.
The selected mode must be explicit.
11.4 Export Request
Potential fields:
• Export operation ID.
• Source domain.
• Source resource references.
• Destination.
• Output format.
• Transformation options.
• Security classification.
• Authorization context.
• Expiration policy.
11.5 Validation
Before import or export:
• Validate permissions.
• Validate supported format.
• Validate destination.
• Check applicable domain policies.
• Identify sensitive content.
• Identify transformation limitations.
• Confirm destructive or externally visible actions where required.
11.6 Import and Export Boundaries
The shared service coordinates the operation.
The source domain controls what may be exported.
The destination domain controls what may be imported.
11.7 Temporary Data
Temporary files and intermediate representations must have:
• Defined storage location.
• Access restrictions.
• Retention limit.
• Cleanup behavior.
• Failure recovery policy.
11.8 Partial Success
A multi-record import or export must report per-item outcomes.
A partially completed operation must not be represented as fully completed.
12. Encryption & Key Management Contract
12.1 Purpose
Provide common cryptographic infrastructure contracts without weakening domain-specific security boundaries.
12.2 Responsibilities
Potential shared capabilities:
• Secure key generation.
• Key storage integration.
• Key wrapping and unwrapping.
• Cryptographic operation interfaces.
• Key version tracking.
• Rotation coordination.
• Encryption metadata.
• Key access auditing.
• Hardware-backed capability reporting.
12.3 Domain Ownership
The shared cryptographic service does not automatically own every domain's encryption policy.
Secure Vault retains its own protected-record and key-lifecycle requirements.
Other domains may use encryption according to their classification and policies.
12.4 Key References
A key reference should identify:
• Key ID.
• Key version.
• Key purpose.
• Owning domain or security scope.
• Provider.
• Availability state.
• Allowed operations.
A key reference must not expose the key material.
12.5 Key Access
Key operations require authorization.
The system must enforce:
• Scope.
• Purpose.
• Expiration.
• Caller identity.
• Key status.
• Applicable domain policy.
12.6 Key Rotation
Rotation must support:
• New key version creation.
• Controlled migration.
• Compatibility during migration.
• Failure recovery.
• Completion verification.
• Retirement of old key versions when safe.
12.7 Cryptographic Failure
If a key is unavailable or invalid:
• Protected access must fail closed.
• No plaintext fallback is permitted.
• The error must remain distinguishable from missing data.
• Recovery guidance must be provided where possible.
12.8 Capability Reporting
Hardware-backed storage, secure enclaves, platform keystores, and other security features must be reported according to actual platform capability.
No capability may be assumed across all devices.
13. Retention & Deletion Contract
13.1 Purpose
Coordinate lifecycle, retention, deletion, and restoration behavior across domains.
13.2 Ownership
Each domain defines its retention and deletion semantics.
The shared service coordinates policies and operations without overriding domain-specific rules.
13.3 Lifecycle States
Possible common states:
• Active.
• Archived.
• Marked for Deletion.
• Deletion Pending.
• Deleted from Active Store.
• Provider Deletion Pending.
• Retained by Policy.
• Backup Expiration Pending.
• Permanently Deleted, Verified.
• Deletion Status Unknown.
13.4 Deletion Request
A deletion request may include:
• Request ID.
• Resource reference.
• Owning domain.
• Requested scope.
• Initiating actor.
• Authorization context.
• Retention policy.
• Dependency assessment.
• Confirmation reference.
• Requested timestamp.
13.5 Cross-Domain Deletion
Deleting one resource must not silently delete all resources that reference it.
Each owning domain must determine how its dependent records are handled.
Example:
Deleting an original file may cause Brain to mark its source reference unavailable, but must not automatically delete a Brain note that contains independently owned knowledge.
13.6 Dependencies
Before deletion, identify where possible:
• References.
• Derived artifacts.
• Cached copies.
• Backups.
• Exports.
• Provider-managed versions.
• Active operations.
The system must distinguish known dependencies from unverified or undiscoverable copies.
13.7 Permanent Deletion
Permanent deletion may require:
• Active record deletion.
• Provider deletion confirmation.
• Backup expiration.
• Temporary-file cleanup.
• Key destruction, where applicable.
• Verification evidence.
If all copies cannot be verified, the system must report the remaining uncertainty.
13.8 Retention Exceptions
Retention may be required by:
• User policy.
• Domain policy.
• Legal obligations.
• Security requirements.
• Provider limitations.
Exceptions must be explicit, scoped, and auditable.
13.9 Restoration
Restoration must respect:
• Current authorization.
• Record ownership.
• Provider capability.
• Current schema.
• Related deletion state.
• Conflict policy.
Restoration must not silently reintroduce protected data or override a later user decision.
14. Cross-Service Coordination
14.1 General Rule
Shared services may coordinate multi-step operations, but each stage remains accountable to its owning domain.
14.2 Example: Importing a Document into Brain
1. User selects a document through Files.
2. Files verifies access to the original.
3. Authorization service evaluates the requested content access.
4. Brain accepts a processing request.
5. Brain extracts and stores its own derived representation.
6. Provenance service records the original source and transformation.
7. Activity records the operation.
8. Notifications report completion or partial failure.
Failure in Brain processing must not delete or corrupt the original file.
14.3 Example: Completed Download
1. Downloads completes a transfer.
2. Destination provider confirms the result.
3. Downloads reports the resulting file reference.
4. Files registers or refreshes its file-management record.
5. Provenance preserves the download source.
6. Activity records each relevant stage.
Transfer completion and file registration remain separate outcomes.
14.4 Example: Secure Vault Autofill
1. An authorized application requests a credential.
2. Secure Vault identifies a candidate record.
3. Authorization validates the origin and requested action.
4. Secure Vault applies its authentication policy.
5. The minimum required value is released through the approved mechanism.
6. Activity records a safe event.
7. Temporary access is terminated according to policy.
Unified Search and ordinary Agents must not receive the secret value.
14.5 Example: Deleting a Source File
1. Files receives the deletion request.
2. Files checks the source provider's deletion capability.
3. Shared lifecycle coordination identifies known references.
4. Brain determines how its own derived records should respond.
5. Files executes the authorized provider operation.
6. Provenance references are updated to reflect source unavailability.
7. Activity and audit records preserve the outcome.
8. The user receives an accurate result, including unresolved dependencies.
15. Offline and Resilience Requirements
Shared services must support defined behavior when:
• The device is offline.
• A provider is unavailable.
• A service is temporarily unavailable.
• A permission is revoked.
• A synchronization conflict occurs.
• A background operation is interrupted.
• A device restarts.
Requirements:
• Persist operations only when safe.
• Preserve operation identity across retries.
• Avoid duplicate side effects.
• Expose pending and unknown outcomes.
• Fail closed for protected access.
• Recover without inventing successful results.
16. Security and Privacy Requirements
Mandatory requirements:
1. Least-privilege access.
2. Domain ownership enforcement.
3. Sensitive metadata protection.
4. Safe logging.
5. Scoped delegation.
6. Revocation handling.
7. Secure transport where applicable.
8. Data classification.
9. Retention enforcement.
10. Audit integrity.
11. Provider trust boundaries.
12. Explicit user approval for sensitive operations.
13. No automatic cross-domain permission inheritance.
14. No automatic exposure of Vault secrets.
15. No silent transfer of sensitive content to cloud services.
Shared services must not become a universal data access bypass.
17. Observability and Diagnostics
Operational diagnostics may include:
• Operation IDs.
• Correlation IDs.
• Service health.
• Provider availability.
• Latency.
• Retry counts.
• Error categories.
• Queue status.
• Sync status.
Diagnostics must not expose protected payloads, secrets, or unnecessary personal information.
Logs should be structured and should use stable identifiers rather than embedding sensitive values.
18. Contract Testing Requirements
Every shared service must provide:
18.1 Contract Tests
Verify:
• Request validation.
• Response schema.
• Version compatibility.
• Error semantics.
• Idempotency.
• Authorization behavior.
18.2 Security Tests
Verify:
• Unauthorized access denial.
• Scope enforcement.
• Revocation behavior.
• Sensitive-data isolation.
• Logging safety.
• Fail-closed behavior.
18.3 Failure Tests
Verify:
• Timeouts.
• Provider outages.
• Partial success.
• Duplicate requests.
• Interrupted operations.
• Recovery.
• Unknown outcomes.
18.4 Cross-Domain Tests
Verify that:
• Domain ownership remains intact.
• References do not grant access.
• Shared services do not bypass domain policies.
• Provenance survives handoffs.
• Deletion does not silently destroy independently owned data.
19. Acceptance Criteria
The shared services architecture is acceptable when:
1. Each service has a defined versioned contract.
2. Domain ownership is preserved.
3. Cross-domain operations are explicitly authorized.
4. Unified Search respects domain-specific visibility.
5. Secure Vault values remain excluded from general search.
6. Provenance is preserved across transformations and handoffs.
7. Sync state is distinct from connectivity state.
8. Conflicts are handled according to domain policies.
9. Activity and audit are distinguishable.
10. Notifications do not leak protected content.
11. Import and export respect source and destination policies.
12. Encryption and key references do not expose key material.
13. Retention and deletion semantics are explicit.
14. Cross-domain deletion is coordinated without implicit cascading.
15. Partial operations are reported honestly.
16. Idempotency prevents unintended duplicate side effects.
17. Offline behavior is explicit.
18. Shared services do not introduce Engine-specific logic into Kernel.
19. Provider implementations remain replaceable.
20. Contract tests cover success, failure, security, and recovery.
20. Open Architectural Decisions
Pending decisions include:
• Contract serialization format.
• Internal service communication protocol.
• Event bus or messaging strategy.
• Search federation and ranking strategy.
• Authorization policy representation.
• Token and grant lifecycle.
• Provenance graph representation.
• Sync protocol.
• Conflict-resolution framework.
• Audit storage and integrity mechanism.
• Notification delivery architecture.
• Import/export job orchestration.
• Key management provider strategy.
• Retention policy evaluation mechanism.
• Cross-domain deletion coordination.
• Offline operation persistence.
• Contract compatibility and migration strategy.
These decisions must be evaluated against Mimir's Android-first constraints, provider portability, security requirements, and Kernel neutrality.
21. Implementation and Verification Boundary
This document defines shared service responsibilities and logical contracts.
It does not confirm that:
• Any shared service has been implemented.
• A particular database or event bus has been selected.
• Search federation is operational.
• Authorization enforcement is complete.
• Provenance survives every current data path.
• Synchronization works across providers.
• Audit records are tamper-resistant.
• Encryption or key rotation is implemented.
• Permanent deletion has been verified.
Implementation claims require code inspection, contract tests, security tests, and evidence from the actual supported runtime.
22. Related Documents
• Mimir Blueprint.
• Mimir Kernel Architecture.
• Mimir Intelligence — Architecture & Domain Boundaries.
• Mimir Brain — Functional & UX Specification.
• Mimir Files — Functional & UX Specification.
• Mimir Downloads — Functional & UX Specification.
• Mimir Secure Vault — Functional, UX & Security Specification.
• Mimir Intelligence Integration Specification.
• Global Model Provenance & Execution Metadata Contract.
• Mimir Application Shell Specification.
• Mimir Design Tokens & Component Library Specifications.
• Security Threat Model.
• Cryptographic Architecture Decision Records.

───

End of Document
Status: Draft — Pending architectural reconciliation, contract review, document registration, and implementation planning.

---
document_id: MIMIR-DOC-REG-INT-014
document_code: MIMIR-INT-014
filename: MIMIR-INT-014-RETENTION-DELETION-AND-DATA-LIFECYCLE-POLICY.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

───

Mimir Intelligence — Retention, Deletion & Data Lifecycle Policy

Document Control

Field| Value
Document Title| Mimir Intelligence — Retention, Deletion & Data Lifecycle Policy
Document ID| "MIMIR-DOC-REG-INT-014"
Version| Draft — Initial Policy Consolidation
Status| Draft — Pending Governance & Existing-Document Reconciliation
Product| Mimir Intelligence
Scope| Brain, Files, Downloads, Secure Vault, and shared services
Policy Owner| Mimir Governance — Pending Assignment
Approval| Not yet approved
Implementation Status| Not verified
Compliance Status| Not assessed

───

1. Purpose

Establish a consistent, domain-aware policy governing the lifecycle of data managed by Mimir Intelligence.

This policy defines the governing principles for:

• Data creation and registration.
• Ownership and classification.
• Active use and maintenance.
• Retention and archival.
• Expiration and scheduled cleanup.
• User-requested deletion.
• Dependency and reference handling.
• Backup and recovery implications.
• Legal or security-related preservation.
• Deletion verification and reporting.
• Lifecycle auditability.

The policy applies across the four Intelligence domains while preserving their independent ownership and lifecycle responsibilities.

It establishes shared governance rules without replacing domain-specific storage, security, synchronization, or deletion implementations.

2. Scope

2.1 Included Domains

1. Mimir Brain.
2. Mimir Files.
3. Mimir Downloads.
4. Mimir Secure Vault.

2.2 Included Shared Services

• Permissions & Authorization.
• Provenance & Source References.
• Sync & Conflict Resolution.
• Activity & Audit Trail.
• Encryption & Key Management.
• Import & Export.
• Retention & Deletion Policies.
• Notifications.
• Unified Search, where deletion and retention affect indexed data.

2.3 Exclusions

This policy does not define:

• Detailed database schemas.
• Physical storage implementation.
• Backup provider implementation.
• Cryptographic algorithms or key derivation.
• Search ranking and retrieval behavior.
• Full synchronization protocols.
• Jurisdiction-specific legal retention periods.
• A complete records-management or regulatory compliance program.

These matters must be governed by the relevant technical, security, legal, and domain specifications.

3. Governing Principles

3.1 Domain Ownership

Every data object must have an identifiable owning domain or service.

The owning domain is responsible for defining and enforcing the lifecycle of its records within the shared policy.

3.2 User Ownership and Control

Users must have understandable and accessible controls over their data, subject to applicable legal, security, and technical constraints.

3.3 Purpose Limitation

Data must not be retained indefinitely without a defined operational, user, security, or legal purpose.

3.4 Data Minimization

Collect and retain only the information needed for the declared purpose.

3.5 Explicit Lifecycle States

Data lifecycle states must be distinguishable and must not be inferred from ambiguous UI labels.

3.6 No Implicit Cross-Domain Deletion

Deleting an object in one domain must not silently delete an object owned by another domain merely because a reference exists.

3.7 Honest Deletion Claims

Mimir must distinguish logical deletion, removal from active storage, backup expiration, cryptographic erasure, and verified physical deletion.

It must never claim permanent deletion unless the implementation and evidence support that claim.

3.8 Security by Classification

Retention, access, archival, export, and deletion behavior must respect the object's security classification.

3.9 Auditability

Material lifecycle operations must be traceable without unnecessarily retaining the sensitive content being deleted.

3.10 Recoverability Without False Guarantees

Recovery must be available only where a valid recovery mechanism exists. A deleted item must not be described as recoverable if its required content or keys have been destroyed.

4. Data Ownership and Lifecycle Authority

4.1 Mimir Brain

Brain owns knowledge records and their knowledge-specific relationships.

Its lifecycle responsibilities include:

• Notes and documents.
• Knowledge collections.
• Tags and relationships.
• WikiLinks and Backlinks.
• Knowledge graph records.
• Knowledge-specific attachments or references.
• Knowledge versions and provenance.

Brain does not automatically own the physical binary of a file referenced by a note.

4.2 Mimir Files

Files owns file-management records and file operations.

Its lifecycle responsibilities include:

• File and folder metadata.
• Folder hierarchy.
• File references.
• File classifications.
• File versions where supported.
• Storage-location metadata.

The actual binary may be held by local storage or an external provider. Deletion claims must reflect the authority and capabilities of the actual storage owner.

4.3 Mimir Downloads

Downloads owns download-related records and state.

Its lifecycle responsibilities include:

• Download history.
• Download task metadata.
• Queue records.
• Source references.
• Download status.
• Downloaded content where it is managed by Downloads.

Deleting a history record does not necessarily delete the downloaded binary, and deleting a binary does not necessarily delete its history record.

These actions must be distinguishable.

4.4 Mimir Secure Vault

Secure Vault owns protected records and secrets.

Its lifecycle must follow stricter requirements, including:

• Protected access.
• Controlled retention.
• Secure deletion.
• Key-management requirements.
• Recovery constraints.
• Minimal secret exposure.
• Explicit handling of backups and protected exports.

Secure Vault secrets must not be copied into generic lifecycle archives or ordinary audit records.

4.5 Memory and Knowledge Separation

System Memory and Brain Knowledge are separate data categories.

Deleting a Brain note must not automatically delete a Memory record derived from it.

Likewise, deleting a Memory record must not automatically delete the underlying Brain knowledge.

Where a Memory item depends on a source, the system should maintain an appropriate source relationship and apply the relevant policy when that source changes or is deleted.

5. Data Classification

The implementation must support a documented classification model.

The following is a proposed logical baseline, subject to reconciliation with existing security and data-model contracts:

Classification| Description| Typical Handling
Public| Intended for public distribution| Standard lifecycle controls
Internal| Ordinary private application data| Authenticated access and normal retention
Confidential| Sensitive user or operational data| Restricted access and stronger protection
Restricted| Secrets, credentials, keys, or highly sensitive records| Strict access, protected storage, specialized deletion

Classification must be assigned according to the data's nature and purpose, not merely its location.

A record's classification must not be weakened because it is copied, exported, indexed, synchronized, or referenced by another domain.

Derived data must inherit appropriate protection from its source unless a documented assessment establishes otherwise.

6. Lifecycle Model

The following is a conceptual lifecycle model. Domain implementations may use different internal states while preserving the required semantics.

1. Created — A new record or object is registered.
2. Active — Available for its intended use.
3. Modified — Updated while retaining applicable history and provenance.
4. Archived — Retained but no longer part of normal active use.
5. Expired — Its defined retention period or purpose has ended.
6. Pending Deletion — A deletion request has been accepted but processing is incomplete.
7. Logically Deleted — Removed from normal user-facing access and active application use.
8. Purge Pending — Awaiting removal from applicable storage, replicas, indexes, or backups.
9. Purged — Removal from the defined target systems has been verified.
10. Deletion Blocked — Deletion is delayed or restricted by a documented legal, security, dependency, or technical condition.

These states must not be collapsed into a single ambiguous status such as "Deleted" if the underlying operation is incomplete.

6.1 State Transitions

Every transition must:

• Have a defined trigger.
• Be authorized.
• Respect domain ownership.
• Be recorded where material.
• Handle failure explicitly.
• Avoid contradictory lifecycle states.

A failed purge must not silently transition to Purged.

7. Retention Policy

7.1 General Rule

Each data category must have a documented retention rule based on:

• Purpose.
• User expectations.
• Operational necessity.
• Security classification.
• Legal or contractual requirements where applicable.
• Dependencies.
• Recovery requirements.
• Storage and technical constraints.

No universal retention duration should be invented for all Intelligence data.

7.2 Retention Schedule

A formal retention schedule must identify, for each data category:

Field| Required Definition
Data Category| The record or object type
Owner| Responsible domain or service
Purpose| Why retention is necessary
Classification| Security level
Retention Trigger| Event starting the retention period
Retention Rule| Duration or lifecycle condition
Expiration Action| Archive, delete, review, or other action
Exceptions| Legal, security, or operational constraints
Backup Treatment| Applicable backup lifecycle
Verification| Evidence needed to confirm enforcement

The schedule must be versioned and approved through the governance process.

7.3 No Indefinite Default Retention

Where no retention rule exists, the owning domain must identify the missing policy and avoid representing indefinite retention as an approved design decision.

A default must not silently authorize unlimited retention.

7.4 User-Managed Retention

Where feasible, users should be able to configure retention for suitable data categories.

User settings must not override mandatory security, legal, or platform-integrity constraints.

8. Domain-Specific Retention Rules

8.1 Brain

Brain retention should support long-lived knowledge while preserving user control.

Potential lifecycle rules include:

• Active knowledge remains available until deleted or archived by an authorized action.
• Archived notes remain discoverable only according to the user's settings and permissions.
• Version history follows an explicit retention policy.
• Deleted notes must not remain indefinitely in ordinary retrieval results.
• Derived summaries and indexes must follow source-deletion rules.
• Attachments follow their actual ownership and storage lifecycle.

Knowledge must not be automatically deleted merely because it has not been opened recently.

8.2 Files

Files retention must distinguish:

• Metadata.
• Binary content.
• Folder records.
• Version history.
• External storage references.
• Cached copies.
• Thumbnails and previews.

The deletion of a metadata record must not be represented as deletion of the binary unless the binary deletion has been verified.

External storage deletion depends on the provider's capabilities and confirmation.

8.3 Downloads

Downloads retention must distinguish:

• Download history.
• Queue records.
• Temporary partial files.
• Completed downloaded content.
• Cached media.
• Source references.

Temporary partial data may be eligible for automatic cleanup according to an approved policy.

Completed content must follow user-configurable or domain-approved retention behavior.

Deleting a queue entry must not silently delete a completed file unless that behavior is explicit and approved.

8.4 Secure Vault

Secure Vault must use stricter retention and deletion rules.

Requirements include:

• No secret retention without an identifiable purpose.
• No secret values in ordinary logs or telemetry.
• Protected records must follow approved encryption and key-management rules.
• Temporary secret material must be minimized.
• Protected backups must have a defined lifecycle.
• Deletion of protected records must consider encrypted replicas and recovery material.
• Recovery claims must reflect actual key availability.

Secure Vault must not inherit permissive defaults from ordinary Files or Brain records.

9. Deletion Types

Mimir must distinguish at least the following deletion operations.

9.1 User-Requested Deletion

Deletion explicitly requested by an authorized user.

The system must communicate the scope and relevant consequences before confirmation.

9.2 Automatic Expiration

Deletion or archival triggered by an approved retention rule.

Automatic actions must be governed by a documented schedule and must not silently expand to unrelated data.

9.3 Administrative Deletion

A privileged operation performed under an approved administrative or support process.

It must require authorization, justification, and auditability.

9.4 Security-Driven Deletion

Deletion or invalidation required to address a security incident, compromised credential, unsafe object, or other documented security condition.

9.5 Account or Workspace Closure

A broader lifecycle operation that may affect multiple domains and services.

It must use a coordinated deletion workflow and must not assume that all data can be purged immediately.

9.6 Selective Deletion

Removal of a specific record, attachment, version, relationship, or category without deleting unrelated objects.

The user must be able to understand which object is being deleted.

10. Deletion Workflow

A standard deletion workflow should follow these stages:

1. Request received.
2. Requester authorization.
3. Scope identification.
4. Ownership resolution.
5. Dependency analysis.
6. Legal and security constraint evaluation.
7. User confirmation where required.
8. Pending Deletion registration.
9. Removal from active access.
10. Propagation to applicable indexes and replicas.
11. Storage-owner deletion request.
12. Backup lifecycle handling.
13. Verification.
14. Completion or exception reporting.
15. Audit record finalization.

Not every domain requires identical internal steps, but the resulting lifecycle state must remain truthful.

10.1 Authorization

The request must be authorized against the actual owning domain.

A user who can view a reference is not automatically authorized to delete the referenced object.

10.2 Scope Preview

Where a deletion has material consequences, the user should see:

• The primary record.
• Related objects that may be affected.
• References that will remain.
• Objects that cannot be deleted automatically.
• Whether the action is reversible.
• Expected propagation limitations.

10.3 Confirmation

Destructive or irreversible actions must require an appropriate confirmation mechanism.

Secure Vault and other restricted data may require reauthentication or additional confirmation.

10.4 Failure Handling

If part of a deletion fails:

• Report the incomplete portion.
• Preserve a truthful state.
• Provide a recovery or retry path where possible.
• Do not report full deletion.
• Record the failure without exposing protected content.

11. Cross-Domain Dependencies

A cross-domain reference does not transfer ownership.

For example:

• A Brain note may reference a Files attachment.
• A Brain note may contain a reference to a Downloads item.
• An external file may be referenced by multiple knowledge records.
• A Secure Vault record may be referenced by an authorized Autofill workflow.

Deleting the source record must not automatically delete every referenced object.

11.1 Dependency Evaluation

Before deletion, the owning domain should identify:

• Incoming references.
• Outgoing references.
• Shared dependencies.
• Derived objects.
• External provider dependencies.
• Active workflows that may depend on the object.

11.2 Reference Cleanup

After deletion:

• Remove or invalidate references owned by the deleted record.
• Preserve references owned by other domains when appropriate.
• Mark unresolved references clearly.
• Avoid redirecting references to unrelated objects.
• Do not expose protected metadata while resolving dependencies.

11.3 Derived Data

Derived content may include:

• Search indexes.
• Embeddings.
• Summaries.
• Thumbnails.
• Previews.
• Cached content.
• AI-generated transformations.

The owning domain must define which derived objects must be removed, regenerated, or retained under a documented purpose.

Derived content must not become a mechanism for bypassing deletion.

12. Search, Indexing, and Memory Implications

Deletion must propagate to applicable search and retrieval systems.

Requirements:

• Logically deleted records must not appear as ordinary active results.
• Search indexes must be updated or invalidated.
• Cached retrieval results must respect deletion state.
• Derived embeddings must follow the source lifecycle.
• Failed propagation must be observable.
• Results must not be represented as fully deleted while stale copies remain accessible.

12.1 Memory

Memory is governed separately from Brain Knowledge.

If a source used to create a Memory item is deleted:

• Evaluate whether the Memory item still has a valid purpose.
• Apply the Memory lifecycle policy.
• Remove or update source references where required.
• Do not automatically delete unrelated Memory records.
• Do not claim that source deletion erased all derived personal context unless that has been verified.

13. Versions and Historical Data

Version history can be valuable for recovery and accountability but must not become an indefinite retention loophole.

Each owning domain must define:

• Which versions are retained.
• Whether users can delete individual versions.
• Whether archival versions remain accessible.
• How deleted records affect their versions.
• Whether versions are included in exports.
• How backups and replicas treat historical versions.

A deleted record must not remain available through an ordinary version-history interface unless an explicit, approved policy permits it.

14. Backup and Replica Lifecycle

Backups and replicas require explicit lifecycle treatment.

The system must distinguish:

• Active data.
• Replicated data.
• Cached data.
• Backup snapshots.
• Archived copies.
• Exported copies held outside Mimir.

14.1 Deletion Propagation

A deletion request must propagate to applicable active replicas and indexes according to the relevant synchronization contract.

14.2 Backup Expiration

A backup may remain temporarily available after logical deletion if the approved backup lifecycle requires it.

In such cases:

• The system must not claim immediate permanent deletion.
• The applicable expiration policy must be documented.
• Restoring a backup must not silently reactivate previously deleted records.
• Deletion tombstones or equivalent controls should be applied during restoration where supported.

14.3 External Copies

Mimir cannot guarantee deletion of a copy that has already been exported to a destination outside its control.

The user must be informed of this limitation when relevant.

15. Cryptographic Erasure and Key Lifecycle

Cryptographic erasure may be used where supported by the approved security architecture.

It must not be treated as a universal substitute for deleting all copies.

Requirements:

• Use approved key-management contracts.
• Establish which encrypted data is protected by each key.
• Verify that key destruction cannot unintentionally destroy unrelated data.
• Preserve necessary non-secret audit evidence.
• Handle key backups and recovery material explicitly.
• Do not claim cryptographic erasure unless key lifecycle evidence supports it.

Secure Vault key destruction must follow its dedicated security and recovery policy.

16. Legal, Security, and Operational Holds

A deletion may be restricted by a valid legal, security, or operational hold.

Any hold must:

• Have an identifiable authority.
• Have a documented reason.
• Be limited to the necessary scope.
• Have a review or expiration condition where applicable.
• Restrict access appropriately.
• Be recorded in audit history.
• Be removed when its basis no longer applies.

A hold must not become an undocumented mechanism for indefinite retention.

Legal and regulatory obligations require jurisdiction-specific review before implementation.

17. Audit and Evidence

Material lifecycle operations should generate audit records containing:

• Operation identifier.
• Data object identifier or appropriately protected reference.
• Owning domain.
• Operation type.
• Authorized actor or principal.
• Trigger.
• Relevant timestamps.
• Lifecycle state transitions.
• Outcome.
• Failure or exception category.
• Approval reference where applicable.
• Verification evidence or verification status.

Audit records must not contain:

• Passwords.
• Passkeys or private keys.
• Authenticator secrets.
• Recovery codes.
• Full sensitive document contents.
• Unnecessary personal information.

Audit retention must have its own documented policy.

The existence of an audit record does not prove that the underlying data was physically deleted.

18. Temporary Data and Processing Artifacts

Temporary data includes:

• Import staging.
• Export staging.
• Parser working files.
• Partial downloads.
• Temporary previews.
• Intermediate transformations.
• Cached copies.
• Temporary plaintext material.

Each temporary category must have:

• An owning service.
• A defined purpose.
• A cleanup trigger.
• An expiration or maximum lifecycle.
• A failure-handling procedure.
• Appropriate protection according to classification.

Temporary data must not be retained indefinitely because a cleanup operation failed.

Cleanup failures must be detectable and recoverable.

19. User Experience and Transparency

Users should be able to understand the state of their data without needing to understand internal storage architecture.

The interface should distinguish:

• Archive.
• Expiration.
• Move to trash or recovery area, if supported.
• Logical deletion.
• Permanent deletion request.
• Deletion processing.
• Deletion completed.
• Deletion partially completed.
• Deletion blocked or delayed.

The interface must not use "Permanently deleted" as a generic label for a logical deletion.

Where deletion is delayed by backup or provider constraints, communicate the limitation in clear language.

19.1 Deletion Preview

Before material deletion, show:

• What will be deleted.
• Which domain owns it.
• Whether binary content is included.
• What related objects will remain.
• Whether recovery is possible.
• Whether external copies are outside Mimir's control.
• Any known delay or restriction.

19.2 Activity Visibility

Users should be able to inspect the progress and outcome of relevant lifecycle operations through the appropriate activity surface.

20. Offline and Android Behavior

The policy must account for mobile application constraints.

Requirements:

• Offline deletion requests must have an explicit pending or queued state.
• The UI must not claim remote deletion before synchronization confirms it.
• Conflicting updates must be resolved through the synchronization contract.
• App termination must not silently discard a deletion request.
• User-granted storage permissions must be respected.
• Loss of access to an external URI must not be treated as successful deletion.
• Local deletion and remote deletion must be represented separately where necessary.

The implementation must not promise background cleanup that Android does not guarantee.

21. Policy Administration and Governance

The retention and deletion policy must be centrally governed but domain-executable.

Governance responsibilities include:

• Defining policy categories.
• Approving retention schedules.
• Reviewing exceptions.
• Assigning policy ownership.
• Auditing enforcement.
• Reviewing changes in legal or security requirements.
• Resolving conflicting domain policies.
• Approving changes to deletion guarantees.

A domain may apply stricter controls than the shared policy but must not weaken mandatory platform-wide protections without authorized approval.

Policy changes must be versioned and traceable.

22. Policy Compliance and Verification

Each domain must demonstrate how it enforces the policy.

Evidence may include:

• Lifecycle state transition tests.
• Retention schedule configuration.
• Deletion propagation tests.
• Backup restoration tests.
• Search and index invalidation tests.
• Cross-domain dependency tests.
• Authorization tests.
• Secure Vault deletion tests.
• Audit completeness checks.
• Cleanup and recovery tests.

A policy statement is not evidence of implementation.

23. Acceptance Criteria

23.1 Ownership and Boundaries

[  ]  Every governed data category has an identifiable owner.
[  ]  Cross-domain references do not transfer ownership.
[  ]  Deletion in one domain does not silently delete another domain's data.
[  ]  Memory, Knowledge, Context, and Secrets remain distinct.

23.2 Retention

[  ]  Retention rules are documented for governed categories.
[  ]  Retention triggers are explicit.
[  ]  Expiration behavior is defined.
[  ]  Indefinite retention is not silently assumed.
[  ]  Exceptions have documented authority and scope.

23.3 Deletion

[  ]  Logical deletion and permanent deletion are distinguishable.
[  ]  Authorization is enforced.
[  ]  Material destructive actions are confirmed.
[  ]  Partial failures are visible.
[  ]  Search and derived data follow source lifecycle rules.
[  ]  Deletion status reflects actual verification.

23.4 Backups and Recovery

[  ]  Backup retention is documented.
[  ]  Restoration respects prior deletion records where supported.
[  ]  External copies are clearly identified as outside Mimir's control.
[  ]  Key destruction is not confused with complete physical deletion.

23.5 Secure Vault

[  ]  Protected records follow stricter lifecycle controls.
[  ]  Secrets are excluded from ordinary logs.
[  ]  Protected backup and recovery rules are documented.
[  ]  Deletion claims are supported by evidence.

23.6 Mobile and Offline

[  ]  Offline requests have an explicit pending state.
[  ]  Local and remote deletion are distinguishable.
[  ]  Interrupted operations can be reconciled.
[  ]  Android storage limitations are handled honestly.

24. Required Test Scenarios

1. Delete a Brain note that references a Files attachment.
2. Delete the Files attachment while retaining the Brain note.
3. Verify that unresolved references are represented safely.
4. Delete a Downloads history record without deleting its binary.
5. Delete a downloaded binary while retaining permitted history.
6. Expire a temporary partial download.
7. Delete a Brain note and verify search-index invalidation.
8. Verify that derived embeddings follow the applicable source policy.
9. Delete a source record while a related Memory item exists.
10. Verify that Memory and Knowledge deletion remain independent.
11. Delete a Secure Vault record using the protected workflow.
12. Verify that secret values never appear in audit logs.
13. Simulate a failed remote deletion.
14. Simulate a backup containing previously deleted data.
15. Restore a backup and verify deletion-state reconciliation.
16. Simulate an active legal or security hold.
17. Remove a hold and verify that eligible deletion resumes.
18. Interrupt deletion during offline operation.
19. Reconcile a queued deletion after reconnecting.
20. Simulate loss of access to an external storage URI.
21. Verify that failed cleanup does not report Purged.
22. Verify that external exported copies are not falsely claimed to be deleted.
23. Verify that audit retention is independently governed.
24. Verify that a retention-policy update does not silently alter unrelated categories.

25. Reconciliation With Existing Governing Documents

This policy is intended to consolidate shared governance rather than duplicate technical specifications.

The following reconciliation must be completed against the actual current repository files before this document is approved.

Existing Document| Expected Coverage| Reconciliation Action
Architecture & Domain Boundaries| Ownership and domain authority| Reuse established boundaries; do not redefine them
Shared Services Contracts| Retention and deletion service responsibilities| Identify existing contracts and add only missing policy-level rules
Security, Privacy & Acceptance Specifications| Classification, authorization, privacy, audit| Reuse approved security requirements
Data Model & Entity Relationships| Ownership, references, derived relationships| Align lifecycle rules with actual entities
API & Event Contracts| Lifecycle operations and events| Avoid duplicating API schemas
Storage, Sync & Offline Architecture| Replicas, offline behavior, backups, synchronization| Defer implementation details to this document
Import, Export & Data Portability| Temporary staging, export copies, deletion implications| Maintain consistent lifecycle handling
Brain Specification| Knowledge and Memory separation| Preserve domain-specific behavior
Files Specification| File and binary ownership| Preserve storage ownership boundaries
Downloads Specification| History and downloaded content| Preserve distinction between metadata and binary
Secure Vault Specification| Secrets, key management, protected deletion| Defer cryptographic implementation to Secure Vault

25.1 Reconciliation Outcomes

For each policy requirement, the review must assign one outcome:

• Covered: Existing governing document already defines the requirement adequately.
• Partially Covered: Existing document defines part of the requirement; a targeted addition is required.
• Missing: No adequate governing rule exists.
• Conflicting: Existing documents contain incompatible rules.
• Not Applicable: The requirement does not apply to the relevant domain or data category.

Only Missing, Partially Covered, or Conflicting items should normally trigger new content or amendments.

25.2 No-Duplication Rule

If existing governing documents already provide adequate coverage:

• Do not create a second competing policy.
• Record the coverage in the reconciliation matrix.
• Add a reference to the existing authoritative section.
• Amend only the missing or conflicting parts.
• Keep one authoritative source for each policy decision.

25.3 Approval Gate

This document must not be marked Approved until:

1. The current governing documents have been inspected.
2. The reconciliation matrix is completed.
3. Conflicts are resolved.
4. Retention ownership is assigned.
5. Applicable legal and security review is completed.
6. Required policy decisions are approved.

26. Open Governance Decisions

ID| Decision| Status
ADR-LIFE-001| Canonical lifecycle states and terminology| Open
ADR-LIFE-002| Initial retention schedule and ownership| Open
ADR-LIFE-003| Trash and recovery-area behavior, if supported| Open
ADR-LIFE-004| Backup expiration and deletion propagation guarantees| Open
ADR-LIFE-005| Audit retention and evidence requirements| Open
ADR-LIFE-006| Derived data and Memory deletion dependencies| Open
ADR-LIFE-007| Legal and security hold governance| Open
ADR-LIFE-008| Secure Vault cryptographic erasure and recovery integration| Governed by Secure Vault security decisions
ADR-LIFE-009| Account and workspace closure behavior| Open
ADR-LIFE-010| Deletion verification levels and user-facing terminology| Open

These are unresolved decisions, not approved architecture.

27. Relationship to the Next Document

The next document is:

Mimir Intelligence — Operational Monitoring & Recovery Specification

It will define:

• Operational health and service status.
• Monitoring and telemetry.
• Alerts and incident classification.
• Failure detection.
• Recovery orchestration.
• Retry and reconciliation.
• Backup restoration operations.
• Operational audit and reporting.
• Recovery verification.
• User-facing incident and service-state transparency.

It must build on the lifecycle and deletion guarantees defined here without duplicating their policy rules.

28. Implementation and Verification Boundary

This document defines intended policy and governance requirements.

It does not establish that:

• Retention schedules are implemented.
• Automatic expiration works.
• Permanent deletion is supported.
• All replicas and backups honor deletion.
• Search indexes are fully invalidated.
• Secure Vault supports cryptographic erasure.
• Android offline deletion is fully recoverable.
• Any acceptance test has passed.
• The policy has been reviewed against the current repository.

All implementation and compliance claims require evidence from the actual system.

29. Final Status

Draft — Pending Existing-Document Reconciliation, Governance Review, and Security Approval.

Document ID remains "MIMIR-DOC-REG-INT-014".

This document is a proposed central policy consolidation. It must be compared with the actual governing files before being adopted as an additional authoritative document.

No repository update, Dropbox save, approval, implementation, or test execution is implied.

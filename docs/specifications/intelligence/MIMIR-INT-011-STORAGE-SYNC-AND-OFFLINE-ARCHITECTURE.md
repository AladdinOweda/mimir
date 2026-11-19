---
document_id: MIMIR-DOC-REG-INT-011
document_code: MIMIR-INT-011
filename: MIMIR-INT-011-STORAGE-SYNC-AND-OFFLINE-ARCHITECTURE.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

Mimir Intelligence — Storage, Sync & Offline Architecture Specification

Document Metadata
Value

Document ID
MIMIR-DOC-REG-INT-011

Document Title
Mimir Intelligence — Storage, Sync & Offline Architecture Specification

Domain
Mimir Intelligence

Document Type
Storage, Data Lifecycle & Synchronization Architecture

Status
Draft — Pending Architecture, Storage & Security Review

Version
Pending Document Registry

Implementation Status
Not verified

Approval Status
Not approved



───

1. Purpose
This document defines the storage, synchronization, and offline architecture for Mimir Intelligence.
It establishes how the four Intelligence domains own and manage their data, how local and remote copies relate to authoritative records, how changes synchronize across devices, and how conflicts, interrupted operations, backups, restoration, retention, and deletion are handled.
The architecture must support Android-first usage while remaining extensible to tablets, desktops, and future deployment environments.
The objective is to provide:
• Clear ownership of stored data.
• Separation between domain records and physical storage.
• Safe local persistence.
• Reliable synchronization.
• Explicit conflict handling.
• Offline operation with honest state reporting.
• Secure handling of sensitive information.
• Consistent backup, restoration, retention, and deletion policies.
• Recovery from interruptions and partial failures.
This document defines requirements and logical architecture. It does not claim that a particular storage engine, synchronization protocol, or backup system has been selected or implemented.
2. Scope
2.1 Included
• Data ownership and storage boundaries.
• Logical versus physical storage.
• Local and remote storage roles.
• Storage classification.
• Domain-specific persistence.
• File and object storage references.
• Offline operation.
• Synchronization lifecycle.
• Conflict detection and resolution.
• Versioning and consistency.
• Interrupted operation recovery.
• Backup and restoration.
• Retention and deletion.
• Encryption and key management integration.
• Android storage constraints.
• Integrity validation.
• Acceptance criteria and test scenarios.
2.2 Excluded
This document does not:
• Select a specific database.
• Mandate a specific cloud storage provider.
• Select a synchronization protocol.
• Make all domains dependent on one database.
• Turn the Kernel into a data-management domain.
• Permit direct cross-domain database access.
• Guarantee physical deletion without provider evidence.
• Promise unlimited offline functionality.
• Claim that backup or synchronization is already operational.
3. Architectural Principles
3.1 Domain ownership
Each domain owns its records, lifecycle, validation rules, and deletion decisions.
Physical storage may be provided by a local or remote storage adapter, but that does not transfer business ownership to the storage provider.
3.2 Separation of logical and physical storage
The architecture distinguishes:
1. Logical resource.
2. Domain-owned metadata.
3. Physical content or object.
4. Storage provider reference.
5. Local cached representation.
6. Synchronization state.
7. Provenance and audit information.
These are related but not interchangeable.
3.3 No shared ownership by accident
A domain may reference another domain's resource without becoming its owner.
A shared file reference does not authorize direct modification or deletion.
3.4 Local-first where appropriate
The client may persist permitted data locally to improve responsiveness and offline availability.
Local-first behavior must not imply that every local copy is authoritative.
3.5 Explicit consistency
Every operation must identify whether it is:
• Locally persisted.
• Queued for synchronization.
• Submitted remotely.
• Acknowledged by the authoritative owner.
• Rejected.
• Conflicted.
• Completed.
3.6 Security by classification
Storage behavior must reflect the sensitivity of the data.
Secure Vault data requires stronger isolation and protection than ordinary application metadata.
4. Storage Ownership Model
4.1 Mimir Brain
Brain owns:
• Knowledge records.
• Notes and documents as knowledge entities.
• Collections and tags.
• Knowledge relationships.
• Memory records.
• Retrieval metadata.
• Source references.
• Knowledge processing results.
• Brain-specific version and lifecycle records.
Brain may reference physical files owned by Files or external storage providers.
It must not assume ownership of the physical source merely because it created a knowledge record from it.
4.2 Mimir Files
Files owns:
• File-management records.
• Folder structures.
• File metadata under its control.
• File operation records.
• File organization relationships.
• Storage references.
• File version metadata where supported.
• File-specific lifecycle and deletion state.
The physical bytes may reside in:
• Local device storage.
• User-selected external storage.
• An authorized cloud provider.
• Another permitted storage adapter.
The Files domain remains responsible for its logical file-management records even when physical content is external.
4.3 Mimir Downloads
Downloads owns:
• Download requests.
• Queue records.
• Download jobs.
• Progress metadata.
• Provider references.
• Download result metadata.
• Capture operation records.
• Download lifecycle and error history.
Once a result is registered with Files, Downloads may retain a relationship to that result without taking ownership of the Files record.
4.4 Mimir Secure Vault
Secure Vault owns:
• Credentials.
• Passkeys and protected authentication material.
• Authenticator entries.
• Secure notes.
• Secure documents and IDs.
• Protected card records.
• Wallet-related sensitive records.
• Autofill authorization state.
• Vault security and lifecycle metadata.
Secure Vault must use isolated storage boundaries and protected cryptographic operations.
Secrets must not be copied into generic application databases, caches, event payloads, or ordinary backup systems.
4.5 Shared services
Shared services may maintain their own operational records, but must not become the owners of domain business records.
Examples include:
• Sync checkpoints.
• Audit records.
• Notification delivery state.
• Search indexes.
• Provenance records.
• Retention policy execution records.
• Encryption metadata.
Each service must respect the source domain's ownership and authorization rules.
5. Logical Storage Architecture
The architecture should separate the following logical layers:

Layer
Responsibility

Domain Persistence
Stores domain-owned records

Content Storage
Stores physical files and objects

Local Persistence
Stores permitted device-side records

Cache Layer
Holds disposable or regenerable data

Sync State
Tracks local and remote change state

Search Index
Holds authorized searchable representations

Provenance Store
Maintains lineage and source relationships

Audit Store
Maintains activity and security evidence

Secure Vault Storage
Protects secrets and sensitive records

Backup and Recovery
Supports authorized backup and restoration


These are logical responsibilities, not a requirement to create one physical database for each row.
The final physical topology requires a separate technology decision.
6. Storage Classification
Every stored resource must have an applicable classification.
Conceptual classification levels:

Classification
Description
Typical Examples

Public
Approved for public exposure
Public reference metadata

Internal
Normal application information
General preferences and non-sensitive operational metadata

Confidential
Personal or business-sensitive information
Private notes and personal documents

Restricted
Highly sensitive information
Credentials, passkeys, recovery material, authentication secrets


The classification must influence:
• Encryption.
• Access control.
• Local persistence.
• Caching.
• Search indexing.
• Logging.
• Backup.
• Export.
• Retention.
• Deletion.
• Synchronization.
A resource must not become less protected merely because it moves to another domain or storage provider.
7. Local Storage Architecture
7.1 Local persistence
Local storage may be used for:
• Application state.
• Permitted domain records.
• Offline operation queues.
• Synchronization checkpoints.
• Download metadata.
• File metadata.
• Cached content.
• User preferences.
• Protected Secure Vault data under its separate security model.
The local storage implementation must be replaceable through stable contracts.
7.2 Local data categories
The client should distinguish:
1. Authoritative remote data cached locally.
2. Locally created but not yet synchronized data.
3. Locally modified data awaiting synchronization.
4. Disposable cache data.
5. Protected secrets.
6. Derived or indexed data.
These categories require different recovery and cleanup behavior.
7.3 Cache policy
Cached data must have:
• Defined ownership.
• Expiration or invalidation behavior.
• Classification.
• Regeneration rules.
• Size limits.
• Access controls.
Sensitive data must not be cached unless explicitly permitted.
7.4 Cache invalidation
A cached record may become stale when:
• Its source changes.
• Its permissions change.
• Its version is superseded.
• Its classification changes.
• Its retention expires.
• Its owner requests deletion.
Invalidation must not depend solely on the client being continuously online.
8. Physical Content Storage
8.1 Storage adapters
Physical storage must be accessed through replaceable provider adapters.
Potential categories include:
• Device-local storage.
• Android Storage Access Framework.
• User-selected external locations.
• Cloud object storage.
• Authorized remote file providers.
No specific provider is mandated by this document.
8.2 Storage reference
A storage reference should identify:
• Provider type.
• Provider-specific object identifier.
• Logical resource identifier.
• Owner domain.
• Version or revision, where applicable.
• Classification.
• Availability state.
• Integrity metadata, where supported.
Provider credentials must not be embedded in ordinary storage references.
8.3 Provider independence
Changing a storage provider must not require changing the business model of Brain, Files, Downloads, or Secure Vault.
Provider-specific details belong behind adapters.
8.4 Availability
A storage reference may remain valid while its content is temporarily unavailable.
The system must distinguish:
• Available.
• Offline.
• Permission denied.
• Provider unavailable.
• Missing.
• Deleted.
• Unknown.
It must not report missing content as deleted without sufficient evidence.
9. Offline Architecture
9.1 Offline operating modes
The client should distinguish:
• Online.
• Offline.
• Limited connectivity.
• Synchronization pending.
• Synchronization blocked.
• Recovery required.
9.2 Offline eligibility
Each operation must declare whether it supports:
• Fully offline execution.
• Offline creation with later synchronization.
• Offline read-only access.
• Online-only execution.
The policy is domain-specific.
9.3 Offline operation queue
An eligible operation should preserve:
• Local operation ID.
• Actor identity.
• Domain and resource reference.
• Operation type.
• Payload reference.
• Creation timestamp.
• Base version.
• Idempotency key.
• Required capability context.
• Approval reference, where applicable.
• Synchronization state.
• Retry metadata.
The queue must not store sensitive values in an unprotected form.
9.4 Queue states
Conceptual lifecycle:
text
CreatedLocally
      |
      v
PendingSync
      |
      v
Submitted
      |
      +------> Acknowledged
      |
      +------> Rejected
      |
      +------> Conflict
      |
      +------> RetryScheduled
      |
      +------> Blocked

9.5 Offline user experience
The UI must distinguish local state from authoritative state.
Examples:
• Saved on this device.
• Waiting to sync.
• Syncing.
• Synced.
• Conflict requires attention.
• Permission changed.
• Remote operation rejected.
The client must not display a remote completion indicator based only on a local write.
9.6 Offline restrictions
Offline mode must not bypass:
• User approval.
• Authorization.
• Capability expiration.
• Secure Vault policy.
• Provider access restrictions.
• Retention rules.
• Deletion requirements.
Operations that require authoritative validation must remain pending or unavailable until validation is possible.
10. Synchronization Architecture
10.1 Ownership of synchronization
The Sync & Conflict Resolution shared service coordinates synchronization.
The domain owner remains responsible for:
• Validating changes.
• Applying domain rules.
• Accepting or rejecting updates.
• Determining valid state transitions.
• Resolving domain-specific conflicts.
The shared service must not independently rewrite domain data.
10.2 Synchronization flow
mermaid
sequenceDiagram
    actor User
    participant Client
    participant Queue as Local Queue
    participant Sync as Sync Service
    participant Domain as Owning Domain

    User->>Client: Create or update resource
    Client->>Queue: Persist local operation
    Queue-->>Client: PendingSync
    Client->>Sync: Submit operation
    Sync->>Domain: Validate and apply change
    Domain-->>Sync: Accepted / Rejected / Conflict
    Sync-->>Client: Synchronization result
    Client->>Queue: Update local operation state

10.3 Change metadata
A synchronized change should carry:
• Resource ID.
• Domain owner.
• Change ID.
• Client or device ID.
• Base version.
• New local version or revision.
• Actor.
• Timestamp.
• Correlation ID.
• Causation ID.
• Idempotency key.
• Change reference.
• Classification.
10.4 Synchronization checkpoints
Checkpoints must support safe continuation after interruption.
A checkpoint should not advance past changes that have not been safely processed.
10.5 Partial synchronization
A batch may partially succeed.
The system must report the status of individual operations or clearly defined groups.
It must not mark the entire batch successful when some operations failed.
10.6 Duplicate synchronization
Repeated submissions must not create duplicate resources or duplicate irreversible effects when idempotency is supported.
The authoritative owner must validate the operation identity and current state.
11. Conflict Detection and Resolution
11.1 Conflict definition
A conflict occurs when a local change cannot safely be applied against the current authoritative state.
Examples:
• The resource changed remotely after the local base version.
• The resource was deleted remotely.
• Permissions changed.
• A lifecycle transition is no longer valid.
• Two devices modified the same field.
• A referenced object was removed.
• The provider version changed.
11.2 Conflict categories

Conflict
Example

Version Conflict
Local and remote versions differ

Concurrent Edit
Two devices modify the same record

Deletion Conflict
One device deletes while another edits

Permission Conflict
Access was revoked during offline work

Relationship Conflict
Referenced resource is no longer available

Lifecycle Conflict
Requested transition is no longer valid

Provider Conflict
External content changed outside Mimir


11.3 Resolution strategies
Possible strategies include:
• Automatic merge for explicitly merge-safe fields.
• Last-write-wins only for approved low-risk fields.
• Domain-specific merge.
• User-assisted resolution.
• Reject and request fresh data.
• Preserve both versions.
• Quarantine for manual review.
No global last-write-wins policy should be applied to all domains.
11.4 Domain-specific rules
Brain
• Note content may support merge strategies if the format and edits permit safe merging.
• Knowledge relationships must be validated.
• Provenance must be preserved.
• Conflicting AI-derived updates must not overwrite reviewed user information silently.
Files
• File metadata may support selective merging.
• Physical file replacement requires version and integrity checks.
• Folder movement must validate the current hierarchy.
• Deletion conflicts require explicit policy.
Downloads
• Job status must be reconciled against the authoritative operation record.
• Progress values must not be merged as ordinary user-editable fields.
• Retry must respect operation identity and provider limitations.
Secure Vault
• No generic merge strategy may expose or overwrite secrets silently.
• Conflicts require protected handling.
• Sensitive updates may require renewed authentication or approval.
• Recovery and conflict operations must follow Vault-specific security policy.
12. Versioning and Consistency
12.1 Resource version
Domain-owned resources should expose an authoritative revision or version.
Clients must identify the base revision used for offline modifications.
12.2 Consistency model
The architecture should distinguish:
• Strongly validated authoritative operations.
• Eventually synchronized offline changes.
• Cached read representations.
• Derived indexes.
• External provider state.
The system must not present eventually consistent data as guaranteed current.
12.3 Derived data
Search indexes, previews, extracted text, embeddings, thumbnails, and other derived artifacts must be traceable to their source version.
A derived artifact must be invalidated or refreshed when its source changes.
12.4 Provenance
Model provenance and source lineage must survive:
• Local persistence.
• Synchronization.
• Conflict resolution.
• Backup.
• Restoration.
• Import.
• Export.
• Copy operations.
Unknown provenance must remain unknown rather than being reconstructed through guesses.
13. Backup and Restoration
13.1 Backup principles
Backup must respect domain ownership and classification.
A backup may contain:
• Domain records.
• Authorized content.
• Resource relationships.
• Version history.
• Provenance.
• Required metadata.
• Protected encryption metadata.
It must not expose secrets through generic backup formats.
13.2 Backup scope
The system should distinguish:
• Device backup.
• Domain backup.
• User-controlled export.
• Provider-side backup.
• Disaster recovery copy.
These are not equivalent.
13.3 Secure Vault backup
Secure Vault backup requires a dedicated security architecture covering:
• Encryption.
• Key derivation and protection.
• Recovery policy.
• Authentication.
• Integrity validation.
• Device trust.
• Restore authorization.
• Backup version compatibility.
Generic application backup must not be treated as a substitute for Secure Vault backup.
13.4 Restoration
Restoration must validate:
• Backup integrity.
• Ownership.
• Version compatibility.
• Authorization.
• Encryption metadata.
• Resource relationships.
• Retention and deletion state.
Restoring a backup must not silently resurrect resources that were subject to a valid deletion or retention policy.
13.5 Restore outcomes
The system must distinguish:
• Restore requested.
• Restore validated.
• Restore started.
• Restore partially completed.
• Restore completed.
• Restore failed.
• Manual intervention required.
14. Retention and Deletion
14.1 Domain-owned deletion
Each domain determines the deletion lifecycle of its own records under applicable shared policies.
A cross-domain reference must not cause automatic deletion of the referenced record.
14.2 Deletion stages
Conceptual lifecycle:
text
Active
  |
  v
DeletionRequested
  |
  v
DeletionAuthorized
  |
  v
LogicalDeletion
  |
  v
PhysicalDeletionPending
  |
  v
PhysicalDeletionVerified

Some resources may require retention, legal hold, or backup cleanup stages.
14.3 Deletion distinctions
The system must distinguish:
• Removing a reference.
• Deleting a logical record.
• Deleting a local cached copy.
• Deleting a physical object.
• Deleting derived artifacts.
• Removing search indexes.
• Expiring backup copies.
• Completing provider-side deletion.
14.4 Cross-domain deletion
If a Brain record references a Files resource, deleting the Files resource must not automatically erase the Brain record.
Instead, the relationship must be revalidated and its state updated according to Brain's rules.
Likewise, deleting a Brain record must not delete the source file.
14.5 Secure Vault deletion
Secure Vault deletion requires:
• Specific authorization.
• Appropriate authentication.
• Audit metadata.
• Protected key lifecycle handling.
• Verification of applicable storage and backup cleanup.
The system must not claim that a secret has been permanently erased while recoverable copies remain.
14.6 Tombstones
Tombstones may be required to propagate deletion state across devices.
Tombstones must:
• Carry minimal metadata.
• Be access-controlled.
• Have defined retention.
• Prevent stale devices from silently resurrecting deleted records.
• Respect legal and retention requirements.
15. Encryption and Key Management
Encryption and Key Management remains a shared service responsibility.
The storage architecture must integrate with it rather than implement independent cryptographic policy in every domain.
15.1 Requirements
• Encryption in transit.
• Encryption at rest.
• Appropriate key separation.
• Secure key rotation.
• Key revocation.
• Protected secret handling.
• Authenticated integrity checks.
• Secure recovery procedures.
• No secrets in logs or ordinary diagnostics.
15.2 Domain separation
Secure Vault must have stronger isolation and dedicated key-handling requirements.
Other domains must not obtain Vault encryption keys or bypass protected operations.
15.3 Local device protection
Local sensitive data must use platform-supported protection mechanisms where available.
The exact Android implementation and fallback behavior require platform-specific review.
16. Android-Specific Requirements
The Android client must account for:
• Process termination.
• Application background restrictions.
• Device restarts.
• Limited memory.
• Storage permission changes.
• Scoped storage.
• User-selected document locations.
• Network transitions.
• Battery restrictions.
• Interrupted downloads.
• Interrupted uploads.
• Application upgrades.
• Local database migration.
• Device replacement.
16.1 Background operations
Background synchronization must use an execution mechanism compatible with Android lifecycle restrictions.
No operation should rely on a continuously running process.
16.2 Storage permissions
The application must respect Android storage permissions and user-granted document access.
A previously granted permission may become unavailable and must be revalidated.
16.3 Database migration
Local data migrations must:
• Preserve user data.
• Validate schema compatibility.
• Support safe failure reporting.
• Avoid partial destructive changes.
• Provide recovery where feasible.
16.4 Device identity
Device identity must be stable enough for synchronization bookkeeping but must not be treated as an authentication credential by itself.
17. Multi-Device Synchronization
17.1 Device registration
Each authorized device should have an identifiable synchronization context.
The system should track:
• Device reference.
• Registration state.
• Last successful synchronization.
• Supported capabilities.
• Security status.
• Revocation status.
17.2 Device revocation
Revoking a device must invalidate its future authorization.
The system should define how pending local changes are handled after revocation.
Sensitive local data must follow a protected revocation and cleanup policy.
17.3 Concurrent devices
Multiple devices may work offline simultaneously.
The architecture must reconcile their changes through the owning domain rather than assuming a single active device.
17.4 Device replacement
Device replacement should support authorized recovery of eligible data while respecting:
• User identity.
• Resource ownership.
• Classification.
• Retention.
• Secure Vault recovery requirements.
18. Search and Derived Storage
Search indexes and derived artifacts are not authoritative domain records.
They must preserve:
• Source resource reference.
• Source version.
• Domain owner.
• Classification.
• Provenance reference.
• Indexing timestamp.
• Refresh status.
When source permissions change, search visibility must be updated.
When a source is deleted, derived artifacts must follow the deletion policy.
Secure Vault secrets remain excluded from general search indexing by default.
19. Integrity and Recovery
19.1 Integrity checks
Where appropriate, the system should validate:
• Content checksums.
• Object size.
• Resource identity.
• Version consistency.
• Relationship integrity.
• Encryption metadata.
• Backup integrity.
• Synchronization sequence consistency.
19.2 Corruption handling
Corrupted records or objects must not be silently accepted.
The system should:
1. Detect the integrity failure.
2. Preserve diagnostic metadata.
3. Prevent unsafe use.
4. Identify the owning domain.
5. Attempt authorized recovery.
6. Report the outcome.
7. Record the incident.
19.3 Recovery boundaries
A failure in one domain must not automatically corrupt or disable all other domains.
The architecture should support domain-level recovery wherever practical.
20. Observability
Operational metrics should include:
• Local queue depth.
• Pending synchronization count.
• Synchronization latency.
• Retry count.
• Conflict frequency.
• Partial synchronization rate.
• Failed uploads and downloads.
• Storage provider availability.
• Cache hit and invalidation rates.
• Backup completion status.
• Restore success and failure.
• Deletion verification status.
• Integrity failure count.
Metrics and diagnostics must not expose secrets or unnecessary personal data.
21. Acceptance Criteria
• [ ] Every domain has explicit storage ownership.
• [ ] Physical storage and logical ownership are separated.
• [ ] Storage adapters are replaceable.
• [ ] Local and authoritative states are distinguishable.
• [ ] Offline eligibility is defined per operation.
• [ ] Local queues preserve idempotency and version metadata.
• [ ] Interrupted operations can recover safely.
• [ ] Synchronization checkpoints are reliable.
• [ ] Partial synchronization is reported accurately.
• [ ] Duplicate operations are handled safely.
• [ ] Conflict resolution is domain-aware.
• [ ] No universal last-write-wins policy is assumed.
• [ ] Secure Vault storage is isolated.
• [ ] Secrets are not stored in generic queues or caches.
• [ ] Backup and restore respect classification.
• [ ] Deletion stages are explicit.
• [ ] Tombstones prevent stale resurrection where required.
• [ ] Derived artifacts preserve source lineage.
• [ ] Model provenance survives synchronization and restore.
• [ ] Android lifecycle restrictions are addressed.
• [ ] Multi-device revocation is covered.
• [ ] Physical deletion is reported only with evidence.
• [ ] Storage and synchronization tests pass before release.
22. Test Scenarios
22.1 Offline behavior
• Create a Brain note while offline.
• Modify an eligible Files metadata record while offline.
• Queue a permitted Downloads operation.
• Terminate the application before synchronization.
• Restart the device and recover the pending queue.
• Reconnect and verify authoritative synchronization.
• Confirm that local pending state was never shown as remote completion.
22.2 Conflict behavior
• Edit the same note from two devices.
• Delete a resource remotely while it is edited offline.
• Revoke permissions while an offline operation is pending.
• Modify a file externally before synchronization.
• Confirm that conflicts are reported and resolved under domain rules.
• Verify that no silent data loss occurs.
22.3 Duplicate and interrupted operations
• Submit the same operation repeatedly.
• Interrupt an upload.
• Interrupt a download.
• Interrupt a synchronization batch after partial success.
• Restart the client and verify safe recovery.
• Confirm idempotency behavior.
22.4 Secure Vault
• Verify isolation from generic local storage.
• Verify that secrets are absent from logs.
• Verify that generic synchronization cannot retrieve secret values.
• Revoke a device with pending local Vault activity.
• Restore an authorized Vault backup.
• Verify key and authentication requirements.
• Confirm that deletion status reflects actual verification.
22.5 Retention and deletion
• Delete a Files record referenced by Brain.
• Delete a Brain record referencing a file.
• Remove a cached copy without deleting its source.
• Propagate a tombstone to an offline device.
• Reconnect a stale device and prevent unauthorized resurrection.
• Verify derived index and artifact cleanup.
• Verify backup retention behavior.
22.6 Integrity and recovery
• Corrupt a stored object.
• Modify a resource version externally.
• Restore an incompatible backup.
• Interrupt a database migration.
• Verify recovery and failure reporting.
• Confirm that unrelated domains remain available where possible.
23. Open Architectural Decisions
The following decisions remain pending:
1. Local database technology.
2. Remote persistence topology.
3. Physical content storage providers.
4. Storage adapter interfaces.
5. Synchronization protocol.
6. Change-feed or event-based synchronization design.
7. Conflict detection algorithm.
8. Per-domain merge policies.
9. Local queue implementation.
10. Checkpoint and cursor design.
11. Offline eligibility matrix.
12. Device registration and revocation model.
13. Backup architecture.
14. Secure Vault backup and recovery design.
15. Key-management implementation.
16. Retention and tombstone durations.
17. Provider-side physical deletion verification.
18. Search index persistence and invalidation.
19. Android background execution strategy.
20. Local database migration strategy.
21. Multi-device recovery policy.
22. Data residency and cloud deployment requirements.
These decisions must be recorded through the appropriate Mimir ADR process.
24. Implementation and Verification Boundary
This document describes proposed requirements and architecture.
It does not establish that:
• Local persistence has been implemented.
• Synchronization works.
• Offline queues exist.
• Conflict resolution has been tested.
• Backup and restoration are operational.
• Secure Vault storage is isolated in the current implementation.
• Physical deletion is verified.
• Android lifecycle recovery has passed testing.
• Multi-device synchronization is available.
Implementation evidence must be linked to the owning module, contract, tests, and verification results.
Stubs must fail honestly when the required functionality is unavailable.
25. Governance and Change Control
Changes to storage ownership, synchronization, deletion, encryption, or recovery must be reviewed against:
• Architecture & Domain Boundaries.
• Shared Services Contracts.
• Data Model & Entity Relationships.
• API & Event Contracts.
• Security, Privacy & Acceptance Specifications.
• Relevant ADRs.
Changes affecting multiple domains require explicit impact analysis.
No storage optimization may silently weaken domain isolation, security classification, provenance, or deletion guarantees.
26. Final Status
Status: Draft — Pending Architecture, Storage & Security Review
This document defines the proposed storage, synchronization, and offline architecture for Mimir Intelligence.
It is not an implementation report or an approved technology selection.
Official Document ID, version, and approval status must be assigned through the Mimir document registry and governance process.

───

End of Document

---
document_id: MIMIR-DOC-REG-INT-009
document_code: MIMIR-INT-009
filename: MIMIR-INT-009-DATA-MODEL-AND-ENTITY-RELATIONSHIPS.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

Mimir Intelligence — Data Model & Entity Relationships Specification
Document ID: MIMIR-DOC-REG-INT-009
Status: Draft — Pending Architectural & Data Architecture Review
Document Type: Data Architecture & Entity Relationship Specification
Product: Mimir Intelligence
Scope: Brain, Files, Downloads, Secure Vault, Shared References, and Cross-Domain Data Relationships

───

1. Purpose
This document defines the conceptual data model for Mimir Intelligence.
It establishes:
• Core entities owned by each domain.
• Entity identifiers and identity rules.
• Relationships between domains.
• Resource references and external identifiers.
• Ownership and lifecycle boundaries.
• Versioning and provenance relationships.
• Data classification and access metadata.
• Synchronization and conflict considerations.
• Deletion and dependency behavior.
• Data integrity and acceptance requirements.
The objective is to create a consistent data architecture without merging the four domains into one business-data model or making the Kernel responsible for domain-specific entities.
This is a conceptual and logical specification. Physical schemas, database technologies, indexes, and migrations must be defined and verified during implementation.
2. Data Architecture Principles
2.1 Domain-owned data
Each domain owns its entities, records, validation rules, and lifecycle.

Domain
Data ownership

Brain
Knowledge, memory, collections, source relationships, retrieval records

Files
File references, folders, storage locations, file-operation records

Downloads
Download tasks, queues, transfer states, source and destination metadata

Secure Vault
Credentials, passkeys, authenticator records, secure notes, protected documents, wallet records


2.2 Logical separation
A unified user experience does not require a unified physical database.
The implementation may use separate databases, schemas, services, or storage mechanisms, provided ownership and authorization boundaries remain enforceable.
2.3 Stable identifiers
Every domain-owned entity must have a stable identifier.
Identifiers must not depend on:
• Display names.
• File names.
• Folder paths.
• Provider-generated labels.
• Mutable URLs.
• Array positions.
• UI navigation positions.
2.4 References are not copies
A cross-domain relationship should use a reference to the source entity unless a specific operation requires a separate copy or derived artifact.
2.5 References are not permissions
Knowing an entity identifier does not grant access to the entity.
The owning domain must authorize each protected operation.
2.6 Schema evolution
Data models must support controlled evolution without silently breaking existing records, references, or synchronization.
2.7 No central business ownership
Shared identifiers and reference metadata do not make a shared service the owner of domain data.
The Kernel must not contain Intelligence-specific entities or domain-specific relationships.
3. Entity Identification Strategy
3.1 Entity ID
Each domain-owned entity must have a stable identifier.
Conceptual format:
text
<entity-id>

The implementation may choose UUID, ULID, or another suitable globally unique identifier strategy.
The final choice must consider:
• Offline creation.
• Distributed synchronization.
• Collision resistance.
• Storage efficiency.
• Ordering requirements.
• Cross-device operation.
3.2 Domain identity
An entity must be attributable to its owning domain.
Conceptual metadata:
json
{
  "entity_id": "stable-entity-id",
  "owner_domain": "brain",
  "entity_type": "knowledge_note"
}

The values above illustrate the logical model; they do not prescribe a physical database schema.
3.3 Resource identity
The platform must distinguish:
• Internal entity ID.
• External provider ID.
• Storage object ID.
• File URI or location.
• User-visible name.
• Source reference ID.
• Operation ID.
These identifiers are not interchangeable.
3.4 External identifiers
Provider identifiers must be stored in the context of their provider and account or connection identity.
A provider ID must not be assumed globally unique across different providers or accounts.
3.5 Identifier stability
Moving, renaming, or changing the storage location of an entity must not silently change its internal identity.
If a provider forces a new external identifier, the implementation must maintain a verified mapping or record the replacement relationship.
4. Common Entity Metadata
Domain entities may expose a common metadata envelope.
This envelope is conceptual and does not mean all domains must store identical columns.
4.1 Common metadata
json
{
  "entity_id": "entity-id",
  "entity_type": "entity-type",
  "owner_domain": "domain-name",
  "schema_version": 1,
  "created_at": "timestamp",
  "updated_at": "timestamp",
  "created_by": "actor-reference",
  "updated_by": "actor-reference",
  "classification": "confidential",
  "lifecycle_state": "active",
  "version": 1
}

4.2 Metadata requirements
Where applicable, entities should retain:
• Stable identity.
• Owner domain.
• Entity type.
• Schema version.
• Creation and modification timestamps.
• Actor references.
• Classification.
• Lifecycle state.
• Version information.
• Provenance.
• Retention policy reference.
• Synchronization metadata.
4.3 Optional fields
Fields must be optional where they do not apply.
For example:
• A locally created note may not have an external provider ID.
• A provider-backed file may not have a locally owned physical object.
• A Secure Vault item may use specialized metadata that must not be exposed through a generic envelope.
4.4 Avoid redundant metadata
Do not duplicate data across domains merely to make querying easier.
If a field is owned by another domain, use a controlled reference or a documented projection.
5. Brain Entity Model
Brain owns knowledge and memory entities, not the original physical files stored elsewhere.
5.1 Primary entities

Entity
Purpose

KnowledgeSpace
Logical knowledge workspace

KnowledgeCollection
Organized group of knowledge records

KnowledgeNote
Structured or free-form knowledge item

KnowledgeDocument
Managed knowledge document

KnowledgeBlock
Section or block within a document

KnowledgeTag
Classification and discovery label

KnowledgeRelation
Relationship between knowledge records

SourceReference
Reference to original or supporting source

ProcessingRecord
Record of AI-assisted processing

RetrievalRecord
Search or retrieval operation metadata

MemoryRecord
Persisted information selected for future use

MemoryPolicy
Rules governing memory behavior

KnowledgeVersion
Version history where supported


5.2 KnowledgeSpace
Represents a logical knowledge environment.
Conceptual fields:
• Space ID.
• Name.
• Description.
• Owner.
• Access policy.
• Default classification.
• Retention policy.
• Creation and modification metadata.
5.3 KnowledgeCollection
Groups notes, documents, and other knowledge records.
A collection may contain references to records rather than duplicating their content.
5.4 KnowledgeNote
Represents an independently addressable knowledge record.
Possible fields:
• Note ID.
• Title.
• Content.
• Content format.
• Tags.
• Collection references.
• Source references.
• Classification.
• Version.
• Provenance.
• Lifecycle state.
5.5 KnowledgeDocument and KnowledgeBlock
KnowledgeDocument supports structured documents.
KnowledgeBlock represents a logical section or content unit.
Block identity should remain stable where practical so that links and references can survive document edits.
5.6 KnowledgeRelation
Represents relationships such as:
• Related to.
• References.
• Derived from.
• Contradicts.
• Supports.
• Part of.
• Depends on.
Relationships must have explicit semantics.
A graph edge must not automatically imply authorization to access either endpoint.
5.7 SourceReference
Identifies the source from which knowledge was obtained.
Conceptual fields:
• Source reference ID.
• Source domain.
• Source entity ID.
• Source version or revision.
• Source type.
• Source location reference.
• Acquisition timestamp.
• Source availability status.
• Provenance reference.
A SourceReference is not a copy of the source content.
5.8 ProcessingRecord
Records processing operations such as:
• Summarization.
• Extraction.
• Classification.
• Entity recognition.
• Indexing.
• Embedding generation.
• Knowledge synthesis.
It should identify:
• Processing operation.
• Input references.
• Output references.
• Execution status.
• Actor.
• Model provenance.
• Start and completion timestamps.
• Errors or limitations.
5.9 RetrievalRecord
May record relevant retrieval metadata without unnecessarily retaining full queries or sensitive content.
It must follow privacy and retention rules.
5.10 MemoryRecord
Represents information explicitly or policy-appropriately retained as memory.
MemoryRecord must remain distinct from KnowledgeNote.
Possible fields:
• Memory ID.
• Content or structured value.
• Memory category.
• Origin.
• Confidence or validation status where applicable.
• Retention policy.
• Review status.
• Creation and update timestamps.
• Provenance.
5.11 MemoryPolicy
Defines the conditions under which memory may be created, updated, reviewed, retained, or deleted.
Memory policy must not permit automatic retention of restricted secrets.
6. Files Entity Model
Files owns file-management records and references to storage resources.
It must distinguish the logical file record from the actual storage object and from the provider that stores it.
6.1 Primary entities

Entity
Purpose

FileRecord
Logical file entry

StorageLocation
Location or provider reference

StorageProviderConnection
Authorized provider connection

FolderRecord
Logical folder record

FileVersion
File revision metadata

FileReference
Reference to a file from another domain

FileOperation
Copy, move, rename, delete, and related operations

FilePermission
Access metadata where supported

FileMetadataRecord
Additional metadata

FilePreview
Preview or thumbnail metadata

FileShareRecord
Controlled sharing record where supported


6.2 FileRecord
Represents a file as managed by Files.
Possible fields:
• File ID.
• Display name.
• Media type.
• Size.
• Classification.
• Storage location reference.
• External provider reference.
• Current version.
• Parent folder reference.
• Lifecycle state.
• Integrity metadata where available.
6.3 StorageLocation
Represents the storage location of an object.
It must distinguish:
• Local application storage.
• Android Storage Access Framework resource.
• Removable storage where supported.
• Cloud provider storage.
• External provider resource.
A storage location is not necessarily a physical path.
6.4 StorageProviderConnection
Represents an authorized connection to a storage provider.
It should identify:
• Provider type.
• Account or connection reference.
• Connection state.
• Granted scope.
• Authorization expiry where applicable.
• Revocation state.
It must not contain raw provider credentials in ordinary Files metadata.
6.5 FolderRecord
Represents a logical folder or a provider-backed folder reference.
Folder relationships must account for providers that use nontraditional or virtual hierarchies.
6.6 FileVersion
Represents a specific revision or content version.
Where a provider does not expose reliable version identity, the system must not invent one.
6.7 FileOperation
Records file-management operations.
Possible operation types:
• Create.
• Copy.
• Move.
• Rename.
• Delete.
• Restore.
• Export.
• Import.
• Share.
An operation record must distinguish requested, running, completed, partial, failed, and cancelled states.
6.8 FilePreview
A preview is derived data.
It must have its own source relationship, lifecycle, and retention behavior.
A preview must not silently become the authoritative original file.
7. Downloads Entity Model
Downloads owns download tasks and transfer state.
It may reference a destination file managed by Files, but does not become the owner of the Files domain.
7.1 Primary entities

Entity
Purpose

DownloadTask
Logical download request

DownloadSource
Source metadata

DownloadQueue
Queue organization

DownloadAttempt
Individual transfer attempt

TransferSession
Transfer execution state

DownloadDestination
Intended destination

DownloadArtifact
Resulting resource reference

CaptureRecord
Media capture record

DownloadPolicy
Transfer and storage policy


7.2 DownloadTask
Possible fields:
• Download ID.
• Requested source.
• Destination reference.
• User or Agent actor.
• Requested timestamp.
• Current state.
• Priority.
• Retry policy.
• Transfer progress.
• Final result reference.
7.3 DownloadSource
Stores appropriate source metadata.
Sensitive URLs, headers, and authentication-related metadata require protection and redaction.
7.4 DownloadAttempt
Each attempt must be distinguishable.
It should record:
• Attempt ID.
• Parent download ID.
• Start and end timestamps.
• Result.
• Failure reason.
• Transfer metadata.
7.5 TransferSession
Represents an active or recoverable transfer session where supported.
Pause and resume capability must not be assumed for every source or provider.
7.6 DownloadDestination
Represents the intended storage target.
It may reference a Files StorageLocation or another supported destination.
7.7 DownloadArtifact
Represents the resulting resource after a successful transfer.
It should reference the corresponding Files record when Files owns or indexes that resource.
The artifact must not imply that the physical resource exists if completion or destination verification failed.
7.8 CaptureRecord
Represents a media capture request or result, including its source, permission context, and actual outcome.
It must not imply that DRM-protected or restricted content was successfully captured.
8. Secure Vault Entity Model
Secure Vault uses a dedicated data model.
Its records must not be treated as ordinary Brain, Files, or Downloads entities.
8.1 Primary entities

Entity
Purpose

VaultProfile
Vault configuration and security state

VaultItem
Common protected-item identity

CredentialItem
Password credential

PasskeyItem
Passkey metadata and protected references

AuthenticatorItem
Authenticator configuration

SecureNote
Protected note

SecureDocument
Protected document

IdentityRecord
Protected identity information

PaymentCardRecord
Protected payment-card information

WalletRecord
Wallet metadata

SecretReference
Controlled reference to protected secret material

VaultAccessEvent
Security-relevant access record

AutofillRecord
Autofill operation metadata

RecoveryConfiguration
Recovery settings and references

VaultKeyReference
Protected key-management reference


8.2 VaultProfile
Represents the security configuration and state of a vault.
It must not expose cryptographic key material through ordinary profile fields.
8.3 VaultItem
Provides a common identity and lifecycle abstraction for protected items.
It may include:
• Vault item ID.
• Item category.
• Display label.
• Classification.
• Creation timestamp.
• Update timestamp.
• Lifecycle state.
• Protected payload reference.
It must not expose secret values through generic entity queries.
8.4 CredentialItem
Represents a password credential and its associated metadata.
Secret material must be stored and accessed through dedicated protected mechanisms.
8.5 PasskeyItem
Represents passkey metadata and protected key references.
The private key must remain under the applicable secure platform or cryptographic boundary.
8.6 AuthenticatorItem
Represents authenticator configuration.
Seed material and other sensitive values must remain protected.
8.7 SecureNote and SecureDocument
Represent protected content with dedicated encryption, access, and export policies.
8.8 IdentityRecord and PaymentCardRecord
Represent restricted personal and payment information.
Field-level protection may be required depending on the selected storage and threat model.
8.9 SecretReference
A SecretReference identifies a protected secret without exposing its value.
It is not a credential copy and does not grant permission to reveal or use the secret.
8.10 VaultAccessEvent
Records appropriate security events without storing raw secret values.
8.11 AutofillRecord
Records the origin, operation, authorization context, and outcome of an Autofill action without retaining the filled secret.
8.12 RecoveryConfiguration
Represents recovery settings and references to protected recovery material.
Recovery secrets must not be stored as ordinary configuration values.
9. Shared Reference Model
Cross-domain relationships require a common conceptual reference model.
9.1 ResourceReference
A ResourceReference identifies an entity owned by a domain.
Conceptual representation:
json
{
  "reference_id": "reference-id",
  "owner_domain": "files",
  "entity_type": "file",
  "entity_id": "file-id",
  "resource_version": "version-reference",
  "reference_state": "available"
}

9.2 Reference properties
A reference should identify:
• Reference ID.
• Owner domain.
• Entity type.
• Entity ID.
• Version or revision where applicable.
• Reference state.
• Creation time.
• Originating operation where relevant.
9.3 Reference state
Possible states:
• Available.
• Unavailable.
• Access denied.
• Deleted.
• Superseded.
• Pending synchronization.
• Unknown.
A reference state is informational and does not replace an authorization check.
9.4 Reference validation
Before using a reference, the destination domain must validate it through the owning domain or an approved contract.
9.5 Reference expiration
Some references or capabilities may expire.
The system must distinguish stable resource identity from temporary access authorization.
10. Cross-Domain Relationship Definitions
10.1 Files → Brain
A Brain SourceReference may point to a Files FileRecord.
Relationship:
text
KnowledgeRecord
    └── SourceReference
            └── Files.FileRecord

The Brain record owns its knowledge content.
Files owns the original file-management record.
10.2 Downloads → Files
A DownloadArtifact may reference a Files FileRecord.
Relationship:
text
Downloads.DownloadTask
    └── DownloadArtifact
            └── Files.FileRecord

The download task remains owned by Downloads.
The resulting file record remains owned by Files.
10.3 Chat → Brain
A conversation output saved to Brain creates a Brain-owned record.
The saved record may retain a provenance reference to its originating conversation and message.
10.4 Chat → Files
An attachment saved to Files creates or references a Files-owned record.
The conversation may retain a reference to that record.
10.5 Forge → Files
A Forge project may reference authorized Files resources.
Forge retains project and engineering ownership.
Files retains file-management ownership.
10.6 Secure Vault → Browser
An Autofill operation may reference a Vault item through a controlled SecretReference and a dedicated authorization flow.
Browser must not obtain general access to the Vault entity or its underlying secret material.
10.7 Agents and Workflows
Agent and Workflow execution records may reference Intelligence entities.
These references must include execution context and authorization metadata where applicable.
They do not transfer domain ownership.
11. Relationship Cardinality
The following are conceptual relationships. Actual constraints must be adapted to provider and storage capabilities.

Relationship
Cardinality

KnowledgeSpace → KnowledgeCollection
One-to-many

KnowledgeCollection → KnowledgeNote
Many-to-many or one-to-many according to ownership design

KnowledgeDocument → KnowledgeBlock
One-to-many

KnowledgeNote → SourceReference
One-to-many

SourceReference → Source Entity
Many-to-one

KnowledgeNote → KnowledgeRelation
Many-to-many

DownloadTask → DownloadAttempt
One-to-many

DownloadTask → DownloadArtifact
Zero-to-many

DownloadArtifact → Files.FileRecord
Zero-or-one per artifact reference

FileRecord → FileVersion
One-to-many where supported

FolderRecord → FileRecord
One-to-many or provider-defined

VaultProfile → VaultItem
One-to-many

VaultItem → VaultAccessEvent
One-to-many

VaultItem → AutofillRecord
Zero-to-many

AgentRun → ResourceReference
Zero-to-many


The implementation must document any deviations from these conceptual relationships.
12. Provenance and Lineage Model
12.1 Provenance entity
The platform must preserve provenance for model-produced and derived content.
Conceptual provenance information includes:
• Provenance ID.
• Origin type.
• Origin reference.
• Actor.
• Model and provider information where applicable.
• Execution location.
• Processing operation.
• Input references.
• Output references.
• Timestamps.
• Status.
• Handoff relationships.
12.2 Lineage
The system should be able to trace:
• Original source.
• Processing operation.
• Derived content.
• Subsequent revisions.
• Exported or copied derivatives.
12.3 Model attribution
Provenance must distinguish:
• User-authored content.
• Model-generated content.
• Model-edited content.
• Tool-generated output.
• External source content.
• Human-reviewed or approved content.
12.4 Provenance integrity
Provenance must not be silently discarded when content moves between domains.
A missing provenance field must remain unknown rather than being filled with an invented model or provider.
12.5 Provenance and versioning
The implementation must determine how provenance relates to individual versions.
A changed document must not automatically inherit an inaccurate claim that every part was generated by the same model.
13. Versioning and Change Management
13.1 Entity version
Entities that support versioning must have a version identity or revision mechanism.
13.2 Content version
The implementation must distinguish:
• Entity metadata changes.
• Content changes.
• Storage-provider revisions.
• Derived content revisions.
• Synchronization versions.
13.3 Version references
A source reference should identify a specific version when available.
If only the latest version is accessible, that limitation must be recorded.
13.4 Conflict handling
Concurrent updates must not silently overwrite one another.
Conflict resolution must follow the owning domain's policy.
13.5 Historical records
Version history and audit history are separate concepts.
Retaining a version does not automatically satisfy audit requirements.
14. Lifecycle State Model
Each domain must define its own lifecycle states.
A common conceptual state vocabulary may include:
• Draft.
• Active.
• Processing.
• Pending.
• Failed.
• Archived.
• Suspended.
• Deleted.
• Unavailable.
Not all states apply to every entity.
14.1 State ownership
The owning domain is authoritative for the lifecycle state of its entities.
Another domain may cache a status for display, but it must not silently change the authoritative state.
14.2 State transitions
Transitions must be validated.
For example, a failed DownloadTask must not be presented as a completed transfer.
14.3 Deletion
Deletion must distinguish record removal, reference invalidation, provider deletion, and backup retention.
14.4 Tombstones
Where distributed synchronization requires deletion markers, tombstones must be designed with appropriate retention and privacy controls.
15. Data Classification and Security Metadata
15.1 Classification fields
Entities may carry classification metadata appropriate to their domain.
15.2 Inheritance
Derived content must preserve an appropriate relationship to source classification.
15.3 Classification changes
Changing classification must trigger appropriate re-evaluation of:
• Access.
• Search visibility.
• Model processing eligibility.
• Synchronization.
• Export.
• Retention.
• Notifications.
15.4 Secure Vault exception
Secure Vault must not expose its complete item metadata through general entity discovery or search.
Only explicitly approved minimal references may cross its security boundary.
16. Search and Index Data Model
Search indexes and derived retrieval data must remain distinguishable from authoritative records.
16.1 SearchIndexEntry
A conceptual search entry may contain:
• Index entry ID.
• Owner domain.
• Source entity reference.
• Source version.
• Indexed content representation.
• Classification.
• Access-policy reference.
• Index timestamp.
• Index state.
16.2 Index lifecycle
When a source changes or becomes inaccessible, its search entry must be updated or invalidated according to the applicable policy.
16.3 Search result references
Search results must identify the original owner domain and source entity.
16.4 Secure Vault exclusion
Secure Vault secret values must not enter general SearchIndexEntry records.
16.5 Derived embeddings
Embeddings are derived data.
Their ownership, access, retention, deletion, and regeneration behavior must be documented.
17. Synchronization Metadata Model
17.1 Synchronization state
Entities that participate in synchronization may retain:
• Sync state.
• Last synchronization timestamp.
• Source device or replica reference.
• Local revision.
• Remote revision.
• Conflict reference.
• Pending operation reference.
17.2 ConflictRecord
A conceptual ConflictRecord may identify:
• Conflict ID.
• Entity reference.
• Local version.
• Remote version.
• Conflict type.
• Detection time.
• Resolution status.
• Resolution actor.
17.3 Offline-created entities
Offline creation must support stable identity without requiring immediate server availability.
17.4 Sync ownership
Synchronization must not transfer data ownership to the synchronization service.
17.5 Sensitive data
Secure Vault synchronization must use its own approved security model.
18. Data Retention and Deletion Relationships
18.1 Dependency tracking
The system must identify relevant dependencies without granting unrestricted access to dependent records.
18.2 Deletion impact
Before deleting a source, the owning domain should identify the relevant references and dependent records according to policy.
18.3 Derived content
Derived content must have an explicit lifecycle relationship to its source.
18.4 Independent ownership
Deleting a source file must not silently delete independently owned Brain records.
Deleting a Brain record must not delete the source file.
18.5 Backup and retention
A logical deletion must not be represented as permanent physical deletion unless all applicable storage layers have been addressed and verified.
19. Data Integrity Constraints
The implementation must enforce, where applicable:
1. Unique stable entity identifiers.
2. Valid owner-domain attribution.
3. Valid entity-type attribution.
4. Referential integrity within each domain.
5. Valid cross-domain references.
6. Valid lifecycle transitions.
7. Valid schema versions.
8. Valid provenance relationships.
9. Consistent version references.
10. No unauthorized relationship traversal.
11. No secret material in general reference metadata.
12. No fabricated provider or storage identifiers.
13. No duplicate records caused by retried operations where idempotency is required.
14. No silent conversion of unavailable references into successful resources.
20. Data Migration and Schema Evolution
20.1 Schema versioning
Each domain must version its own data schema.
20.2 Migration requirements
Migrations must define:
• Source schema.
• Target schema.
• Transformation rules.
• Compatibility requirements.
• Rollback or recovery strategy.
• Data integrity checks.
• Migration evidence.
20.3 Backward compatibility
The system should preserve compatibility where feasible.
Breaking changes must be explicit and accompanied by migration plans.
20.4 Unknown fields
Migration and serialization logic must avoid silently destroying fields that are not understood when safe preservation is possible.
20.5 Domain independence
A change in one domain's internal schema must not require unrelated domains to adopt its internal data model.
Cross-domain contracts must remain stable or be versioned explicitly.
21. Conceptual Entity Relationship Diagram
mermaid
erDiagram
    BRAIN_SPACE ||--o{ BRAIN_COLLECTION : contains
    BRAIN_COLLECTION }o--o{ BRAIN_NOTE : organizes
    BRAIN_NOTE ||--o{ SOURCE_REFERENCE : cites
    BRAIN_NOTE ||--o{ PROCESSING_RECORD : produces
    BRAIN_NOTE ||--o{ KNOWLEDGE_RELATION : participates

    FILE_RECORD ||--o{ FILE_VERSION : has
    FOLDER_RECORD ||--o{ FILE_RECORD : organizes
    STORAGE_LOCATION ||--o{ FILE_RECORD : stores

    DOWNLOAD_TASK ||--o{ DOWNLOAD_ATTEMPT : has
    DOWNLOAD_TASK ||--o{ DOWNLOAD_ARTIFACT : produces
    DOWNLOAD_ARTIFACT o|--o| FILE_RECORD : references

    VAULT_PROFILE ||--o{ VAULT_ITEM : contains
    VAULT_ITEM ||--o{ VAULT_ACCESS_EVENT : records
    VAULT_ITEM ||--o{ AUTOFILL_RECORD : supports

    SOURCE_REFERENCE }o--|| FILE_RECORD : may_reference
    AGENT_RUN }o--o{ RESOURCE_REFERENCE : uses
    WORKFLOW_RUN }o--o{ RESOURCE_REFERENCE : uses

This diagram is conceptual. It does not prescribe a single relational database or require all entities to be stored in one database.
22. Acceptance Criteria
Domain ownership
• [ ] Each entity has an identifiable owning domain.
• [ ] Domain-owned records are not directly modified by unrelated domains.
• [ ] Shared references do not transfer ownership.
• [ ] Kernel remains independent of domain-specific entities.
Identity
• [ ] Stable identifiers are used.
• [ ] Internal and provider identifiers are distinguished.
• [ ] Resource identity survives supported rename and move operations.
• [ ] Duplicate identity collisions are detected and handled.
Relationships
• [ ] Cross-domain references identify their source domain and entity.
• [ ] Reference validation is performed before protected use.
• [ ] References do not imply permissions.
• [ ] Unavailable and deleted references are represented accurately.
Provenance
• [ ] Derived records retain source relationships.
• [ ] Model provenance survives handoffs and synchronization.
• [ ] User-authored and model-generated content are distinguishable.
• [ ] Missing provenance is not fabricated.
Lifecycle and versioning
• [ ] Lifecycle state is authoritative in the owning domain.
• [ ] Version references are consistent.
• [ ] Deletion respects cross-domain ownership.
• [ ] Tombstones and retention behavior are documented.
• [ ] Schema migrations preserve data integrity.
Security and privacy
• [ ] Classification metadata is applied where appropriate.
• [ ] Secure Vault secrets are not exposed through generic references.
• [ ] Search indexes follow source authorization.
• [ ] Sensitive metadata receives appropriate protection.
• [ ] Derived data follows retention and deletion rules.
Synchronization
• [ ] Offline-created entities receive stable identities.
• [ ] Conflicts are detected.
• [ ] Synchronization does not silently overwrite protected data.
• [ ] Restore preserves ownership and access boundaries.
23. Required Data Model Test Scenarios
1. Create a Brain note with a Files source reference.
2. Rename the referenced file without breaking its stable identity.
3. Move a file between supported storage locations.
4. Revoke access to a referenced file.
5. Delete a source file while retaining a Brain-derived record under policy.
6. Create a download and associate its completed artifact with a Files record.
7. Retry artifact registration without creating unintended duplicates.
8. Create an offline entity and synchronize it later.
9. Resolve conflicting updates.
10. Migrate an entity to a new schema version.
11. Preserve unknown safe fields during migration where required.
12. Retrieve a Search result and verify source authorization.
13. Verify that Secure Vault secrets are excluded from general search data.
14. Preserve provenance through processing and export.
15. Verify that a Secure Vault reference cannot reveal secret material.
16. Verify that a deleted reference is not reported as available.
17. Restore data without reintroducing revoked permissions.
18. Validate relationships between Agent runs and resource references.
19. Validate Workflow resource references after retry.
20. Verify that provider identifiers are scoped to the correct connection.
24. Open Data Architecture Decisions
The following decisions require review before physical implementation:
1. Final global identifier format.
2. Domain database and storage separation strategy.
3. Shared metadata envelope implementation.
4. Cross-domain reference resolution mechanism.
5. Provider identity and account scoping.
6. Knowledge graph storage approach.
7. Versioning strategy for large files and documents.
8. Provenance storage and lineage representation.
9. Search-index data model and invalidation strategy.
10. Offline identity generation and collision handling.
11. Tombstone retention and garbage collection.
12. Cross-domain dependency discovery mechanism.
13. Schema migration and compatibility policy.
14. Secure Vault entity storage and key hierarchy.
15. Data export and import mapping.
No decision in this list should introduce a central business-data owner or weaken domain isolation.
25. Implementation and Verification Boundary
This specification does not establish that:
• Physical database schemas exist.
• All entities have been implemented.
• Referential integrity is enforced.
• Cross-domain references are operational.
• Provenance survives every data path.
• Synchronization is complete.
• Schema migrations have been tested.
• Secure Vault storage has been independently reviewed.
• Deletion reaches all storage and backup layers.
These claims require implementation evidence, test results, and appropriate review.
26. Relationship to Other Specifications
This document must be read with:
• Mimir Intelligence — Architecture & Domain Boundaries.
• Mimir Intelligence — Shared Services Contracts.
• Mimir Intelligence — Integration Specification.
• Mimir Intelligence — Security, Privacy & Acceptance Specifications.
• Mimir Brain — Functional & UX Specification.
• Mimir Files — Functional & UX Specification.
• Mimir Downloads — Functional & UX Specification.
• Mimir Secure Vault — Functional, UX & Security Specification.
• Platform-wide Model Provenance and Execution Transparency Specification.
• Mimir Platform Architecture and governing ADRs.
If a conflict occurs, approved architecture decisions and domain ownership rules take precedence.

───

End of Document
Status: Draft — Pending Architectural & Data Architecture Review
Document ID: MIMIR-DOC-REG-INT-009
Implementation Status: Not established by this specification
Schema Verification Status: Pending implementation and evidence

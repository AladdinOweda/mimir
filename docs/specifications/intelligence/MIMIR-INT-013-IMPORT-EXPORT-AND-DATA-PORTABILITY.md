---
document_id: MIMIR-DOC-REG-INT-013
document_code: MIMIR-INT-013
filename: MIMIR-INT-013-IMPORT-EXPORT-AND-DATA-PORTABILITY.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

───

Mimir Intelligence — Import, Export & Data Portability Specification

Document Control

Field| Value
Document Title| Mimir Intelligence — Import, Export & Data Portability Specification
Document ID| "MIMIR-DOC-REG-INT-013"
Version| Draft — Initial Specification
Status| Draft — Pending Architecture, Security & Data Governance Review
Product| Mimir Intelligence
Scope| Brain, Files, Downloads, Secure Vault, and shared Import/Export services
Dependencies| Architecture & Domain Boundaries; Shared Services Contracts; Data Model & Entity Relationships; API & Event Contracts; Storage, Sync & Offline Architecture; Search & Retrieval Architecture; Security, Privacy & Acceptance Specifications
Approval| Not yet approved
Implementation Status| Not verified
Test Status| Not verified

───

1. Purpose

Define a unified, secure, auditable, and domain-aware import/export capability for Mimir Intelligence.

The specification establishes how users can:

• Import data from local devices, supported files, external storage providers, and compatible applications.
• Export selected data in portable formats.
• Preserve ownership, structure, metadata, relationships, provenance, and applicable security classifications.
• Move their data between Mimir and other compatible systems without unnecessary vendor lock-in.
• Inspect and approve changes before imported data is committed.
• Recover safely from interrupted, invalid, or partially completed operations.

Import and Export are shared capabilities, not independent Engines and not replacements for the responsibilities of individual domains.

2. Scope

2.1 Included

• Import and export orchestration.
• Format identification and compatibility validation.
• Data parsing, transformation, mapping, and serialization.
• Preview and user approval.
• Duplicate detection and conflict resolution.
• Domain-specific import/export behavior.
• Cross-domain references and dependencies.
• Integrity verification and portable package manifests.
• Encryption and protected handling of sensitive exports.
• Offline operation and Android storage integration.
• External provider adapters.
• Audit records, temporary staging, cleanup, and recovery.

2.2 Excluded

This specification does not redefine:

• The internal data model owned by each domain.
• General storage and synchronization architecture.
• Search and retrieval behavior.
• The general authorization model.
• The full retention and deletion policy.
• Secure Vault cryptographic architecture.
• Provider-specific implementations.

These remain governed by their respective specifications and contracts.

3. Architectural Principles

1. Domain ownership: Each domain remains authoritative for its own records and lifecycle.
2. Shared orchestration: Common import/export workflows are implemented through shared services.
3. Kernel neutrality: The Kernel must not know Intelligence domains, file formats, providers, or business-specific import/export rules.
4. Explicit authorization: A reference to data does not grant permission to export or import it.
5. User control: The user must understand the scope and consequences of an operation before committing it.
6. Preview before commit: Where an operation changes existing or creates substantial data, provide a preview before final commitment.
7. Integrity: Imported and exported data must be verifiable for completeness and corruption.
8. Portability: Prefer documented, interoperable formats and avoid unnecessary proprietary dependencies.
9. Security by default: Untrusted content is treated as untrusted regardless of its source.
10. Honest capability reporting: Unsupported formats, incomplete exports, and unavailable provider features must be reported accurately.
11. Recoverability: Interruptions must not silently produce an inconsistent or falsely successful result.
12. No implicit cross-domain authority: One domain cannot export another domain's data merely because it holds a reference to it.

4. Ownership and Domain Boundaries

4.1 Mimir Brain

Brain owns its knowledge structures, including:

• Notes and documents managed as knowledge.
• Markdown content.
• WikiLinks and Backlinks.
• Tags and collections.
• Knowledge graph relationships.
• Knowledge-specific attachments and references.
• Knowledge versions and applicable provenance.

Brain does not own the underlying file merely because a note references it.

4.2 Mimir Files

Files owns file-management records and operations, including:

• File and folder metadata.
• Folder hierarchy.
• File references and applicable storage locations.
• File operations and supported version information.
• File classification and metadata owned by Files.

The actual binary content may be stored by an external or local storage provider. Export must respect the provider's access and authorization constraints.

4.3 Mimir Downloads

Downloads owns download-related records and their associated state, including where supported:

• Download history.
• Source references.
• Download metadata.
• Queue and task records.
• Available downloaded content.
• Applicable status and timestamps.

An export must distinguish between a download record and the actual downloaded binary. A history export must not be presented as a complete content archive if the binaries are not included.

4.4 Mimir Secure Vault

Secure Vault owns protected records, including:

• Passwords and credentials.
• Passkeys and associated metadata where export is supported.
• Authenticator-related data where technically and securely supported.
• Secure Notes.
• Secure Documents and IDs.
• Cards and digital wallet records.
• Autofill-related protected data.

Secure Vault requires a separate protected import/export path.

Its secrets must never be silently included in generic Brain, Files, or Downloads archives.

5. Import and Export Architecture

The architecture consists of the following logical responsibilities:

Responsibility| Description
Import/Export Coordinator| Coordinates operation lifecycle and shared workflow
Format Detection| Identifies supported formats and package types
Parser and Serializer Adapters| Interpret incoming data and produce outgoing data
Validation Pipeline| Validates structure, schema, integrity, and constraints
Mapping and Transformation| Maps external representations to domain-owned models
Preview and Approval| Presents proposed changes and obtains user authorization
Domain Import Handlers| Apply domain-specific import rules
Domain Export Handlers| Produce domain-owned export data
Package Manifest| Describes contents, versions, checksums, and dependencies
Integrity Service| Verifies package and content integrity
Staging Manager| Manages temporary data during processing
Audit Integration| Records relevant operation events
Recovery Coordinator| Handles interruption, retry, and safe recovery

These are logical responsibilities. They do not require separate Engines or independently deployable services.

5.1 Provider Neutrality

External storage and application integrations must use provider adapters behind Mimir-owned contracts.

The architecture must not hard-code a specific cloud provider or external application into the Kernel or domain models.

Provider-specific behavior belongs in adapters and integration implementations.

6. Import Scenarios

Supported scenarios may include:

1. Importing individual files.
2. Importing folders and folder hierarchies.
3. Importing a multi-file archive.
4. Importing Brain notes and knowledge collections.
5. Importing compatible data from external knowledge applications.
6. Importing file metadata and available binaries.
7. Importing Downloads history and supported content.
8. Importing a Mimir-generated portable package.
9. Importing supported Secure Vault packages through the protected workflow.
10. Restoring data from a previous export where the relevant restore contract permits it.

The availability of each scenario depends on actual format support, permissions, provider capabilities, and domain validation.

7. Import Pipeline

The standard import lifecycle is:

1. Source selection.
2. Source authorization.
3. File or package acquisition.
4. Temporary staging.
5. Format detection.
6. Security inspection.
7. Parsing.
8. Structural validation.
9. Schema and compatibility validation.
10. Metadata and relationship extraction.
11. Mapping and transformation.
12. Duplicate and conflict analysis.
13. Import preview.
14. User approval.
15. Commit preparation.
16. Domain-level commit.
17. Integrity and consistency verification.
18. Completion reporting.
19. Staging cleanup.

7.1 Source Selection

Sources may include:

• Android local storage.
• User-selected files and folders.
• Supported external storage providers.
• Compatible application exports.
• Previously generated Mimir packages.
• Other explicitly authorized sources.

On Android, the implementation should use appropriate system document-selection and storage-access mechanisms, including the Storage Access Framework where applicable.

Mimir must not assume unrestricted filesystem access.

7.2 Format Detection

Format detection must use more than filename extensions.

Where applicable, detection should consider:

• File signature or magic bytes.
• MIME type.
• Container structure.
• Package manifest.
• Declared format and schema version.
• Parser compatibility.

An extension mismatch must not automatically establish that a file is malicious, but it must trigger appropriate validation.

Unknown or unsupported formats must be rejected or offered as ordinary files without attempting unsafe interpretation.

7.3 Security Inspection

Before parsing untrusted content:

• Inspect archive structure.
• Detect suspicious or malformed content.
• Enforce size and resource limits.
• Detect archive expansion abuse and zip bombs.
• Prevent path traversal and unsafe extraction.
• Reject or quarantine prohibited executable content according to policy.
• Use malware-scanning integration where available.
• Isolate risky parsers and document processors.
• Avoid automatic execution of imported scripts, macros, or embedded actions.

A successful malware scan does not establish that a file is trustworthy or semantically safe.

7.4 Parsing and Validation

Validation must cover:

• Package structure.
• Required fields.
• Data types.
• Field constraints.
• Supported schema versions.
• Reference validity.
• Relationship consistency.
• Binary integrity.
• Metadata consistency.
• Domain-specific invariants.

Invalid records must not be silently converted into valid-looking records.

7.5 Mapping and Transformation

When importing from an external format:

• Map fields explicitly.
• Preserve source values where practical.
• Record transformations that materially change representation.
• Preserve unknown fields when safe and technically possible.
• Report fields that cannot be mapped.
• Avoid silently discarding meaningful relationships or metadata.

Mappings must not invent ownership, provenance, permissions, or timestamps.

7.6 Preview and Approval

The preview should communicate:

• Source and detected format.
• Target domain.
• Number of records and files.
• Estimated data size.
• New records.
• Potential duplicates.
• Conflicts with existing data.
• Unsupported or ignored elements.
• Security warnings.
• Relationships that cannot be restored.
• Whether the operation is reversible.

For high-impact or protected operations, explicit confirmation is mandatory.

7.7 Commit

The commit phase must:

• Revalidate authorization.
• Recheck relevant destination state.
• Respect domain ownership.
• Apply validated changes through domain contracts.
• Preserve transaction boundaries where supported.
• Record operation progress.
• Avoid reporting success before verification.

If full atomicity across domains is unavailable, the operation must expose its partial-completion state and recovery options.

8. Export Scenarios

Supported scenarios may include:

• Exporting one record.
• Exporting selected records.
• Exporting a collection.
• Exporting a folder or hierarchy.
• Exporting a domain-specific archive.
• Exporting selected Intelligence domains.
• Exporting a portable Mimir package.
• Exporting a backup or restore package where supported.
• Exporting Secure Vault data through a separate protected workflow.

Export scope must always be explicit.

The user must be able to distinguish metadata-only exports from exports that include actual binary content.

9. Export Pipeline

The standard export lifecycle is:

1. Scope selection.
2. Authorization.
3. Dependency and reference analysis.
4. Export preview.
5. User approval.
6. Consistency snapshot or equivalent consistency mechanism.
7. Data preparation.
8. Serialization.
9. Binary inclusion where authorized.
10. Manifest generation.
11. Integrity calculation.
12. Optional encryption.
13. Package verification.
14. Delivery to the selected destination.
15. Completion reporting.
16. Temporary staging cleanup.

9.1 Consistency

The export should represent a coherent state of the selected data.

Where a true atomic snapshot is unavailable, the system must:

• Record the effective export time or range.
• Identify inconsistent or changing records where relevant.
• Avoid presenting an inconsistent result as a complete snapshot.
• Report any records omitted because they changed or became inaccessible.

9.2 Authorization

Authorization must be checked at the time of export and, where appropriate, again before delivery.

The system must not assume that selecting a record or folder automatically grants permission to export its contents.

9.3 Encryption

Encryption requirements depend on the data classification and export destination.

Protected data must use approved cryptographic mechanisms defined by the relevant security and key-management contracts.

Encryption keys and recovery material must not be embedded in the same unprotected package as the protected content.

9.4 Delivery

Delivery destinations may include:

• Android local storage.
• User-selected document providers.
• Supported external storage providers.
• Other explicitly authorized destinations.

The system must report whether the destination accepted the complete output.

A generated package in temporary staging is not equivalent to a successfully delivered export.

10. Portable Package and Manifest

Mimir should define a versioned portable package contract.

The exact archive container, serialization format, and compression method remain subject to architecture and compatibility decisions.

10.1 Conceptual Manifest

The following is illustrative only and is not an approved wire format:

{
"package_type": "mimir.portable-data",
"package_schema_version": "pending",
"created_at": "timestamp",
"producer": {
"application": "Mimir",
"version": "application-version"
},
"export_scope": {
"domains": [],
"record_count": 0,
"includes_binary_content": false
},
"contents": [],
"relationships": [],
"integrity": {
"algorithm": "approved-algorithm",
"entries": []
}
}

10.2 Manifest Requirements

A final package contract should define:

• Package type.
• Package schema version.
• Producer identity and version.
• Export timestamp.
• Export scope.
• Included domains.
• Record and file counts.
• Binary inclusion status.
• Content identifiers.
• Relationship references.
• Data classification.
• Integrity information.
• Encryption metadata where applicable.
• Compatibility requirements.
• Partial-export indicators.

The manifest must not contain secrets or sensitive values unnecessarily.

11. Format Strategy

Mimir should support interoperable formats where practical.

Potential format categories include:

Data Category| Candidate Formats
Text and notes| Markdown, plain text
Structured records| JSON or other documented structured formats
Tabular data| CSV where relationships and data types can be represented safely
Documents| Original supported document formats
Images and media| Original binary formats
Folder collections| A documented archive package
Mimir portable data| Versioned Mimir package contract
Secure Vault| Dedicated protected format

These are candidate formats, not a final mandatory compatibility matrix.

A format must only be advertised as supported after its parser, serializer, security handling, and compatibility behavior have been verified.

12. Domain-Specific Requirements

12.1 Brain Import and Export

Brain portability should preserve, where supported:

• Notes and document content.
• Markdown formatting.
• Stable record identifiers or portable identifiers.
• WikiLinks.
• Backlinks or enough information to reconstruct them.
• Tags.
• Collections.
• Knowledge graph relationships.
• Attachments and their references.
• Source references.
• Version information.
• Creation and modification timestamps.
• Applicable provenance and classification metadata.

Relationship Handling

If an imported note references another note:

• Resolve the reference using stable identifiers where available.
• Use a documented mapping process when identifiers change.
• Report unresolved references.
• Avoid silently redirecting references to unrelated records.

Backlinks may be reconstructed from valid forward relationships if the domain contract defines them as derived data.

Memory Separation

Brain Knowledge must remain distinct from system Memory.

An ordinary Brain export must not silently include personal or operational Memory records.

Memory portability must follow its own ownership, authorization, and export rules.

12.2 Files Import and Export

Files portability should preserve, where supported:

• Folder hierarchy.
• File names.
• File metadata.
• Binary content.
• File classifications.
• Version information.
• Source and storage references.
• Creation and modification timestamps.
• Ownership and access metadata where portable and authorized.

External References

A provider-specific URI may not remain usable outside its original provider.

The export must distinguish between:

• Binary content included.
• Metadata only.
• External reference only.
• Content unavailable.
• Content excluded due to permission restrictions.

Name and Path Safety

Import must prevent:

• Path traversal.
• Unsafe absolute paths.
• Invalid or reserved names.
• Unintended overwrites.
• Collision-based data loss.

12.3 Downloads Import and Export

Downloads portability should distinguish:

• Download history.
• Source URL or reference.
• Download metadata.
• Download state.
• Actual downloaded binary.
• Provider-specific information.

Sensitive source references, authentication tokens, or temporary access URLs must not be exported without explicit authorization and an appropriate security policy.

An imported history record must not be represented as a successfully completed download unless the required evidence exists.

12.4 Secure Vault Import and Export

Secure Vault must use a separate protected workflow.

Requirements:

1. Never include Secure Vault secrets in a generic Intelligence archive.
2. Require explicit entry into the protected import/export flow.
3. Reauthenticate the user using an approved authentication mechanism.
4. Explain the scope and sensitivity of the operation.
5. Require explicit approval before committing or delivering protected data.
6. Encrypt protected export packages using approved cryptographic mechanisms.
7. Prevent secrets from appearing in logs, previews, error messages, analytics, or ordinary notifications.
8. Keep temporary plaintext exposure to the minimum necessary.
9. Clean up temporary protected material according to the applicable security policy.
10. Record security-relevant events without recording secret values.

Passkeys and Authenticator Data

Passkeys may depend on platform authenticators, hardware-backed keys, relying-party rules, or provider-specific mechanisms.

Mimir must not promise that all passkeys can be exported as portable private-key material.

Where direct portability is unsupported, provide an honest explanation and a safe alternative, such as re-registration or platform-supported transfer.

Authenticator secrets and recovery codes require separate protection and explicit authorization.

Recovery and Key Policy

Protected export and restoration must comply with Secure Vault key-management and recovery contracts.

A user must not be led to believe that an encrypted archive can be restored if the necessary recovery material is unavailable.

The precise recovery model remains governed by the Secure Vault security architecture.

13. Cross-Domain References and Dependencies

A record in one domain may reference an object owned by another domain.

Export behavior must distinguish:

• The owning record.
• The referenced record.
• The referenced binary.
• Derived information.
• External provider resources.

A cross-domain reference does not grant permission to export the referenced object.

13.1 Dependency Policies

For each reference, the export process must determine whether to:

• Include the referenced object, if authorized and within scope.
• Include a portable reference.
• Include a documented unresolved-reference marker.
• Exclude the reference with a clear warning.

The import process must not create unauthorized objects merely to satisfy a reference.

13.2 Relationship Preservation

When a package contains several related domains, it should preserve portable identifiers and relationship mappings.

If a dependency cannot be restored, the user must be informed before commit where the consequence is material.

14. Provenance and Model Metadata

Import and export must preserve applicable provenance metadata.

This includes, where available and relevant:

• Original source.
• Source references.
• Record origin.
• Creation and modification timestamps.
• Version information.
• Classification.
• Transformation history.
• Model and provider attribution.
• Local or cloud execution type.
• Model role and contribution.
• Generation or execution timestamps.
• Status.
• Participating models.
• Final composing model where applicable.
• Handoff and contribution relationships.

Import must distinguish between original metadata and metadata created by the import operation.

The importing application must not overwrite original creation time or model attribution with the import timestamp.

Where a source format cannot represent a provenance field, the export must preserve it in an appropriate documented metadata structure where feasible, or report its omission.

User-authored content must never be falsely attributed to an AI model.

15. Schema Compatibility and Evolution

Portable packages must declare their schema version.

The import system should support:

• Known compatible versions.
• Explicit version migrations.
• Validation of required fields.
• Preservation of safe unknown fields.
• Clear rejection of unsupported versions.
• Reporting of lossy transformations.

15.1 Migration Rules

Migrations must be:

• Versioned.
• Deterministic where possible.
• Testable.
• Auditable.
• Explicit about data loss.
• Reversible where technically feasible.

A migration must not silently reinterpret an unknown field as a different field.

The application version and package schema version are separate concepts.

16. Duplicate Detection and Conflict Resolution

Duplicate detection may use:

• Stable identifiers.
• Content hashes.
• Source identifiers.
• Metadata similarity.
• Domain-specific matching rules.

Content similarity alone must not automatically establish that two records are identical.

16.1 Conflict Options

Depending on the domain and record type, supported policies may include:

• Skip existing records.
• Import as a new record.
• Update an existing record.
• Keep both versions.
• Apply a user-selected resolution.
• Reject the conflicting record.

The default policy must prioritize avoiding unintended data loss.

16.2 Idempotency

Repeated execution of the same import operation must not create uncontrolled duplicates.

The implementation should use operation identifiers and stable source references where available.

An interrupted operation must be distinguishable from a new independent import.

17. Partial Failure, Resume, and Rollback

The operation coordinator must represent the difference between:

• Pending.
• Running.
• Awaiting approval.
• Completed.
• Completed with warnings.
• Partially completed.
• Failed.
• Cancelled.
• Recovery required.

17.1 Partial Failure

If some records fail:

• Report successful and failed items separately.
• Preserve error details without exposing secrets.
• Identify whether retry is safe.
• Avoid claiming that the whole package was imported successfully.

17.2 Resume

Where supported, resumption must continue from a verified checkpoint.

It must not blindly repeat non-idempotent actions.

17.3 Rollback

Rollback should be supported where the underlying domain operations allow it.

If a complete rollback is impossible:

• Report the remaining changes.
• Identify affected records.
• Provide a safe recovery path.
• Do not claim atomicity.

18. Security and Privacy Requirements

The import/export system must comply with the security and authorization contracts.

Required protections include:

• Least-privilege capability scopes.
• Revalidation of permissions at commit and delivery.
• Isolation of untrusted parsers.
• Archive expansion limits.
• Path traversal protection.
• Malware-scanning integration where available.
• Protection against malicious document content.
• Protection against prompt injection in imported content.
• No automatic execution of imported instructions.
• Secret redaction in logs and errors.
• Secure temporary storage.
• Encryption where required by classification.
• Audit records for sensitive operations.
• User-visible warnings for material risks.

18.1 Prompt Injection

Imported documents, notes, webpages, metadata, and attachments may contain instructions intended to manipulate AI systems.

Such content must be treated as untrusted data, not as system instructions or authorization.

Importing content must not automatically authorize an Agent or Workflow to execute instructions contained in that content.

18.2 Audit

Audit records should capture relevant operation metadata:

• Operation identifier.
• Actor or authorized principal.
• Operation type.
• Selected scope.
• Source and destination categories.
• Start and completion timestamps.
• Outcome.
• Relevant warnings and failures.
• Approval and authorization references.

Audit records must not contain secret values or unnecessary personal content.

19. Android and Offline Behavior

19.1 Android

The Android implementation must account for:

• Storage Access Framework.
• User-granted URI permissions.
• Scoped storage.
• Document-provider limitations.
• Large files.
• App lifecycle interruptions.
• Background execution restrictions.
• Available storage.
• Network changes.
• User cancellation.

A user-selected URI must not be assumed to remain accessible indefinitely.

19.2 Offline

Where possible, local import/export should work without network access.

Provider-dependent operations must report when connectivity is required.

Offline operations must preserve their state safely and must not be represented as synchronized or remotely delivered until that outcome is verified.

19.3 Large Files and Background Jobs

Large operations should support:

• Progress reporting.
• Cancellation.
• Resource limits.
• Checkpointing where supported.
• Safe cleanup.
• Recovery after application termination where feasible.

The implementation must not promise uninterrupted background execution on Android when the operating system does not guarantee it.

20. Temporary Staging and Cleanup

Temporary import/export data must have a defined lifecycle.

Requirements:

• Use restricted staging locations.
• Separate protected staging from ordinary staging.
• Avoid unnecessary plaintext copies.
• Track temporary artifacts by operation identifier.
• Clean up abandoned staging data.
• Handle cancellation and application termination.
• Avoid deleting the only valid copy of user data.
• Record cleanup failures for recovery.

Exact retention durations and permanent-deletion guarantees are governed by the central Retention, Deletion & Data Lifecycle Policy, subject to its review and approval.

21. User Experience Requirements

The interface should make import/export understandable without exposing implementation complexity.

21.1 Import Experience

The user should be able to:

• Select a source.
• See the detected content type.
• Understand the destination.
• Review what will be imported.
• Inspect conflicts and warnings.
• Choose available resolution policies.
• Approve or cancel.
• Track progress.
• Review the final result.

21.2 Export Experience

The user should be able to:

• Select the export scope.
• Choose available formats.
• Decide whether binaries are included.
• Review excluded or inaccessible content.
• Understand whether protected data is involved.
• Choose a destination.
• Approve the export.
• Track progress.
• Verify delivery status.

21.3 Mobile-First Behavior

The workflow should be suitable for Android screens and one-handed use.

Advanced options should remain contextual rather than permanently displayed.

Long-running operations should remain discoverable through an appropriate activity or operation-status surface.

22. Conceptual Operation Contract

The following is illustrative and does not establish an approved API or wire format:

{
"operation_id": "operation-id",
"operation_type": "import",
"status": "pending",
"source": {
"type": "user-selected-source"
},
"scope": {
"domains": []
},
"options": {
"conflict_policy": "user-approved"
},
"progress": {
"completed_items": 0,
"total_items": 0
},
"result": {
"successful_items": 0,
"failed_items": 0,
"warnings": []
}
}

A production contract must be defined through the API & Event Contracts Specification and relevant shared-service contracts.

23. Observability and Operational Reporting

Import/export operations should expose measurable operational information, including:

• Operation counts.
• Success and failure rates.
• Processing duration.
• Data volume.
• Validation failure categories.
• Unsupported format frequency.
• Retry and recovery rates.
• Cleanup failures.
• Provider delivery failures.

Operational telemetry must follow privacy and data-minimization requirements.

Sensitive content and secret values must not be collected as telemetry.

Detailed monitoring, alerting, and recovery behavior will be specified in:

Mimir Intelligence — Operational Monitoring & Recovery Specification.

24. Acceptance Criteria

The specification is considered implementation-ready only after the relevant contracts and security decisions are resolved.

24.1 General

[  ]  Import/export uses shared orchestration rather than new Engines.
[  ]  Kernel remains domain- and provider-neutral.
[  ]  Domain ownership is enforced.
[  ]  Authorization is checked before sensitive operations.
[  ]  Unsupported formats are reported honestly.
[  ]  Preview and approval are provided where required.
[  ]  Operation outcomes distinguish full, partial, and failed completion.

24.2 Import

[  ]  Format detection is validated.
[  ]  Malformed packages are rejected safely.
[  ]  Archive traversal and expansion abuse are prevented.
[  ]  Schema compatibility is checked.
[  ]  Duplicate and conflict behavior is tested.
[  ]  Cross-domain references are handled safely.
[  ]  Interrupted imports can be recovered where supported.
[  ]  No false success is reported.

24.3 Export

[  ]  Scope is explicit.
[  ]  Authorization is revalidated.
[  ]  Binary inclusion is clearly represented.
[  ]  Metadata and provenance are preserved where supported.
[  ]  Package integrity is verifiable.
[  ]  Delivery is distinguished from temporary package generation.
[  ]  Partial exports are clearly identified.

24.4 Secure Vault

[  ]  Generic exports exclude secrets.
[  ]  Protected export requires explicit authorization.
[  ]  Reauthentication is enforced according to policy.
[  ]  Secret values are excluded from ordinary logs and telemetry.
[  ]  Encryption and recovery behavior follow approved security contracts.
[  ]  Passkey limitations are communicated honestly.

24.5 Android and Offline

[  ]  User-selected storage access is respected.
[  ]  URI permission loss is handled.
[  ]  Large-file limits are enforced.
[  ]  Cancellation and app termination are tested.
[  ]  Offline operations do not falsely report remote delivery.

25. Required Test Scenarios

1. Import a valid supported package.
2. Import an unsupported format.
3. Import a package with an invalid manifest.
4. Import a corrupted binary.
5. Import an archive containing path traversal attempts.
6. Import a decompression bomb test fixture.
7. Import content containing prompt-injection instructions.
8. Import records with duplicate identifiers.
9. Import records with unresolved references.
10. Import a package with unknown optional fields.
11. Import an older supported schema version.
12. Interrupt import during parsing.
13. Interrupt import during commit.
14. Resume an interrupted operation.
15. Export a single Brain note with WikiLinks.
16. Export a Brain collection with attachments.
17. Export a Files folder with nested directories.
18. Export metadata without binaries.
19. Export Downloads history without downloaded content.
20. Export a multi-domain package with cross-domain references.
21. Attempt unauthorized cross-domain export.
22. Attempt to include Secure Vault secrets in a generic archive.
23. Perform an authorized protected export.
24. Test protected export with unavailable recovery material.
25. Lose access to a selected Android document URI.
26. Cancel a large export.
27. Simulate insufficient destination storage.
28. Verify package integrity after delivery.
29. Verify provenance preservation after import and re-export.
30. Verify that failed cleanup is detectable and recoverable.

26. Open Architecture Decisions

The following decisions require explicit resolution before final implementation:

ID| Decision| Status
ADR-IMP-001| Final portable package container and serialization| Open
ADR-IMP-002| Initial supported import/export format matrix| Open
ADR-IMP-003| Stable portable identifier strategy| Open
ADR-IMP-004| Cross-domain dependency inclusion policy| Open
ADR-IMP-005| Default duplicate and conflict policy| Open
ADR-IMP-006| Atomicity boundaries across domains| Open
ADR-IMP-007| Import checkpoint and resume strategy| Open
ADR-IMP-008| Malware scanning and parser isolation implementation| Open
ADR-IMP-009| Protected export encryption and recovery integration| Governed by Secure Vault decisions
ADR-IMP-010| Android large-file and background execution strategy| Open
ADR-IMP-011| External provider adapter contract| Open
ADR-IMP-012| Portable provenance representation| Open

No decision in this table is considered approved merely because it appears in this specification.

27. Dependencies and Related Specifications

This document depends on and must remain consistent with:

1. Mimir Intelligence — Architecture & Domain Boundaries.
2. Mimir Intelligence — Shared Services Contracts.
3. Mimir Brain — Functional & UX Specification.
4. Mimir Files — Functional & UX Specification.
5. Mimir Downloads — Functional & UX Specification.
6. Mimir Secure Vault — Functional, UX & Security Specification.
7. Mimir Intelligence — Integration Specification.
8. Mimir Intelligence — Security, Privacy & Acceptance Specifications.
9. Mimir Intelligence — Data Model & Entity Relationships Specification.
10. Mimir Intelligence — API & Event Contracts Specification.
11. Mimir Intelligence — Storage, Sync & Offline Architecture Specification.
12. Mimir Intelligence — Search & Retrieval Architecture Specification.

27.1 Relationship to the Next Documents

The next document is:

Mimir Intelligence — Retention, Deletion & Data Lifecycle Policy

Its scope must first be reconciled against the existing governing documents. If existing policies already cover the requirements adequately, the correct outcome is to record the coverage and update references rather than create a duplicate policy.

The following document is:

Mimir Intelligence — Operational Monitoring & Recovery Specification

It will define operational health, observability, incident handling, recovery coordination, and service-level operational behavior without duplicating the import/export lifecycle specified here.

28. Implementation and Verification Boundary

This document defines intended architecture and behavior.

It does not prove that:

• Import/export services exist.
• Any listed format is implemented.
• Any external provider is integrated.
• Android background execution is supported.
• Secure Vault export is operational.
• Recovery and rollback are implemented.
• The listed test scenarios have passed.

Each capability must be marked as implemented and verified only after evidence from the actual repository, implementation, and relevant tests is available.

29. Final Status

Draft — Pending Architecture, Security, Data Governance, and Compatibility Review.

Document ID remains "MIMIR-DOC-REG-INT-013".

No approval, repository update, Dropbox save, implementation, or test execution is implied by this document.

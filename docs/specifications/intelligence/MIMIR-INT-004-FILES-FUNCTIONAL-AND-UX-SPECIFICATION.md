---
document_id: MIMIR-DOC-REG-INT-004
document_code: MIMIR-INT-004
filename: MIMIR-INT-004-FILES-FUNCTIONAL-AND-UX-SPECIFICATION.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

Mimir Files — Functional & UX Specification
Document ID: MIMIR-DOC-REG-INT-004
Status: Draft — For Architectural Review
Domain: Mimir Intelligence
Component: Mimir Files
Document Type: Functional & UX Specification
Platform Priority: Android-first, Mobile-first
Architecture Alignment: Mimir Kernel-first Architecture

───

1. Purpose
Mimir Files is the unified file-management and storage-exploration domain within Mimir Intelligence.
It provides a consistent interface for discovering, browsing, organizing, inspecting, and managing files that the user has explicitly made accessible to Mimir.
Its responsibilities include:
• Managing accessible files and folders.
• Browsing supported storage locations.
• Organizing user-authorized content.
• Performing file operations.
• Inspecting file metadata and integrity information when available.
• Previewing supported file formats.
• Tracking file references and relationships.
• Coordinating file access with other Mimir domains.
• Providing a unified experience across supported storage providers.
Mimir Files is not a knowledge-management system, download engine, password manager, or general-purpose operating-system file browser.
2. Scope
2.1 Included
1. File Manager.
2. Storage Explorer.
3. File Operations.
4. File and folder metadata.
5. Categories and collections.
6. Recent files and favorites.
7. Shared and externally accessible files.
8. Trash and restoration workflows.
9. Search, filtering, sorting, and grouping.
10. File preview and details.
11. Import and export coordination.
12. Storage-provider adapters.
13. File references and provenance.
14. Sync and conflict handling.
15. Integration with other Mimir domains.
2.2 Excluded
The following responsibilities belong elsewhere:
• Knowledge organization, notes, backlinks, and semantic knowledge: Mimir Brain.
• Download acquisition, queues, and download-specific lifecycle: Mimir Downloads.
• Passwords, credentials, passkeys, cards, and secrets: Mimir Secure Vault.
• Code project lifecycle and source-control workflows: Mimir Forge.
• Generic execution, permissions infrastructure, and engine registration: platform contracts and Kernel.
Files may provide access to content owned by these domains through explicit contracts, but does not take ownership of their internal records or policies.
3. Architectural Principles
3.1 File Ownership
Mimir Files owns its file-management records and the metadata required to manage user-authorized file references.
It does not necessarily own the physical bytes of every file.
A file may reside in:
• Android shared storage.
• An app-private directory.
• A user-selected document provider.
• A connected cloud provider.
• A supported external storage device.
• A storage location managed by another Mimir domain.
Ownership of the physical content remains with the actual storage provider or owning domain.
3.2 Storage Abstraction
Storage providers are accessed through explicit adapter contracts.
The user interface must not depend directly on provider-specific implementation details.
3.3 Honest Capability Reporting
The application must distinguish between:
• Supported.
• Available with permission.
• Available with provider limitations.
• Read-only.
• Temporarily unavailable.
• Unsupported.
• Not yet implemented.
The UI must never display an operation as available when the underlying provider cannot perform it.
3.4 No Automatic Permission Expansion
Access to one file or folder does not imply access to the rest of the device or provider.
Every operation must remain within the granted authorization boundary.
4. Domain Ownership and Boundaries

Responsibility
Owner

Original file bytes
Actual storage provider or owning domain

File and folder organization records
Mimir Files

File-management metadata
Mimir Files

Knowledge notes and structured knowledge
Mimir Brain

Extracted knowledge and semantic indexes
Mimir Brain

Memory records
Mimir Brain / Memory contracts

Download tasks and queues
Mimir Downloads

Credentials and secrets
Mimir Secure Vault

Code project structure and lifecycle
Mimir Forge

Generic authorization infrastructure
Shared platform services

Cross-domain provenance contracts
Shared platform services


4.1 Original Files vs. References vs. Extracted Content
These must remain separate concepts.
Original file
The actual file content stored by a provider.
File reference
A managed reference to a file, including its provider, location identifier, available metadata, and access state.
Extracted content
Text, metadata, thumbnails, embeddings, summaries, or other derived representations produced by processing.
Extracted content must not be treated as the original file.
Deleting an extracted representation does not necessarily delete the original file. Deleting the original file does not automatically authorize deletion of every derived record owned by another domain.
5. Core Data Concepts
The following entities are conceptual contracts, not a requirement to use a specific database schema.
5.1 File Record
Represents a file known to Mimir Files.
Suggested attributes:
• Stable internal file reference.
• Provider identifier.
• Provider-specific object identifier.
• Display name.
• MIME type, when available.
• File extension, when available.
• Size, when available.
• Created timestamp, when available.
• Modified timestamp, when available.
• Last accessed timestamp, when available.
• Parent folder reference, when supported.
• Storage location reference.
• Availability state.
• Access mode.
• Sync state.
• Integrity information, when available.
• Provenance information.
• Optional user labels and organization metadata.
Missing provider metadata must remain missing. The system must not fabricate values.
5.2 Folder Record
Represents a folder or logical collection.
Attributes may include:
• Stable folder reference.
• Provider and location.
• Parent reference.
• Display name.
• Child count, when available.
• Access mode.
• Availability state.
• Sync state.
Logical collections must be distinguishable from physical folders.
5.3 Storage Location
Represents an authorized storage root or location.
Attributes:
• Provider identity.
• Location identifier.
• User-facing name.
• Location type.
• Permission state.
• Available capabilities.
• Capacity information, if exposed.
• Connectivity state.
• Last successful access.
• Last synchronization status.
5.4 File Operation
Represents an operation requested or performed on a file.
Suggested attributes:
• Operation ID.
• Operation type.
• Source reference.
• Destination reference, when applicable.
• Initiating actor.
• Authorization context.
• Requested timestamp.
• Completion timestamp.
• Execution status.
• Result details.
• Error details.
• Partial-success information.
5.5 File Reference Relationship
Represents a relationship between a file and another Mimir object.
Examples:
• A Brain note references a document.
• A Chat conversation references an attachment.
• A Forge project references a source file.
• A Download record points to a downloaded artifact.
The relationship does not grant access by itself.
6. Mimir Files Information Architecture
6.1 Primary Areas
1. Files Home.
2. File Browser.
3. Storage Explorer.
4. Categories.
5. Recent.
6. Favorites.
7. Shared.
8. Trash.
9. Search Results.
10. File Details.
11. File Preview.
12. File Operations Center.
13. Files Settings.
These are functional destinations, not necessarily separate top-level navigation items.
6.2 Files Home
Files Home should provide:
• Search entry point.
• Quick access to recent files.
• Favorites.
• Storage locations.
• Common categories.
• Recently used folders.
• Storage status, when available.
• Pending or failed operations.
• Contextual actions.
The home screen must prioritize finding and acting on files rather than displaying excessive storage statistics.
7. File Manager
7.1 Purpose
The File Manager provides a unified browsing and organization experience for accessible files.
7.2 Browsing Modes
Supported presentation modes may include:
• List.
• Grid.
• Compact list.
• Folder-first view.
• Grouped view.
The selected view should be remembered independently for relevant browsing contexts where practical.
7.3 Navigation
Users should be able to:
• Open folders.
• Navigate to parent folders.
• Navigate through breadcrumbs where suitable.
• Return to previous locations.
• Switch between authorized storage locations.
• Open file details.
• Preview supported files.
• Select multiple files.
• Perform permitted bulk operations.
Navigation history must not imply continued access if a provider revokes permission.
7.4 Selection
Selection supports:
• Single selection.
• Multi-selection.
• Select all within the currently accessible result set.
• Clear selection.
• Contextual bulk actions.
Bulk actions must clearly report which items succeeded and which failed.
8. Storage Explorer
8.1 Purpose
Storage Explorer exposes the user's accessible storage locations and their capabilities.
8.2 Storage Categories
Potential location categories:
• Device storage.
• App-private storage.
• User-selected folders.
• External storage.
• Connected cloud storage.
• Domain-managed storage locations.
Only actually configured and accessible providers should appear as active locations.
8.3 Storage Information
When available, display:
• Provider name.
• Location name.
• Total capacity.
• Used capacity.
• Available capacity.
• Last synchronization.
• Connectivity.
• Permission status.
• Supported operations.
If a provider does not expose capacity information, the UI must say that it is unavailable rather than estimating it.
8.4 Provider Capabilities
Each provider should expose its supported capabilities through a common adapter contract.
Possible capabilities:
• List files.
• Read content.
• Create folders.
• Rename.
• Move.
• Copy.
• Delete.
• Restore.
• Generate previews.
• Read metadata.
• Write metadata.
• Report revisions.
• Synchronize.
• Provide stable identifiers.
Capability availability may vary by location, file, account, and permission.
9. File Operations
9.1 Supported Operation Types
The conceptual operation set includes:
• Open.
• Preview.
• Rename.
• Move.
• Copy.
• Delete.
• Restore.
• Create folder.
• Import.
• Export.
• Share through supported system mechanisms.
• Duplicate detection.
• Metadata inspection.
Not all providers are required to support every operation.
9.2 Rename
Before renaming:
• Validate the requested name.
• Check provider restrictions.
• Detect potential name conflicts.
• Preserve the original extension when appropriate, without silently changing the file format.
• Request confirmation when a rename may affect linked references or external workflows.
9.3 Move
A move operation must distinguish between:
• Native provider move.
• Copy followed by deletion.
• Cross-provider transfer.
• Unsupported operation.
When a move is implemented as copy and delete, the interface must not claim atomic movement.
If copying succeeds but deletion fails, the result must report that both source and destination remain.
9.4 Copy
Copy operations must:
• Verify destination access.
• Check available destination capabilities.
• Handle naming conflicts.
• Report partial copies.
• Avoid silently overwriting existing content.
9.5 Delete
Deletion must distinguish between:
• Provider trash.
• Mimir-managed trash.
• Permanent deletion.
• Provider-specific deletion.
• Deletion unavailable.
The confirmation interface must identify the actual deletion behavior.
Mimir must not claim permanent deletion until the provider confirms the operation and any relevant retention limitations are understood.
9.6 Restore
Restore is available only when the relevant provider supports restoration or when Mimir manages a reversible deletion record.
The UI must explain where the item will be restored.
9.7 Duplicate Handling
Potential duplicates may be identified using:
• Provider identifiers.
• File size.
• File names.
• Modification metadata.
• Content hashes, when safely available.
A matching name or size alone is not proof of identical content.
Duplicate detection must not automatically delete files.
10. Categories and Organization
10.1 Categories
Categories may include:
• Documents.
• Images.
• Videos.
• Audio.
• Archives.
• Applications and packages.
• Code and development files.
• Other.
• Unknown.
Classification should use available metadata and must remain open to unknown or unsupported formats.
10.2 Recent
Recent displays files accessed through supported Mimir interactions.
It must distinguish between actual access events and provider-reported modification timestamps.
10.3 Favorites
Favorites are user-defined shortcuts.
Removing a favorite must not delete the file.
10.4 Shared
Shared displays files whose sharing state is known through supported provider information or explicit Mimir sharing records.
Mimir must not infer public access merely because a file is located in a shared-looking folder.
10.5 Trash
Trash displays items that can be restored or whose deletion state is tracked.
The interface must distinguish provider trash from Mimir-managed deletion records.
11. Search, Filtering, and Sorting
11.1 Search
Search may include:
• File names.
• Folder names.
• File types.
• Supported metadata.
• User-defined labels.
• Accessible provider metadata.
Content-based and semantic search are coordinated with Mimir Brain rather than becoming a second knowledge index owned by Files.
11.2 Filters
Potential filters:
• File type.
• Storage location.
• Date created.
• Date modified.
• Size.
• Folder.
• Favorite.
• Availability.
• Sync state.
• Access mode.
11.3 Sorting
Supported sort options:
• Name.
• Modified date.
• Created date.
• Size.
• Type.
• Last accessed, when available.
11.4 Search Boundaries
Search must only return results from locations the user is authorized to access.
Results must not expose file names or metadata from revoked or unauthorized locations.
12. Preview and File Details
12.1 Preview
Preview support depends on format, provider, platform, and available permissions.
Possible formats include:
• Text.
• Markdown.
• PDF.
• Images.
• Audio.
• Video.
• Office documents.
• Archives.
• Code files.
Unsupported formats should offer an appropriate external opening mechanism when available.
12.2 Preview Safety
Previewing untrusted files must not automatically execute embedded code, macros, scripts, or active content.
Potentially unsafe formats should use safe rendering or external applications with explicit user action.
12.3 File Details
The details screen may show:
• Name.
• Type.
• Size.
• Location.
• Provider.
• Created and modified dates.
• Access state.
• Available operations.
• Integrity information.
• Revision information.
• Related Mimir references.
• Provenance.
• Sync status.
Unavailable values must be omitted or explicitly marked unavailable.
13. Import and Export
13.1 Import
Import should support user-authorized selection through platform mechanisms and supported provider adapters.
The user must be able to review the source and destination before an import that creates a new managed copy.
Import must distinguish:
• Linking to an existing file.
• Copying content into another location.
• Registering a file reference.
• Importing content into another domain.
13.2 Export
Export should support authorized transfer of content from Mimir-managed locations to a user-selected destination.
Export must respect:
• Source permissions.
• Destination permissions.
• File ownership.
• Domain policies.
• Security classifications.
• Relevant provenance requirements.
Exporting a Brain note or a Secure Vault record must follow that domain's own export contract rather than bypassing it through Files.
14. Storage Provider Architecture
14.1 Adapter Model
Mimir Files should use provider-neutral contracts.
An adapter is responsible for translating generic file operations into provider-specific behavior.
The Files domain remains responsible for:
• Unified user experience.
• File-management records.
• Capability presentation.
• Operation coordination.
• Result normalization.
• Error and partial-success reporting.
14.2 Candidate Providers
Potential providers include:
1. Android shared storage through supported platform APIs.
2. App-private storage.
3. User-selected documents and folders through Storage Access Framework (SAF).
4. External document providers.
5. Cloud storage adapters.
6. Domain-managed file locations.
These are architectural targets, not claims of current implementation.
14.3 Android Storage Constraints
Android access must respect the operating system's storage model.
Requirements:
• Prefer SAF for user-selected documents and folders where appropriate.
• Use supported MediaStore APIs for applicable shared media access.
• Use app-private storage for internal application data.
• Request only necessary permissions.
• Do not assume unrestricted filesystem access.
• Do not assume that a selected document provider supports every file operation.
• Handle persisted URI permissions and their revocation.
• Handle provider disconnection and unavailable documents.
• Do not bypass Android security restrictions.
14.4 Cloud Providers
Cloud adapters must independently define:
• Authentication.
• Authorization.
• Token lifecycle.
• Rate limits.
• Upload/download behavior.
• Revision support.
• Conflict handling.
• Offline behavior.
• Deletion semantics.
No cloud provider should be considered available until its adapter, authentication, and permissions are actually configured.
15. Offline and Synchronization
15.1 Offline Behavior
When offline, Files may display cached metadata and previously available local content.
The interface must distinguish:
• Locally available.
• Cached metadata only.
• Remote and unavailable.
• Pending upload.
• Pending synchronization.
• Permission unavailable.
15.2 Pending Operations
Offline operations may be queued only when the provider and operation semantics make safe replay possible.
Each pending operation must retain:
• Original request.
• Authorization context.
• Target reference.
• Expected state, when available.
• Operation identity.
• Retry state.
• Result history.
15.3 Conflict Handling
Potential conflicts include:
• Remote modification after local caching.
• Concurrent rename.
• Concurrent move.
• Deletion on another device.
• Provider revision changes.
• Permission revocation.
Conflict resolution should offer safe options where possible:
• Keep remote.
• Keep local copy.
• Save as a new file.
• Compare versions.
• Retry after refreshing.
• Ask the user.
The system must not silently overwrite a newer version.
15.4 Sync Ownership
Files coordinates its own file-management metadata and provider interactions.
Shared Sync infrastructure handles common synchronization primitives, but does not take ownership of Files domain semantics.
16. Integrity, Hashes, and Revisions
16.1 Integrity
When supported and appropriate, Files may store:
• File size.
• Content hash.
• Hash algorithm.
• Verification timestamp.
• Verification result.
A hash must identify its algorithm and the exact content revision it describes.
16.2 Revision Support
Provider revisions may be exposed when available.
Mimir must distinguish:
• Provider-managed revisions.
• Mimir-created copies.
• Cached copies.
• Derived artifacts.
Files must not claim version history where the provider does not supply it.
17. Provenance and Access Control
Every file reference should retain available provenance:
• Origin provider.
• Origin location.
• Import method.
• Creating actor.
• Creation timestamp.
• Related operation.
• External reference, when available.
• Access state.
• Related domain references.
17.1 Authorization
Authorization must be checked at the point of access and operation.
A stored reference is not a permanent authorization grant.
17.2 Cross-Domain Access
Other domains must request access through explicit contracts.
Examples:
• Brain requests a file reference or content access for knowledge processing.
• Downloads registers a completed download or provides an artifact reference.
• Forge requests access to project files.
• Chat attaches an authorized file.
• Agents and Workflows request access through scoped permissions.
A cross-domain reference must not bypass provider permissions or domain ownership.
18. Integration Contracts
18.1 Mimir Brain
Files provides authorized original-file references and, where permitted, content access.
Brain owns:
• Extracted knowledge.
• Semantic indexing.
• Notes and knowledge structures.
• Knowledge processing history.
18.2 Mimir Downloads
Downloads owns acquisition tasks and queues.
Files may register completed files, expose them in browsing, and coordinate organization.
A completed download does not automatically become a knowledge record.
18.3 Mimir Secure Vault
Files must not index or expose secrets by default.
Secure Vault controls its own protected documents and credentials.
Any file-related integration must preserve Secure Vault authorization and encryption boundaries.
18.4 Chat
Chat may attach files through authorized references or explicit copies.
Files retains file ownership according to the actual storage location.
Conversation attachment metadata must not imply unrestricted access to the original file.
18.5 Forge
Forge may request project-file access through scoped references.
Files does not become responsible for source control, builds, code execution, or project lifecycle.
18.6 Browser
Browser may request access to selected files for upload or download workflows, subject to user authorization and browser security boundaries.
18.7 Agents and Workflows
Agents and Workflows may perform file operations only through authorized capabilities.
Requirements:
• Explicit operation scope.
• User consent when required.
• Clear execution identity.
• Audit records.
• Confirmation for destructive or externally visible operations.
• Honest failure reporting.
19. Security and Privacy
Requirements:
• Least-privilege access.
• Explicit user authorization.
• Provider permission revocation handling.
• Secure handling of temporary copies.
• No silent upload of file content to cloud services.
• No automatic content indexing without applicable consent and policy.
• Secure temporary-file cleanup.
• Protection against path traversal and unsafe filenames.
• Safe previewing of untrusted content.
• No secret exposure through search or logs.
• Auditability of sensitive operations.
• Respect for provider retention and deletion semantics.
File names and metadata can themselves be sensitive. Access controls must cover metadata as well as content.
20. User Experience and Navigation
20.1 Mobile-first
The primary experience must work comfortably on Android phones.
Priorities:
• One-handed navigation.
• Clear file and folder hierarchy.
• Touch-friendly selection and actions.
• Contextual menus.
• Compact metadata.
• Efficient search.
• Clear operation progress.
• Minimal unnecessary dialogs.
20.2 Tablet and Desktop
Larger screens may support:
• Split-pane browsing.
• Persistent folder navigation.
• Multi-column file views.
• Side-by-side preview and details.
• Keyboard shortcuts.
• Drag-and-drop where supported.
These enhancements must not create a separate product model.
20.3 Shared Application Shell
Files uses the shared Mimir Application Shell and Design System.
It must not introduce a duplicate global navigation or a second global assistant composer.
Advanced actions appear contextually.
20.4 RTL and LTR
All layouts must support Arabic RTL and English LTR.
Requirements include:
• Correct directional icons.
• Logical alignment.
• Breadcrumb direction.
• Filename handling for mixed scripts.
• Safe display of file extensions and paths.
• Correct bidirectional rendering of metadata.
20.5 Accessibility
Support:
• Screen readers.
• Scalable text.
• Adequate contrast.
• Keyboard navigation where applicable.
• Reduced motion.
• Clear operation status announcements.
• Accessible selection and context menus.
21. File Operations Center
A unified activity view should show:
• Active operations.
• Pending operations.
• Completed operations.
• Failed operations.
• Partially completed operations.
• Retryable operations.
• User-action-required operations.
Each entry should expose:
• Operation type.
• Target file.
• Destination when relevant.
• Progress when measurable.
• Initiating actor.
• Timestamp.
• Result.
• Error details when requested.
The activity view must distinguish provider synchronization from file operations.
22. Error and Partial-Success Semantics
Required error categories include:
• Permission denied.
• Permission revoked.
• Provider unavailable.
• File not found.
• Destination unavailable.
• Unsupported operation.
• Name conflict.
• Insufficient storage.
• Network failure.
• Integrity verification failure.
• Authentication expired.
• Partial transfer.
• Unknown provider error.
The system must:
1. Preserve the original request and result.
2. Report which items succeeded.
3. Report which items failed.
4. Avoid reporting false success.
5. Offer retry only when safe.
6. Avoid destructive automatic retries.
7. Preserve enough context for recovery.
A partial operation must never be presented as a complete operation.
23. Settings
Files Settings should remain practical and concise.
Potential settings:
• Default browsing view.
• Default sorting.
• Default grouping.
• File preview preferences.
• Confirm destructive operations.
• Duplicate detection preferences.
• Storage provider management.
• Offline cache preferences.
• File operation notifications.
• Import/export preferences.
• Privacy and indexing controls.
Global typography, themes, glass intensity, and shadow settings remain in Mimir General Settings.
24. Acceptance Criteria
The Files domain is acceptable for release only when the applicable implementation demonstrates:
1. Unified browsing across configured and authorized providers.
2. Correct distinction between original files and references.
3. Accurate provider capability reporting.
4. No unauthorized filesystem access.
5. Correct SAF and Android permission behavior.
6. Safe handling of revoked permissions.
7. Correct file and folder operations where supported.
8. Honest partial-success reporting.
9. Safe duplicate handling.
10. Search limited to authorized content.
11. Accurate preview capability reporting.
12. Correct offline and sync states.
13. No silent overwrites.
14. Cross-domain access through explicit contracts.
15. Preservation of provenance.
16. No accidental exposure of Secure Vault secrets.
17. Correct RTL and LTR behavior.
18. Accessible mobile interactions.
19. Clear distinction between provider trash and permanent deletion.
20. No false claims about implemented providers or capabilities.
25. Open Architectural Decisions
The following require later technical evaluation and must not be treated as approved implementation choices:
• Canonical internal file-reference format.
• Database and metadata persistence strategy.
• Provider adapter interface details.
• Cloud-provider prioritization.
• Cache and temporary-file policies.
• Hash algorithm and verification policy.
• Revision abstraction.
• Cross-provider transfer strategy.
• Offline operation replay policy.
• Duplicate detection thresholds.
• Large-file preview and streaming strategy.
• Storage usage reporting.
• Background operation scheduling.
• Provider-specific permission recovery.
• Backup and restore behavior.
26. Implementation and Verification Boundary
This document defines intended behavior and architecture.
It does not confirm that:
• Android storage integration exists.
• SAF integration has been implemented.
• Any cloud provider is connected.
• File operations are working.
• Background synchronization is operational.
• Hash verification or revision history is available.
• Cross-domain integration has passed tests.
Each capability must be verified against its implementation, provider behavior, permission model, and tests before being presented as supported.
27. Related Documents
• Mimir Blueprint.
• Mimir Kernel Architecture.
• Mimir Intelligence — Architecture & Domain Boundaries.
• Mimir Brain — Functional & UX Specification.
• Mimir Downloads — Functional & UX Specification.
• Mimir Secure Vault — Functional, UX & Security Specification.
• Mimir Intelligence Shared Services Contracts.
• Mimir Intelligence Integration Specification.
• Global Model Provenance & Execution Metadata Contract.
• Mimir Application Shell Specification.
• Mimir Design Tokens & Component Library Specifications.

───

End of Document
Status: Draft — Pending architectural reconciliation, document registration, and implementation planning.

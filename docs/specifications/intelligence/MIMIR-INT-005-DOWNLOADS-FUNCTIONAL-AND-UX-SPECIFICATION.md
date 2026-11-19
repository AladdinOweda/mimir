---
document_id: MIMIR-DOC-REG-INT-005
document_code: MIMIR-INT-005
filename: MIMIR-INT-005-DOWNLOADS-FUNCTIONAL-AND-UX-SPECIFICATION.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

Mimir Downloads — Functional & UX Specification
Document ID: MIMIR-DOC-REG-INT-005
Status: Draft — For Architectural Review
Domain: Mimir Intelligence
Component: Mimir Downloads
Document Type: Functional & UX Specification
Platform Priority: Android-first, Mobile-first
Architecture Alignment: Mimir Kernel-first Architecture

───

1. Purpose
Mimir Downloads is the download acquisition and lifecycle management domain within Mimir Intelligence.
It provides a unified interface for initiating, monitoring, pausing, resuming where supported, organizing, and reviewing download operations from authorized sources.
Its responsibilities include:
• Managing download requests.
• Managing download queues.
• Monitoring transfer progress.
• Managing supported download states.
• Handling retries and failures.
• Managing destination selection.
• Coordinating completed downloads with Mimir Files.
• Supporting authorized media capture where technically and legally permitted.
• Recording download provenance.
• Providing download history and activity.
Mimir Downloads is not a general-purpose file manager, browser, media library, or unrestricted media extraction tool.
2. Scope
2.1 Included
1. Downloads Home.
2. Download Manager.
3. Download Queue.
4. Active Downloads.
5. Paused Downloads.
6. Completed Downloads.
7. Failed Downloads.
8. Download Details.
9. Destination Management.
10. Retry and recovery.
11. Download scheduling where supported.
12. Media Capture workflows where permitted.
13. Download provenance.
14. Transfer integrity checks where supported.
15. Notifications and operation activity.
16. Integration with Files, Browser, Chat, Agents, and Workflows.
2.2 Excluded
The following responsibilities belong elsewhere:
• File browsing and folder organization: Mimir Files.
• Knowledge extraction and indexing: Mimir Brain.
• Credential storage: Mimir Secure Vault.
• Browser navigation and page interaction: Mimir Browser.
• Generic task orchestration: Mimir Orchestration.
• Generic authorization and audit infrastructure: shared platform services.
Downloads may coordinate with these domains but must not duplicate their responsibilities.
3. Architectural Principles
3.1 Acquisition vs. Storage
A download is an acquisition operation, not merely a file record.
The download operation has its own lifecycle, state, source, transfer information, and result.
The resulting file may be stored by a provider and referenced by Mimir Files.
3.2 Honest Capability Reporting
The application must distinguish between:
• Download supported.
• Download supported with restrictions.
• Download requires user interaction.
• Download temporarily unavailable.
• Resume supported.
• Resume unsupported.
• Provider does not expose progress.
• Capture unsupported.
• Permission required.
• Operation blocked by source restrictions.
A feature must not be presented as supported until its implementation and source capabilities are verified.
3.3 Permission and Source Compliance
Downloads must respect:
• Source authorization.
• Authentication requirements.
• Copyright and licensing restrictions.
• DRM.
• Website and provider restrictions.
• Platform security policies.
• User permissions.
The application must not bypass DRM, access controls, paywalls, or other technical restrictions.
3.4 Provider-neutral Design
Download sources and destination providers are accessed through explicit adapters.
The domain must not depend on a single browser, downloader library, or cloud provider.
4. Domain Ownership

Responsibility
Owner

Download request and lifecycle
Mimir Downloads

Queue and transfer state
Mimir Downloads

Acquisition source metadata
Mimir Downloads

Transfer progress and retry state
Mimir Downloads

Physical completed file
Destination storage provider

File organization and file references
Mimir Files

Extracted knowledge
Mimir Brain

Secrets and credentials
Mimir Secure Vault

Browser page interaction
Mimir Browser

Generic scheduling and task distribution
Orchestration contracts

Shared notifications and audit primitives
Shared platform services


5. Core Data Concepts
The following are conceptual entities and do not prescribe a database implementation.
5.1 Download Task
Represents one requested acquisition operation.
Suggested attributes:
• Stable download task ID.
• Source URI or source reference.
• Source provider.
• Display name.
• Expected file type, when known.
• Expected size, when known.
• Destination location reference.
• Requested output name.
• Initiating actor.
• Authorization context.
• Creation timestamp.
• Current state.
• Transfer progress.
• Retry count.
• Error information.
• Completion timestamp.
• Resulting file reference, when available.
• Provenance information.
Sensitive URLs, authentication tokens, and signed query parameters must not be exposed in ordinary logs or UI.
5.2 Download Queue Entry
Represents a task waiting for execution.
Attributes may include:
• Queue entry ID.
• Download task ID.
• Queue position.
• Priority.
• Requested execution time.
• Scheduling state.
• Eligibility state.
• Blocking reason.
5.3 Download Source
Represents the source from which content is acquired.
Possible source types:
• Direct HTTP/HTTPS source.
• Browser-authorized download.
• User-selected remote source.
• Supported application provider.
• Other explicitly integrated source.
5.4 Download Destination
Represents the authorized destination for the resulting content.
Possible destinations:
• App-private storage.
• User-selected folder.
• Supported document provider.
• Supported cloud destination.
• Domain-managed storage location.
5.5 Download Result
Represents the outcome of an acquisition operation.
It may contain:
• Result status.
• Resulting file reference.
• Actual content size.
• Integrity verification status.
• Final destination.
• Completion timestamp.
• Error or partial-result information.
5.6 Download Session
Where required by the implementation, a download may have multiple transfer sessions or attempts.
Each attempt must be distinguishable from the overall download task.
This supports retries without erasing the previous attempt history.
6. Download Lifecycle
6.1 Conceptual States
The lifecycle may include:
1. Draft.
2. Awaiting Authorization.
3. Queued.
4. Preparing.
5. Downloading.
6. Pausing.
7. Paused.
8. Resuming.
9. Verifying.
10. Finalizing.
11. Completed.
12. Failed.
13. Cancelled.
14. Blocked.
15. Expired.
16. Partially Completed.
Not every source or provider supports every state.
6.2 State Semantics
Queued: Accepted into the queue but not yet executing.
Preparing: Resolving source information, destination, or required metadata.
Downloading: Content transfer is actively progressing.
Paused: Transfer is stopped while retaining a resumable state, if supported.
Verifying: Optional integrity or completeness checks are underway.
Finalizing: The result is being committed to the destination.
Completed: The destination provider has confirmed successful completion according to the supported contract.
Failed: The operation cannot continue without intervention or a new attempt.
Cancelled: The user or an authorized policy stopped the operation.
Blocked: A permission, source, policy, or capability restriction prevents execution.
Partially Completed: Some content or subtasks completed, but the requested operation did not finish fully.
6.3 State Integrity
The system must not:
• Mark a transfer complete before destination confirmation.
• Treat an interrupted transfer as a valid completed file.
• Claim resumability when the source does not support it.
• Erase previous failures when retrying.
• Convert a blocked operation into a successful one without a new valid authorization.
7. Downloads Information Architecture
7.1 Primary Areas
1. Downloads Home.
2. All Downloads.
3. Active.
4. Queue.
5. Paused.
6. Completed.
7. Failed.
8. Download Details.
9. Destination Settings.
10. Media Capture.
11. Download Settings.
These areas may be implemented as tabs, filters, or contextual destinations.
7.2 Downloads Home
Downloads Home should show:
• Current active transfers.
• Queue summary.
• Recently completed downloads.
• Failed downloads requiring attention.
• Storage destination status.
• Relevant capture workflows, if available.
• Quick action to initiate a download.
The screen should prioritize current work and issues requiring action.
8. Download Manager
8.1 Overview
Download Manager provides a unified view of download tasks regardless of the source adapter.
Each task should display:
• Display name.
• Source identity, when safe.
• File type, when known.
• Progress, when measurable.
• Current state.
• Destination summary.
• Relevant action.
8.2 Task Actions
Depending on task state and provider capability:
• Pause.
• Resume.
• Retry.
• Cancel.
• Remove from queue.
• Change priority.
• Open details.
• Open resulting file.
• Reveal destination.
• Copy safe source reference.
Unavailable actions must be disabled or omitted with a clear explanation.
8.3 Bulk Actions
Potential bulk actions:
• Pause supported tasks.
• Resume eligible tasks.
• Cancel selected tasks.
• Remove completed entries from the visible history.
• Retry eligible failures.
Bulk operations must report individual outcomes.
Removing an entry from download history must not automatically delete the resulting file.
9. Download Queue
9.1 Purpose
The queue manages download requests that have been accepted but are not currently executing.
9.2 Queue Features
• View pending tasks.
• Reorder eligible tasks.
• Change priority.
• Remove tasks.
• Review blocking reasons.
• Schedule execution where supported.
• View destination requirements.
9.3 Concurrency
Concurrent download limits must be configurable within safe platform and provider constraints.
The system must not assume that unlimited parallel transfers are beneficial or supported.
Concurrency policy should account for:
• Network availability.
• Battery state.
• Platform background execution limits.
• Provider restrictions.
• Destination performance.
• User preferences.
9.4 Priority
Priority may be expressed as:
• Normal.
• High.
• Low.
Priority must not override authorization, source restrictions, or platform policies.
10. Transfer Management
10.1 Progress Reporting
Progress may include:
• Bytes received.
• Total bytes, if known.
• Percentage, if calculable.
• Transfer rate, if measurable.
• Estimated remaining time, if reliable.
• Current state.
• Last progress timestamp.
If total size is unknown, the interface should show an indeterminate progress state rather than an invented percentage.
If the provider does not expose a reliable transfer rate, do not display a fabricated rate.
10.2 Pause and Resume
Pause and resume depend on source and provider support.
Resume may require:
• Stable source identity.
• Range request support or equivalent provider functionality.
• Valid transfer state.
• Destination write capability.
• Valid authentication.
If these conditions are not met, the system should explain that restarting may be required.
10.3 Retry
Retry behavior should:
• Preserve the previous attempt.
• Identify the failure reason.
• Revalidate source and destination.
• Avoid duplicate final files.
• Respect retry limits and backoff policies.
• Require user intervention when authorization is invalid or expired.
10.4 Cancel
Cancellation must distinguish between:
• Successfully stopped.
• Cancellation requested.
• Cancellation unavailable.
• Cancellation completed after a delay.
• Partial content retained.
• Partial content removed.
The application must disclose what happens to partial data.
11. Destination Management
11.1 Destination Selection
The user may select an authorized destination for a download.
Available destinations depend on configured storage providers and permissions.
11.2 Default Destination
A default destination may be configured.
The default must not imply access to an entire device filesystem.
11.3 Destination Validation
Before starting, validate when possible:
• Destination access.
• Provider availability.
• Write capability.
• Available storage information.
• Name restrictions.
• File-size restrictions.
• Authentication status.
If capacity information is unavailable, the system must not claim that sufficient space has been confirmed.
11.4 Destination Changes
Changing the destination of an active task must be handled explicitly.
The system must not silently move an active transfer or change its destination without updating the task record.
12. File Naming and Conflict Handling
12.1 Suggested Names
The system may derive a display name from:
• Source metadata.
• Content disposition metadata.
• User input.
• Provider metadata.
Names must be sanitized according to destination-provider restrictions.
12.2 Name Conflicts
When a destination already contains a file with the same name, offer safe choices where supported:
• Keep both with a new name.
• Replace after explicit confirmation.
• Cancel.
• Select another destination.
The system must not silently overwrite existing files.
12.3 Content Identity
Identical names do not prove identical content.
Content-based duplicate checks should use trustworthy metadata or hashes when available.
13. Media Capture
13.1 Purpose
Media Capture is a controlled acquisition workflow for supported media sources.
It is not an unrestricted video or audio extraction mechanism.
13.2 Supported Scenarios
Potential scenarios:
• User-authorized media downloads.
• Direct downloadable media links.
• Official provider export mechanisms.
• Browser-initiated downloads.
• Supported public media resources with permitted acquisition.
13.3 Restrictions
The system must respect:
• DRM.
• Authentication.
• Paywalls.
• Access controls.
• Copyright and licensing.
• Provider terms and technical restrictions.
• Local laws.
• User permissions.
It must not bypass restrictions or present prohibited capture methods as supported features.
13.4 Capture Workflow
A supported capture workflow may include:
1. User selects or shares a source.
2. Mimir identifies the source type.
3. The system determines whether an authorized acquisition path exists.
4. It presents the available output and limitations.
5. The user confirms the destination and operation.
6. Downloads executes the supported acquisition.
7. The result is verified where possible.
8. The resulting file is registered with Files.
If no authorized path exists, the system must explain the limitation.
14. Browser Integration
Browser may initiate a download through an explicit handoff.
Requirements:
• Preserve source provenance.
• Preserve user authorization context.
• Pass only necessary information.
• Avoid exposing session cookies or credentials to unrelated components.
• Respect browser permission boundaries.
• Record whether the download originated from a user action or an authorized workflow.
Browser page access must not automatically grant Downloads permission to access unrelated browser sessions.
15. Integrity and Verification
15.1 Verification
When supported, Downloads may verify:
• Actual transferred size.
• Expected size.
• Provider-supplied checksum.
• Content hash.
• Transfer completion.
• Destination write completion.
15.2 Integrity States
Potential states:
• Not checked.
• Pending.
• Verified.
• Mismatch.
• Incomplete.
• Unsupported.
• Verification failed.
15.3 Trust
A completed transfer does not prove that the content is safe, authentic, or trustworthy.
Security scanning and content analysis require their own explicit capabilities and contracts.
16. Offline and Background Behavior
16.1 Network Loss
On network loss:
• Preserve task state.
• Report interruption.
• Resume only if supported.
• Avoid duplicate transfer sessions.
• Keep the original source reference.
• Record the interruption.
16.2 Android Background Execution
Background transfer behavior must respect Android platform constraints and battery policies.
Implementation may use appropriate platform-supported mechanisms.
The UX must disclose when background continuation cannot be guaranteed.
16.3 Battery and Connectivity
User-configurable policies may include:
• Wi-Fi only.
• Allow mobile data.
• Restrict large transfers.
• Pause under low battery.
• Allow background transfers where supported.
These controls must reflect actual platform behavior and must not promise execution when the operating system can suspend or terminate the process.
17. Security and Privacy
Requirements:
• Least-privilege source access.
• Explicit destination authorization.
• Secure handling of authentication material.
• No credentials in ordinary logs.
• Safe URL handling.
• Protection against malicious redirects.
• Validation of downloaded content metadata.
• Safe treatment of partial files.
• Protection against path traversal.
• No automatic execution of downloaded content.
• Secure cleanup of temporary data.
• Audit records for sensitive operations.
Download source URLs may contain tokens or signed parameters. The system must protect them from unnecessary display, logging, analytics, and cross-domain propagation.
18. Provenance
Each download should preserve available provenance:
• Original source.
• Source provider.
• Initiating actor.
• User action or workflow origin.
• Request timestamp.
• Transfer attempts.
• Destination.
• Completion timestamp.
• Verification status.
• Resulting file reference.
• Relevant authorization and handoff references.
The system must not invent a source when one is unavailable.
Provenance must remain available when a completed file is handed to Files.
19. Integration Contracts
19.1 Mimir Files
Downloads owns the acquisition task.
Files owns file-management records and organization of the resulting file.
On successful completion:
1. Downloads reports the result.
2. Downloads provides the destination and resulting file reference when available.
3. Files registers or updates the accessible file reference through its contract.
4. The download record retains a link to the resulting file.
5. Failure to register the file must not rewrite a successful transfer as a failed transfer.
The UI should distinguish:
• Transfer completed.
• File registration completed.
• File registration pending.
• File registration failed.
19.2 Mimir Brain
Brain may receive an explicit request to process a completed file.
Downloads must not automatically create knowledge or memory records.
Knowledge processing must preserve source references and processing provenance.
19.3 Mimir Secure Vault
Authentication material belongs to Secure Vault or the appropriate secure platform credential mechanism.
Downloads must not store reusable passwords or tokens in ordinary task records.
19.4 Chat
Chat may initiate a download through a user-confirmed action or authorized integration.
The resulting artifact may be attached to a conversation through an explicit file reference.
19.5 Agents and Workflows
Agents and Workflows may initiate downloads only through scoped capabilities.
Requirements:
• Explicit source and destination scope.
• Applicable user consent.
• Defined execution limits.
• Auditable actor identity.
• Destructive-operation confirmation where required.
• Honest result reporting.
An agent must not bypass source restrictions by initiating the same operation through another adapter.
20. Notifications and Activity
Notifications may be generated for:
• Download started.
• Download completed.
• Download failed.
• Permission required.
• Authentication expired.
• Destination unavailable.
• User action required.
• Integrity verification failed.
Notification frequency should be configurable.
The shared Activity system records relevant events, while Downloads retains download-specific lifecycle history.
Connectivity and synchronization status must remain distinct from download execution status.
21. User Experience
21.1 Mobile-first Design
The primary interface must support:
• Quick access to active downloads.
• Clear progress indicators.
• Large touch targets.
• Simple pause and retry controls.
• Contextual actions.
• Clear destination information.
• Readable error messages.
• Efficient bulk management.
21.2 Tablet and Desktop
Larger layouts may support:
• Multi-column download lists.
• Detailed transfer panels.
• Queue management sidebars.
• More visible source and destination metadata.
21.3 Shared Application Shell
Downloads uses the shared Mimir Application Shell.
It must not create a duplicate global navigation system or global assistant composer.
21.4 RTL and LTR
Support Arabic RTL and English LTR.
Progress indicators, byte values, filenames, URLs, and mixed-direction metadata must remain readable.
21.5 Accessibility
Support:
• Screen readers.
• Scalable text.
• Contrast requirements.
• Keyboard navigation where applicable.
• Reduced motion.
• Accessible progress announcements.
• Clear distinction between success, warning, and failure.
22. Settings
Downloads Settings may include:
• Default destination.
• Concurrent transfer limits.
• Wi-Fi-only policy.
• Mobile-data policy.
• Background transfer preferences.
• Retry policy.
• Notifications.
• Temporary-file cleanup.
• Integrity verification preferences.
• File conflict behavior.
• Media Capture preferences.
• Privacy controls.
Global appearance and typography settings remain in Mimir General Settings.
23. Error and Partial-Success Semantics
Required failure categories:
• Invalid source.
• Source unavailable.
• Source authorization denied.
• Authentication expired.
• Source restrictions.
• Network interruption.
• Destination permission denied.
• Destination unavailable.
• Insufficient storage.
• Name conflict.
• Transfer interrupted.
• Resume unsupported.
• Integrity mismatch.
• Provider error.
• Partial transfer.
• Registration with Files failed.
Each failure must preserve:
• Task identity.
• Attempt identity.
• Last known state.
• Source reference, protected as necessary.
• Destination.
• Error category.
• Recovery options.
• Whether partial content remains.
Retry must not silently create an unrelated second download.
24. Acceptance Criteria
The domain is acceptable for release only when applicable tests demonstrate:
1. Correct download lifecycle transitions.
2. Accurate progress reporting.
3. Honest handling of unknown transfer sizes.
4. Correct queue behavior.
5. Safe pause and resume behavior where supported.
6. Correct retry semantics.
7. No false completion.
8. No silent overwrites.
9. Correct destination permission handling.
10. Safe handling of interrupted transfers.
11. Honest background execution behavior on Android.
12. Compliance with source restrictions.
13. No DRM or access-control bypass.
14. Protection of sensitive source URLs and credentials.
15. Accurate integrity status.
16. Preservation of provenance.
17. Correct handoff to Files.
18. Separation of transfer completion from file registration.
19. No automatic knowledge indexing.
20. Correct RTL and LTR behavior.
21. Accessible progress and error reporting.
22. No false claims about unsupported providers or capabilities.
25. Open Architectural Decisions
The following remain pending technical evaluation:
• Download adapter interface.
• Transfer engine selection.
• Android background execution strategy.
• Queue persistence.
• Concurrency policy.
• Retry and backoff defaults.
• Partial-file storage and cleanup.
• Resume support abstraction.
• Large-file transfer strategy.
• Cloud destination priorities.
• Source metadata validation.
• Integrity verification defaults.
• Scheduling semantics.
• Notification aggregation.
• Media Capture provider eligibility.
• Download history retention.
• Storage pressure handling.
26. Implementation and Verification Boundary
This document defines intended architecture, behavior, and UX.
It does not confirm that:
• A download engine has been implemented.
• Background transfers work reliably.
• Pause and resume are supported.
• Cloud destinations are connected.
• Media Capture is implemented.
• Any DRM-protected source can be acquired.
• Transfer integrity verification is operational.
• Files integration has passed tests.
Every capability must be verified against its source adapter, destination provider, Android execution constraints, security requirements, and tests before being presented as supported.
27. Related Documents
• Mimir Blueprint.
• Mimir Kernel Architecture.
• Mimir Intelligence — Architecture & Domain Boundaries.
• Mimir Files — Functional & UX Specification.
• Mimir Brain — Functional & UX Specification.
• Mimir Secure Vault — Functional, UX & Security Specification.
• Mimir Intelligence Shared Services Contracts.
• Mimir Intelligence Integration Specification.
• Global Model Provenance & Execution Metadata Contract.
• Mimir Application Shell Specification.
• Mimir Design Tokens & Component Library Specifications.

───

End of Document
Status: Draft — Pending architectural reconciliation, document registration, and implementation planning.

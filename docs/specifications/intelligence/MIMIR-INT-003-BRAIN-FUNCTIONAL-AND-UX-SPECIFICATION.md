---
document_id: MIMIR-DOC-REG-INT-003
document_code: MIMIR-INT-003
filename: MIMIR-INT-003-BRAIN-FUNCTIONAL-AND-UX-SPECIFICATION.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

───

document_title: Mimir Brain — Functional & UX Specification document_id: MIMIR-DOC-REG-INT-003
Mimir Brain — Functional & UX Specification
1. Purpose
Mimir Brain is the knowledge, memory, and intelligent retrieval domain of Mimir Intelligence.
It provides a unified environment for capturing, organizing, understanding, retrieving, reviewing, and maintaining information across Mimir.
Mimir Brain consists of three distinct but integrated capabilities:
1. Knowledge Vault
2. Memory Center
3. Intelligent Retrieval
These capabilities MUST preserve separate responsibilities, data ownership, lifecycle policies, and authorization rules.
2. Foundational Concepts
2.1 Knowledge
Knowledge represents information the user or authorized system has intentionally collected, organized, or derived.
Examples:
• Notes.
• Documents and summaries.
• Concepts.
• References.
• Topics and collections.
• Relationships between knowledge items.
• Research findings.
• Project documentation.
• User-approved extracted information.
Knowledge is managed by Knowledge Vault.
2.2 Memory
Memory represents information that Mimir has been permitted to retain for future personalization, continuity, or assistance.
Examples:
• Stable user preferences.
• Approved recurring instructions.
• Relevant personal context.
• Project continuity facts.
• Explicitly retained decisions.
• Useful interaction patterns when retention is authorized.
Memory is managed by Memory Center.
Memory MUST NOT automatically absorb every conversation, file, or extracted fact.
2.3 Context
Context is the information assembled for a particular task, conversation, agent run, or workflow.
Context may temporarily combine:
• Conversation history.
• Relevant Memory.
• Retrieved Knowledge.
• Authorized file references.
• Current task instructions.
• Relevant execution metadata.
Context is assembled for a specific operation and does not become persistent Memory automatically.
2.4 Secrets
Secrets include passwords, credentials, authentication tokens, private keys, and other sensitive access information.
Secrets belong exclusively to Mimir Secure Vault.
Knowledge Vault, Memory Center, and Intelligent Retrieval MUST NOT become alternative secret-storage systems.
3. Knowledge Vault
3.1 Purpose
Knowledge Vault is the user's structured and searchable knowledge environment.
It should provide the organizational flexibility and interconnected knowledge experience associated with advanced Second Brain systems, while remaining integrated with Mimir's broader platform.
3.2 Core capabilities
• Notes and Markdown documents.
• Document organization.
• Collections and folders.
• Tags and metadata.
• WikiLinks.
• Backlinks.
• Graph View.
• Source references.
• Knowledge relationships.
• Version history.
• Search and retrieval.
• AI-assisted summarization and extraction.
• Knowledge review and maintenance.
• Import and export.
3.3 Knowledge item types
The system SHOULD support typed knowledge items, including:
• Note.
• Document.
• Concept.
• Reference.
• Research Finding.
• Summary.
• Project Knowledge Item.
• Meeting Record.
• Task-related Knowledge.
• Collection.
• Source.
Additional types may be introduced without changing the Kernel.
3.4 Knowledge item structure
A knowledge item SHOULD support:
• Stable identifier.
• Title.
• Content.
• Item type.
• Tags.
• Collection membership.
• Created timestamp.
• Updated timestamp.
• Author or origin.
• Source references.
• Provenance.
• Relationships.
• Version information.
• Access policy.
• Processing status.
• Optional user-defined metadata.
Fields MUST be extensible without breaking older records.
3.5 Capture and ingestion
Knowledge may enter through:
• Manual note creation.
• Direct text capture.
• User-selected files.
• Browser content capture.
• Chat outputs explicitly saved by the user.
• Forge project documentation.
• Authorized imports.
• Approved AI extraction workflows.
Every ingestion method MUST identify the source and the requested operation.
The system MUST distinguish:
• Original content.
• Extracted content.
• AI-generated summaries.
• User-edited content.
• External references.
AI extraction MUST NOT be treated as verified truth.
3.6 Knowledge processing lifecycle
Suggested lifecycle:
1. Capture.
2. Source identification.
3. Content extraction where authorized.
4. Processing.
5. User review when required.
6. Organization.
7. Relationship discovery.
8. Indexing.
9. Retrieval.
10. Maintenance and review.
11. Archive or deletion.
Processing status MUST be visible where relevant.
A failed extraction MUST NOT be presented as a completed knowledge import.
3.7 Editing and versioning
The editor SHOULD support:
• Markdown editing.
• Rich text where appropriate.
• Headings and formatting.
• Links and attachments.
• Internal WikiLinks.
• Backlink navigation.
• Autosave with visible state.
• Manual save where required.
• Version history.
• Restore and comparison.
• Conflict detection.
Autosave MUST NOT silently overwrite a conflicting remote revision.
3.8 WikiLinks and backlinks
WikiLinks connect knowledge items through stable references.
The system SHOULD support:
• Creating links while typing.
• Resolving existing items.
• Creating a new item from an unresolved link.
• Listing backlinks.
• Detecting broken links.
• Renaming while preserving link identity.
• Navigating between connected items.
A link is a relationship, not a permission grant.
3.9 Graph View
Graph View visualizes knowledge relationships.
It SHOULD support:
• Local graph.
• Full or filtered graph.
• Topic and collection filters.
• Relationship types.
• Search within the graph.
• Selecting and opening nodes.
• Hiding unrelated branches.
• Visual distinction between source types.
• Accessible alternative list views.
Graph visualization MUST remain optional. It must not become the only way to navigate knowledge.
3.10 Sources and provenance
Every imported or derived knowledge item SHOULD preserve available source information.
Source information may include:
• Source title.
• Original location or reference.
• Source domain.
• Source owner where known.
• Capture time.
• Source revision where available.
• Extraction method.
• Model and provider contribution.
• Human review status.
When source information is unavailable, the system MUST say so rather than invent it.
3.11 Knowledge deletion and source invalidation
Deleting a source file does not automatically delete every knowledge item derived from it.
Dependent items SHOULD be identified and marked for review.
The user may:
• Keep the derived knowledge.
• Remove the derived knowledge.
• Update it from a replacement source.
• Preserve the source reference as unavailable.
The system MUST distinguish source deletion from derived-content deletion.
4. Memory Center
4.1 Purpose
Memory Center manages the information Mimir is allowed to retain for future use.
It provides transparency, review, correction, retention controls, and user control over memory.
4.2 Memory principles
Memory MUST:
• Have an identifiable purpose.
• Respect applicable consent and privacy controls.
• Preserve available provenance.
• Support correction and review.
• Support deletion and forgetting workflows.
• Avoid retaining unnecessary sensitive information.
• Remain distinct from Knowledge Vault.
Memory MUST NOT:
• Store passwords or credentials.
• Treat every conversation as permanent memory.
• Convert uncertain model inferences into confirmed user facts.
• Override explicit user instructions.
• silently expand its retention scope.
4.3 Memory categories
The system SHOULD distinguish:
Semantic Memory
• Stable facts and preferences.
• Durable user-approved information.
• Long-term contextual facts.
Episodic Memory
• Relevant events and past interactions.
• Decisions and milestones.
• Important project history.
Procedural Memory
• Approved recurring workflows.
• User preferences for how tasks are performed.
• Reusable operating instructions.
Working Memory
• Temporary information used during an active task or session.
• Not necessarily persistent.
• Expires or is cleared according to context policy.
These categories are conceptual data classifications. They do not mandate separate databases or technology.
4.4 Memory record structure
A Memory record SHOULD support:
• Stable identifier.
• Content or structured representation.
• Memory category.
• Source reference.
• Confidence or certainty classification.
• Created and updated timestamps.
• Last confirmed timestamp where applicable.
• Retention policy.
• Sensitivity classification.
• User approval state.
• Expiration or review date where applicable.
• Related records.
• Status.
4.5 Memory acquisition
Potential acquisition sources:
• Explicit user requests to remember.
• Approved preferences.
• Approved project decisions.
• Authorized recurring context.
• User-approved extraction from previous interactions.
The system MUST distinguish explicit user statements from model-generated interpretations.
If a memory candidate is uncertain, sensitive, or consequential, it SHOULD require review before retention.
4.6 Memory review
Memory Center SHOULD provide a review queue.
The user can:
• Accept.
• Reject.
• Edit.
• Correct.
• Merge.
• Mark as outdated.
• Set a review date.
• Delete.
The system SHOULD identify potentially conflicting or duplicated memories.
It MUST NOT silently resolve meaningful conflicts by choosing one version without preserving the relevant history or requesting review where necessary.
4.7 Memory lifecycle
Suggested lifecycle:
1. Candidate detected.
2. Policy evaluation.
3. Approval or rejection.
4. Storage.
5. Use in authorized context.
6. Periodic review where appropriate.
7. Correction or update.
8. Expiration or deletion.
A candidate is not a stored memory until the applicable retention policy permits it.
4.8 Forgetting and deletion
Memory Center MUST support clear forgetting workflows.
The system SHOULD distinguish:
• Removing a memory from active retrieval.
• Deleting the authoritative memory record.
• Removing derived indexes and caches.
• Handling replicated copies.
• Handling backups under the applicable retention policy.
The user MUST receive an accurate description of what has and has not been deleted.
The platform MUST NOT claim complete physical deletion unless it can verify that the applicable storage and backup requirements have been satisfied.
4.9 Sensitive information
Sensitive personal information requires stricter policy evaluation.
The system SHOULD avoid retaining highly sensitive information unless there is a valid, explicit, supported purpose and appropriate protection.
Secrets MUST remain outside Memory Center.
Memory retrieval MUST respect the current user's authorization and applicable privacy settings.
5. Intelligent Retrieval
5.1 Purpose
Intelligent Retrieval finds and assembles relevant information from authorized sources.
It serves Brain and, through approved contracts, other Mimir components.
It is not a new owner of Knowledge, Memory, Files, or Secrets.
5.2 Retrieval sources
Potential sources:
• Knowledge Vault.
• Authorized Memory records.
• User-selected Files.
• Project documentation.
• Conversation context.
• Authorized external connectors.
• Approved reference indexes.
Secure Vault secrets are excluded from general retrieval.
5.3 Retrieval pipeline
Suggested logical pipeline:
1. Receive request.
2. Determine user and execution identity.
3. Resolve the authorized retrieval scope.
4. Identify relevant source domains.
5. Execute supported retrieval methods.
6. Normalize candidate results.
7. Apply relevance and policy filters.
8. Deduplicate where appropriate.
9. Rank results.
10. Assemble evidence and source references.
11. Return results with provenance and limitations.
The pipeline may use lexical search, vector search, metadata filtering, reranking, graph traversal, or other strategies.
No specific retrieval technology is mandatory.
5.4 Retrieval strategies
The system SHOULD support a combination of:
• Keyword and lexical retrieval.
• Semantic retrieval.
• Metadata filtering.
• Hybrid retrieval.
• Relationship and graph traversal.
• Source-specific retrieval.
• Context-aware retrieval.
• Reranking.
Strategy selection MUST remain replaceable behind Mimir-owned contracts.
5.5 Result structure
A retrieval result SHOULD include:
• Result identity.
• Source domain.
• Source reference.
• Relevant content or excerpt.
• Relevance information where available.
• Source timestamp or revision where available.
• Permission and visibility state.
• Provenance.
• Known limitations.
The system MUST distinguish relevance from factual confidence.
A high relevance score does not prove that a source is correct.
5.6 Evidence-grounded answers
When retrieval is used to generate an answer:
• Important factual claims SHOULD be linked to supporting sources.
• Conflicting sources SHOULD be disclosed.
• Missing evidence SHOULD be identified.
• Unavailable sources MUST NOT be fabricated.
• Generated summaries MUST be distinguishable from source content.
• The final answer MUST preserve applicable model and provider attribution.
5.7 Retrieval quality
Retrieval quality SHOULD be evaluated through:
• Recall.
• Precision.
• Ranking quality.
• Source coverage.
• Duplicate rate.
• Freshness.
• Permission correctness.
• Citation correctness.
• Latency.
• Failure and partial-result rates.
Quality evaluation MUST include privacy and authorization correctness, not only relevance.
5.8 Partial retrieval
If one or more source domains fail:
• Successful results may still be returned.
• The response MUST indicate incomplete coverage.
• Failed domains MUST NOT be represented as having no matching records.
• The user SHOULD be able to retry the incomplete portion where supported.
6. Brain User Experience
6.1 Brain Home
Brain Home SHOULD provide:
• Recent knowledge.
• Quick capture.
• Continue reading or editing.
• Recently used collections.
• Important sources.
• Memory review reminders.
• Retrieval entry point.
• Knowledge activity.
• Optional graph entry point.
The page MUST remain uncluttered on mobile.
6.2 Knowledge Explorer
Knowledge Explorer SHOULD provide:
• Notes.
• Documents.
• Concepts.
• Collections.
• Tags.
• Sources.
• Backlinks.
• Search and filtering.
It SHOULD support list and grid layouts where appropriate.
6.3 Note Editor
Note Editor SHOULD provide:
• Read and edit modes.
• Markdown support.
• Formatting controls.
• WikiLink insertion.
• Source attachment.
• Save status.
• Version history.
• Conflict handling.
• Accessible keyboard behavior.
On mobile, advanced editing controls SHOULD be contextual rather than permanently occupying the screen.
6.4 Document Viewer
Document Viewer SHOULD:
• Display supported formats.
• Preserve original-file references.
• Provide source information.
• Allow authorized annotation or extraction.
• Clearly distinguish original and derived content.
• Provide an accessible reading experience.
6.5 Graph View
Graph View SHOULD provide a visual representation with an alternative list-based navigation mode.
Graph View MUST not be required for basic knowledge operations.
6.6 Sources & Inbox
Sources & Inbox SHOULD collect items awaiting processing or review.
Possible states:
• New.
• Processing.
• Needs review.
• Ready.
• Failed.
• Rejected.
• Archived.
6.7 Memory Center Home
Memory Center SHOULD show:
• Memory overview.
• Categories.
• Recent changes.
• Review queue.
• Potential conflicts.
• Outdated memories.
• Privacy controls.
• Memory usage settings.
6.8 Memory Detail
Memory Detail SHOULD show:
• Memory content.
• Category.
• Source.
• Creation and update history.
• Confidence classification.
• Approval state.
• Last confirmation.
• Retention information.
• Related memories.
• Edit and deletion controls.
6.9 Memory Review Queue
The review queue SHOULD support:
• Accept.
• Reject.
• Edit.
• Merge.
• Mark outdated.
• Delete.
• Defer review.
The user MUST be able to understand why a candidate was suggested.
6.10 Intelligent Retrieval Interface
The retrieval interface SHOULD show:
• Search or question input.
• Search scope.
• Active source domains.
• Filters.
• Results.
• Source attribution.
• Relevant excerpts.
• Conflicts and limitations.
• Retrieval completeness.
The default experience SHOULD be simple, with advanced retrieval controls revealed when needed.
7. Navigation and Integration
Mimir Brain MUST use the shared Mimir Application Shell.
It MUST NOT introduce an independent global navigation architecture.
Internal Brain navigation SHOULD support:
• Brain Home.
• Knowledge.
• Collections.
• Sources.
• Graph.
• Memory.
• Retrieval.
• Brain Settings.
The available navigation pattern MUST adapt to mobile, tablet, and desktop.
The shared global Composer remains owned by the Application Shell.
Brain-specific capture and retrieval controls may be presented contextually without creating a second global Composer.
8. Model and Agent Integration
Agents and models may assist with:
• Summarization.
• Classification.
• Extraction.
• Relationship suggestions.
• Duplicate detection.
• Retrieval.
• Knowledge organization.
• Memory candidate generation.
The system MUST:
• Identify AI-generated and user-authored content.
• Preserve available model and provider metadata.
• Identify uncertain or unverified extraction.
• Request approval when policy requires it.
• Avoid claiming that generated relationships are confirmed facts.
• Respect domain permissions.
• Avoid unrestricted Memory or Knowledge access.
AI suggestions MUST remain distinguishable from accepted user decisions.
9. Offline and Synchronization Behavior
Brain SHOULD support appropriate offline reading and editing.
Offline support MUST distinguish:
• Available locally.
• Pending synchronization.
• Conflict detected.
• Sync failed.
• Remote-only content.
• Restricted content unavailable offline.
Sensitive Memory and derived indexes MUST follow applicable security and local-storage policies.
A synchronization failure MUST NOT silently discard user edits.
10. Accessibility and Presentation
Brain MUST follow the global Mimir Design System.
Requirements include:
• Light, Dark, System, and Adaptive theme behavior.
• Global typography and accessibility settings.
• Appropriate contrast.
• Reduced-motion support.
• Screen-reader-compatible controls.
• Keyboard navigation where applicable.
• Readable content surfaces.
• Responsive layouts.
• RTL and LTR support.
Liquid Material MUST be used conservatively.
Long-form notes, documents, search results, and data-dense lists SHOULD use solid or low-transparency surfaces.
11. Error and Operational States
The interface SHOULD distinguish:
• Empty.
• Loading.
• Processing.
• Offline.
• Permission denied.
• Source unavailable.
• Partial retrieval.
• Extraction failed.
• Sync conflict.
• Save pending.
• Save failed.
• Review required.
• Memory expired.
• Deletion pending.
• Unsupported format.
Errors MUST communicate the actual state and available recovery actions.
12. Settings
Brain Settings SHOULD include:
• Knowledge organization preferences.
• Default capture behavior.
• Import and extraction permissions.
• Retrieval preferences.
• Source inclusion controls.
• Memory retention controls.
• Memory review preferences.
• Privacy controls.
• Offline availability.
• Export and portability.
• Accessibility-related options where domain-specific.
Global theme, typography, transparency, and shadow controls remain owned by Mimir General Settings.
13. Acceptance Criteria
Knowledge Vault
1. Knowledge records have stable identities.
2. Original files remain owned by Files.
3. Sources and provenance are preserved where available.
4. WikiLinks and backlinks use stable references.
5. Editing and version history handle conflicts honestly.
6. Graph View is optional.
7. AI-generated content is distinguishable from verified user content.
8. Source invalidation does not silently destroy derived knowledge.
Memory Center
1. Memory remains distinct from Knowledge and Context.
2. Memory candidates are policy-evaluated before retention.
3. Explicit user statements are distinguishable from model inferences.
4. Users can review and correct memories.
5. Users can identify memory sources where available.
6. Forgetting and deletion states are represented accurately.
7. Secrets are excluded.
8. Sensitive retention follows stricter rules.
Intelligent Retrieval
1. Retrieval respects authorization.
2. Sources are attributable.
3. Relevance is not confused with factual certainty.
4. Partial results are identified.
5. Conflicting sources are not silently concealed.
6. Retrieval strategies are replaceable.
7. Secure Vault secrets are excluded from general retrieval.
8. Model and provider provenance is preserved where available.
Overall Brain
1. Brain uses the shared Application Shell.
2. Brain uses shared Design Tokens and Components.
3. Mobile-first behavior is supported.
4. RTL and LTR are supported.
5. Offline and synchronization states are explicit.
6. Agents and Workflows use authorized contracts.
7. No Brain-specific business logic is added to the Kernel.
14. Open Decisions
The following require detailed follow-up specifications:
• Knowledge schema and extensibility.
• Memory acquisition and retention policy.
• Sensitive-data classification.
• Memory review and forgetting implementation.
• Retrieval contracts and ranking strategy.
• Source invalidation and reprocessing.
• Offline storage boundaries.
• Knowledge versioning.
• Export and portability formats.
• Evaluation datasets and quality thresholds.
• Agent access scopes.
• Backup and recovery behavior.
15. Document Status
This document establishes the functional and UX requirements for Mimir Brain.
It is a specification draft, not evidence of implementation or testing.
Document ID, repository location, governing ADR references, and final approval remain pending reconciliation with the canonical Mimir documentation repository.

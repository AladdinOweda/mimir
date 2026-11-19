---
document_id: MIMIR-DOC-REG-INT-001
document_code: MIMIR-INT-001
filename: MIMIR-INT-001-ARCHITECTURE-AND-DOMAIN-BOUNDARIES.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

───

document_title: Mimir Intelligence — Architecture & Domain Boundaries document_id: MIMIR-DOC-REG-INT-001
Mimir Intelligence — Architecture & Domain Boundaries
1. Purpose
This document defines the architectural boundaries, responsibilities, data ownership rules, shared capabilities, security constraints, and integration principles of Mimir Intelligence.
Mimir Intelligence is a unified user-facing product composed of four independent functional domains:
1. Mimir Brain
2. Mimir Files
3. Mimir Downloads
4. Mimir Secure Vault
The four domains share platform capabilities but retain independent responsibilities, data ownership, permissions, and lifecycle policies.
This document does not authorize implementation or introduce new mandatory technology dependencies.
2. Architectural Principles
2.1 Mimir Kernel neutrality
The Mimir Kernel MUST remain generic and engine-agnostic.
The Kernel MUST NOT:
• Know the identities or internal structures of Intelligence domains.
• Contain domain-specific business rules.
• Contain conditional logic tied to a particular engine or product area.
• Depend directly on Brain, Files, Downloads, or Secure Vault implementations.
Integration MUST use generic platform contracts, capability registration, and approved service interfaces.
2.2 Domain independence
Each Intelligence domain MUST:
• Own its domain-specific data and lifecycle.
• Define its own validation and business rules.
• Enforce its own authorization requirements.
• Expose capabilities through documented contracts.
• Be replaceable or extensible without requiring changes to unrelated domains.
2.3 Shared capabilities, separate ownership
Shared capabilities MUST NOT become hidden owners of domain data.
Unified Search may coordinate queries, but it does not own the indexed source records.
Sync may transport changes, but it does not decide domain-specific conflict semantics.
Provenance may preserve source and contribution metadata, but it does not become the owner of the underlying content.
2.4 Privacy by default
Access to a domain does not imply access to every other domain.
A user-approved operation in one domain MUST NOT silently authorize unrelated operations in another.
3. Product Structure
3.1 Mimir Brain
Purpose: Knowledge management, personal knowledge organization, memory management, and intelligent retrieval.
Subdomains:
• Knowledge Vault
• Memory Center
• Intelligent Retrieval
Primary responsibilities:
• Knowledge notes and documents.
• Concepts, tags, collections, and relationships.
• WikiLinks and backlinks.
• Source references and citations.
• Knowledge graph relationships.
• Memory lifecycle and review.
• Context-aware retrieval.
• Knowledge processing and organization.
Mimir Brain MUST NOT become the owner of original files stored by Mimir Files.
3.2 Mimir Files
Purpose: General file management and storage exploration.
Subdomains:
• File Manager
• Storage Explorer
• File Operations
Primary responsibilities:
• File and folder organization.
• File metadata and locations.
• File previews and supported content inspection.
• Categories, favorites, recent files, and shared items.
• Storage visibility and usage information.
• File operations subject to permissions.
• File lifecycle and trash management.
Mimir Files owns the original file record within its supported storage scope.
It MUST NOT silently assume ownership of extracted knowledge, personal memory, or credentials.
3.3 Mimir Downloads
Purpose: Download acquisition, queue management, and supported media capture.
Subdomains:
• Download Manager
• Queue
• Media Capture
Primary responsibilities:
• Download creation and tracking.
• Queue scheduling and management.
• Progress and transfer status.
• Completed and failed download records.
• Retry behavior where supported.
• Download destination selection.
• Link analysis.
• Supported browser and media capture workflows.
Download resumption, multi-connection transfers, and background execution MUST be exposed only when the relevant server, protocol, operating system, and device capabilities support them.
Media Capture MUST respect authentication, access restrictions, DRM, platform rules, and applicable rights.
3.4 Mimir Secure Vault
Purpose: Secure storage and controlled use of sensitive user information.
Subdomains:
• Passwords
• Passkeys
• Authenticator
• Secure Notes
• Secure Documents & IDs
• Cards
• Digital Wallet
• Autofill
• Security Center
Primary responsibilities:
• Secure credential storage.
• Controlled credential retrieval.
• Passkey operations where supported.
• Authenticator data and workflows.
• Secure notes and documents.
• Payment-card information where permitted.
• Wallet integrations where supported.
• Autofill authorization.
• Security monitoring and user-facing security controls.
Secure Vault MUST remain logically and cryptographically isolated from general knowledge and file-search experiences.
4. Data Ownership Matrix

Data category
Authoritative owner
Other domains may

Original files
Mimir Files
Reference or request access

Knowledge notes
Mimir Brain
Reference or retrieve

Knowledge relationships
Mimir Brain
Query through contracts

Personal memory records
Memory Center
Request permitted context

Retrieval indexes
Owning retrieval service/domain
Query through authorized interfaces

Download jobs
Mimir Downloads
Reference resulting files

Download transfer state
Mimir Downloads
Read permitted status

Passwords and credentials
Secure Vault
Request narrowly scoped operations

Passkeys
Secure Vault / supported platform integration
Request authorized use

Authentication secrets
Secure Vault
No general access

Provenance records
Applicable producing domain and shared provenance contract
Read permitted metadata

Permission decisions
Applicable authorization authority
Request authorization


An index, cache, preview, or derived representation MUST NOT silently become the authoritative source of the original data.
5. Cross-Domain References
Domains SHOULD use stable references rather than duplicating another domain's authoritative records.
A cross-domain reference SHOULD identify:
• Source domain.
• Stable record identifier.
• Resource type.
• Relevant version or revision when available.
• Access requirements.
• Provenance information where applicable.
A reference MUST NOT grant access by itself.
Every access MUST be independently authorized against the current applicable permissions.
If the referenced object is moved, deleted, revoked, or becomes inaccessible, dependent references MUST be handled according to the owning domain's lifecycle policy.
6. Shared Platform Capabilities
The following are shared capabilities, not additional Intelligence domains or Engines.
6.1 Unified Search
Responsibilities:
• Coordinate searches across authorized domains.
• Apply domain-specific permission checks.
• Preserve source identity.
• Support filtering and contextual ranking.
• Present results with clear domain attribution.
Restrictions:
• Search MUST NOT expose Secure Vault secrets through general search.
• Search MUST NOT bypass domain permissions.
• Search results MUST distinguish original records from derived or indexed content.
• A failed domain query MUST NOT be represented as a complete successful search.
6.2 Permissions & Authorization
Responsibilities:
• Authenticate the requesting principal.
• Evaluate requested capabilities and resource scope.
• Enforce user consent and policy.
• Support temporary and revocable permissions where appropriate.
• Record security-relevant authorization events.
Authorization MUST be checked at the point of use, not inferred solely from a previously opened screen or an earlier unrelated approval.
6.3 Provenance
Preserve available:
• Source identity.
• Source domain.
• Model and provider attribution where applicable.
• Local or cloud execution type.
• Agent or tool contribution.
• Timestamps.
• Processing and transformation history.
• Handoffs and aggregation details.
• Final producing model or component when identifiable.
The platform-wide Model Attribution & Execution Transparency contract applies to all Intelligence domains.
User-authored content MUST NOT be attributed to a model.
6.4 Sync & Conflict Resolution
Responsibilities:
• Transport authorized changes.
• Track synchronization state.
• Detect conflicts.
• Preserve recoverable change information where supported.
• Report partial success and failure accurately.
Conflict resolution MUST respect the owning domain's data semantics.
A single generic last-write-wins policy MUST NOT be imposed on all Intelligence data.
6.5 Activity & Audit Trail
Activity records SHOULD distinguish:
• User actions.
• Agent actions.
• Automated operations.
• Synchronization events.
• Permission decisions.
• Security-sensitive operations.
• Failed and partially completed operations.
Security audit records and ordinary user activity history may have different access, retention, and deletion policies.
6.6 Encryption & Key Management
Encryption and key management MUST follow the platform security architecture.
Secure Vault MUST NOT depend on general-purpose search indexes or ordinary knowledge storage as its secret-storage mechanism.
The exact cryptographic design, key hierarchy, recovery model, and hardware-backed options require a separate security specification.
7. Cross-Domain Operations
Cross-domain workflows MUST be explicit and traceable.
Example: Import a document into Brain.
1. The user selects a file in Files.
2. The system checks access to the selected file.
3. The user authorizes the requested import or processing scope.
4. Brain receives an authorized representation or reference.
5. Brain records the source reference and applicable provenance.
6. The original file remains owned by Files.
7. The user can inspect or revoke the relationship where supported.
The import MUST NOT imply permission to retain unrelated personal information in Memory.
Example: Autofill from Secure Vault.
1. An application or browser requests a specific credential operation.
2. Secure Vault evaluates the request and its origin.
3. The user is prompted when required by policy.
4. Only the approved fields are released.
5. The operation is recorded according to the security policy.
The requesting application MUST NOT receive unrestricted access to the Vault database.
8. Deletion, Retention, and Revocation
Deletion MUST be domain-aware.
Deleting a Brain note MUST NOT automatically delete its source file in Files.
Deleting a Files record MUST NOT silently erase unrelated knowledge or memory records that reference it.
Instead, dependent records SHOULD be marked as:
• Source unavailable.
• Reference broken.
• Access revoked.
• Content requiring review.
Memory deletion MUST follow the Memory Center lifecycle and retention policy.
Secure Vault deletion MUST follow its dedicated secure deletion and key-management requirements.
The platform MUST distinguish:
• User-visible removal.
• Logical deletion.
• Physical deletion.
• Backup expiration.
• Cryptographic erasure, where supported.
The system MUST NOT claim permanent deletion until the applicable storage and backup policies support that claim.
9. Agent and Workflow Access
Agents, Bots, Workflows, and Orchestration components MUST access Intelligence capabilities through authorized contracts.
They MUST NOT:
• Bypass domain authorization.
• Read secrets through general-purpose data access.
• Assume that a user-approved task grants unlimited future access.
• Persist sensitive content outside approved policies.
• Treat a reference as proof of authorization.
Every operation MUST carry sufficient execution identity and provenance to establish who or what initiated it.
10. Platform Integration
Mimir Intelligence integrates with:
• Chat
• Forge
• Browser
• Terminal where applicable
• Agent Runtime
• Orchestration
• Workflow Runtime
• Bot Profiles
• Loop Runtime
• Intelligent Execution
Integration MUST remain capability-based and contract-driven.
No Intelligence domain may introduce engine-specific conditions into the Kernel.
11. Mobile-First and Platform Constraints
The product MUST support a mobile-first experience while respecting actual platform capabilities.
The design and implementation MUST account for:
• Android storage permissions and scoped storage.
• Background execution restrictions.
• Battery optimization.
• Network interruptions.
• Offline states.
• Device security capabilities.
• Biometric availability.
• Passkey support.
• Wallet and payment-provider availability.
Unsupported capabilities MUST be clearly identified as unavailable, rather than represented as functioning features.
12. Failure and Partial-Success Semantics
Operations MUST report their actual outcome.
Supported result distinctions SHOULD include:
• Success.
• Partial success.
• Rejected.
• Permission denied.
• Unsupported capability.
• Temporarily unavailable.
• Conflict.
• Cancelled.
• Failed.
• Unknown outcome.
A domain MUST NOT report success when the requested operation was not completed.
A successful transfer does not automatically mean a successful import, indexing, or knowledge extraction.
13. Architectural Acceptance Criteria
The architecture is acceptable when:
1. The Kernel contains no Intelligence-specific business logic.
2. Each domain has clear data ownership.
3. Cross-domain references do not bypass authorization.
4. Secure Vault data is excluded from general-purpose search by default.
5. Memory, Knowledge, Files, and Secrets remain separate concepts and storage responsibilities.
6. Shared capabilities do not become hidden owners of domain data.
7. Provenance survives supported cross-domain operations.
8. Sync and conflict handling respect domain semantics.
9. Deletion and revocation behavior is explicitly defined.
10. Agents and workflows use authorized contracts.
11. Unsupported device or provider capabilities are reported honestly.
12. Partial failures are represented accurately.
13. Domains can be extended or replaced without requiring Kernel changes.
14. Open Items for Detailed Specifications
The following require dedicated design and security decisions:
• Exact authorization contract and permission scopes.
• Data classification and sensitivity levels.
• Secure Vault cryptographic architecture.
• Key recovery and account recovery.
• Backup and disaster recovery.
• Memory retention and forgetting lifecycle.
• Knowledge versioning and source invalidation.
• Search ranking and retrieval evaluation.
• Sync conflict strategies by data type.
• Android background download strategy.
• Passkey and wallet provider integration requirements.
• Audit retention and privacy controls.
• Export formats and portability.
• Cross-domain transaction and compensation behavior.
No technology or provider is mandated by this document.
15. Document Status
This is the foundational architecture and domain-boundary specification for Mimir Intelligence.
It establishes the intended ownership and integration rules. It does not claim that the contracts have been implemented, tested, or reconciled against every existing repository file.
Document ID, repository location, governing ADR references, and final approval status remain pending registry and repository verification.

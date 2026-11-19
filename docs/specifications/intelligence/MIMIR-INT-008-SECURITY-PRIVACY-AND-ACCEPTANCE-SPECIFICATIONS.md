---
document_id: MIMIR-DOC-REG-INT-008
document_code: MIMIR-INT-008
filename: MIMIR-INT-008-SECURITY-PRIVACY-AND-ACCEPTANCE-SPECIFICATIONS.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

Mimir Intelligence — Security, Privacy & Acceptance Specifications
Document ID: MIMIR-DOC-REG-INT-008
Status: Draft — Pending Security, Privacy & Architectural Review
Document Type: Security, Privacy, Compliance & Quality Acceptance Specification
Product: Mimir Intelligence
Scope: Brain, Files, Downloads, Secure Vault, Shared Services, AI Access, and Platform Integrations

───

1. Purpose
This document defines the security, privacy, data-protection, and quality acceptance requirements for Mimir Intelligence.
It establishes:
• Security objectives and threat boundaries.
• Data classification and handling rules.
• Authentication and authorization requirements.
• Encryption and key-management requirements.
• Secure Vault isolation requirements.
• AI, Agent, and Workflow access restrictions.
• Privacy, retention, deletion, and export requirements.
• Local-device and cloud-storage security.
• Auditability and incident-handling requirements.
• Security and privacy testing.
• Domain-specific and platform-wide acceptance criteria.
The goal is to ensure that Mimir Intelligence protects user information throughout its lifecycle, including creation, processing, storage, synchronization, sharing, backup, export, and deletion.
This specification defines required behavior. It does not claim that the controls have already been implemented, audited, or certified.
2. Security Objectives
Mimir Intelligence must protect the following security properties.
2.1 Confidentiality
Only authorized users, components, and explicitly approved processes may access protected information.
2.2 Integrity
The system must protect data against unauthorized modification, corruption, substitution, or silent loss.
2.3 Availability
Users must retain appropriate access to their data and operations, subject to device, provider, network, and security constraints.
2.4 Authenticity
The system must establish the identity and authority of actors performing sensitive operations.
2.5 Accountability
Security-sensitive operations must be traceable to their initiating actor, authorization context, and outcome without exposing protected values in audit records.
2.6 Privacy
The system must collect, process, retain, and disclose only the information necessary for the intended purpose.
2.7 User Control
Users must have understandable controls over permissions, sharing, synchronization, retention, export, and deletion.
3. Security Architecture Principles
3.1 Domain isolation
Each domain owns its data and enforces its own security rules.
A shared user interface or integration layer does not eliminate domain boundaries.
3.2 Defense in depth
Security must not depend on a single control.
Appropriate layers include:
• Authentication.
• Authorization.
• Domain-level validation.
• Encryption.
• Secure storage.
• Application isolation.
• Operating-system protections.
• Audit and monitoring.
• Recovery and incident response.
3.3 Least privilege
Every actor receives the minimum permissions necessary to complete an authorized operation.
Permissions must be limited by resource, operation, purpose, scope, and duration where applicable.
3.4 Secure by default
New resources, integrations, and features must begin with restrictive privacy and access defaults.
3.5 Explicit trust boundaries
The system must distinguish:
• User-controlled data.
• Application-controlled data.
• Model-generated data.
• External provider data.
• Agent and Workflow execution data.
• Credentials and secrets.
• Untrusted external content.
3.6 No implicit trust
Being part of Mimir does not automatically make a component trusted.
An Engine, Agent, Workflow, plugin, provider, or external service must receive only explicitly granted capabilities.
3.7 Honest security status
The interface must not claim that information is encrypted, securely deleted, isolated, backed up, or protected unless the corresponding implementation and verification support that claim.
4. Threat Model
4.1 Assets requiring protection

Asset
Security importance

User documents and files
Confidentiality and integrity

Knowledge records
Confidentiality, integrity, provenance

Memory records
Privacy and controlled retention

Downloaded content
Integrity and safe handling

Passwords and passkeys
Critical confidentiality

Authenticator secrets
Critical confidentiality

Recovery credentials
Critical confidentiality

Identity documents
High confidentiality

Payment and wallet information
High confidentiality

Access tokens and encryption keys
Critical confidentiality

Model context
Privacy and controlled disclosure

Agent and Workflow permissions
Authorization integrity

Audit records
Integrity and confidentiality

Synchronization metadata
Privacy and integrity


4.2 Threat actors and sources
The threat model must consider:
1. Unauthorized local device access.
2. Malicious or compromised applications.
3. Malicious websites and web content.
4. Malicious or compromised external providers.
5. Unauthorized API clients.
6. Compromised user accounts.
7. Stolen or exposed session credentials.
8. Malicious files and documents.
9. Prompt injection and adversarial content.
10. Misconfigured Agents or Workflows.
11. Accidental user disclosure.
12. Insecure synchronization or backup mechanisms.
13. Lost or stolen devices.
14. Software vulnerabilities and dependency compromise.
15. Insider or administrative misuse where applicable.
4.3 Security threat scenarios
The implementation must explicitly evaluate:
• Unauthorized reading of a private file.
• Cross-domain privilege escalation.
• Exposure of Secure Vault credentials to a model.
• Malicious document instructions influencing an Agent.
• Unauthorized Browser Autofill.
• Leakage through notifications or logs.
• Access to stale cached data after permission revocation.
• Synchronization of protected information to an unintended account.
• Restoration of deleted data from backups.
• Tampering with provenance or audit history.
• Malicious downloads and unsafe file handling.
• Export of information beyond the user's intended scope.
4.4 Threat modeling methodology
Each domain and major integration must maintain a threat assessment that identifies:
• Assets.
• Entry points.
• Trust boundaries.
• Threat scenarios.
• Potential impact.
• Likelihood.
• Existing controls.
• Residual risks.
• Required mitigations.
• Verification evidence.
A suitable threat-modeling methodology may be selected during implementation, but it must be applied consistently and reviewed when significant architecture changes occur.
5. Data Classification and Handling
5.1 Classification levels
Mimir Intelligence uses the following conceptual classification levels.

Level
Classification
Examples

L0
Public
Public documents and non-sensitive metadata

L1
Internal
Application preferences and ordinary operational metadata

L2
Confidential
Private documents, personal notes, knowledge records

L3
Restricted
Identity documents, sensitive personal records, protected business information

L4
Critical Secret
Passwords, passkey private material, recovery secrets, authenticator seeds, encryption keys


Classification must be assigned according to the actual content and risk, not merely the storage location or file extension.
5.2 Handling requirements
L0 — Public
• May be shared according to user and platform policy.
• Integrity and source provenance must remain available where relevant.
L1 — Internal
• Must not be exposed to unauthorized external parties.
• Must be protected against unauthorized modification.
• Must not be unnecessarily included in model context.
L2 — Confidential
• Requires authenticated access.
• Requires appropriate authorization.
• Must use protected storage and transmission.
• Must not be disclosed to external models without an authorized data-sharing decision.
L3 — Restricted
• Requires stronger access controls.
• Must use explicit purpose-based authorization.
• Must have controlled export and sharing.
• Must be excluded from general AI context by default.
L4 — Critical Secret
• Requires dedicated secret-handling mechanisms.
• Must be encrypted using an appropriate cryptographic design.
• Must never enter general search indexes, ordinary logs, or general model context by default.
• Must not be exposed to unauthorized Agents, Workflows, Engines, or providers.
• Must use specialized recovery and deletion policies.
5.3 Classification inheritance
Derived content must retain an appropriate relationship to its source classification.
A summary or extracted text must not automatically become less sensitive simply because it is no longer stored in the original file format.
Where content combines multiple sources, the resulting classification must reflect the applicable sensitivity and policy requirements.
5.4 Metadata protection
Metadata can itself reveal sensitive information.
The system must assess exposure of:
• File names.
• Folder names.
• Document titles.
• Tags.
• Search snippets.
• Resource relationships.
• Access history.
• Download URLs.
• Provider identifiers.
• Device and synchronization information.
Metadata must receive protection appropriate to its sensitivity.
6. Authentication and Session Security
6.1 Authentication
Mimir must use secure and appropriately verified authentication mechanisms for protected operations.
The architecture must distinguish:
• User authentication.
• Device authentication.
• Application or service identity.
• Agent identity.
• Workflow execution identity.
• External provider authentication.
6.2 Session management
Session handling must support:
• Secure session creation.
• Session expiration.
• Revocation.
• Device and account logout.
• Credential rotation where applicable.
• Protection against session fixation and replay.
• Reauthentication for high-risk operations where required.
6.3 Sensitive operation reauthentication
Depending on policy and risk, require renewed user verification before:
• Revealing critical secrets.
• Exporting Secure Vault data.
• Changing recovery settings.
• Disabling important security controls.
• Performing sensitive account or credential operations.
6.4 Device lock and application backgrounding
The application must define behavior when:
• The device is locked.
• The application enters the background.
• The process is terminated.
• The user switches accounts.
• A session expires.
• The user revokes device access.
Protected views and in-memory sensitive state must be handled according to the applicable security policy.
7. Authorization and Permission Security
7.1 Authorization requirements
Every protected operation must be authorized at the resource and operation level.
The system must not rely only on:
• Navigation visibility.
• Client-side UI restrictions.
• Previously granted broad permissions.
• Knowledge of a resource identifier.
• Possession of a stale reference.
• An Agent's general autonomy setting.
7.2 Permission scopes
Permission scopes should distinguish:
• Read.
• Create.
• Update.
• Move.
• Delete.
• Share.
• Export.
• Execute.
• Reveal secret.
• Submit credential.
• Manage permissions.
7.3 Scope limitations
Permissions should be limited to:
• Specific resources.
• Selected folders or collections.
• Specific projects.
• Specific operations.
• A defined purpose.
• A defined period where appropriate.
7.4 Revocation
The platform must support permission revocation.
Revocation must be propagated to relevant components and must prevent new unauthorized access.
Where immediate cancellation of an already-running operation is impossible, the system must explain the actual state and apply the documented recovery policy.
7.5 Authorization testing
Tests must verify that unauthorized operations are rejected even when requests are issued outside the normal user interface.
8. Encryption and Key Management
8.1 Encryption in transit
Sensitive information transmitted between Mimir components or external providers must use appropriately protected communication channels.
Certificate validation and secure transport configuration must be enforced.
8.2 Encryption at rest
Sensitive data stored locally or remotely must use an appropriate encryption strategy.
The design must account for:
• Local databases.
• File content.
• Temporary processing artifacts.
• Cached data.
• Backups.
• Synchronization queues.
• Export packages.
• Secret records.
8.3 Key separation
The design must distinguish, where applicable:
• Device-protected keys.
• User-controlled encryption keys.
• Domain-specific data-encryption keys.
• Secure Vault key material.
• Provider-managed keys.
• Recovery material.
Keys must not be stored alongside protected data in a way that defeats the intended security boundary.
8.4 Key lifecycle
The implementation must define:
• Key generation.
• Secure storage.
• Key access.
• Rotation.
• Revocation.
• Recovery.
• Backup.
• Destruction.
8.5 Cryptographic requirements
Cryptographic algorithms and parameters must be selected through a reviewed security design using established, maintained cryptographic implementations.
Custom cryptographic algorithms or improvised encryption protocols must not be introduced.
8.6 Key compromise
The architecture must define a response for suspected key compromise, including:
• Revoking affected credentials or keys.
• Re-encrypting affected data where possible.
• Re-establishing trust.
• Reviewing potentially exposed records.
• Notifying the user where required.
8.7 Recovery limitations
Recovery mechanisms must clearly explain their security trade-offs.
The system must not promise recovery of encrypted data if the required key material has been permanently lost.
9. Mimir Secure Vault — Critical Security Requirements
Secure Vault has a higher protection requirement than ordinary application data.
9.1 Isolation
Secure Vault must maintain a dedicated security boundary.
Its secrets must not automatically flow into:
• Brain Knowledge Vault.
• Memory Center.
• Unified Search.
• General Chat context.
• General Agent context.
• Workflow variables.
• Ordinary application logs.
• Notifications.
• Analytics events.
• General export packages.
9.2 Secret access
Access to a secret must be an explicit operation.
The system must verify:
• Requesting actor.
• Target credential or item.
• Intended purpose.
• Destination.
• Required scope.
• User approval or applicable policy.
• Current vault security state.
9.3 Secret exposure minimization
Where possible:
• Use protected handoff mechanisms.
• Avoid unnecessary copying.
• Avoid persistent plaintext storage.
• Limit secret lifetime in memory.
• Clear temporary buffers where technically supported.
• Prevent secret values from appearing in error messages or diagnostics.
9.4 Passwords and passkeys
The implementation must evaluate secure storage and use of password credentials and passkeys.
Private key material must never be exposed through ordinary application APIs or model context.
Passkey support must follow the relevant platform and protocol security requirements.
9.5 Authenticator
Authenticator secrets and generated codes must remain within the dedicated security boundary.
The system must define secure provisioning, storage, display, recovery, and deletion behavior.
9.6 Secure Notes and Documents
Secure Notes and protected documents must support appropriate classification, encryption, access control, export restrictions, and lifecycle management.
9.7 Cards and Digital Wallet
Payment-related data must use appropriate protections and follow applicable platform, provider, and regulatory requirements.
Mimir must not claim payment-network certification or compliance without independent evidence.
9.8 Autofill
Autofill must:
• Validate the destination origin.
• Prevent cross-origin credential disclosure.
• Request appropriate user confirmation.
• Fill only authorized fields.
• Avoid exposing secret values to unrelated page content.
• Avoid silent form submission unless explicitly authorized under a separately reviewed policy.
• Respect vault locking and session revocation.
9.9 Vault locking
The system must define:
• Manual locking.
• Automatic locking.
• Background locking.
• Device-lock interaction.
• Session expiration.
• Reauthentication.
• Emergency revocation where supported.
9.10 Vault recovery
Recovery must be designed to avoid creating an easier attack path than the primary authentication mechanism.
The recovery design must document:
• Recovery factors.
• Recovery authority.
• Recovery data.
• Recovery verification.
• Failure and lockout behavior.
• Consequences of losing recovery material.
10. AI, Models, Agents and Workflows Security
10.1 Default AI access
AI components must not receive unrestricted access to Intelligence domains.
Every access request must be authorized for its actual resource and purpose.
10.2 Context minimization
Only the information necessary for the task may be added to model context.
The system must distinguish:
• Conversation history.
• Working context.
• Long-term memory.
• Project knowledge.
• Files supplied for a task.
• Secrets.
These categories must not be merged into one unrestricted context pool.
10.3 External model disclosure
Before confidential or restricted content is sent to an external model provider, the system must apply the relevant user permissions, privacy settings, provider policies, and data-sharing controls.
The interface must make meaningful data-sharing choices understandable.
10.4 Local and Cloud execution
The platform must accurately identify whether processing is local or cloud-based where that information is available.
Local execution must not be described as inherently secure without evaluating the actual local environment.
10.5 Prompt injection
External documents, webpages, downloaded files, and retrieved knowledge must be treated as potentially untrusted content.
Instructions contained inside those sources must not automatically override:
• System-level rules.
• User instructions.
• Authorization policies.
• Domain security controls.
• Agent permissions.
• Workflow restrictions.
Retrieved content must be distinguishable from trusted instructions.
10.6 Agent permissions
Agents must operate within bounded permissions.
The system must support:
• Task-specific authorization.
• Resource-specific access.
• Permission expiration.
• Approval requirements.
• Revocation.
• Operation history.
• Separation between Agent identity and user identity.
10.7 Workflow permissions
Workflow steps must not silently inherit broader privileges than those explicitly authorized.
A Workflow must preserve authorization boundaries across:
• Branches.
• Retries.
• Scheduled executions.
• Delegated steps.
• External tool calls.
• Human approval stages.
10.8 Model provenance
All model-produced outputs must preserve the platform-wide Model Provenance and Execution Transparency contract.
Security and privacy controls must not silently remove known attribution metadata.
10.9 AI-generated knowledge
AI-generated content must be identifiable as generated or derived content.
The system must not automatically present extracted claims as independently verified facts.
11. Files Security Requirements
11.1 Storage boundaries
Files must respect the ownership and permissions of:
• Android local storage.
• Storage Access Framework resources.
• Application-private storage.
• Removable storage where supported.
• Cloud storage providers.
• External file sources.
11.2 File access
File access must validate:
• Resource identity.
• Current availability.
• User permission.
• Requested operation.
• Storage-provider restrictions.
11.3 File content validation
Untrusted files must be handled defensively.
The implementation must evaluate:
• File type validation.
• MIME-type inconsistencies.
• Malformed files.
• Path traversal.
• Archive extraction risks.
• Malicious embedded content.
• Oversized files.
• Parser vulnerabilities.
• Executable content.
File extensions alone must not be treated as proof of file safety.
11.4 Temporary files
Temporary files must have defined:
• Security classification.
• Access permissions.
• Storage location.
• Retention period.
• Cleanup policy.
• Crash-recovery behavior.
11.5 Sharing and export
Before sharing or exporting a file, the system must identify the destination and apply the relevant authorization and privacy policies.
11.6 File deletion
Deletion must distinguish:
• Removing a reference.
• Moving to trash.
• Deleting a local copy.
• Requesting deletion from a cloud provider.
• Permanent deletion where supported.
• Backup retention.
The user must receive an accurate description of the action performed.
12. Downloads Security Requirements
12.1 Source validation
Downloads must identify the source and validate the transfer destination.
12.2 Download integrity
Where supported, the system should verify integrity using appropriate metadata or cryptographic checks.
A successful network transfer alone does not prove that a downloaded file is authentic or safe.
12.3 Untrusted content
Downloaded files must remain untrusted until appropriate validation and user-controlled handling.
12.4 Destination protection
Downloads must not write outside authorized locations or bypass operating-system storage restrictions.
12.5 Dangerous content
The system must apply appropriate warnings and handling for potentially dangerous content.
It must not automatically execute downloaded files.
12.6 Media capture
Media capture must respect:
• Authentication requirements.
• DRM.
• Copyright.
• Website restrictions.
• Applicable law.
• User authorization.
12.7 Download metadata
Download URLs, headers, authentication information, and source metadata may contain sensitive information.
They must be protected and excluded from ordinary logs unless specifically required and safely redacted.
13. Brain Security and Privacy Requirements
13.1 Knowledge Vault
Knowledge records must have:
• Ownership.
• Access policy.
• Classification.
• Source references.
• Creation and modification history where supported.
• Retention policy.
• Deletion behavior.
13.2 Memory Center
Memory must remain distinct from Knowledge and temporary Context.
The system must provide understandable controls for:
• What may be remembered.
• Memory review.
• Memory correction.
• Memory retention.
• Memory deletion.
• Restrictions on sensitive information.
13.3 Intelligent Retrieval
Retrieval must apply source-level authorization before exposing content.
Search ranking or semantic similarity must never override access control.
13.4 Derived content
Summaries, embeddings, extracted entities, indexes, and other derivatives must be covered by the source's applicable privacy and retention policies.
13.5 Knowledge graph and relationships
Relationships between records may reveal sensitive information even when individual record contents are hidden.
Graph visibility and traversal must therefore follow authorization rules.
14. Privacy by Design
14.1 Data minimization
Collect and retain only information necessary for the intended function.
14.2 Purpose limitation
Information collected for one purpose must not be silently reused for an unrelated purpose.
14.3 User transparency
Users must be able to understand:
• What information is stored.
• Which domain owns it.
• Where it is stored.
• Whether it is synchronized.
• Whether it is shared externally.
• Whether AI processing is involved.
• How long it is retained.
• How it can be exported or deleted.
14.4 Consent and permissions
Consent must be meaningful, specific, and revocable where applicable.
A general acceptance of application terms must not be treated as authorization for every sensitive data-sharing operation.
14.5 Privacy modes
Privacy-restricted or Incognito modes must define their actual behavior.
The system must not claim that a mode prevents persistence, logging, synchronization, or external disclosure unless those behaviors are technically enforced.
14.6 External providers
For every external provider, the implementation must document:
• Data sent.
• Purpose.
• Authentication method.
• Provider retention behavior where known.
• User controls.
• Revocation mechanism.
• Relevant privacy limitations.
14.7 Privacy-safe diagnostics
Diagnostic systems must avoid collecting:
• Passwords.
• Tokens.
• Encryption keys.
• Recovery secrets.
• Private document contents.
• Sensitive personal information.
Any diagnostic collection must be purpose-limited and appropriately protected.
15. Retention, Deletion and Data Lifecycle
15.1 Retention policies
Each domain must define retention policies for:
• Active records.
• Deleted records.
• Temporary content.
• Cached data.
• Search indexes.
• Derived knowledge.
• Audit records.
• Backups.
• Synchronization queues.
15.2 Deletion request
A deletion request must identify:
• The target resource.
• The owning domain.
• Related references.
• Derived records.
• Copies and caches.
• Backup implications.
• External provider dependencies.
15.3 Cross-domain deletion
Deletion in one domain must not silently destroy records owned by another domain.
Dependent records must be handled according to their own lifecycle policies.
15.4 Deletion status
The system must distinguish:
• Deletion requested.
• Deletion in progress.
• Removed from active view.
• Deleted from primary storage.
• Pending provider confirmation.
• Retained under a documented policy.
• Backup expiration pending.
• Permanent deletion verified, where verifiable.
15.5 Deletion verification
The system must not claim physical or permanent deletion without evidence from all relevant storage and retention layers.
15.6 User export before deletion
Where appropriate and technically supported, users should have an opportunity to export their data before irreversible deletion.
16. Synchronization, Offline and Backup Security
16.1 Synchronization
Synchronization must preserve:
• Data ownership.
• Access policies.
• Classification.
• Provenance.
• Version information.
• Deletion state.
• Conflict information.
16.2 Device trust
The platform must define which devices may access protected information and how device authorization is revoked.
16.3 Offline data
Offline data must receive protection appropriate to its classification.
Cached access must not become an unrestricted path around revoked permissions.
16.4 Conflict handling
Conflicts must not silently overwrite newer or protected information.
Sensitive conflicts must be handled using the relevant domain policy.
16.5 Backup
Backup design must document:
• Included domains.
• Excluded secrets.
• Encryption.
• Key ownership.
• Retention.
• Restore authorization.
• Deletion interaction.
• Cross-device recovery.
16.6 Restore
Restore operations must validate the user's identity and authorization.
Restoration must not silently reintroduce data that should remain deleted or permissions that have been revoked.
17. Audit, Monitoring and Incident Response
17.1 Audit coverage
Security-sensitive events must be auditable, including:
• Authentication changes.
• Permission grants and revocations.
• Secret access.
• Autofill operations.
• Sensitive exports.
• Deletion requests.
• Recovery operations.
• Agent permission changes.
• Administrative security actions.
17.2 Audit integrity
Audit records must be protected against unauthorized modification and deletion.
The design must define retention, access, and integrity-verification requirements.
17.3 Log redaction
Sensitive values must not be written to ordinary application logs.
Redaction must apply to error paths, retries, diagnostics, and third-party integrations.
17.4 Incident response
The platform must define procedures for:
1. Detection.
2. Initial assessment.
3. Containment.
4. Credential or key revocation.
5. Investigation.
6. Recovery.
7. User notification where required.
8. Evidence preservation.
9. Remediation.
10. Post-incident review.
17.5 Incident scenarios
At minimum, procedures must cover:
• Suspected account compromise.
• Lost or stolen device.
• Credential disclosure.
• Unauthorized Agent access.
• External provider compromise.
• Malicious file execution or exploitation.
• Synchronization data exposure.
• Encryption-key compromise.
• Unauthorized data export.
17.6 Security status
The interface must distinguish:
• Normal.
• Warning.
• Action required.
• Restricted.
• Compromised or suspected compromise.
• Recovery in progress.
A warning must not be presented as a confirmed breach unless evidence supports that conclusion.
18. Android and Platform Security
18.1 Android storage
The implementation must respect Android storage isolation, Storage Access Framework permissions, and supported platform security mechanisms.
18.2 Secure local storage
Sensitive local information must use suitable platform-protected storage and cryptographic mechanisms.
18.3 Clipboard
The application must treat clipboard operations involving secrets or restricted information as sensitive.
Where appropriate, minimize exposure duration and avoid unnecessary copying.
18.4 Screenshots and recent-app previews
The security design must determine when protected views should restrict screenshots or obscure recent-app previews.
These controls must be applied according to sensitivity and platform capability.
18.5 Notifications
Notifications must not reveal secret values or restricted document contents by default.
18.6 Background execution
Background jobs must use appropriately scoped permissions and must not retain access indefinitely after the initiating operation is cancelled or revoked.
18.7 Device capability limitations
The system must communicate limitations when a device or operating-system version does not support a required security feature.
It must not simulate a security guarantee that the device cannot provide.
19. User Experience and Security Transparency
Security must remain understandable without overwhelming the user.
19.1 Default presentation
The default interface should present concise security information.
Advanced details should be available when the user requests them.
19.2 Security Center
Security Center should provide a coherent view of:
• Vault protection status.
• Account and session status.
• Device access.
• Recent sensitive activity.
• Security warnings.
• Recovery readiness.
• Permission review.
• Relevant security actions.
It must not report checks as passed when they have not actually run.
19.3 Permission explanations
Before a sensitive operation, explain:
• What resource will be accessed.
• Which component will access it.
• Why it is needed.
• What data will be exposed.
• Whether an external provider is involved.
• Whether the permission is temporary or persistent.
19.4 Reversibility
Where possible, provide clear ways to revoke permissions, cancel operations, and undo reversible changes.
Irreversible operations must be clearly identified before execution.
19.5 Accessibility
Security controls and warnings must be accessible through:
• Screen readers.
• Keyboard navigation.
• Appropriate contrast.
• Clear text labels.
• Understandable error messages.
• Consistent focus behavior.
Security-critical information must not depend on color alone.
20. Security Testing Strategy
Security testing must be continuous and cover individual domains, integrations, and the platform as a whole.
20.1 Unit testing
Test:
• Permission evaluation.
• Data classification.
• Secret redaction.
• Lifecycle policies.
• Input validation.
• Cryptographic wrapper behavior.
• State transitions.
• Revocation logic.
20.2 Integration testing
Test:
• Cross-domain authorization.
• Secure handoffs.
• Provenance propagation.
• Search visibility.
• Sync and conflict behavior.
• Export and deletion flows.
• Agent and Workflow access.
20.3 Negative testing
Verify that forbidden operations fail.
Examples:
• Reading a resource without permission.
• Using an expired capability.
• Reusing a revoked token.
• Accessing Secure Vault through general search.
• Filling credentials on an unauthorized origin.
• Writing outside an approved folder.
• Exporting protected content without authorization.
20.4 Adversarial testing
Test against:
• Malicious documents.
• Prompt injection.
• Path traversal.
• Malformed files.
• Oversized inputs.
• Cross-origin attacks.
• Replay attempts.
• Privilege escalation.
• Stale authorization.
• Dependency vulnerabilities.
20.5 Recovery testing
Test:
• Interrupted writes.
• Interrupted downloads.
• Failed synchronization.
• Partial export.
• Key rotation.
• Device loss scenarios.
• Session revocation.
• Backup restoration.
• Deletion propagation.
20.6 Security review
Before production release, require an appropriate security review of:
• Authentication.
• Authorization.
• Encryption and key management.
• Secure Vault.
• External provider integrations.
• Agent and Workflow permissions.
• Data export and deletion.
• Critical attack surfaces.
Independent penetration testing should be considered proportionately to the product's deployment and risk.
21. Domain-Specific Acceptance Criteria
21.1 Brain
• [ ] Knowledge records have defined ownership and access policies.
• [ ] Memory is distinct from Knowledge and Context.
• [ ] Retrieval respects source authorization.
• [ ] Derived content retains source and classification relationships.
• [ ] AI-generated claims are not automatically treated as verified facts.
• [ ] Memory review and deletion behavior are defined.
• [ ] Sensitive content is excluded from general retrieval by default.
21.2 Files
• [ ] Local and provider-backed resources respect their actual permissions.
• [ ] File operations validate the requested scope.
• [ ] Untrusted files are handled defensively.
• [ ] Temporary resources have lifecycle policies.
• [ ] Sharing and export require appropriate authorization.
• [ ] Deletion status reflects the actual storage operation.
• [ ] File metadata is protected according to classification.
21.3 Downloads
• [ ] Download sources and destinations are validated.
• [ ] Transfer status reflects actual completion.
• [ ] Downloaded content is treated as untrusted.
• [ ] Integrity checks are applied where supported.
• [ ] Dangerous files are not automatically executed.
• [ ] Sensitive URLs and headers are protected.
• [ ] DRM and access restrictions are respected.
21.4 Secure Vault
• [ ] Vault data is isolated from general Brain and Search operations.
• [ ] Secrets are not inserted into general model context by default.
• [ ] Secret access is explicitly authorized.
• [ ] Key lifecycle and recovery behavior are documented.
• [ ] Autofill validates destination origin.
• [ ] Secret values are excluded from ordinary logs and notifications.
• [ ] Vault locking and session expiration are enforced.
• [ ] Export and recovery require dedicated security workflows.
• [ ] Revocation behavior is tested.
22. Platform-Wide Acceptance Criteria
Identity and authorization
• [ ] Every protected operation has an identifiable actor.
• [ ] Authorization is enforced outside the UI.
• [ ] Resource and operation scopes are validated.
• [ ] Revocation prevents new unauthorized access.
• [ ] Agent and Workflow access is bounded and auditable.
Data protection
• [ ] Data classification is applied consistently.
• [ ] Sensitive data is protected at rest and in transit as applicable.
• [ ] Temporary and cached data are covered by lifecycle policies.
• [ ] Metadata exposure is considered.
• [ ] External data sharing follows approved policies.
Privacy
• [ ] Users can understand data ownership and storage location.
• [ ] Data processing purposes are documented.
• [ ] Export and deletion behavior is transparent.
• [ ] Sensitive information is excluded from general AI processing by default.
• [ ] Privacy-restricted modes behave according to their actual guarantees.
Reliability and recovery
• [ ] Security controls remain enforced during offline operation.
• [ ] Synchronization preserves permissions and classification.
• [ ] Restore does not silently reintroduce revoked access.
• [ ] Partial operations report accurate status.
• [ ] Recovery behavior is tested.
Observability
• [ ] Security-sensitive actions are auditable.
• [ ] Logs are appropriately redacted.
• [ ] Provenance survives domain handoffs.
• [ ] Security Center reports evidence-backed status.
• [ ] Incident procedures are documented.
23. Mandatory Security Test Matrix
The implementation must include, at minimum, the following test cases.

ID
Test scenario
Expected result

SEC-001
Unauthenticated access to protected resource
Rejected

SEC-002
Unauthorized file read
Rejected

SEC-003
Unauthorized file deletion
Rejected

SEC-004
Expired capability use
Rejected

SEC-005
Revoked permission reuse
Rejected

SEC-006
Secure Vault secret in general search
Not exposed

SEC-007
Secret included in ordinary application log
Must not occur

SEC-008
Unauthorized Agent secret request
Rejected

SEC-009
Autofill on mismatched origin
Rejected

SEC-010
Unauthorized form submission
Blocked

SEC-011
Malicious document prompt injection
Must not override trusted policies

SEC-012
Path traversal attempt
Rejected or safely contained

SEC-013
Malformed downloaded file
Handled safely

SEC-014
Unauthorized external export
Rejected

SEC-015
Stale cached access after revocation
Blocked according to revocation policy

SEC-016
Synchronization conflict
No silent destructive overwrite

SEC-017
Restore of revoked permission
Revocation remains effective

SEC-018
Deletion with dependent Brain references
Ownership and retention rules preserved

SEC-019
Temporary secret exposure after vault lock
Prevented according to supported guarantees

SEC-020
Provenance metadata removal during handoff
Must not occur

SEC-021
Sensitive notification preview
Protected by default

SEC-022
Unauthorized device access
Rejected

SEC-023
Export of Secure Vault data through general export
Rejected

SEC-024
Key rotation
Data remains accessible only through valid key lifecycle

SEC-025
Interrupted security-sensitive operation
Accurate recoverable state


Every test must record:
• Test identifier.
• Build or implementation version.
• Test environment.
• Preconditions.
• Execution steps.
• Expected result.
• Actual result.
• Evidence.
• Pass/fail status.
• Known limitations.
24. Release Security Gates
A release must not be considered security-ready solely because ordinary functional tests pass.
Gate 1 — Architecture
• Domain boundaries reviewed.
• Threat model completed.
• Security responsibilities assigned.
• External dependencies identified.
• Critical design decisions documented.
Gate 2 — Implementation
• Authentication and authorization controls implemented.
• Data classification applied.
• Encryption and key handling reviewed.
• Sensitive logging protections implemented.
• Secure Vault isolation verified.
Gate 3 — Verification
• Required security tests executed.
• Negative tests passed.
• Critical integration paths reviewed.
• Known vulnerabilities assessed.
• Recovery behavior verified.
Gate 4 — Operational Readiness
• Incident procedures documented.
• Security monitoring defined.
• User-facing security status accurate.
• Recovery and revocation processes tested.
• Known residual risks documented and accepted by the appropriate authority.
Gate 5 — Release Approval
Release approval must identify:
• Reviewed version.
• Verification evidence.
• Remaining risks.
• Approved exceptions.
• Responsible reviewer.
• Approval date.
• Applicable deployment scope.
Unresolved critical security vulnerabilities must block release unless an explicitly authorized risk decision establishes otherwise.
25. Open Security and Privacy Decisions
The following require dedicated design decisions and implementation research:
1. Exact local encryption architecture.
2. Key hierarchy and recovery model.
3. Secure Vault hardware-backed protection availability.
4. Cross-device secret synchronization design.
5. Backup encryption and recovery boundaries.
6. Secure deletion feasibility across storage providers.
7. Local and Cloud AI data-sharing controls.
8. Provider-specific retention and privacy behavior.
9. Android Autofill integration architecture.
10. Device trust and revocation model.
11. Security audit retention and integrity mechanism.
12. Data classification automation and manual overrides.
13. Privacy behavior of diagnostics and analytics.
14. Incident notification and response responsibilities.
15. Applicable legal and regulatory requirements for target deployment regions.
These decisions must be documented and reviewed before the corresponding security guarantees are advertised.
26. Compliance and Assurance Boundary
This document defines internal product security and privacy requirements.
It does not establish certification or compliance with any particular law, regulation, or security standard.
Where relevant to the intended deployment, the responsible team must assess applicable requirements, including data protection, payment information, identity data, and third-party provider obligations.
Any compliance claim must be supported by a documented assessment and appropriate evidence.
27. Implementation and Verification Boundary
The existence of this specification does not prove that:
• Encryption has been implemented.
• Secure Vault is secure.
• Autofill is protected.
• All permissions are enforced.
• All Agents and Workflows are isolated.
• Data is permanently deleted.
• Backup recovery is secure.
• External providers meet required privacy guarantees.
• The application has passed penetration testing.
• The platform is compliant with a specific regulatory framework.
Each claim requires implementation inspection, reproducible tests, and appropriate review evidence.
28. Relationship to Other Specifications
This document must be read with:
• Mimir Intelligence — Architecture & Domain Boundaries.
• Mimir Intelligence — Shared Services Contracts.
• Mimir Intelligence — Integration Specification.
• Mimir Brain — Functional & UX Specification.
• Mimir Files — Functional & UX Specification.
• Mimir Downloads — Functional & UX Specification.
• Mimir Secure Vault — Functional, UX & Security Specification.
• Platform-wide Model Provenance and Execution Transparency Specification.
• Mimir Platform Architecture and governing ADRs.
If a conflict occurs, approved architecture decisions and governing security policies take precedence. Any proposed change must be reviewed and documented.

───

End of Document
Status: Draft — Pending Security, Privacy & Architectural Review
Document ID: MIMIR-DOC-REG-INT-008
Implementation Status: Not established by this specification
Security Verification Status: Pending implementation and evidence
Release Approval Status: Not granted

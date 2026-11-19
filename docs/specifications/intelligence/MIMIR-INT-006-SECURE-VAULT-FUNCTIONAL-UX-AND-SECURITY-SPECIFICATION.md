---
document_id: MIMIR-DOC-REG-INT-006
document_code: MIMIR-INT-006
filename: MIMIR-INT-006-SECURE-VAULT-FUNCTIONAL-UX-AND-SECURITY-SPECIFICATION.md
status: Draft — Registry assigned; consistency reviewed 2026-11-11
adopted: 2026-11-11
series: MIMIR-INT
source_archive: "Mimir intelligence.zip"
---

Mimir Secure Vault — Functional, UX & Security Specification
Document ID: MIMIR-DOC-REG-INT-006
Status: Draft — For Architectural and Security Review
Domain: Mimir Intelligence
Component: Mimir Secure Vault
Document Type: Functional, UX & Security Specification
Platform Priority: Android-first, Mobile-first
Architecture Alignment: Mimir Kernel-first Architecture

───

1. Purpose
Mimir Secure Vault is the protected storage and controlled-use domain for the user's sensitive digital information.
It provides a unified environment for securely managing:
• Passwords.
• Passkeys.
• Authenticator entries.
• Secure Notes.
• Secure Documents and IDs.
• Payment Cards.
• Digital Wallet records.
• Autofill.
• Security Center.
Its primary responsibilities are:
1. Securely storing sensitive records.
2. Protecting access through authentication and authorization.
3. Managing encryption and key lifecycle through approved security contracts.
4. Providing controlled access to secrets when needed.
5. Supporting secure autofill and authentication workflows.
6. Monitoring security conditions.
7. Managing import, export, recovery, and deletion under explicit policies.
Secure Vault is not a general file manager, knowledge repository, or general-purpose memory system.
2. Security Principles
The following principles are mandatory.
2.1 Zero Unnecessary Exposure
Sensitive values must remain protected unless the user or an explicitly authorized operation requires access.
2.2 Least Privilege
Every operation receives only the minimum access required.
2.3 Explicit Authorization
Access to a secret must be authorized at the time of use, not inferred from a previous unrelated permission.
2.4 Separation of Storage and Use
Reading, copying, autofilling, exporting, and sharing a secret are different operations with separate authorization requirements.
2.5 Encryption by Design
Sensitive records must be protected at rest and in transit according to the approved cryptographic architecture.
2.6 Honest Security Claims
Mimir must not claim:
• End-to-end encryption without a verified implementation.
• Zero-knowledge architecture without a verified design and implementation.
• Hardware-backed protection unless actually available and verified.
• Secure deletion without evidence that relevant copies and key material have been handled.
• Recovery capability that has not been implemented and tested.
2.7 User Control
The user must be able to understand, review, revoke, export, and delete protected information within the limitations of the actual storage and recovery architecture.
3. Domain Ownership and Boundaries

Responsibility
Owner

Password records
Secure Vault

Passkey metadata and protected references
Secure Vault / platform credential integration

Authenticator entries
Secure Vault

Secure Notes
Secure Vault

Secure Documents and IDs
Secure Vault

Payment card records
Secure Vault

Wallet records
Secure Vault

Autofill authorization
Secure Vault

Encryption and key lifecycle policies
Secure Vault security contracts

Generic identity and authorization infrastructure
Shared platform services

Physical storage provider
Configured storage provider

Ordinary file management
Mimir Files

Knowledge and semantic indexing
Mimir Brain

Memory records
Mimir Brain / Memory contracts

Download acquisition
Mimir Downloads


3.1 Secure Vault vs. Files
Secure Vault owns protected records and their security policies.
Files owns ordinary file-management operations.
A Secure Vault document may use a protected storage backend, but it must not become an ordinary searchable file merely because it has file-like content.
3.2 Secure Vault vs. Brain
Brain may manage knowledge about general topics.
Secure Vault manages credentials and secrets.
Secrets must not enter Brain's general knowledge index, semantic retrieval, or long-term memory by default.
3.3 Secure Vault vs. Context
Task context may contain a limited authorization reference or a safe indication that a credential is available.
It must not automatically contain the credential value.
3.4 Secure Vault vs. Agents
Agents may request narrowly scoped access to a credential for an authorized task.
They must not receive unrestricted access to the Vault or its entire contents.
4. Secure Vault Information Architecture
4.1 Main Areas
1. Secure Vault Home.
2. Passwords.
3. Passkeys.
4. Authenticator.
5. Secure Notes.
6. Secure Documents & IDs.
7. Cards.
8. Digital Wallet.
9. Autofill.
10. Security Center.
11. Secure Vault Settings.
These are functional areas within one domain, not separate Engines.
4.2 Secure Vault Home
The home screen should provide:
• Vault lock state.
• Security status.
• Quick access to record categories.
• Recently used records, without exposing secret values.
• Items requiring attention.
• Autofill status.
• Security alerts.
• Recent security activity.
Sensitive values must never appear in dashboard summaries or ordinary notifications.
5. Vault Access and Locking
5.1 Lock States
Conceptual states:
• Locked.
• Unlocking.
• Unlocked.
• Temporarily Locked.
• Authentication Required.
• Recovery Required.
• Security Restricted.
5.2 Unlock Methods
Potential methods:
• Device authentication.
• Biometric authentication.
• Local Vault password or passphrase.
• Hardware-backed authentication where available.
• Other approved authentication mechanisms.
Availability depends on the platform and implementation.
5.3 Unlock Session
An unlock session should have:
• Session identifier.
• Authentication method.
• Start time.
• Expiration policy.
• Last activity.
• Authorization scope.
• Lock status.
The session must not be treated as an unlimited authorization grant.
5.4 Auto-Lock
Auto-lock should consider:
• User-configured inactivity period.
• Application backgrounding.
• Device lock.
• Authentication changes.
• Security events.
• Platform lifecycle events.
The user should be able to select an appropriate lock policy within safe limits.
5.5 Lock Behavior
When the Vault locks:
• Sensitive values must no longer be accessible through ordinary UI.
• Pending sensitive UI content must be cleared or protected.
• Clipboard contents previously copied from the Vault should be handled according to the configured clipboard policy.
• Unauthorized background access must be rejected.
• Existing UI references must not bypass the new lock state.
6. Data Model and Protected Records
The following are conceptual entities.
6.1 Secure Record
Each protected record should have:
• Stable record ID.
• Record type.
• Display title.
• Encrypted payload reference.
• Creation timestamp.
• Modification timestamp.
• Last used timestamp, where appropriate.
• Security classification.
• Associated tags, if supported.
• Record lifecycle state.
• Version or revision reference, if supported.
• Provenance metadata.
• Access policy.
6.2 Protected Payload
The secret content must be separated from ordinary descriptive metadata wherever practical.
Examples:
• Password value.
• Recovery code.
• TOTP seed.
• Private key material.
• Sensitive document content.
• Card security code.
Ordinary metadata must not accidentally expose protected payload values.
6.3 Record Relationships
A record may be related to:
• A website.
• An application.
• A person or organization.
• A user-defined category.
• An authentication identity.
• Another protected record.
Relationships must not expose secret values.
7. Password Manager
7.1 Purpose
Manage login credentials securely.
7.2 Password Record
Potential fields:
• Title.
• Username.
• Password.
• Website or application origin.
• Notes.
• Tags.
• Creation date.
• Modification date.
• Last used date.
• Password strength assessment.
• Optional custom fields.
Sensitive fields must remain encrypted.
7.3 Password Generation
A password generator may support:
• Length.
• Uppercase characters.
• Lowercase characters.
• Numbers.
• Symbols.
• Excluded ambiguous characters.
Generated passwords should be created using a cryptographically secure random source.
The generator must not use model-generated randomness for security-critical values.
7.4 Password Strength
Strength indicators should be based on defensible rules and must not imply that a password is safe merely because it is long.
Potential checks:
• Length.
• Character variety.
• Common password patterns.
• Known compromised-password checks through a privacy-preserving mechanism, if implemented.
• Reuse detection within the Vault.
Compromised-password checking must not transmit plaintext passwords to external services.
7.5 Password Actions
• View after authorization.
• Copy.
• Edit.
• Delete.
• Generate replacement.
• Review history where supported.
• Open associated website through an explicit action.
• Autofill where authorized.
Copying a password must be treated as a sensitive action.
8. Passkeys
8.1 Purpose
Provide access to passkey credentials and supported platform authentication integrations.
8.2 Ownership
Passkey private keys must remain under the protection of the credential provider or secure platform mechanism responsible for them.
Secure Vault may manage metadata and authorized references without extracting or exporting private key material.
8.3 Passkey Metadata
Potential metadata:
• Account name.
• Relying party identifier.
• Username.
• Creation timestamp.
• Last used timestamp.
• Provider.
• Sync status, when known.
• Credential status, when available.
8.4 Passkey Operations
Depending on provider capability:
• View metadata.
• Rename display label.
• Review associated account.
• Use through an authorized authentication flow.
• Remove a credential through the owning provider.
• Review provider and synchronization information.
8.5 Restrictions
Mimir must not claim that it can export, duplicate, or recover passkey private keys unless the underlying credential provider explicitly supports the operation.
9. Authenticator
9.1 Purpose
Manage supported authenticator entries and generate time-based or other supported authentication codes.
9.2 Entry Types
Potential types:
• TOTP.
• HOTP.
• Other standards-based methods when explicitly supported.
9.3 Authenticator Record
Potential attributes:
• Display name.
• Account identifier.
• Issuer.
• Protected secret seed.
• Algorithm.
• Digit count.
• Time period or counter.
• Creation timestamp.
• Last used timestamp.
Secret seeds must remain protected.
9.4 Code Generation
Codes must be generated locally through validated cryptographic implementations where possible.
The system must:
• Respect time synchronization.
• Show expiration countdown when applicable.
• Avoid logging generated codes.
• Avoid placing codes into general conversation context.
• Prevent accidental screenshots or exposure where platform controls permit.
9.5 Recovery
Authenticator migration and recovery must be handled through explicit, secure workflows.
A normal application backup must not be assumed to contain recoverable authenticator secrets.
10. Secure Notes
10.1 Purpose
Store sensitive text that should not enter ordinary notes or general knowledge storage.
Examples:
• Recovery instructions.
• Private reference information.
• Sensitive personal notes.
• Confidential access instructions.
10.2 Features
• Create.
• Edit.
• Delete.
• Search titles and permitted metadata.
• Organize using protected tags.
• Lock and unlock.
• Copy selected content after authorization.
• Optional revision history where safely supported.
10.3 Content Protection
Secure Note content must be encrypted according to the Vault encryption contract.
It must not be automatically indexed by Brain.
AI assistance must require explicit, scoped disclosure of the selected content.
11. Secure Documents & IDs
11.1 Purpose
Store sensitive identity and document records.
Potential examples:
• Identity documents.
• Passports.
• Licenses.
• Certificates.
• Confidential records.
• Scanned documents.
• Protected attachments.
11.2 Document Record
Potential fields:
• Document type.
• Display title.
• Issuing authority.
• Document identifier, protected.
• Issue date.
• Expiry date.
• Encrypted attachment reference.
• Verification state, when applicable.
• Reminder settings.
11.3 Document Storage
Secure documents must use a protected storage contract.
A normal Files listing must not expose document contents or protected metadata without an authorized integration.
11.4 Preview
Preview must require appropriate authorization.
Temporary rendered copies and thumbnails must be protected against unintended disclosure.
11.5 Reminders
Expiry reminders may display safe descriptions.
They must not expose document numbers or full identity details in notifications.
12. Cards
12.1 Purpose
Manage payment-card information securely.
12.2 Record Fields
Potential fields:
• Card label.
• Cardholder name.
• Issuer.
• Card network.
• Last four digits.
• Expiry month and year.
• Protected card number.
• Protected security code, if the user chooses to store it and applicable policies permit.
12.3 Display
Default display should reveal only minimal identifying information.
Full card details require fresh authorization.
12.4 Security
• Card numbers must not appear in ordinary logs.
• Security codes must receive especially restrictive handling.
• Card information must not be included in general search.
• Export must be explicit and strongly authorized.
• Autofill must be limited to supported, authorized contexts.
13. Digital Wallet
13.1 Purpose
Provide a managed interface for supported digital wallet records and integrations.
13.2 Scope
Potential records:
• Payment methods.
• Loyalty cards.
• Membership cards.
• Tickets.
• Supported passes.
• Other user-authorized digital credentials.
13.3 Provider Integration
Wallet integrations must respect the underlying provider's security model.
Mimir must not claim to replace platform wallets or payment providers unless the required capabilities and certifications actually exist.
13.4 Payment Boundaries
Mimir Secure Vault is not automatically a payment processor.
It must not execute financial transactions without an explicitly designed, authorized, and compliant payment integration.
14. Autofill
14.1 Purpose
Provide secure, context-aware access to selected credentials during supported login and form workflows.
14.2 Core Rules
• Autofill is disabled until explicitly configured.
• The user must authorize the applicable platform integration.
• Matching must use trustworthy application or website identity information.
• The system must not autofill secrets into unrelated origins.
• Sensitive fields require appropriate authorization.
• The user must retain control over each autofill event according to configured policy.
14.3 Matching
Potential matching signals:
• Verified application identity.
• Website origin.
• Relying party identifier.
• Explicit user association.
Display names alone must never be sufficient to authorize credential autofill.
14.4 Confirmation Modes
Potential modes:
• Confirm every use.
• Confirm after Vault unlock.
• Limited trusted-app behavior, if supported safely.
The most permissive mode must not be enabled silently.
14.5 Autofill Audit
The Vault may record:
• Credential reference.
• Target application or origin.
• Time.
• Authorization method.
• Result.
It should not record the actual password or secret value in the audit event.
15. Search and Organization
15.1 Search
Secure Vault search should be limited to authorized metadata.
Secret values must not be sent to general search services.
15.2 Categories
• Passwords.
• Passkeys.
• Authenticator.
• Secure Notes.
• Documents & IDs.
• Cards.
• Wallet.
• Custom collections, where supported.
15.3 Tags
Tags must be handled as protected metadata where they may reveal sensitive information.
15.4 Recent Activity
Recent activity should show safe record descriptions and actions.
It must not expose values simply because a record was recently accessed.
16. Encryption Architecture
16.1 Requirements
The final cryptographic design must specify:
• Encryption algorithms.
• Authenticated encryption mode.
• Key derivation.
• Key hierarchy.
• Nonce generation.
• Key rotation.
• Backup encryption.
• Recovery.
• Device binding where applicable.
• Secure deletion and cryptographic erasure semantics.
16.2 Key Separation
The architecture should separate:
• Vault root or master key material.
• Data encryption keys.
• Per-record or per-category keys where appropriate.
• Device-specific protection keys.
• Recovery-related key material.
The exact hierarchy requires a separate cryptographic architecture decision.
16.3 Key Storage
Use platform-supported secure key storage where available.
Hardware-backed protection must be detected and reported accurately.
Software-backed fallback behavior must be explicitly documented and must not be represented as hardware-backed security.
16.4 Key Rotation
The system must define:
• Trigger conditions.
• Re-encryption behavior.
• Failure recovery.
• Version compatibility.
• Backup interactions.
Key rotation must not make records unrecoverable if an operation is interrupted.
16.5 Cryptographic Implementation
Cryptographic primitives must come from maintained, reviewed implementations.
Do not invent custom encryption algorithms or security-critical random-number generation.
17. Recovery and Backup
17.1 Recovery Model
Recovery must be designed before production use.
Potential approaches may include:
• User-controlled recovery secret.
• Device-assisted recovery.
• Securely encrypted backup.
• Approved recovery-key mechanisms.
Each approach has trade-offs between recoverability and resistance to unauthorized access.
17.2 Recovery Warnings
The user must understand:
• What can be recovered.
• What cannot be recovered.
• Which recovery material is required.
• What happens if recovery material is lost.
• Whether recovery requires another device or provider.
17.3 Backup
Backups must preserve encryption boundaries.
A backup must not silently convert encrypted Vault records into plaintext files.
17.4 Restore
Restore must verify:
• Backup authenticity.
• Integrity.
• Compatibility.
• Key availability.
• Record ownership.
• Conflict behavior.
18. Import and Export
18.1 Import
Import may support approved formats and sources.
Requirements:
• Validate input format.
• Detect duplicate records.
• Protect temporary import files.
• Avoid logging secrets.
• Explain the destination and security implications.
• Require confirmation before replacing existing records.
18.2 Export
Export is a high-risk operation.
Requirements:
• Explicit user intent.
• Strong authentication.
• Scope selection.
• Clear format and destination.
• Encryption where supported.
• Temporary-file protection.
• Expiration or cleanup guidance.
• Audit event.
• Clear warning about plaintext exports.
18.3 Domain Restrictions
Secure Vault must not be exported through ordinary Files or Brain operations.
The Vault owns its export authorization and security rules.
19. Deletion and Retention
19.1 Record Deletion
Deletion must distinguish:
• Marked for deletion.
• Removed from active views.
• Encrypted payload deletion requested.
• Provider deletion confirmed.
• Backup retention pending.
• Permanent deletion verified.
19.2 Cryptographic Erasure
Where a cryptographic erasure design is used, the system must verify the scope of key destruction and disclose remaining copies or limitations.
19.3 Backups
Deletion from the active Vault does not automatically mean all encrypted backups have been deleted.
Retention and backup expiration must be documented.
19.4 Recovery Conflicts
Restoring an older backup must not silently resurrect records that the user deliberately deleted without a clearly disclosed recovery policy.
20. AI and Agent Integration
20.1 Default Isolation
Vault records are excluded by default from:
• General AI context.
• Brain knowledge indexing.
• Long-term memory.
• General semantic search.
• Unrestricted agent access.
• Ordinary activity summaries.
20.2 Explicit Disclosure
When a user requests an AI-assisted operation involving a secret:
1. Identify the required record.
2. Explain why access is needed when appropriate.
3. Request authorization.
4. Grant only the minimum required scope.
5. Avoid exposing unrelated Vault data.
6. Limit the duration of access.
7. Record a safe audit event.
8. Clear temporary sensitive material when no longer needed.
20.3 Agent Access
Agents must receive scoped capabilities rather than direct unrestricted Vault access.
A credential reference alone does not authorize secret retrieval.
20.4 Model Execution
Where secret disclosure to a model is proposed, the user must receive clear information about the execution destination and relevant privacy implications.
Local execution does not automatically guarantee complete isolation; the actual runtime and data path must be verified.
20.5 Provenance
AI-generated summaries or outputs based on Vault content must not contain hidden secret values.
Any permitted disclosure must preserve applicable provenance and authorization records without copying secrets into ordinary logs.
21. Security Center
21.1 Purpose
Security Center provides visibility into Vault security conditions.
21.2 Potential Checks
• Weak passwords.
• Reused passwords.
• Expiring documents.
• Duplicate credentials.
• Unused records.
• Compromised credentials through a privacy-preserving integration, if implemented.
• Unusual Vault activity.
• Incomplete security configuration.
• Recovery readiness.
• Authentication and lock settings.
21.3 Security Recommendations
Recommendations must:
• Explain the underlying finding.
• Distinguish verified findings from heuristics.
• Avoid claiming a compromise without evidence.
• Offer an actionable next step.
• Avoid automatic changes to credentials.
22. Security Audit and Activity
Record security-relevant events such as:
• Vault unlocked.
• Vault locked.
• Authentication failure.
• Record created.
• Record updated.
• Record deleted.
• Secret copied.
• Autofill performed.
• Export initiated.
• Recovery initiated.
• Security configuration changed.
• Agent access authorized.
• Access denied.
Audit records must avoid secret payloads.
Access to audit history must itself be protected.
23. Threat Model
The security design must consider:
1. Unauthorized physical device access.
2. Malware on the device.
3. Compromised application components.
4. Malicious or compromised storage providers.
5. Network interception.
6. Malicious imported files.
7. Clipboard leakage.
8. Screen capture and UI exposure.
9. Backup theft.
10. Lost or stolen devices.
11. Credential phishing.
12. Malicious browser origins.
13. Unauthorized agent or workflow access.
14. Log and analytics leakage.
15. Memory disclosure.
16. Key loss or corruption.
17. Rollback to outdated records.
18. Cross-domain authorization mistakes.
A formal threat model and security review are required before production release.
24. User Experience
24.1 Mobile-first
Priorities:
• Fast access to common record categories.
• Clear lock state.
• Minimal exposure of secret values.
• Easy record creation.
• Strong confirmation for risky operations.
• Accessible authentication.
• Simple recovery guidance.
24.2 Reveal Behavior
Secret values remain masked by default.
Reveal should:
• Require authorization when policy demands it.
• Have a clear visible state.
• Automatically hide after the configured interval or relevant lifecycle event.
• Avoid retaining plaintext in navigation previews or background snapshots where platform support permits.
24.3 RTL and LTR
Support Arabic RTL and English LTR.
Special care is required for:
• Passwords.
• URLs.
• Email addresses.
• Card numbers.
• Document identifiers.
• Mixed-direction labels.
Sensitive values should not be reordered or visually misrepresented.
24.4 Accessibility
Support screen readers and scalable text while avoiding accidental disclosure of hidden values through accessibility descriptions.
25. Settings
Secure Vault Settings may include:
• Unlock methods.
• Auto-lock interval.
• Biometric authentication.
• Clipboard cleanup policy.
• Autofill configuration.
• Export policy.
• Backup and recovery.
• Security alerts.
• Protected record categories.
• Privacy controls.
• Recovery readiness.
• Device-specific security status.
Security-sensitive settings should explain their consequences before changes are applied.
26. Error and Failure Semantics
Required categories:
• Authentication failed.
• Vault locked.
• Authorization denied.
• Key unavailable.
• Key invalid.
• Encryption failure.
• Decryption failure.
• Corrupt record.
• Unsupported credential provider.
• Provider unavailable.
• Recovery unavailable.
• Backup verification failed.
• Export blocked.
• Import rejected.
• Permission revoked.
• Secure storage unavailable.
Failure handling must:
• Fail closed for secret access.
• Avoid plaintext fallback.
• Avoid reporting successful decryption when verification fails.
• Preserve non-sensitive diagnostic information.
• Avoid destructive recovery attempts without explicit authorization.
• Provide safe recovery guidance.
27. Acceptance Criteria
The Vault is acceptable for release only when applicable tests demonstrate:
1. Secret payloads are protected at rest.
2. Cryptographic implementation uses approved libraries.
3. Key lifecycle is documented and tested.
4. Authentication and authorization are enforced.
5. Auto-lock works under defined conditions.
6. Locked Vault content cannot be accessed through stale UI references.
7. Secret values are excluded from ordinary logs.
8. Secret values are excluded from general search.
9. Brain indexing does not ingest Vault secrets by default.
10. Agent access is scoped and auditable.
11. Autofill validates application or origin identity.
12. Clipboard handling follows the configured policy.
13. Import and export protect temporary data.
14. Backup and recovery are verified.
15. Deletion semantics are accurately represented.
16. Passkey limitations are respected.
17. Authenticator seeds remain protected.
18. Card information is masked by default.
19. Notifications do not expose sensitive values.
20. Cross-domain references do not grant automatic access.
21. Security failures fail closed.
22. RTL and LTR behavior is correct.
23. Accessibility does not disclose hidden secrets.
24. Threat-model findings are addressed before production release.
28. Open Architectural Decisions
The following require dedicated security and architecture review:
• Vault key hierarchy.
• Encryption algorithm and parameters.
• Key derivation and password-hardening parameters.
• Device binding strategy.
• Hardware-backed key support.
• Recovery model.
• Encrypted backup architecture.
• Key rotation.
• Multi-device synchronization.
• Passkey provider integration.
• Authenticator migration.
• Autofill platform integration.
• Clipboard cleanup guarantees.
• Secure temporary storage.
• Screenshot and task-switcher protection.
• Audit retention.
• Cryptographic deletion semantics.
• Import/export formats.
• Secure Vault integration with Android credential APIs.
29. Implementation and Verification Boundary
This document defines intended architecture, behavior, and security requirements.
It does not confirm that:
• Encryption has been implemented.
• Hardware-backed storage is available.
• Passkeys are supported.
• Autofill is operational.
• Authenticator generation has passed security testing.
• Recovery and encrypted backups work.
• Secure deletion has been verified.
• Security Center checks are operational.
• AI isolation has been enforced in code.
No production security claim may be made without implementation evidence, independent review where appropriate, and successful security tests.
30. Related Documents
• Mimir Blueprint.
• Mimir Kernel Architecture.
• Mimir Intelligence — Architecture & Domain Boundaries.
• Mimir Brain — Functional & UX Specification.
• Mimir Files — Functional & UX Specification.
• Mimir Downloads — Functional & UX Specification.
• Mimir Intelligence Shared Services Contracts.
• Mimir Intelligence Integration Specification.
• Global Model Provenance & Execution Metadata Contract.
• Mimir Application Shell Specification.
• Mimir Design Tokens & Component Library Specifications.
• Security Threat Model.
• Cryptographic Architecture Decision Records.

───

End of Document
Status: Draft — Pending architectural reconciliation, cryptographic design review, document registration, and implementation planning.

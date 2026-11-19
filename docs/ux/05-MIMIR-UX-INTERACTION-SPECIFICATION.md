---
document_id: MIMIR-UX-INT-001
title: Mimir UX Interaction Specification
document_type: Cross-platform Interaction Contract
status: Approved interaction direction — implementation and device validation pending
language: en
scope: Navigation, assistant interaction, execution, approvals, search, memory, privacy, recovery
review_date: 2026-11-09
---

# Mimir UX Interaction Specification

## 1. Purpose

This document defines how users interact with Mimir across Android, tablet, desktop, and web. It translates approved product decisions into predictable interaction contracts. It is a design specification, not proof of implementation.

## 2. Interaction principles

1. User authority: the user remains in control of consequential actions.
2. Context awareness: actions use the active project, workspace, conversation, and selected resources only when their scope is visible.
3. Progressive disclosure: simple defaults first; advanced controls appear when useful.
4. Non-blocking work: navigation must not silently cancel background execution.
5. Honest feedback: distinguish queued, started, running, completed, failed, partial, cancelled, and unknown outcomes.
6. Recoverability: preserve drafts and offer safe retry/restore paths.
7. Explicit permission: viewing, reading, editing, executing, sharing, and publishing are separate capabilities.
8. Accessibility: support touch, keyboard, screen readers, text scaling, RTL/LTR, reduced motion, and reduced transparency.
9. Provenance: preserve source and model attribution throughout result presentation and export.
10. Provider neutrality: UI interactions call Mimir-owned contracts, not vendor SDKs.

## 3. Global interaction flow

`User Intent -> Context Resolution -> Input Validation -> Policy/Permission Check -> Plan/Preview -> Approval (when required) -> Route to Capability -> Execute -> Verify -> Present Evidence -> Recover or Continue`

The user must be able to inspect the intended target and impact before high-impact actions.

## 4. Navigation and workspace continuity

### 4.1 Hybrid navigation
- Drawer: global destinations and grouped workspaces.
- Contextual top tabs: sections inside the active workspace.
- Bottom navigation on mobile: Home, Projects, Tasks, More.
- Back action order: dismiss modal/bottom sheet/popover; close drawer; collapse expanded composer; return from nested detail; then navigate back.
- Back never means cancel task, stop agent, abort tool, or delete draft unless the user explicitly chooses that action.

### 4.2 Workspace switching
- Preserve workspace identity, selected project, active tab, scroll position, filters, and draft where safe.
- Background task/agent/workflow runs continue according to their own policies.
- Switching does not grant or extend permissions.
- Restoration must display stale/offline state where relevant.

## 5. Global assistant and composer

The assistant/composer is a Shell-level capability, not owned by Chat.

States:
- Collapsed.
- Expanded.
- Keyboard active.
- Draft saved.
- Attachment selected/processing/failed.
- Sending/queued.
- Generating.
- Approval required.
- Error/retryable.
- Disabled by policy or unavailable capability.

Requirements:
- Mobile placement remains reachable near the bottom, above bottom navigation and safe areas.
- Keyboard opening adjusts the composer without covering content.
- Drafts persist across workspace navigation and recoverable app restarts.
- Context attachments show exactly which project/files/knowledge sources will be used.
- Sending while offline must clearly show whether the message is locally queued or remotely accepted.

## 6. Conversation, task, agent, workflow, and tool state separation

These are distinct entities and state machines:
- Conversation: open, archived, deleted, restored.
- Message generation: idle, generating, completed, stopped, failed.
- Task: queued, ready, running, paused, blocked, awaiting approval, completed, failed, cancelled.
- Agent run: initializing, active, waiting, handing off, completed, failed, stopped.
- Workflow run: pending, running, waiting, completed, failed, cancelled.
- Tool invocation: requested, permission pending, running, succeeded, failed, timed out, aborted, unknown.

Controls must match the entity:
- Stop generation is not Cancel Task.
- Pause Task is not Pause Agent unless the run contract supports it.
- Abort Tool is not Cancel Workflow.
- Archiving a conversation does not silently stop linked work.

## 7. Approval and consequential action flow

An approval request must state:
- Who/what requests the action.
- Target resource and account.
- Exact action and scope.
- Expected impact and reversibility.
- Data that will leave the device or trust boundary.
- Permissions required and duration.
- Evidence or preview where possible.

Actions: Approve, Reject, Review Details. Opening or viewing an approval card is not approval. High-impact actions require explicit confirmation at the point of execution.

## 8. Search and research interaction

### 8.1 Global search
- Header Search is icon-only.
- Tapping opens a focused search surface.
- Suggestions appear immediately while typing, including likely queries and contextual suggestions.
- Scope is explicit: Mimir-wide, current project, Knowledge Vault, or external research.
- Internal search and external web search must not be visually or semantically conflated.

### 8.2 Research modes
Expose understandable modes such as Speed, Balanced, and Deep Research. Provider-specific modes remain adapter details.
- Speed favors bounded latency and fewer research steps.
- Balanced trades latency and coverage.
- Deep Research allows more iterations, sources, and verification, subject to user limits.

### 8.3 Results and citations
- Display source title, domain, URL, snippet, and retrieval time when available.
- Keep citations attached to claims/results through message editing, export, and handoff.
- Distinguish direct source evidence from model synthesis and widget-derived context.
- Missing source metadata must be labeled unknown; never invent a URL or timestamp.
- Partial research must be labeled partial.

### 8.4 Optional Vane adapter
The reviewed repository `BunsDev/perplexica-search-engine-ai` currently brands itself as Vane. It is an optional Search/Research provider candidate, not a mandatory dependency. The Mimir adapter validates inputs, normalizes its newline-delimited JSON streaming protocol, enforces Mimir permissions and quotas, and preserves source metadata. The client must not receive provider secrets.

## 9. Memory, Knowledge, Context, and Secrets interactions

- Memory: durable information Mimir is permitted to remember.
- Knowledge: curated, versioned documents, notes, links, and graph relationships in Knowledge Vault.
- Context: task-scoped information assembled for a particular execution.
- Secrets: credentials and cryptographic material isolated in Secrets Vault.

User interactions:
- Show what memory/knowledge is being used when the user opens Context details.
- Allow explicit Save to Knowledge and, where appropriate, Save as Memory.
- Explain why a memory is suggested and where it will be stored.
- Provide review, correction, retention, export, and deletion controls.
- Do not silently promote every chat, file, or search result to long-term memory.

### 9.1 Optional Cognee adapter
Cognee is accepted as an optional Knowledge & Memory provider behind Mimir-owned contracts. The UI must never expose Cognee-specific identifiers or require the provider to be active. If unavailable, use another provider or show a clear capability-unavailable state.

### 9.2 Source and conflict review
When two sources disagree:
- Show the conflicting claims and their sources.
- Indicate dates, confidence, and source quality only when supported.
- Let the user select, reject, or defer the decision.
- Preserve the decision and evidence history.
- Do not silently overwrite canonical knowledge.

## 10. Multimodal Live interaction in Chat

Approved functional scope:
- Voice Live: two-way natural voice conversation, interruption support, and synchronized text when supported.
- Camera Live: user-authorized camera observation and scene understanding.
- Screen Live: user-authorized screen sharing and contextual guidance.
- Multimodal Live: combine supported voice, camera, screen, files, and context in one session.
- Switch media sources during a session where device/provider capabilities allow it.

Controls:
- Start, Pause, Resume, End.
- Separate source indicators and independent stop controls.
- Context and privacy controls.
- Explicit permission for each capture source.
- Viewing permission does not imply control permission.
- Assisted interaction (tap/type/navigation) is a separate permission and requires platform support.
- Sensitive external actions require additional approval.

Do not claim all model providers support every modality. Capability discovery and graceful fallback are mandatory. No hidden capture or automatic recording without explicit policy and consent.

## 11. Model provenance and execution feedback

Every model-produced result must preserve, when available:
- Actual model and version.
- Provider.
- Local/Cloud/Hybrid execution.
- Role and contribution.
- Start/completion timestamps as appropriate.
- Execution status.
- Participating models and final synthesizer when applicable.
- Source/handoff relationship.

The default UI remains concise; details are available on demand. User-authored content is never attributed to a model.

## 12. Offline, synchronization, and uncertain outcomes

- Local persistence is not the same as cloud synchronization.
- Show queued, syncing, synced, conflict, and failed states separately.
- Prevent duplicate external side effects after uncertain network outcomes.
- Preserve drafts and pending requests.
- Resolve conflicts using entity-aware rules; do not default to last-write-wins for meaningful user data.
- Display freshness and last successful sync time when available.

## 13. Error and recovery patterns

Every failure should identify:
- What failed.
- Whether any side effect may have occurred.
- What data was preserved.
- Whether retry is safe.
- What the user can do next.

Use retry only with idempotency or a verified safe retry path. Stubs must fail honestly; no fake success or exit code zero.

## 14. Accessibility and responsive behavior

- Mobile-first, Android-first validation.
- Tablet and desktop layouts are designed for their available space, not merely scaled phone screens.
- RTL/LTR layout and mixed-direction code/URLs.
- Dynamic text scaling and readable contrast.
- Screen-reader labels and focus order.
- Keyboard navigation and shortcuts on desktop.
- Reduced motion/transparency.
- Touch targets and safe areas.

## 15. Acceptance checklist

- [ ] Workspace navigation preserves state without cancelling background work.
- [ ] Composer is global and preserves drafts.
- [ ] Conversation/task/agent/workflow/tool states remain distinct.
- [ ] Approvals are explicit and scoped.
- [ ] Search suggestions appear while typing.
- [ ] Internal Knowledge search and external research are distinguishable.
- [ ] Citations preserve source identity through handoff/export.
- [ ] Cognee is optional and provider-neutral.
- [ ] Vane adapter validates and normalizes streaming responses.
- [ ] Memory/Knowledge saving is governed by explicit policy.
- [ ] Live camera/screen/voice permissions are independent.
- [ ] Model provenance survives sync and handoff.
- [ ] Offline and uncertain outcomes are represented honestly.
- [ ] RTL/LTR, accessibility, and responsive states are verified.

## 16. Status

The interaction direction and core user-visible behaviors are approved in the project conversation. Detailed API contracts, implementation, platform permission mapping, and automated/device tests remain pending.

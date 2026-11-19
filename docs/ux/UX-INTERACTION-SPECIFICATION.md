---
document_id: MIMIR-UX-INTERACTION-SPECIFICATION
title: Mimir UX Interaction Specification
document_type: Product UX Specification
status: Working Specification
version: 1.0
language: en
---

# MIMIR — UX Interaction Specification

## 1. Purpose and status

This document defines the user-facing interaction model for Mimir across mobile and desktop. It translates the agreed product direction into observable interface behavior, interaction rules, states, and acceptance criteria.

**Status:** Working Specification. This document is not, by itself, an architectural approval, implementation claim, or release authorization. Any mismatch with the current Constitution, locked decisions, approved ADRs, or canonical architecture must be recorded and reconciled rather than silently resolved in the UI.

## 2. Product experience principles

1. **One Mimir, one coherent product.** Do not create separate applications or navigation systems for novice, intermediate, and developer users.
2. **Hybrid experience.** Balance the assistant and the user's work context. Neither should dominate the Home screen.
3. **Progressive disclosure.** Reveal advanced controls when the current task needs them. Keep advanced capabilities discoverable without making them the default burden.
4. **Intent before machinery.** Users describe outcomes; Mimir selects the appropriate available capability. The interface must not require users to understand internal Engines.
5. **Context continuity.** Projects, conversations, tasks, files, approvals, and outputs should remain connected where the user expects them to be.
6. **Truthful state.** The UI must display only states supported by authoritative runtime evidence. It must never manufacture success, verification, availability, or permission.
7. **User control.** Sensitive, external, destructive, costly, or otherwise policy-protected actions require explicit, scoped approval.
8. **Responsive by design.** Mobile and desktop share the same product model while adapting layout and interaction to their form factors.
9. **Accessible and internationalized.** Support RTL/LTR, keyboard and assistive technology, text scaling, adequate contrast, and touch-friendly controls.
10. **Capability-driven.** The UI reflects registered, available, authorized capabilities; it does not hard-code a separate navigation destination for every Engine.

## 3. User groups and onboarding

Mimir serves:
- People who primarily use AI assistants and everyday productivity tools.
- Users comfortable with projects, tasks, files, and more advanced workflows.
- Developers and technical users who need terminals, code, browser automation, agents, and detailed execution evidence.

Do not ask users to classify themselves at first launch. Do not permanently lock users into an experience tier. Use progressive disclosure, contextual help, and user-controlled advanced views.

## 4. Global application shell and navigation

### 4.1 Shared information architecture

Primary destinations:
- Home
- Chat
- Projects
- Tasks
- Workspace
- Library
- Settings

Supporting surfaces:
- Notifications
- Profile/account
- Approval Center
- Search / command entry where supported

The list above is the proposed product-level information architecture. Final placement, grouping, and responsive presentation are subject to UX Architecture Reconciliation and the approved navigation contract.

### 4.2 Mobile navigation

- Use a bottom navigation bar for the primary destinations.
- Initial proposed compact set: **Home / Projects / Tasks / More**. Chat and other destinations remain reachable through the assistant entry point and/or More, subject to validation.
- Keep the assistant composer fixed at the bottom of Home, above the bottom navigation and device safe area.
- The Home content region scrolls independently; the composer and navigation remain stable.
- Respect the on-screen keyboard and safe-area insets. The composer must remain visible and usable when the keyboard opens.
- Do not place competing assistant composers on the same screen.

### 4.3 Desktop navigation

- Use a persistent sidebar for primary navigation.
- The assistant is clearly visible on Home and can open into a full Chat surface.
- The precise desktop assistant position (central Home region, side panel, or another arrangement) remains an open visual-design decision; it must preserve the agreed balanced Home composition.

### 4.4 Global interaction rules

- Current project/workspace context must be visible when it materially affects an action.
- Navigation changes must not silently discard unsent user input or active work.
- Deep links and back navigation should return the user to a meaningful prior context.
- A capability being unavailable must not be represented as ready or executable.
- Hiding a contextual capability card must not cancel or stop a running task.

## 5. Home

### 5.1 Purpose

Home is the daily starting point: a balanced overview of the assistant and the user's active work, not a technical control panel.

### 5.2 Content hierarchy

1. Header and account/context controls.
2. Assistant entry/composer.
3. Recent and pinned projects.
4. Active and recently updated tasks.
5. Recent activity.
6. Pending approvals and attention items.
7. Useful contextual suggestions, only when grounded in available data.

Do not show fabricated counts, placeholder productivity metrics, or invented activity.

### 5.3 Assistant placement

**Mobile:** fixed near the bottom of the display, reachable by the thumb during one-handed use, above the bottom navigation and safe area.

**Desktop:** prominent and easy to reach without overwhelming project/task context. The exact placement is intentionally unresolved pending wireframes and validation.

### 5.4 Home states

- First-use / onboarding
- Empty projects and tasks
- Normal active workspace
- Active task(s)
- Pending approval(s)
- Offline
- Partial data load
- Service unavailable
- Error with retry/recovery path

Each state must explain what is happening and what the user can do next. Empty states must not masquerade as errors.

## 6. Assistant and Chat

### 6.1 Assistant availability

The assistant is a global interaction capability, not a feature restricted to the Chat destination. It can be invoked from relevant product surfaces while retaining the current context.

On mobile, Home uses the fixed assistant composer. When the user enters the full Chat screen, the conversation composer is fixed at the bottom of the conversation. There must not be two competing input bars.

### 6.2 Chat composition

A conversation may include:
- User and assistant messages
- Streaming responses
- Attachments and generated artifacts
- Model/provider selection where appropriate
- Project context
- Task/action cards
- Approval requests
- Execution progress and results
- Evidence, citations, and limitations

### 6.3 Message intent handling

Distinguish internally and visually where useful:
- Conversational response
- Action request
- Multi-step task
- Project-related work

Do not expose internal classification jargon unnecessarily. Ask one concise clarification question when ambiguity materially changes the intended outcome, target, permission, or risk. Otherwise make a safe, reversible assumption and state it.

### 6.4 Model selection

- Use automatic routing as the default when an eligible Auto mode exists.
- Allow explicit model/provider selection when supported and relevant.
- Never display a model as available if it is not configured, reachable, or permitted.
- Explain fallback or routing changes when they materially affect the task.

### 6.5 Contextual capabilities

- Show tools/capabilities relevant to the current intent and available permissions.
- Advanced options may be progressively disclosed.
- Hiding a card is a presentation preference; it does not stop or revoke the underlying task.
- Do not present a capability catalog as the primary user experience.

### 6.6 Conversation management

Support, where implemented:
- New conversation
- Rename
- Archive
- Delete
- Link to project
- Move/change project association
- Convert a conversation into a task
- Find/search

Clearly distinguish deleting a conversation from deleting its attachments, generated artifacts, project files, or external records. Confirm destructive operations and describe their scope.

### 6.7 Chat states

- Empty
- Drafting
- Sending
- Generating/streaming
- Executing an action
- Waiting for approval
- Interrupted
- Failed
- Completed

Preserve unsent text after recoverable errors. Provide retry only when safe and idempotency is known or the user is warned about possible duplicate effects.

## 7. Task details and lifecycle

### 7.1 Task detail layout

- Header: title, current status, project association.
- Summary: original request and concise current interpretation.
- Plan: proposed and approved steps, clearly distinguished.
- Timeline: step-by-step progress.
- Intervention: approvals, questions, pause/resume/stop controls.
- Results: outputs and artifacts.
- Evidence: logs, test results, citations, provenance, and limitations.
- Activity/history: material changes and user decisions.

### 7.2 Canonical user-visible statuses

Use a consistent status vocabulary across the product:
- Draft
- Queued
- Running
- Waiting for Approval
- Blocked
- Failed
- Cancelled
- Completed
- Verified

`Completed` and `Verified` are not synonyms. A task may complete without independent verification. Use `Inconclusive` as an evidence/result qualification where the available evidence is insufficient; do not misrepresent it as success.

Status transitions must come from the task lifecycle/runtime contract, not local UI guesses.

### 7.3 Step records

Each displayed step should expose, as applicable:
- Step name and purpose
- Status
- Start/end time or elapsed time
- Capability used
- Output/artifact
- Evidence reference
- Failure or blocking reason

### 7.4 Approvals and interventions

- Separate the original request, Mimir's interpretation, proposed plan, approved plan, and any subsequent change.
- Approval must be specific to the action, target, scope, purpose, and duration where applicable.
- A material plan change that increases risk or scope requires renewed approval.
- Rejecting an approval must prevent the protected action.
- Pause, Resume, Stop, Retry, Cancel, Approve, and Reject appear only when the runtime supports the operation and the current state permits it.
- Do not imply that a button click succeeded until the authoritative operation result is received.

### 7.5 Results and evidence

Keep separate:
- User-facing result/summary
- Generated artifacts
- Execution evidence
- Verification verdict
- Known limitations and unverified claims

A successful process exit is not automatically proof that the intended outcome is correct.

## 8. Projects

### 8.1 Purpose

A Project is a durable context boundary and workspace for related conversations, tasks, files, knowledge, and decisions.

### 8.2 Project sections

- Overview
- Conversations
- Tasks
- Files
- Knowledge
- Decisions
- Activity
- Settings

These are product sections, not a requirement to create one Engine per tab.

### 8.3 Context and governance

- Show which project is active when it affects assistant behavior.
- Do not silently mix information between projects.
- Knowledge and Decisions are distinct: a message or draft is not automatically an approved project fact or decision.
- Preserve provenance, status, and ownership of project knowledge and decisions.
- Project deletion/archive must clearly describe its effect on conversations, tasks, files, and linked outputs.

## 9. Workspace

### 9.1 Purpose

Workspace is a task-oriented working environment, not a directory of internal Engines.

Potential contextual workspaces include:
- Browser
- Development
- Automation
- Agents

Show only workspaces/capabilities that are actually registered, available, and authorized.

### 9.2 Interaction and safety

- Browser and computer-use actions must respect granted site/session permissions and approval policies.
- Development actions must expose the relevant project/repository/workspace context.
- Terminal/process execution must show command intent, working directory, and risk/approval where relevant.
- File access and transfer must be scoped and visible.
- Results must link back to the task and evidence record.
- Do not imply that a workspace can perform an operation that is currently a stub, unavailable, or unsupported on the host.

## 10. Library

Library provides a unified user-facing place to find and manage files and reusable knowledge.

Where implemented, support:
- Browse and search
- File type and source filters
- Preview
- Project association
- Collections/folders
- Generated artifacts
- Knowledge references

Clearly distinguish:
- User-uploaded
- Generated by Mimir
- Modified by Mimir
- External/linked
- Temporary/intermediate

Never overwrite or replace an original file silently. Show the destination and consequences and obtain confirmation for destructive or replacing operations.

## 11. Notifications and Approval Center

Notification categories:
- Task updates
- Approval requests
- Task failures/blocks
- Project updates
- System/security alerts

Approval Center is a unified view of pending user decisions, not a new authorization engine. It must display:
- What action is requested
- Why it is needed
- Target/resource
- Scope and duration
- Risk/impact
- What happens if approved or rejected
- Expiration or current validity, if supported

Distinguish approval of a proposed plan from approval of a sensitive execution step.

## 12. Settings

Organize settings into understandable groups, exposing only supported controls:
- Account
- Appearance
- Language and region
- AI models and providers
- Integrations
- Permissions and security
- Memory and context
- Usage and limits
- Advanced
- Developer settings

Do not expose configuration controls that are not wired to an authoritative backend. Explain unavailable settings instead of showing inert toggles.

## 13. Global states and error handling

Standardize the following states:
- Loading
- Empty
- Error
- Offline
- Permission Denied
- Unavailable
- Partial Success
- Inconclusive
- Conflict

Every error should, where possible, explain:
1. What failed.
2. Whether any side effect may already have occurred.
3. What data was preserved.
4. Whether retry is safe.
5. What the user can do next.

Do not erase user drafts or task context because a network call failed.

## 14. Responsive behavior

| Surface | Mobile | Desktop |
|---|---|---|
| Primary navigation | Bottom navigation; overflow through More | Persistent sidebar |
| Home assistant | Fixed near bottom, above navigation/safe area | Prominent balanced Home area or panel |
| Chat | Full-screen conversation; composer fixed at bottom | Main panel or full-screen |
| Workspace | Focused full-screen task workspace | Multi-panel layout where useful |
| Task details | Stacked sections and expandable evidence | Timeline plus detail/evidence panels |
| Projects | Stacked sections | Sidebar/tabs and content panels |
| Dialogs | Full-screen or bottom sheet when appropriate | Modal/dialog or side panel |

Additional requirements:
- Respect safe areas, keyboard insets, and orientation.
- Support RTL and LTR without mirrored icons that should not mirror.
- Preserve state when changing layout or orientation.
- Avoid horizontal scrolling for primary workflows.
- Touch targets must be comfortably usable.

## 15. Accessibility and localization

- Full keyboard navigation on desktop.
- Screen-reader names, roles, states, and meaningful reading order.
- Visible focus.
- Sufficient contrast and no status communication by color alone.
- Text scaling without clipping essential controls.
- Reduced-motion support.
- Clear, actionable validation and error messages.
- RTL/LTR layouts and mixed-direction content handling.
- Localizable strings; avoid embedding UI copy in logic.
- Touch target sizing appropriate for mobile use.

## 16. Architecture boundary rules for UX

1. UI consumes stable product-facing contracts; it does not call Engine internals directly.
2. UI does not own authorization, policy evaluation, task state transitions, or audit truth.
3. UI does not infer capability availability from a hard-coded list.
4. Navigation is product-level and must not be expanded one-for-one for every Engine.
5. A new visual section does not automatically justify a new Engine.
6. Existing project, task, approval, file, event, identity, and capability services should be reused where their contracts are sufficient.
7. UI actions are requests; authoritative services return accepted/rejected/completed outcomes.
8. Evidence and verification must be surfaced from their owning services and retain provenance.
9. The UX must remain usable when optional capabilities are absent.
10. Architecture gaps discovered during reconciliation must be logged as contract, implementation, wiring, or documentation gaps.

## 17. UX-to-architecture mapping (initial hypothesis)

This table is a mapping hypothesis to be verified against the actual Mimir repository; it is not a claim that each capability is implemented.

| UX surface | Expected product-facing responsibility | Reconciliation target |
|---|---|---|
| Home | User context, projects, tasks, activity, approvals | Project/task/activity/notification contracts |
| Assistant | Intent handling and conversation entry | Conversation and orchestration contracts |
| Chat | Conversation, model routing, streaming, attachments | AI runtime, provider, conversation, artifact contracts |
| Projects | Project context and durable organization | Project management and persistence |
| Tasks | Lifecycle, orchestration, interventions | Task and workflow contracts |
| Workspace | Contextual capability access | Capability registry/catalog and policy |
| Browser | Browser interaction | Browser capability and computer-use adapter |
| Development | Build, test, terminal, repository work | Forge, toolchain, process runtime, verification |
| Agents | Agent execution and coordination | Agent runtime and orchestration |
| Library | File and knowledge access | File/artifact/knowledge services |
| Approvals | User authorization interaction | Policy/authorization service |
| Activity | Event history and audit display | Event and audit services |
| Results | Artifacts and evidence | Artifact, evidence, verification services |
| Settings | Identity, providers, permissions, preferences | Identity/configuration/provider/security contracts |

## 18. UX acceptance criteria

The following are product-level acceptance tests. They require implementation and test evidence before being marked passed.

- [ ] UX-T01: A first-time user can start a basic assistant conversation without configuring technical settings.
- [ ] UX-T02: The assistant can be invoked from relevant sections while preserving context.
- [ ] UX-T03: On mobile, the Home assistant entry is reachable with one hand and remains above the bottom navigation.
- [ ] UX-T04: The keyboard does not obscure the active composer or send control.
- [ ] UX-T05: A failed send does not silently discard the user's draft.
- [ ] UX-T06: A conversation can be converted to a task only through a supported task-creation flow.
- [ ] UX-T07: A conversation can be linked to a project and the association is visible.
- [ ] UX-T08: Project context is preserved and is not silently mixed with another project.
- [ ] UX-T09: Approval requests clearly explain scope, target, and consequence.
- [ ] UX-T10: Rejecting an approval prevents the protected action.
- [ ] UX-T11: The UI never reports success without authoritative evidence.
- [ ] UX-T12: Failures expose a useful reason and safe next step where available.
- [ ] UX-T13: Unavailable capabilities are not displayed as executable.
- [ ] UX-T14: Hiding a capability card does not cancel a running task.
- [ ] UX-T15: Advanced technical detail is available when relevant without burdening novice workflows.
- [ ] UX-T16: The same project/task state is coherent across supported devices.
- [ ] UX-T17: Dark and Light themes are both usable and consistent.
- [ ] UX-T18: RTL and LTR layouts preserve reading order and control behavior.
- [ ] UX-T19: Adding a capability does not require adding a primary navigation destination by default.
- [ ] UX-T20: Project, conversation, task, file, artifact, and approval relationships remain consistent across screens.

## 19. Open decisions

These items remain unresolved and must not be treated as approved:
1. Exact desktop assistant placement and behavior at different widths.
2. Final mobile bottom-navigation destinations and the contents of More.
3. Exact global search/command-entry placement.
4. Detailed behavior for suggestions, prompt history, and draft persistence.
5. Which advanced controls are contextual versus user-pinned.
6. Final wireframe and visual design decisions.

Resolve these through UX Architecture Reconciliation, information architecture, wireframes, and validation—not by inventing backend responsibilities.

## 20. Next workflow

1. **UX Architecture Reconciliation:** compare this specification against the current canonical Blueprint, Constitution, locked decisions, ADRs, module graph, class catalog, repository structure, and actual source code.
2. **Information Architecture & Navigation:** settle destinations, hierarchy, labels, and responsive navigation.
3. **Wireframes:** mobile-first and desktop layouts, including the agreed bottom assistant placement.
4. **Visual Design System:** typography, spacing, components, states, Dark/Light themes, RTL/LTR.
5. **Interactive Prototype:** validate key paths and state transitions.
6. **UX Validation:** run acceptance tests with novice, intermediate, and developer workflows.

---

## Change log

| Version | Status | Change |
|---|---|---|
| 1.0 | Working Specification | Consolidated the agreed Hybrid experience, mobile assistant placement, global navigation model, interaction rules, lifecycle states, responsive behavior, architecture boundaries, and acceptance criteria. |

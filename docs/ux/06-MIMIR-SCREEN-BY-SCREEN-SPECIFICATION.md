---
document_id: MIMIR-UX-SCREEN-001
title: Mimir Screen-by-Screen Specification
document_type: Screen and Workspace Specification
status: Approved for explicitly approved screens; remaining screen families pending design review
language: en
scope: Home, Chat, Forge, shared navigation and screen families
review_date: 2026-11-09
---

# Mimir Screen-by-Screen Specification

## 1. Purpose and status rules

This document records screen decisions that have been explicitly approved. It does not invent detailed specifications for screens that have not yet been discussed and approved.

Status labels:
- **Approved:** user explicitly approved the screen direction.
- **Specified by reference:** detailed specification lives in a dedicated subsystem document.
- **Planned / pending:** destination is part of the product map but its screen design is not yet approved.

## 2. Global Shell and navigation

The Shell is shared by all workspaces and owns navigation, global assistant/composer, global search, notifications, connectivity status, overlays, and workspace continuity.

### 2.1 Mobile navigation
- Hybrid navigation: Drawer + contextual Top Tabs + Bottom Navigation.
- Bottom Navigation: Home, Projects, Tasks, More.
- Global assistant/composer remains near the bottom, above bottom navigation and safe areas.
- Advanced tools are contextual and progressively disclosed.

### 2.2 Header
- Drawer control.
- Original Mimir logo asset; preserve the approved concept and use a placeholder until the actual asset is supplied.
- Icon-only global Search with proactive live suggestions.
- Notifications bell with newest-first items and clear distinction between informational and action-required events.
- Connectivity/runtime indicator with Online/Offline/Hybrid state; Syncing is a separate state.
- Account/profile control.

### 2.3 More
More is a dedicated destination, not an overloaded drawer. It provides a two-column grid of available workspaces/tools followed by management destinations. Only capabilities currently available to the user are shown.

## 3. Home — Approved

### 3.1 Purpose
A balanced overview of the assistant, active projects, tasks, important attention items, and recent activity.

### 3.2 Layout order
1. Header.
2. Smart Welcome.
3. Projects Overview.
4. Tasks & Execution.
5. Needs Attention (conditional).
6. Recent Activity.
7. Global Assistant Composer.
8. Bottom Navigation.

### 3.3 Smart Welcome
- Dynamic summary grounded in actual project/task/activity data.
- One or two useful suggested next steps.
- Shows last update/freshness.
- Distinguishes facts, inference, and recommendation.
- Does not invent project progress.

### 3.4 Projects Overview
- Compact vertical list.
- Name/icon, state, last activity, next step/blocker.
- Progress only when supported by reliable task data.
- Prioritize urgent and recently active work.
- Completed/paused projects do not dominate the view.

### 3.5 Tasks & Execution
- Unified priority-oriented list, while keeping Task, Agent, and Workflow state models distinct.
- Supports upcoming, running, paused, awaiting approval, blocked, and completed states.
- Clear links to originating project and execution details.

### 3.6 Needs Attention
Appears only for approvals, conflicts, blockers, security alerts, exceptions, or decisions. Opening an item does not clear it; only resolution does. Avoid duplicates with the task list.

### 3.7 Recent Activity
Chronological important events with real timestamps, grouped where useful, linked to their source context, and privacy-aware.

### 3.8 Connectivity and synchronization
A dedicated connectivity/synchronization layer manages connection state, local persistence, sync queue, conflict resolution, retry/recovery, freshness, and transfers. It is distinct from Orchestration and Intelligent Execution.

## 4. Chat — Approved

### 4.1 Purpose
A conversation, reasoning, collaboration, and work-conversion space—not merely a message list.

### 4.2 Header and history
- Conversation title can be edited.
- New conversation, history, share, model/routing summary, and options.
- History supports recent, pinned, archived, project-linked, search, rename, pin, move, archive, restore, export, and confirmed delete.

### 4.3 Conversation content
Supports text, Markdown, code, images, files, audio, video, links, tool results, execution outputs, approvals, and citations where the relevant capability exists.

Message actions include copy, edit, regenerate, stop generation, rate, share, convert to task/document/note/project, and continue in Forge or Browser.

Editing an earlier message must not silently erase later outputs; preserve a branch or recoverable revision path.

### 4.4 Model and execution
- Smart/Auto default.
- Explicit model selection, multi-model, Local Only, Cloud Only, Hybrid where supported.
- Routing belongs to Orchestration & Intelligent Execution, not Chat presentation.
- Multi-model results are consolidated by default; contribution/provenance details are available on demand.
- Stop Generation, Pause/Resume/Cancel Task, Retry, Abort Tool, and View Execution are separate controls.

### 4.5 Context & Execution panel
Same functional content across Mobile, Tablet, and Desktop:
- Context, Files, Memory, Knowledge, Instructions, Model & Execution, Tools, Agents & Workflows, Activity, Approvals.
- Mobile opens as an appropriate overlay; Tablet as expandable panel; Desktop as persistent/collapsible side panel.
- It is an operational control/monitoring panel, not merely an information drawer.

### 4.6 Chat-specific customization
A separate Chat Settings/Customize Chat control covers tone, detail, language, conversation instructions, model/execution preferences, memory/context sources, tools/permissions, privacy, and display/interaction. It does not alter global Mimir settings.

### 4.7 Multimodal Live
Approved scope includes Voice Live, Camera Live, Screen Live, and combined Multimodal Live where supported. Each source has explicit permission and a persistent indicator. Observation does not grant control; consequential actions require separate approval.

### 4.8 Knowledge and Memory integration
The user can inspect context sources, open related Knowledge Vault entries, explicitly save information, and inspect related graph relationships. Chat does not own the Vault or silently save every message.

## 5. Forge — Approved; detailed specification by reference

Forge is an AI-assisted software engineering workspace for users with and without programming experience. It manages the engineering lifecycle and is distinct from Terminal.

The complete specification is in:
`07-MIMIR-FORGE-SPECIFICATION.md` (`MIMIR-ENG-FORGE-001`).

Approved Forge pages:
1. Forge Home.
2. Projects.
3. Code Workspace.
4. Toolchains & Environments.
5. Tasks & Planning.
6. Agents & Workers.
7. Build & Test Center.
8. Changes & Code Review.
9. Artifacts & Releases.
10. Sandbox & Execution.
11. Forge Settings.

Forge AI roles (Architect, Developer, Reviewer, Tester, Debugger, Explainer, Security) are functional roles and may be implemented by one or multiple models. Forge is technology-neutral and does not require Flutter.

## 6. Planned screen families — pending individual specification

The following destinations exist in the product map, but their detailed screen layouts and interaction decisions must not be treated as approved until reviewed:
- Browser workspace.
- General Projects (distinct from Forge's software Projects).
- General Tasks and Execution Center.
- Agent & Swarm Center.
- Workflow management.
- Bots and Automations.
- Terminal workspace.
- Activity & History.
- Approval Center.
- Files & Knowledge / Knowledge Vault.
- Secrets Vault.
- Settings & Personalization.
- Help & Support.

Each must reuse global Shell, provenance, permission, and accessibility contracts without duplicating global services.

## 7. Cross-screen requirements

- Mobile-first and Android-first; Tablet/Desktop have intentional layouts.
- RTL/LTR and Arabic/English.
- Model Provenance & Execution Metadata remains available across screens and handoffs.
- Local/Cloud/Hybrid execution state is explicit.
- Offline and synchronization states are distinct.
- Context, Memory, Knowledge, and Secrets remain separate.
- Background work is not cancelled by navigation.
- Every consequential action has a visible permission and approval boundary.
- Screen designs consume Mimir-owned contracts, not vendor-specific APIs.

## 8. Acceptance checklist

- [ ] Home and Chat match their approved descriptions.
- [ ] Forge pages match the dedicated Forge specification.
- [ ] Unreviewed screen families are clearly marked pending.
- [ ] Shared Shell behavior is consistent.
- [ ] Provenance, permissions, and execution states remain consistent across screens.
- [ ] Internal knowledge and external research sources are visually distinct.
- [ ] Mobile, Tablet, Desktop, RTL/LTR, and accessibility behavior are specified and tested before release.

## 9. Status

Home, Chat, and all eleven Forge pages are approved at the design-direction/specification level. Other screen families remain planned pending individual review. No implementation or device test is claimed by this document.

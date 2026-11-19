---
document_id: MIMIR-UX-SHELL-001
title: Mimir Application Shell Specification
status: Draft for architectural/design review
scope: Global application experience and workspace hosting
---

# Mimir Application Shell Specification

## 1. Purpose

The Application Shell provides the consistent outer experience of Mimir. It owns global navigation, workspace hosting, global assistant/composer placement, application-level context presentation, overlays, and cross-workspace continuity.

**Core principle: The Shell owns the experience; Engines provide capabilities.**

The Shell must remain generic. It must not contain engine-specific execution logic or require changes to the kernel when an engine is added, removed, or replaced.

## 2. Scope and non-goals

### In scope
- Global application frame.
- Navigation and workspace switching.
- Workspace content host.
- Global assistant and composer.
- Context presentation.
- Global overlays.
- State preservation.
- Shared application-level surfaces such as task/notification/approval entry points.
- Responsive behavior across Android, tablet, and desktop.

### Out of scope
- Implementing Chat, Forge, or Browser internals.
- Owning Agent Runtime or Orchestration execution.
- Owning workflow graph execution.
- Defining engine-specific business rules.
- Duplicating Projects, Tasks, Approvals, Files, or Activity domain services.
- Making the kernel aware of screen names, routes, or engine IDs.

## 3. Governing UX decisions

The following decisions are carried forward:
- Navigation model: **Hybrid**.
- Overall visual personality: **Balanced**.
- Dark and Light themes.
- Adaptive appearance mode is the default for new installations.
- Adaptive performs a gradual time-based day/night color transition; this behavior is exclusive to Adaptive mode.
- Light theme foundation: **Pearl**.
- Motion character: **Adaptive**.
- Mobile-first, with Android as the first validation target.
- Global assistant/composer remains near the bottom of the mobile screen for one-handed reach.
- Advanced controls are disclosed contextually rather than displayed all at once.
- Original Mimir logo concept must be preserved; use a placeholder until the authoritative logo asset is supplied.

## 4. Shell architecture

The Shell is composed of these presentation responsibilities:

1. **Global Frame:** app-level safe area, header, navigation, content region, and bottom region.
2. **Navigation System:** drawer, top tabs, bottom navigation, and navigation history.
3. **Workspace Manager:** active workspace, workspace switching, restoration, and layout mode.
4. **Workspace Host:** mounts capability-provided workspace UI.
5. **Global Assistant/Composer:** shared input and assistant access.
6. **Context Presenter:** displays the active project, task, file, agent, or other relevant context.
7. **Overlay Manager:** dialogs, bottom sheets, popovers, menus, and global command surfaces.
8. **Global Service Entry Points:** access to shared Projects, Tasks, Approvals, Activity, Notifications, and Files/Knowledge experiences.
9. **State Coordinator:** preserves presentation state and coordinates restoration without owning execution state.
10. **Platform Adapter:** safe areas, keyboard, Back behavior, share intents, lifecycle, and platform conventions.

These are Shell responsibilities, not new engines.

## 5. Mobile application frame

### 5.1 Header

The mobile header may contain:
- Menu/drawer control.
- Mimir logo placeholder or approved logo asset.
- Current workspace or screen title.
- Context indicator where useful.
- Search entry point where space allows.
- Notifications entry point.
- Profile/account entry point.

Do not place every possible action in the header. Use contextual overflow or secondary navigation.

### 5.2 Main content region

The content region hosts the active screen/workspace. It must:
- Respect safe areas.
- Scroll independently where appropriate.
- Preserve screen state when navigating away and back.
- Support full workspace, split view, master-detail, embedded panel, and overlay presentation modes when a workspace declares support.
- Avoid hard-coding engine-specific layouts into the Shell.

### 5.3 Bottom navigation

The agreed mobile bottom navigation destinations are:
- Home.
- Projects.
- Tasks.
- More.

The More destination provides access to secondary global areas such as Activity, Agents, Approvals, Files/Knowledge, and Settings. Exact grouping may be refined during screen-by-screen specification, but the Shell must avoid overcrowding the primary bar.

Bottom navigation:
- Shows the current global destination.
- Respects Android safe-area/navigation-bar insets.
- Does not become a container for engine-specific tabs.
- Must coexist with the global composer without overlapping it.

### 5.4 Global assistant/composer placement

The assistant/composer is positioned in the lower portion of the mobile experience for easy one-handed access.

Requirements:
- It remains globally available across workspaces unless a specific screen has a documented reason to temporarily adapt it.
- It sits above the bottom navigation/safe-area region rather than being obscured by it.
- It moves appropriately when the keyboard opens.
- It can collapse to a compact affordance and expand for composition.
- It indicates the active context.
- It preserves drafts when switching workspaces.
- It must not cover essential screen actions or content.
- It must not be implemented as a Chat-only component.

The exact vertical arrangement and compact/expanded dimensions are finalized in responsive screen design and tested on real Android devices.

## 6. Hybrid navigation model

### 6.1 Drawer

The drawer provides broad navigation groups:

**Primary**
- Home.
- Workspaces:
  - Chat.
  - Forge.
  - Browser.

**Organization**
- Projects.
- Tasks.

**Intelligence**
- Agents.
- Activity.

**Management**
- Approvals.
- History.

**Knowledge**
- Files and Knowledge.

**System**
- Settings.
- Help.

This is a navigation grouping only. It does not imply that each item is a separate engine or that the Shell owns the underlying domain logic.

Drawer behavior:
- Mobile: modal/overlay drawer.
- Tablet: adaptive drawer or rail.
- Desktop: persistent or collapsible drawer.
- Current destination is visibly indicated.
- Opening/closing does not cancel background work.
- The drawer supports keyboard and assistive technology navigation.

### 6.2 Top tabs

Top tabs are scoped to the active workspace or screen. They represent views within that context, not global destinations.

Examples may include workspace-defined views, but the Shell must not hard-code their meanings. A workspace declares its own supported tabs through a presentation contract.

### 6.3 Bottom navigation

Bottom navigation exposes only the agreed primary mobile destinations. Secondary destinations remain in More or the drawer.

### 6.4 Back navigation

Back behavior follows a predictable priority order:
1. Close the topmost dismissible overlay.
2. Close the drawer.
3. Collapse an expanded composer.
4. Navigate to the previous workspace tab/view.
5. Navigate to the previous workspace/screen.
6. Exit or delegate to platform behavior only when no in-app destination remains.

Back must not silently cancel a task or background operation. A task cancellation requires an explicit action and confirmed state transition.

## 7. Workspace model

A workspace is a presentation context hosted by the Shell. It may be backed by an engine or a global application capability.

The Shell should support:
- Opening a workspace.
- Switching workspaces.
- Returning to a previous workspace.
- Restoring a workspace.
- Workspace-specific tabs.
- Workspace-specific layout modes.
- Workspace context for the global composer.
- Capability-driven actions.

The Shell must not branch on hard-coded engine identifiers to implement workspace behavior. Use registered descriptors and contracts.

### 7.1 Workspace descriptor (illustrative)

The following is a conceptual example, not a finalized serialization schema:

```yaml
workspace:
  key: example-workspace
  title: Example
  presentation:
    icon: example-icon
    supported_layouts:
      - full
      - split
    tabs:
      - key: overview
        title: Overview
  capabilities:
    - compose
    - contextual_search
```

The final descriptor schema must be defined by the architecture/extension contract. The Shell consumes declared presentation capabilities and does not infer internal engine behavior.

### 7.2 Workspace lifecycle

A workspace may be:
- Not opened.
- Opening.
- Active.
- Inactive but retained.
- Restoring.
- Unavailable.
- Closed.

These are presentation lifecycle states. They must not be confused with agent, task, or workflow execution states.

### 7.3 Workspace switching

When the user switches workspaces:
- Save the current presentation state.
- Preserve unsent composer drafts.
- Preserve navigation position and local view state where supported.
- Update the composer context.
- Restore the destination workspace.
- Do not cancel active background work merely because its workspace is no longer visible.
- Communicate unavailable or failed restoration clearly.

## 8. State ownership and preservation

Keep these state categories separate:

| State category | Owner | Examples |
|---|---|---|
| UI State | Screen/component | Expanded panel, selected tab, scroll position |
| Workspace State | Workspace host/service | Active workspace context, workspace draft |
| Execution State | Responsible runtime/service | Task/agent/workflow status |
| User Preference State | Settings/preferences service | Theme, font, transparency |
| Navigation State | Shell | Route history, selected global destination |

The Shell may display execution state but must not become its source of truth.

Restoration requirements:
- Persist only state that is useful and safe to restore.
- Avoid restoring expired authentication or permission grants as valid.
- Revalidate access before showing protected data.
- Restore drafts where possible.
- Never represent an old execution snapshot as current without refreshing it.

## 9. Global assistant and composer contract

The composer is a Shell-level shared capability.

It receives context from the active workspace and may expose:
- Text input.
- Attachments.
- Context indicator.
- Available capability/tool affordances.
- Send.
- Stop generation when supported.
- Expanded/collapsed state.
- Draft recovery.

The composer must not:
- Assume every workspace is a chat.
- Directly invoke engine internals.
- Bypass approval or permission checks.
- Claim a message or action was accepted before the responsible service confirms it.

Workspace-specific capabilities are discovered through declared contracts. Unsupported actions are hidden or disabled with an explanation.

## 10. Context presentation

The Shell may present concise context such as:
- Current workspace.
- Active project.
- Selected task.
- Selected file or knowledge source.
- Selected agent or team.
- Current conversation/session.

Context rules:
- Display only context relevant to the current action.
- Make context changes visible when they materially affect the composer or action.
- Allow the user to inspect and clear removable context.
- Avoid leaking sensitive context into notifications or lock-screen surfaces.
- Enforce authorization at the data/service layer, not merely by hiding UI.

## 11. Global overlays and layer management

A central overlay manager coordinates:
- Drawer overlays.
- Dialogs.
- Bottom sheets.
- Popovers.
- Context menus.
- Command palette.
- Global search surfaces.
- Approval prompts where appropriate.

Rules:
- Establish predictable stacking and dismissal.
- Avoid multiple blocking modal layers.
- Preserve focus and restore it when overlays close.
- Respect Android Back priority.
- Do not allow an overlay to obscure an urgent system state without a clear reason.
- Overlay visibility does not imply approval or consent.

## 12. Global service entry points

The Shell provides navigation entry points to shared application capabilities:
- Projects.
- Tasks.
- Approvals.
- Activity.
- Notifications.
- History.
- Files and Knowledge.

These are unified user-facing surfaces over their responsible services. The Shell must not duplicate domain data, execution logic, or authorization rules.

## 13. Responsive behavior

### 13.1 Android phone

- Prioritize one-handed reach.
- Keep the global assistant/composer near the bottom.
- Use overlay drawer.
- Use bottom navigation for Home, Projects, Tasks, More.
- Use bottom sheets for many contextual actions.
- Respect keyboard, system bars, cutouts, and safe areas.
- Preserve state across app background/resume and configuration changes.

### 13.2 Tablet

- Use available width for a persistent/adaptive navigation rail or drawer.
- Allow split view where useful.
- Avoid stretching phone layouts across the entire display.
- Keep the composer reachable and context-aware.

### 13.3 Desktop

- Support persistent/collapsible navigation.
- Support wider workspace layouts and optional side panels.
- Provide keyboard navigation and shortcuts where appropriate.
- Support resizable split panes if the workspace declares support.
- Keep global navigation and workspace-specific navigation visually distinct.

## 14. Platform integration contracts

### 14.1 Keyboard

- Composer moves above the keyboard.
- Focus remains predictable.
- Keyboard dismissal does not discard the draft.
- Avoid layout jumps when the keyboard opens/closes.

### 14.2 Android Back

Follow the priority in section 6.4. Do not bind Back to task cancellation.

### 14.3 Share intents

The Shell may receive shared text/files from Android and route them to a user-selected destination. Before sending or uploading externally, show the destination and obtain any required consent.

### 14.4 Lifecycle

On pause/background:
- Preserve safe presentation state.
- Do not assume execution has stopped.
- Avoid starting visual transitions that cannot complete usefully.

On resume:
- Refresh stale data and permissions.
- Re-resolve Adaptive appearance if selected.
- Restore workspace and composer state.
- Reconcile visible execution status with the authoritative runtime.

## 15. Permissions and security

- The Shell is not the authorization authority.
- Every action must be authorized by the responsible service.
- Hidden UI is not a security boundary.
- Approval surfaces must show meaningful scope and impact.
- Permission changes must be reflected promptly.
- Do not expose private content in lock-screen notifications unless the user explicitly enables it.
- Do not reuse expired permissions after app restoration.

## 16. Error and recovery behavior

The Shell must provide a consistent way to surface:
- Workspace unavailable.
- Workspace restoration failure.
- Network/offline state.
- Authentication expiry.
- Permission denial.
- Service unavailable.
- Storage/persistence failure.

Requirements:
- Preserve user input when safe.
- Explain whether an operation may have partially completed.
- Offer retry only when retry is safe.
- Do not duplicate external side effects after an uncertain result.
- Keep errors associated with the relevant workspace or action.

## 17. Observability and diagnostics

For support and quality assurance, record appropriate non-sensitive events such as:
- Workspace opened/switched/restored.
- Navigation destination changed.
- Composer state changed.
- Overlay opened/closed.
- Restoration failed.
- Theme preference/resolution changed.

Do not log message contents, secrets, precise location, or sensitive context unless explicitly justified and governed by privacy policy.

## 18. Acceptance criteria

- [ ] Shell owns global navigation and workspace hosting, not engine execution.
- [ ] New workspaces can be registered without hard-coded kernel changes.
- [ ] Hybrid navigation is implemented consistently across form factors.
- [ ] Mobile bottom navigation contains Home, Projects, Tasks, More.
- [ ] Drawer contains grouped global destinations and Chat/Forge/Browser workspaces.
- [ ] Top tabs are workspace-scoped.
- [ ] Global assistant/composer is available across workspaces and bottom-positioned on mobile.
- [ ] Composer adapts to keyboard and safe areas.
- [ ] Drafts survive workspace switching where supported.
- [ ] Switching workspaces does not cancel background work.
- [ ] UI, workspace, execution, preference, and navigation state remain distinct.
- [ ] Back closes overlays/drawer/collapsed UI before navigating and never silently cancels work.
- [ ] Global service entry points do not duplicate domain logic.
- [ ] Permissions are enforced by responsible services.
- [ ] Android lifecycle restoration is tested.
- [ ] Tablet and desktop layouts are defined without stretching phone UI.
- [ ] RTL/LTR and accessibility behavior are validated.
- [ ] Theme changes do not reset workspace or execution state.

## 19. Implementation sequence

Recommended implementation order after the design contracts are reviewed:
1. Shell foundation and safe-area model.
2. Navigation contracts and hybrid navigation.
3. Workspace registry/host and lifecycle.
4. Global assistant/composer integration.
5. Context and state preservation.
6. Overlay and Back coordination.
7. Global service entry points.
8. Android lifecycle and share integration.
9. Tablet/desktop responsive behavior.
10. Accessibility, RTL/LTR, and integration tests.

Do not begin implementation by hard-coding engine-specific routes or duplicating global capabilities inside individual workspaces.

## 20. Status

This is a consolidated Application Shell specification draft. It captures the decisions and requirements recorded so far. It is not a claim that the Shell has been implemented, integrated, or tested.

---

## 21. Global search and research entry point

The Shell exposes a global search trigger without owning search execution logic.

Behavior:
- The header uses an icon-only Search control.
- Opening Search presents a dedicated search surface with a focused input.
- Suggestions and predictions appear as soon as the user types; submission is not required to begin suggestion display.
- Suggestions may include recent searches, likely queries, current-project context, and Knowledge Vault matches, subject to privacy and access policy.
- Search scope must be visible and distinguish internal Mimir content from external web research.
- The user can open a result, inspect its source, continue research in Chat/Browser, or explicitly save it to Knowledge Vault.
- The Shell routes requests through provider-neutral Search/Research contracts; it must not know Cognee, Vane, SearXNG, or provider-specific API details.
- If an external Search provider is unavailable, show a truthful service state and preserve the query. Do not label stale cached data as live results.
- Search history and suggestions obey the user's privacy mode and retention settings.

Integration boundary:
- External web research may use an optional Search/Research adapter such as the reviewed Vane candidate.
- Internal knowledge retrieval may use the native Knowledge Vault or an optional provider such as Cognee.
- These result classes remain distinguishable in the UI and in model context.

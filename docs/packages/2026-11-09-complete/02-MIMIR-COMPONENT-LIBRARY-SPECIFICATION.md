---
document_id: MIMIR-UX-CLS-001
title: Mimir Component Library Specification
status: Draft for architectural/design review
scope: Shared reusable UI components
---

# Mimir Component Library Specification

## 1. Purpose

This document defines the reusable component system for Mimir. Components provide consistent visual and interaction behavior across Home, Chat, Forge, Browser, Projects, Tasks, Agents, Activity, Approvals, Files, and Settings.

The library is a presentation-layer system. It must not contain business-specific execution logic or become a dependency of the Mimir kernel.

## 2. Governing principles

- Build reusable components around stable UI responsibilities.
- Prefer composition over screen-specific variants.
- Use semantic design tokens from `MIMIR-UX-DTC-001`.
- Keep interaction behavior consistent across workspaces.
- Support mobile-first layouts, tablet, and desktop.
- Support Arabic/English and RTL/LTR.
- Make loading, empty, error, disabled, selected, and success states explicit.
- Do not show fake progress or imply an operation succeeded before confirmation.
- Do not expose advanced controls by default when contextual disclosure is more appropriate.
- Preserve accessibility and keyboard/screen-reader behavior.

## 3. Component contract

Each component must document:
1. Purpose and ownership.
2. Inputs/properties.
3. Events/callbacks.
4. Visual states.
5. Accessibility semantics.
6. Responsive behavior.
7. Loading, empty, error, and disabled behavior where relevant.
8. Token dependencies.
9. Test coverage.

Components must not silently perform external operations. They emit user intent through callbacks or commands to the responsible application service.

## 4. Shared state model

Common visual states:
- Default.
- Hover (where pointer input exists).
- Focused.
- Pressed.
- Active.
- Selected.
- Disabled.
- Loading.
- Error.
- Success.

Not every component needs every state. The component specification must identify applicable states explicitly.

## 5. Foundation components

### 5.1 Typography

Roles:
- Display.
- Heading 1–3.
- Title.
- Body.
- Body emphasis.
- Label.
- Caption.
- Monospace/code.

Requirements:
- Consume typography tokens.
- Support dynamic text scaling.
- Support RTL/LTR and mixed-direction content.
- Avoid truncation unless the component provides a way to reveal the full value.
- Use semantic heading levels where supported.

### 5.2 Icon

Requirements:
- Use a consistent icon family and sizing scale.
- Decorative icons must be hidden from assistive technology.
- Meaningful icons require accessible labels.
- Do not rely on icon shape alone for destructive or ambiguous actions.

### 5.3 Avatar and identity marker

Support:
- User.
- Agent.
- Team/group.
- Workspace/service identity.

Fallback to initials or a neutral symbol when no image is available. Never invent a brand logo or replace the authoritative Mimir logo.

### 5.4 Divider and spacing primitives

Use semantic spacing tokens. Dividers must support subtle and strong variants and must not be used as decoration without structural purpose.

## 6. Action components

### 6.1 Button

Variants:
- Primary.
- Secondary.
- Tertiary/Ghost.
- Destructive.

Recommended size targets:
- Small: 32 px minimum target height where suitable.
- Medium: 40 px.
- Large: 48 px.

These are design targets; platform accessibility target sizes may require larger hit areas.

States:
- Default, hover, pressed, focused, disabled, loading, destructive confirmation where needed.

Rules:
- One primary action per local decision area where practical.
- Loading state prevents accidental duplicate submission.
- Disabled state explains why an action is unavailable when that explanation is useful.
- Destructive actions require clear wording and confirmation proportional to impact.

### 6.2 Icon Button

- Requires an accessible name.
- Provides visible focus and pressed feedback.
- Hit target must meet platform accessibility guidance.
- Tooltip may be provided on pointer devices.

### 6.3 Floating Action Button

Use sparingly for a clear primary action. Must not obscure content, the global composer, or bottom navigation. On mobile, respect safe areas and one-handed reach.

### 6.4 Button group and segmented control

Use for a small set of mutually exclusive options. Do not use as a substitute for navigation when the choices represent distinct workspaces.

## 7. Input components

### 7.1 Text Field

Supports:
- Label.
- Helper text.
- Placeholder.
- Leading/trailing accessory.
- Validation state.
- Clear action where appropriate.

States:
- Default, focused, filled, disabled, read-only, error, success.

### 7.2 Multiline Text Area

- Supports content growth within a defined maximum.
- Preserves text during validation and recoverable errors.
- Supports selection, copy, paste, and accessible labels.

### 7.3 Search Field

- Supports query, clear, loading, and empty states.
- Distinguishes global search from contextual search.
- Does not bypass authorization or data visibility rules.

### 7.4 Select and Menu Select

- Supports single selection by default.
- Clearly indicates selected item.
- Long lists should support search or grouped sections.
- Must be usable with keyboard and assistive technologies.

### 7.5 Checkbox, Radio, and Switch

- Checkbox: independent selections.
- Radio: one choice from a group.
- Switch: immediate binary setting where the effect is clear.
- Do not use a switch for an action that requires a separate confirmation workflow.

### 7.6 Slider

Use for continuous, bounded settings such as transparency or shadow intensity.
- Display current value or a meaningful qualitative label.
- Provide keyboard and accessible value support.
- Enforce safe minimum/maximum values.
- Support reset to default.

### 7.7 Date and Time Picker

- Use locale-aware formatting.
- Respect time zone.
- Clearly distinguish local time from UTC where relevant.
- Support Arabic and English locale presentation.

### 7.8 File Picker and Attachment

- Show file name, type, size, upload state, and failure state.
- Preserve recoverable upload state where possible.
- Do not claim a file is uploaded until the service confirms it.
- Support cancellation without losing unrelated composer text.

## 8. Containers and layout components

### 8.1 Card

Variants:
- Standard.
- Interactive.
- Selected.
- Compact.
- Elevated.

Cards must not become nested containers without a clear hierarchy. Interactive cards require clear focus and pressed states.

### 8.2 Panel

Used for persistent or contextual content. Supports:
- Header.
- Optional actions.
- Body.
- Optional footer.
- Collapsed/expanded state where applicable.

### 8.3 Expandable Section

- Exposes expanded/collapsed state to assistive technology.
- Preserves content state when collapsed unless explicitly documented.
- Avoids unexpectedly shifting focus.

### 8.4 List Row

Supports leading identity/icon, primary and secondary text, trailing metadata/action, and selected/disabled states.

### 8.5 Data Table

- Supports responsive overflow or alternate mobile presentation.
- Uses semantic column headers.
- Preserves readable density.
- Supports loading, empty, and error states.
- Does not use strong glass/refraction behind dense data.

## 9. Navigation components

### 9.1 Drawer

- Mobile: overlay drawer.
- Tablet: adaptive drawer.
- Desktop: persistent/collapsible navigation rail or drawer.
- Supports current-location indication, grouped navigation, and keyboard navigation.
- Closing the drawer must not cancel background work.

### 9.2 Top Tabs

Used for views within the current workspace. Tabs must not duplicate global navigation destinations.

### 9.3 Bottom Navigation

Mobile global destinations are defined by the Application Shell contract. The bar must respect safe areas and remain compatible with the bottom-positioned global assistant/composer.

### 9.4 Breadcrumbs

Use on desktop or deep hierarchical views where they materially improve orientation. Avoid forcing breadcrumbs into narrow mobile layouts.

### 9.5 Navigation Item

Supports default, active, focused, disabled, and notification/attention indicator states. Indicators must not expose sensitive information.

## 10. Overlays and transient surfaces

### 10.1 Dialog

For decisions requiring focused attention. Must:
- Have a clear title and action labels.
- Trap focus appropriately where applicable.
- Support Escape/Back behavior.
- Avoid dismissing destructive or critical confirmation accidentally.

### 10.2 Bottom Sheet

Preferred for many mobile contextual actions and details. Must respect keyboard and safe-area insets.

### 10.3 Modal

Use only when the task requires a blocking focused surface. Avoid modal stacking.

### 10.4 Popover and Tooltip

- Tooltip is supplementary, not the only label for a critical action.
- Popover must have predictable dismissal and focus behavior.
- On touch devices, do not depend on hover.

### 10.5 Context Menu

Must expose equivalent actions through accessible alternatives and avoid hiding critical actions exclusively in a long-press gesture.

### 10.6 Command Palette

- Supports search and keyboard navigation.
- Shows commands appropriate to current context and permissions.
- Does not expose actions the user cannot perform.
- Destructive commands require appropriate confirmation.

## 11. Feedback components

### 11.1 Toast

Use for brief, non-blocking status. Important errors must remain discoverable and not disappear before the user can act.

### 11.2 Alert and Inline Message

Variants:
- Info.
- Success.
- Warning.
- Error.

Use text and iconography, not color alone.

### 11.3 Progress

- Show determinate progress only when the underlying operation provides a reliable measure.
- Otherwise use indeterminate progress and a clear activity description.
- Do not fabricate percentages.

### 11.4 Skeleton

Use only when the expected content structure is known. Avoid excessive shimmer, particularly under reduced-motion preferences.

### 11.5 Empty State

Must explain:
- What is absent.
- Why it may be absent, if known.
- The next useful action, if one exists.

### 11.6 Offline and Connection State

Clearly distinguish offline, reconnecting, degraded, and authenticated states. Preserve user input when possible.

## 12. AI and execution components

These components visualize state and user intent. They do not own agent execution or orchestration logic.

### 12.1 Message

Supports:
- User message.
- Assistant message.
- Agent/system activity message where appropriate.
- Tool result.
- Error or warning.
- Attachments and citations.
- Copy and relevant message actions.

Requirements:
- Distinguish generated content from tool/system status.
- Preserve reading order in RTL/LTR.
- Avoid overly dense metadata in the default view.

### 12.2 Agent Status

Supported display states may include:
- Idle.
- Thinking.
- Planning.
- Working.
- Waiting.
- Approval Required.
- Paused.
- Completed.
- Failed.
- Cancelled.

The UI must render confirmed runtime state. It must not infer or fabricate a state from elapsed time.

### 12.3 Tool Activity

Shows a concise description of the tool/action, current state, and result when available. Sensitive arguments and secrets must be redacted according to policy.

### 12.4 Task Progress

Distinguishes:
- Task status.
- Agent status.
- Workflow status.
- Individual step status.

Never collapse these into one ambiguous status indicator.

### 12.5 Approval Request

Shows:
- Requested action.
- Reason and context.
- Resources affected.
- Scope of permission.
- Duration or expiry, where applicable.
- Reversibility and potential impact.
- Approve once, approve within a defined scope (only when policy allows), reject, and review details.

Opening or viewing an approval is not consent.

### 12.6 Composer

The global composer is shared by the application shell and available across workspaces. It is not owned by Chat.

It supports:
- Text input.
- Attachments.
- Context indicator.
- Optional mode/tool controls.
- Send.
- Stop generation where supported.
- Expanded and collapsed states.
- Draft preservation.
- Sending, processing, approval-required, error, and disabled states.

Workspace-specific behavior must be provided through context/capability contracts rather than duplicated composer implementations.

## 13. Appearance controls

The settings component group includes:
- Appearance mode selector: Light, Dark, System, Adaptive.
- Font selector restricted to available fonts.
- Font-size control.
- Transparency/glass-intensity control.
- Shadow Level 1 control.
- Shadow Level 2 control.
- Shadow Level 3 control.
- Live preview.
- Reset-one-setting action.
- Reset-all action.

Requirements:
- Adaptive is the default appearance mode for new installations.
- The Adaptive gradual day/night transition is exclusive to Adaptive mode.
- Appearance settings save automatically.
- Controls expose current values accessibly.
- Safe bounds protect readability and usability.
- A preview must not alter active workspace or execution state.

## 14. Liquid material usage

The library may expose semantic material variants:
- Solid.
- Subtle Liquid.
- Regular Liquid.
- Prominent Liquid.

Guidelines:
- Use Solid for reading-heavy, dense, or critical content.
- Use Subtle/Regular for navigation, floating controls, and selected panels.
- Reserve Prominent for a small number of intentional focal surfaces.
- Provide non-refraction fallbacks.
- Do not require a specific third-party library in component APIs.
- Effects must respect reduced motion/transparency and performance budgets.

## 15. Responsive and platform requirements

- Mobile-first and Android-first validation.
- Tablet layouts should use available space without simply scaling phone components.
- Desktop may expose hover, keyboard shortcuts, persistent navigation, and multi-panel layouts.
- Touch targets must be practical.
- Safe areas and keyboard insets must be respected.
- Back navigation must follow the Application Shell interaction contract.
- Support RTL/LTR without duplicating components.

## 16. Component testing

Every component should have:
- Unit tests for state and event behavior.
- Accessibility checks.
- RTL/LTR checks where text or layout direction matters.
- Responsive checks at supported breakpoints.
- Theme checks in Light, Dark, System-resolved Light/Dark, and Adaptive intermediate states.
- Reduced-motion and reduced-transparency checks for animated/material components.
- Loading, empty, error, disabled, and success-state tests where applicable.

Integration tests must confirm that shared components do not duplicate workspace state or bypass permissions.

## 17. Acceptance criteria

- [ ] Shared components use semantic tokens.
- [ ] Component contracts define inputs, events, states, and accessibility.
- [ ] Buttons prevent accidental duplicate actions.
- [ ] Inputs preserve user content through recoverable errors.
- [ ] Navigation components do not duplicate global destinations.
- [ ] Overlays have predictable focus and dismissal behavior.
- [ ] Feedback never fabricates progress or success.
- [ ] AI components distinguish task, agent, workflow, and tool states.
- [ ] Approval components require explicit user intent.
- [ ] Composer is global and context-aware, not Chat-owned.
- [ ] Appearance controls expose all agreed settings.
- [ ] Liquid effects have solid/fallback rendering.
- [ ] Components support Arabic/English and RTL/LTR.
- [ ] Reduced-motion and reduced-transparency preferences are respected.
- [ ] Component behavior is tested across themes and responsive layouts.

## 18. Status

This is a consolidated component-library specification draft. Component inventory and behavior are defined at contract level; final visual measurements, implementation APIs, and automated test results remain to be completed.

---

## 19. Search, research, and evidence components

Added after approval of the cross-platform search and research experience.

Reusable presentation components may include:
- **Global Search Trigger:** icon-only entry point in the global header; opens proactive suggestions while the user types.
- **Search Suggestions List:** live suggestions, recent queries, contextual project suggestions, and clear empty/loading/error states.
- **Search Scope Selector:** distinguishes Mimir-wide content, current project, Knowledge Vault, and external web/research sources.
- **Research Mode Selector:** presents simple speed/balanced/deep options without exposing provider-specific names by default.
- **Source Filter:** optional filters for web, academic, discussions, selected domains, and user-authorized files.
- **Research Progress:** shows classification, searching, reading, synthesis, and completion as distinct observable stages.
- **Search Result Card:** title, domain, snippet, retrieval time when available, source type, and actions to open, cite, save, or inspect.
- **Citation/Evidence List:** links each cited claim to the exact source reference; never fabricate missing citations.
- **Source Provenance Detail:** displays source URL, title, retrieval timestamp, extraction status, and provider identity where available.
- **Save to Knowledge action:** asks for destination, source metadata, and whether to save as a document, note, or reference; retrieval alone does not silently persist knowledge.

Component rules:
- Search results from external providers are visually distinguished from Knowledge Vault results.
- Source metadata remains readable on solid surfaces; Liquid material effects must not reduce citation legibility.
- Streaming progress must not imply that the final answer is complete before the terminal completion event.
- A failed or partial source must be marked as unavailable/partial, not silently omitted when that omission changes the reliability of the answer.
- Search scope, source filters, and privacy choices must be keyboard- and touch-accessible and support RTL/LTR.
- Components must not depend on Cognee or Vane SDKs. They consume Mimir-owned view models and contracts.

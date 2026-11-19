---
document_id: MIMIR-UX-LIQUID-001
title: Mimir Liquid Design System and Comprehensive UX Specification
document_type: Design System and UX Specification
status: Proposed consolidated specification — incorporates approved direction; detailed implementation values require validation
language: en
scope: Visual language, themes, tokens, components, application shell, interaction, accessibility, responsive UX
---

# Mimir Liquid Design System & Comprehensive UX Specification

## 1. Purpose

This document defines the visual and interaction system for Mimir. It consolidates the Liquid visual direction with the shared component library, application shell, and interaction principles.

It is a specification, not proof of implementation. Values described as provisional must be validated on target devices before they become fixed production tokens.

## 2. Approved design direction

| Dimension | Approved direction |
|---|---|
| Product visual metaphor | Mimir — a well of knowledge |
| Material language | Liquid, translucent, layered surfaces |
| Visual personality | Balanced |
| Motion philosophy | Adaptive |
| Light palette character | Pearl |
| Appearance experience | Hybrid Dark/Light |
| Theme modes | Light, Dark, System, Adaptive |
| Default for new users | Adaptive |
| Mobile priority | Android-first, one-handed usability |
| Logo | Preserve original concept; use approved source asset only |

Do not replace the original Mimir logo with an approximate redesign. Until the exact approved asset is supplied, use a neutral logo placeholder and label it as such.

## 3. Design principles

1. **Clarity before spectacle** — glass effects must not reduce legibility.
2. **Depth with restraint** — layered materials communicate hierarchy, not decoration.
3. **Consistent semantics** — color and material meaning remain stable across themes.
4. **Adaptive, not distracting** — motion responds to context and accessibility preferences.
5. **Touch-first ergonomics** — important mobile actions remain reachable.
6. **Progressive disclosure** — advanced controls appear when useful.
7. **Responsive continuity** — the same mental model works on phone, tablet, and desktop.
8. **Accessible by default** — contrast, scaling, focus, RTL/LTR, and reduced motion are foundational.
9. **Performance-aware** — blur, refraction, shadows, and animation are budgeted effects.
10. **User control** — appearance and interaction preferences are transparent and reversible.

## 4. Theme modes and behavior

### 4.1 Modes

- **Light:** always uses the Pearl light palette.
- **Dark:** always uses the dark palette.
- **System:** follows the operating system's appearance preference. It does not run Mimir's time-based gradual transition.
- **Adaptive:** follows the local day/night cycle and transitions gradually around sunrise and sunset. This is the only mode with time-based gradual theme interpolation.

Adaptive is the default for new users. During an upgrade, preserve an existing explicit preference; do not overwrite it.

### 4.2 Adaptive schedule

Preferred source of day/night boundaries:
1. Local sunrise/sunset data from a location permission granted specifically for this purpose.
2. A user-selected city or region.
3. A locally configured schedule or user-defined times.

Location is optional. Mimir must not require location access for the app or for theme operation. Explain why location is requested and do not transmit it without appropriate disclosure and consent.

A transition window of approximately 30–45 minutes around sunrise and sunset is a provisional design target, not a final validated constant. The implementation should support configurable duration and graceful fallback.

### 4.3 Adaptive transition behavior

Interpolate semantic design tokens, not arbitrary raw colors scattered through components. The transition may affect:
- Canvas and surface tones.
- Text and muted text colors.
- Borders and separators.
- Accent and highlight colors where safe.
- Glass tint and material opacity.
- Shadow and edge-light intensity.

Maintain readable contrast at every intermediate point. Avoid flashing, abrupt luminance jumps, or color combinations that reduce text visibility.

Handle:
- Time-zone and daylight-saving changes.
- Travel and location changes.
- Manual clock changes.
- App resume after being suspended.
- Missing or stale sunrise/sunset data.
- Reduced-motion preferences.

If reliable schedule data is unavailable, use a deterministic fallback and make the active behavior understandable in settings.

## 5. Semantic token architecture

Components must consume semantic tokens rather than hard-coded palette values.

### 5.1 Token groups

- `color.canvas.*`
- `color.surface.*`
- `color.text.*`
- `color.border.*`
- `color.accent.*`
- `color.status.*`
- `material.glass.*`
- `material.blur.*`
- `material.refraction.*`
- `shadow.level1.*`, `shadow.level2.*`, `shadow.level3.*`
- `typography.family.*`, `typography.size.*`, `typography.weight.*`, `typography.lineHeight.*`
- `spacing.*`, `radius.*`, `size.*`
- `motion.duration.*`, `motion.easing.*`, `motion.distance.*`
- `layer.*`, `opacity.*`, `focus.*`

Exact numeric values should be defined in a token source file after device and accessibility validation.

### 5.2 Pearl light palette

Pearl is a soft, luminous light appearance. It should feel calm and clear rather than stark white. Use restrained cool-neutral and warm-neutral surface relationships, with clear separation between canvas, cards, elevated panels, and overlays.

Avoid relying on translucency alone to distinguish adjacent surfaces.

### 5.3 Dark palette

Dark mode must be designed as a first-class palette, not a simple inversion of Light. Use layered dark neutrals, carefully controlled highlights, and sufficient separation between surfaces. Avoid excessive pure black where it harms hierarchy or OLED transition smoothness.

### 5.4 Status colors

Success, warning, danger, and informational states must remain semantically consistent across Light, Dark, and Adaptive. Never communicate a critical state through color alone; include text or an icon.

## 6. Liquid material system

### 6.1 Material levels

- **L0 — Solid:** opaque, high-clarity surfaces for reading, forms, tables, code, and critical controls.
- **L1 — Soft Glass:** subtle translucency and restrained blur for secondary panels and navigation surfaces.
- **L2 — Liquid Glass:** stronger depth, edge lighting, and optional refraction for selected floating or decorative surfaces.
- **L3 — Hero Material:** limited use for prominent visual moments; never a default for dense content.

### 6.2 Appropriate use

Good candidates:
- Floating assistant/composer container.
- Navigation surfaces and selected tabs.
- Small contextual panels.
- Floating action controls.
- Lightweight overlays where the background remains visually calm.

Prefer solid or near-solid surfaces for:
- Long-form text.
- Code editors and terminal output.
- Data tables and dense lists.
- Forms and settings controls.
- Approval details and security-sensitive content.
- Error messages and critical alerts.

### 6.3 Material properties

A material may define:
- Base tint.
- Opacity/transparency.
- Blur radius.
- Edge highlight.
- Shadow depth.
- Refraction/displacement strength.
- Saturation and noise/grain restraint.

Do not assume every platform/browser supports every effect. Provide graceful fallbacks to tint + blur or a solid surface. Feature-detect capabilities and test actual rendering.

### 6.4 quickLiquid evaluation boundary

The `quickLiquid` repository may be evaluated as a source of presentation techniques, such as SVG displacement/refraction, edge lighting, and spring motion. It is not the Mimir design system, not a complete theme architecture, and not a Kernel dependency.

Before adopting any code:
- Pin and review the exact revision.
- Verify the license and preserve required notices.
- Audit dependencies and security.
- Benchmark rendering and battery impact on target Android devices.
- Verify browser/platform fallbacks.
- Check reduced-motion and reduced-transparency behavior.
- Wrap adopted behavior behind Mimir-owned material tokens and adapters.

No claim of build, device, or performance testing is made by this specification.

## 7. Appearance settings

Settings should be simple, fast, and reversible.

Required controls:
- Theme mode: Light / Dark / System / Adaptive.
- Font family selector showing only fonts actually available to the app/platform.
- Font size control with clear preview.
- Glass/transparency intensity.
- Three independently adjustable shadow levels.
- Restore default for an individual setting.
- Restore all appearance defaults.
- Live preview and automatic persistence.

Transparency and opacity must be defined unambiguously. The user-facing control should describe the effect (for example, “Glass intensity”) while the underlying token semantics remain consistent.

Shadow levels 1, 2, and 3 must be independently configurable. Changing one must not silently modify the others.

Defaults should remain the recommended starting point. Resetting one control must not reset unrelated preferences.

## 8. Typography and content

- Use a clear hierarchy for display, headings, body, labels, metadata, and code.
- Preserve readable line length and line height.
- Support user font scaling without clipping or hiding controls.
- Use tabular numerals for durations, counters, and aligned numeric data where appropriate.
- Keep code and technical output distinguishable from prose.
- Support Arabic RTL, English LTR, mixed-direction text, punctuation, URLs, and code blocks.
- Do not mirror icons whose meaning is direction-independent; mirror directional icons where appropriate.

## 9. Motion system

Motion personality is Adaptive:
- Use motion to explain state changes, hierarchy, and spatial relationships.
- Prefer short, purposeful transitions.
- Avoid continuous decorative animation.
- Respect reduced-motion settings.
- Provide non-motion state cues.
- Avoid parallax or blur-heavy animation on low-power devices.

Adaptive theme interpolation is a special time-based behavior and is exclusive to Adaptive mode. Other theme modes may use ordinary short UI transitions, but must not perform the day/night gradual color transition.

## 10. Component library

All components use semantic tokens and shared interaction contracts.

### 10.1 Actions
- Button: Primary, Secondary, Tertiary/Ghost, Destructive.
- Icon Button.
- Floating Action Button.
- Split Button only where a real secondary action exists.

### 10.2 Inputs
- Text Field, Multiline Field, Search Field.
- Select/Combobox.
- Checkbox, Radio, Switch.
- Slider.
- Date/Time Picker.
- File Picker and attachment control.

### 10.3 Containers
- Card, Panel, Section, Expandable Section.
- Divider and grouped list.
- Bottom Sheet and Dialog.
- Popover, Context Menu, Command Palette.

### 10.4 Navigation
- Drawer.
- Bottom Navigation.
- Contextual Top Tabs.
- Breadcrumbs or back-navigation context where needed.
- Workspace switcher.

### 10.5 Feedback
- Toast, Inline Alert, Banner.
- Progress and activity indicators.
- Skeleton loading.
- Empty, Offline, Error, and Success states.

### 10.6 AI and execution components
- Message and conversation item.
- Composer and attachment tray.
- Agent status.
- Tool activity.
- Task/workflow progress.
- Approval request and decision summary.
- Multi-agent coordination summary.
- Execution result and recovery action.

Each component must define states, accessibility semantics, keyboard/touch behavior, loading/error behavior, and responsive behavior.

## 11. Component states

Use only states relevant to each component:
- Default.
- Hover (pointer environments).
- Focus-visible.
- Pressed.
- Active/Selected.
- Disabled.
- Loading.
- Error.
- Success.
- Expanded/Collapsed.
- Read-only.

Disabled controls must remain understandable and should explain why an action is unavailable when that context matters.

## 12. Application shell and navigation

### 12.1 Shell principle

The Shell owns the overall experience. Engines provide capabilities through registered workspace contributions.

### 12.2 Mobile navigation

Bottom navigation:
- Home.
- Projects.
- Tasks.
- More.

The Drawer provides broader destinations and workspace access. It opens as an overlay on mobile.

### 12.3 Tablet and desktop

- Tablet: adaptive drawer and flexible content panes.
- Desktop: persistent or collapsible navigation, wider work areas, optional split view.
- Do not simply stretch the mobile layout; use the extra space for useful context and parallel work.

### 12.4 Workspace model

Support:
- Full workspace.
- Split view.
- Master-detail.
- Embedded contextual panel.
- Overlay view.

Workspace switching preserves drafts and view state. It must not implicitly cancel execution.

### 12.5 Global assistant/composer

The composer is a shared shell capability, not a Chat-only component.

On mobile:
- Place it near the bottom for one-handed reach.
- Keep it above bottom navigation and system safe areas.
- Move and resize it correctly when the keyboard opens.
- Preserve draft text and attachments through navigation and recoverable errors.
- Make active context visible and editable.
- Avoid covering critical content or primary controls.

On larger screens, it may adapt to a docked or contextual placement while remaining globally accessible.

## 13. Home and dashboard

The Home experience follows the previously selected Balanced direction:
- Clear overview without excessive density.
- Prominent global assistant entry.
- Recent and active work.
- Projects and tasks that need attention.
- Agent/workflow activity when relevant.
- Approvals and notifications surfaced by importance.
- Progressive disclosure for advanced metrics and controls.

Do not turn Home into a wall of cards. Prioritize the next useful action and allow the user to customize or dismiss nonessential sections.

## 14. Core screen specification outline

Each screen specification must define purpose, primary user jobs, information hierarchy, actions, states, permissions, responsive layout, accessibility, and acceptance tests.

Required screen families:
1. Home/Dashboard.
2. Chat.
3. Forge.
4. Browser.
5. Projects.
6. Tasks and Execution Center.
7. Agent and Swarm Center.
8. Activity and History.
9. Approval Center.
10. Files and Knowledge.
11. Settings and Personalization.

This document defines their shared system and shell. Each screen needs a dedicated detailed specification before implementation.

## 15. Interaction architecture

General flow:

`User Action → Context Resolution → Policy/Permission Check → Action Routing → Execution → Confirmed Feedback → Recovery or Result`

The UI requests actions and displays authoritative state. It is not the security boundary and must not invent execution status.

### 15.1 Composer states

Collapsed, Expanded, Keyboard Active, Sending, Processing, Approval Required, Error, Disabled.

Prevent accidental duplicate sends. Preserve drafts. Show the active model/tool/context when useful. Distinguish stop generation from cancelling a task or aborting an external operation.

### 15.2 Execution states

Agent states may include:
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

Task, workflow, tool, and agent state machines remain distinct. A displayed percentage must come from measured progress.

### 15.3 Approval interaction

An approval request should explain:
- What action will occur.
- Why it is requested.
- Which account, resource, or data is affected.
- Scope and duration of permission.
- Whether the action is reversible.
- Relevant risks.

Actions may include Approve Once, Reject, and Review Details. Broader or persistent permission grants require a separate, explicit scope and must be supported by policy.

### 15.4 Cancellation and uncertain outcomes

Pause, Resume, Retry, Cancel, Stop Generation, and Abort are distinct operations. Do not claim success before the responsible service confirms it. If an external action may have completed but confirmation was lost, show an unknown outcome and provide a safe verification path rather than blindly retrying.

## 16. Responsive and platform behavior

- Android-first implementation and validation.
- Respect system bars, cutouts, safe areas, keyboard, back navigation, and lifecycle.
- Tablet layouts use space intentionally.
- Desktop supports pointer, keyboard, and multi-pane workflows.
- Web layouts degrade gracefully across supported browsers.
- Preserve essential functionality when advanced visual effects are unavailable.

## 17. Accessibility and localization

Required:
- WCAG-aligned contrast targets appropriate to the product and platform.
- Visible focus indicators.
- Screen-reader labels and meaningful reading order.
- Keyboard navigation.
- Touch target sizing validated against platform guidance.
- Dynamic text scaling.
- Reduced motion and reduced transparency.
- High-contrast compatibility where available.
- RTL/LTR and mixed-direction testing.
- Localized dates, times, numbers, and pluralization.

Do not encode information by color, blur, or motion alone.

## 18. Performance budgets and graceful degradation

Define measurable budgets during implementation for:
- Initial shell render.
- Navigation transition.
- Scroll performance.
- Memory use.
- GPU/CPU use of glass effects.
- Battery impact during long sessions.
- Theme transition smoothness.
- Accessibility tree responsiveness.

If the device cannot sustain an effect, reduce blur/refraction or use a solid fallback. Never make essential controls dependent on a costly effect.

## 19. Privacy and permission UX

- Ask for permissions at the moment of need with a clear explanation.
- Request the narrowest scope.
- Show active access and provide revocation.
- Avoid exposing sensitive content in lock-screen notifications.
- Keep audit history useful without storing unnecessary content.
- Browser, file, microphone, camera, location, and external account permissions must be separately scoped.

## 20. Design QA and acceptance checklist

### Visual
- [ ] Pearl Light and Dark palettes are independently reviewed.
- [ ] Theme tokens are semantic and centrally defined.
- [ ] Glass is restrained and content-appropriate.
- [ ] Original approved logo asset is used; no substitute is invented.
- [ ] Focus, error, disabled, and selected states are visually clear.

### Theme behavior
- [ ] Light remains Light.
- [ ] Dark remains Dark.
- [ ] System follows OS appearance.
- [ ] Adaptive follows local day/night schedule.
- [ ] Only Adaptive performs time-based gradual interpolation.
- [ ] Contrast remains readable during every transition frame.
- [ ] Existing user preference is preserved during upgrade.

### UX
- [ ] Mobile composer is reachable and keyboard-safe.
- [ ] Drawer, top tabs, and bottom navigation work coherently.
- [ ] Workspace switching preserves state.
- [ ] Background execution is not silently cancelled.
- [ ] Approval and uncertain-outcome states are explicit.

### Accessibility
- [ ] RTL and LTR are verified.
- [ ] Text scaling does not clip content.
- [ ] Reduced motion/transparency are respected.
- [ ] Screen reader and keyboard flows are tested.

### Performance
- [ ] Glass effects are benchmarked on target Android devices.
- [ ] Fallbacks are tested on unsupported browsers/platforms.
- [ ] Battery, memory, and scrolling impact are measured.
- [ ] Dense content uses readable solid surfaces.

## 21. Implementation sequence

1. Freeze semantic token names and theme contract.
2. Implement Light, Dark, System, Adaptive mode controller.
3. Validate Adaptive scheduling and interpolation independently.
4. Implement solid component foundations.
5. Add controlled liquid material variants.
6. Build application shell and global composer.
7. Implement component states and accessibility.
8. Specify and implement screen families one by one.
9. Run visual, functional, accessibility, and performance QA.
10. Adopt any third-party liquid effect only after license, code, fallback, and device review.

## 22. Known open items

- Exact numeric token values and color values.
- Final font families and font availability by platform.
- Approved original logo asset.
- Exact sunrise/sunset data provider and privacy model.
- Transition duration and interpolation curve after device testing.
- Exact blur/refraction implementation and platform support matrix.
- Component-level touch target and spacing values.
- Final Home content prioritization and per-screen layouts.
- Actual framework and code packaging.

These items must be resolved through design review, ADRs, prototypes, or measured tests rather than assumptions.

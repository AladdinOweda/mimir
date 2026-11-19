---
document_id: MIMIR-UX-DTC-001
title: Mimir Design Tokens & Theme Contract
status: Draft for architectural/design review
scope: Cross-platform presentation foundation
---

# Mimir Design Tokens & Theme Contract

## 1. Purpose

This document defines the visual foundations and theme contract shared by all Mimir screens, workspaces, and reusable components. It establishes a consistent design language while allowing the presentation layer to evolve independently of the application kernel and execution systems.

The contract covers:
- Design-token categories and naming principles.
- Light, dark, system, and adaptive appearance modes.
- The Mimir Liquid visual direction.
- Color, typography, spacing, shape, elevation, transparency, and motion tokens.
- Runtime theme resolution and transition behavior.
- Accessibility, responsive behavior, persistence, and acceptance criteria.

This is a design and implementation contract, not a claim that the tokens have already been implemented or validated on devices.

## 2. Governing principles

1. **One source of truth:** shared visual values must come from semantic tokens, not scattered hard-coded values.
2. **Semantic before literal:** components consume semantic roles such as `surface.primary` rather than palette-specific values.
3. **Theme independence:** components must work in every supported appearance mode without custom per-screen theme logic.
4. **Presentation-layer ownership:** theme resolution and visual effects belong to the presentation layer. They must not become dependencies of the kernel, Agent Runtime, Orchestration, Workflow Runtime, Bot Profiles, Loop Runtime, or Intelligent Execution.
5. **Readable before decorative:** translucency, blur, refraction, and shadows must never compromise text clarity or control discoverability.
6. **Progressive enhancement:** advanced liquid effects may degrade gracefully where unsupported or too expensive.
7. **User control:** users can select an appearance mode and customize supported appearance settings.
8. **Accessible motion:** respect reduced-motion and reduced-transparency preferences.
9. **Stable contracts:** token names and semantics are more stable than their current numerical values.

## 3. Visual direction

Mimir uses a **Hybrid** visual system with both Dark and Light themes.

The approved visual direction is:
- Personality: **Balanced**.
- Motion character: **Adaptive**.
- Light theme foundation: **Pearl**.
- Material language: **Mimir Liquid**, using restrained water/glass-inspired transparency and depth.
- The original Mimir logo concept must be preserved. Do not invent, approximate, redraw, or substitute the logo. Until the authoritative asset is supplied, use a clearly identified logo placeholder.

The interface should feel calm, intelligent, clear, and tactile. Avoid excessive glass effects, high-contrast neon styling, decorative gradients that reduce readability, and unnecessary animation.

## 4. Appearance modes

The appearance selector exposes exactly four user-facing options, in this order:

| UI label | Contract value | Behavior |
|---|---|---|
| نهاري | `light` | Always resolve to the Light theme |
| ليلي | `dark` | Always resolve to the Dark theme |
| حسب النظام | `system` | Follow the operating system's effective appearance |
| تكيفي | `adaptive` | Resolve appearance from the local day/night cycle and perform a gradual color transition |

### 4.1 Default

`adaptive` is the default appearance mode for new installations and users without a saved preference.

A previously saved explicit user selection must not be silently overwritten during an upgrade. Migration logic must preserve an existing valid selection; the default applies only when no preference exists or when a deliberate product migration is approved.

### 4.2 Light mode

- Uses the Pearl light-theme foundation.
- Remains light regardless of time or operating-system appearance.
- Does not run the Adaptive time-based color transition.
- Must preserve sufficient contrast on translucent surfaces.

### 4.3 Dark mode

- Uses a purpose-designed dark palette, not a simple inversion of Light.
- Avoid pure black as the default large-area surface unless a specific platform or accessibility need justifies it.
- Maintains hierarchy between canvas, surfaces, elevated surfaces, borders, and text.
- Does not run the Adaptive time-based color transition.

### 4.4 System mode

- Follows the operating system's effective light/dark appearance.
- Reacts to operating-system appearance changes while the app is active or when it resumes.
- Does not use local sunrise/sunset calculations.
- Does not run the Adaptive time-based transition. Platform-driven changes should be applied according to platform conventions.

### 4.5 Adaptive mode

Adaptive is a distinct mode, not an alias for System.

It uses the user's local day/night cycle. The preferred model is sunrise/sunset-aware scheduling, with a gradual transition around those boundaries.

Approved behavior:
- Daytime resolves toward the Pearl Light theme.
- Nighttime resolves toward the Mimir Dark theme.
- The transition is gradual rather than an abrupt swap.
- The transition applies to the semantic color/material token layer, not by cross-fading two complete screenshots.
- The transition is exclusive to Adaptive mode.
- It must not continuously animate when no meaningful visual change is required.
- The interface must remain readable at every intermediate point.

Recommended initial transition window: **30–45 minutes around sunrise and sunset**. This is a tunable product parameter, not a hard-coded architectural constant. The final duration should be validated through design review and device testing.

#### 4.5.1 Solar-time inputs and privacy

Preferred input order:
1. User-approved location, used locally to calculate sunrise and sunset.
2. If location permission is not granted, use a documented fallback such as a manually selected region/city or a reliable locally available solar schedule.
3. If no solar data is available, use a predictable local-time fallback with user-adjustable day/night boundaries.

Requirements:
- Do not require location permission merely to use Mimir.
- Explain why location is requested if it is offered for more accurate solar transitions.
- Do not transmit precise location solely for theme calculation unless separately justified, disclosed, and authorized.
- Recalculate when the time zone, date, location setting, or system clock changes.
- Handle travel, daylight-saving changes, clock corrections, and app resume.
- If solar data is stale or unavailable, use the documented fallback and avoid erratic switching.

### 4.6 Appearance resolution precedence

The selected mode is the user's preference. The resolved theme is the current visual result.

```text
appearancePreference = light | dark | system | adaptive

resolve(preference):
  light    -> Light
  dark     -> Dark
  system   -> operatingSystemAppearance
  adaptive -> localDayNightSchedule
```

Do not allow operating-system appearance to override an explicit `light`, `dark`, or `adaptive` selection.

## 5. Adaptive transition contract

### 5.1 Transition scope

The adaptive transition may interpolate:
- Canvas and background colors.
- Surface and elevated-surface colors.
- Primary and secondary text colors.
- Border and divider colors.
- Accent and state colors where safe.
- Liquid material tint and highlight colors.
- Shadow color and intensity where appropriate.

The transition must not interpolate values in a way that makes controls disappear, changes layout geometry unexpectedly, or reduces text contrast below accessibility requirements.

### 5.2 Motion behavior

- Use a smooth, non-distracting easing curve.
- Avoid flashing, abrupt hue jumps, or simultaneous high-amplitude movement.
- Do not animate layout dimensions as part of a theme transition.
- Do not animate every child independently if a shared token transition can produce the same result.
- Avoid restarting the transition on every render or routine state update.
- When the app resumes after being suspended, resolve the correct current theme and continue from the current state without replaying a long transition unnecessarily.

### 5.3 Reduced motion

When the platform or user preference requests reduced motion:
- Disable decorative motion and spring effects.
- Adaptive color changes may use a shorter, low-motion transition or an immediate accessible theme update.
- Do not sacrifice readability or delay access to controls.

## 6. Token architecture

Use three conceptual layers:

1. **Primitive tokens:** raw palette, dimensions, durations, and numeric values.
2. **Semantic tokens:** roles such as `surface.canvas`, `text.primary`, and `border.subtle`.
3. **Component tokens:** narrowly scoped values for a component when semantic tokens alone are insufficient.

Components should normally consume semantic tokens. Primitive tokens should not be referenced directly by application screens.

### 6.1 Naming convention

Recommended format:

```text
category.role.variant.state
```

Examples:
- `color.surface.canvas`
- `color.surface.panel`
- `color.text.primary`
- `color.border.subtle`
- `color.action.primary.background`
- `color.status.error.foreground`
- `space.layout.section`
- `radius.control.medium`
- `shadow.level.2`
- `motion.duration.standard`

Names must describe purpose, not a particular screen or current color value.

### 6.2 Color token groups

Define at least the following semantic groups:

| Group | Required roles |
|---|---|
| Canvas | `canvas`, `canvas.subtle` |
| Surface | `surface.primary`, `surface.secondary`, `surface.elevated`, `surface.sunken` |
| Text | `text.primary`, `text.secondary`, `text.tertiary`, `text.inverse`, `text.disabled` |
| Border | `border.default`, `border.subtle`, `border.strong`, `border.focus` |
| Action | `action.primary`, `action.secondary`, `action.destructive`, including foreground/background/hover/pressed/disabled roles |
| Accent | `accent.primary`, `accent.secondary`, `accent.soft` |
| Status | `success`, `warning`, `error`, `info`, each with foreground/background/border roles |
| Overlay | `overlay.scrim`, `overlay.modal` |
| Focus | `focus.ring` |

Exact color values must be established in the visual-token implementation and checked for contrast. This document intentionally does not invent final hex values.

### 6.3 Typography tokens

Typography tokens must cover:
- Font family: interface, reading, and monospace roles.
- Font size: display, heading, title, body, label, caption, and microcopy.
- Font weight: regular, medium, semibold, bold.
- Line height and letter spacing.
- Text scaling behavior.

Requirements:
- Support Arabic and English, including mixed RTL/LTR content.
- Use fonts actually bundled with the application or available through a verified platform font catalog.
- Never display a font as selectable if it is not available.
- Preserve user text-size preferences and respect platform text scaling.
- Avoid fixed-height text containers that clip enlarged text.

### 6.4 Spacing and sizing tokens

Use a consistent spacing scale and semantic aliases for:
- Inline spacing.
- Control padding.
- Component gaps.
- Card padding.
- Section spacing.
- Screen edge insets.
- Safe-area insets.

The final scale must be documented centrally. Avoid one-off values unless justified by a platform or accessibility requirement.

### 6.5 Shape tokens

Define shared tokens for:
- Control radii.
- Card radii.
- Panel radii.
- Dialog and bottom-sheet radii.
- Pill/circular controls.
- Border widths.

The design should be soft and fluid without making all components excessively rounded.

### 6.6 Elevation and shadow tokens

Mimir exposes **three independently adjustable shadow levels**.

| Level | Intended use |
|---|---|
| Level 1 | Subtle separation for low-elevation surfaces |
| Level 2 | Cards, panels, and standard floating surfaces |
| Level 3 | Dialogs, prominent overlays, and high-elevation elements |

Each level must have independently configurable intensity. Do not derive all three from one global multiplier in a way that prevents independent adjustment.

Shadow tokens should define color, blur, spread, offset, and opacity/intensity as appropriate for the rendering platform. Keep shadows restrained in Light and Dark themes.

### 6.7 Transparency and liquid-material tokens

Keep these concepts distinct:
- **Opacity:** how opaque an element is.
- **Transparency:** how much of the background is visible through it.

Define material tokens for:
- Tint color.
- Tint opacity.
- Backdrop blur.
- Refraction strength, where supported.
- Edge highlight.
- Surface highlight.
- Material border.
- Fallback treatment.

Provide restrained material tiers such as:
- `solid`: opaque, maximum readability.
- `subtle`: slight transparency, low blur.
- `regular`: balanced liquid-glass treatment.
- `prominent`: stronger glass treatment for selected floating surfaces only.

Do not apply prominent refraction to long-form text, dense data tables, code editors, or critical forms by default.

### 6.8 Motion tokens

Define:
- Duration: instant, fast, standard, deliberate.
- Easing: standard, emphasized, decelerate, accelerate.
- Spring: restrained, standard, expressive.
- Reduced-motion alternatives.

Use motion to communicate state and continuity, not decoration.

## 7. User appearance settings

Settings must remain simple, practical, and fast.

Users can:
- Choose one of the four appearance modes.
- Choose an available font.
- Adjust font size.
- Adjust transparency/glass intensity.
- Adjust each of the three shadow levels independently.
- Preview changes immediately.
- Restore one setting to its default.
- Restore all appearance settings to defaults.

Behavior:
- Apply supported visual changes immediately.
- Save automatically.
- Show a clear preview.
- Keep reset controls discoverable.
- Protect readability and control boundaries from unsafe values.
- Defaults must be sensible and usable without customization.

## 8. Liquid visual system and external implementation candidates

Mimir Liquid is a presentation-layer material system. A library such as `quickLiquid` may be evaluated for selected effects including refraction, edge lighting, spring motion, and grouped liquid surfaces.

Integration rules:
- Treat external libraries as implementation candidates, not as the design authority.
- Keep Mimir semantic tokens and component contracts independent of a specific library.
- Place any adapter in the presentation/material layer.
- Provide a fallback for unsupported browsers, devices, and rendering modes.
- Pin and review dependency versions and licenses before production adoption.
- Do not allow a visual-effects dependency into the kernel or execution runtimes.
- Measure frame time, memory, battery impact, and scrolling performance on representative Android devices.

## 9. Responsive and platform behavior

- Mobile-first, with Android as the first validation target.
- Adapt spacing and navigation to tablet and desktop.
- Respect status/navigation bars, keyboard insets, display cutouts, and safe areas.
- Keep the global assistant/composer reachable near the bottom of the mobile screen.
- Ensure RTL and LTR layouts are both supported.
- Theme changes must not reset workspace state or interrupt active work.

## 10. Accessibility requirements

- Meet applicable WCAG contrast requirements for text and interactive controls.
- Do not communicate status by color alone.
- Provide visible keyboard focus.
- Support screen readers and semantic roles.
- Support text scaling.
- Respect reduced motion and reduced transparency.
- Ensure translucent surfaces maintain adequate contrast over variable backgrounds.
- Provide solid-surface fallback where transparency makes content difficult to read.

## 11. Persistence and state

Persist:
- User-selected appearance mode.
- Font selection.
- Font size.
- Transparency preference.
- Three independent shadow intensities.

Do not persist transient animation frames or a stale resolved theme as the authoritative preference. Recompute the resolved theme from the saved preference and current inputs.

If a preference becomes invalid because a font or effect is unavailable, fall back safely and explain only when user action is needed.

## 12. Acceptance criteria

- [ ] Exactly four appearance choices are exposed: Light, Dark, System, Adaptive.
- [ ] Adaptive is the default for a new installation.
- [ ] Existing saved user preferences survive upgrades.
- [ ] Light remains light regardless of time.
- [ ] Dark remains dark regardless of time.
- [ ] System follows the operating system.
- [ ] Adaptive follows the local day/night schedule.
- [ ] Only Adaptive performs the gradual time-based color transition.
- [ ] The transition covers the defined semantic visual tokens.
- [ ] Contrast remains accessible during intermediate transition states.
- [ ] Location is not required to use the app or Adaptive mode.
- [ ] Time-zone, travel, clock, and app-resume changes are handled.
- [ ] Reduced-motion and reduced-transparency preferences are respected.
- [ ] Typography supports Arabic/English and RTL/LTR.
- [ ] Three shadow levels can be adjusted independently.
- [ ] Transparency and opacity are represented distinctly.
- [ ] Unsupported liquid effects degrade gracefully.
- [ ] Theme changes do not interrupt active tasks or reset workspace state.
- [ ] All appearance settings can be previewed and reset.

## 13. Open decisions before implementation freeze

The following require visual validation rather than assumptions:
- Final primitive color values for Pearl Light and Dark.
- Exact typography families and bundled font assets.
- Exact spacing and radius scale.
- Exact default transparency and shadow values.
- Final Adaptive transition duration and easing.
- Solar-time fallback UX when location is unavailable.
- Device/browser support matrix for refraction effects.
- Performance budget for liquid materials.

## 14. Status

This document is a consolidated design contract draft based on the decisions recorded so far. It is not evidence of implementation, device testing, or final numerical-token approval.

Mimir Browser — Complete UX, Screen & Interaction Design Specification

Document Status: Comprehensive Design Draft — Pending Visual Review and Approval
Scope: Browser Engine — Android, Tablet, Desktop
Design Direction: Mimir Liquid Design — Balanced
Theme: Pearl Light + Hybrid Dark/Light
Navigation: Hybrid — Drawer + Contextual Navigation + Global Navigation
Language: Arabic RTL and English LTR
Important: This document defines the intended user experience and interaction behavior. It does not claim that browser automation, remote browsing, third-party integrations, or any other runtime capability has already been implemented or verified.

───

1. Browser Vision

Mimir Browser is an independent browsing environment that combines:

• Professional web browsing.
• Multi-tab and multi-session management.
• Intelligent page understanding.
• AI-assisted browsing and interaction.
• Multi-model collaboration through user-authorized browser tabs.
• Page capture and integration with Mimir Intelligence.
• Download management.
• Privacy, permissions, and security controls.
• Unified activity and execution transparency.

The Browser Engine remains independent from Chat, Forge, Terminal, and Mimir Intelligence.

It consumes shared Mimir services through defined contracts. It does not own global permissions, global search, global activity, or the secure credential store.

1.1 Main design principles

1. Browsing must remain fast and familiar.
2. AI must be helpful without taking control away from the user.
3. The user must always understand what the AI can see and do.
4. Complex features must appear contextually.
5. The default experience must remain simple.
6. Advanced controls must be accessible without overwhelming the main interface.
7. Every significant AI action must retain its provenance.
8. Sensitive actions must require the appropriate authorization.
9. Browser AI must never silently bypass site security, authentication, or user permissions.
10. Android is the primary interaction reference; Tablet and Desktop expand the workspace rather than merely enlarging the phone layout.

───

2. Browser Navigation Architecture

The Browser has its own contextual navigation inside the global Mimir shell.

It must not create a second global navigation system.

2.1 Navigation hierarchy

Global Mimir Navigation

• Home
• Projects
• Tasks
• More

Browser Workspace

• Browser Home
• Current Browsing Workspace
• Tabs & Sessions
• Browser AI
• History
• Bookmarks & Reading List
• Downloads
• Saved Pages & Captures
• Site Information & Controls
• Profiles
• Browser Settings

Contextual panels

• Page Information
• AI Page Understanding
• AI Task Progress
• Tab Details
• Permission Requests
• Site Controls
• Download Details

Global services remain accessible through their shared Mimir locations:

• Global Approval Center
• Unified Search
• Activity & Audit Trail
• Notifications
• Secure Vault
• Mimir Files

2.2 Navigation behavior

• Opening Browser from Mimir enters the last active browsing workspace when appropriate.
• Browser Home remains accessible from the browser navigation.
• Switching tabs preserves each tab's page state where supported.
• Opening a new tab does not destroy the current tab.
• Returning from Browser AI returns to the same page and tab.
• Closing an AI panel must not close the browsing session.
• Back navigation distinguishes between page history, closing a panel, and leaving the Browser workspace.
• Long-running authorized tasks must not be silently discarded when the user changes tabs.
• Browser state and Mimir global navigation state remain separate.

───

3. Complete Screen Inventory

The Browser experience consists of 17 primary screens.

ID| Screen| Main responsibility
B01| Browser Home| Entry point and browsing overview
B02| Browser Workspace| Main browsing interface
B03| Tabs & Windows Manager| Tab and window management
B04| Tab Groups & Sessions| Organizing browsing sessions
B05| Browser AI Home| AI browsing control center
B06| AI Page Assistant| Understanding and interacting with a page
B07| Multi-Model Workspace| Coordinating multiple AI websites
B08| AI Task Monitor| Tracking AI browsing tasks
B09| Permissions & Security Center| Browser-specific permission overview and controls
B10| History| Browsing history
B11| Bookmarks & Reading List| Saved links and reading organization
B12| Downloads| Download management
B13| Saved Pages & Captures| Captured and saved web content
B14| Site Information & Controls| Current website information and controls
B15| Browser Profiles| Separate browsing identities and contexts
B16| Browser Settings| Browser configuration
B17| Browser Command Palette| Contextual quick actions and navigation

The global Activity, Approval, Search, Notification, and Secure Vault screens are not duplicated inside Browser.

───

4. Detailed Screen Designs

B01 — Browser Home

Purpose

The starting point for browsing, returning to recent work, and launching a new browsing task.

Main layout

Top section

• Mimir Browser title.
• Current profile indicator.
• Notifications shortcut.
• Browser settings shortcut.

Main address/search field

• Enter a URL.
• Search the web.
• Paste a copied address.
• Open a new browsing task.
• Show a clear distinction between a URL and a search query.

Quick access

• User-pinned websites.
• Frequently visited websites, if enabled.
• Recent websites.

Continue browsing

• Recently active tabs.
• Recently used sessions.
• Previously opened browsing workspaces.

AI entry point

• Ask AI about a topic.
• Research a subject.
• Summarize a website.
• Compare information from multiple pages.

Recent activity

• Recently saved pages.
• Recent downloads.
• Recently captured content.

Interactions

• Selecting a website opens it in the current or a new tab according to the user's preference.
• Selecting a recent session restores the available session state.
• Starting an AI research task opens Browser AI with the relevant context.
• Long-pressing a shortcut opens contextual actions.
• Search results are not presented as verified facts merely because an AI generated them.

States

• First-time browser.
• Empty history.
• No pinned websites.
• Restored browsing session.
• Offline or unavailable network.
• Browser profile unavailable.

───

B02 — Browser Workspace

This is the most important Browser screen.

It is the actual browsing environment.

Desktop layout

Global Mimir shell

• Global navigation.
• Browser workspace header.
• Shared Mimir services.

Browser tab strip

• Open tabs.
• Active tab.
• New tab.
• Tab group indicators.
• Tab overflow menu.

Address and navigation bar

• Back.
• Forward.
• Reload or stop.
• Address/search field.
• Site information.
• Bookmark.
• Downloads shortcut.
• Browser AI shortcut.
• More actions.

Main page area

• The actual website content.
• Page scrolling.
• Links and interactive elements.
• Selection and copy behavior.
• Contextual page actions.

Optional AI side panel

• Page summary.
• Ask about this page.
• Find information.
• Explain selected content.
• Compare pages.
• Prepare an action.
• Review an action before execution.

The AI panel must be collapsible.

The website must retain the majority of the available viewing area when the AI panel is closed.

Android layout

The mobile workspace prioritizes the web page itself.

Top browser header

• Current tab title.
• Address/search field.
• Tab counter.
• Profile indicator where appropriate.

Page area

• Full-width browsing content.
• Native page scrolling.
• Selection and contextual actions.

Browser action controls

• Back and forward.
• Reload.
• New tab.
• Tab overview.
• More menu.

AI interaction

• A contextual AI entry point.
• The shared Mimir Composer remains the only general-purpose composer.
• Browser AI can open as a bottom sheet or a dedicated panel.
• The Composer adapts to the available space and keyboard state.

The global Mimir bottom navigation remains distinct from browser-specific navigation.

Tablet layout

• Expanded browser toolbar.
• More visible tab information.
• Optional split view.
• Web page and AI panel can appear side by side.
• Tab groups can be easier to inspect.
• Browser navigation remains contextual.

Important interactions

• Long-pressing a link opens the link context menu.
• Selecting text offers copy, search, and AI explanation.
• A page can be sent to Mimir Brain or saved to Mimir Files through the relevant permission and integration flows.
• Opening an external AI website does not automatically grant Browser AI permission to read or control it.
• Closing a tab does not automatically delete its history or saved captures.

───

B03 — Tabs & Windows Manager

Purpose

Manage open tabs without cluttering the main browsing interface.

Layout

Header

• Tabs & Windows.
• Current profile.
• Search tabs.
• New tab.

Tab display

• Tab title.
• Website identity.
• Current loading state.
• Active or inactive status.
• Group membership.
• Optional AI task indicator.

Available views

• Grid view.
• Compact list view.

Tab actions

• Activate.
• Close.
• Duplicate.
• Pin.
• Move to group.
• Save for later.
• Copy address.
• Reopen recently closed tab where available.

Android

• Card-based tab overview.
• Compact cards.
• Swipe gestures may be supported.
• Clear close controls.
• New tab action remains accessible.

Tablet

• Larger tab cards.
• More visible metadata.
• Optional side-by-side tab preview.

Desktop

• Horizontal tab strip.
• Dedicated tab overview.
• Drag-and-drop organization where supported.
• Keyboard shortcuts.

Empty state

An empty state should offer:

• Open a new tab.
• Restore a recent session if available.
• Go to Browser Home.

───

B04 — Tab Groups & Sessions

Purpose

Organize browsing work into meaningful contexts.

Examples:

• Research.
• Development.
• Documentation.
• AI Collaboration.
• Personal Browsing.

Layout

Groups

• Group name.
• Color or visual identifier.
• Number of tabs.
• Last active time.
• Active status.

Session information

• Session name.
• Tabs associated with the session.
• Profile association.
• Restoration status.

Interactions

• Create group.
• Rename group.
• Add or remove tabs.
• Collapse or expand group.
• Save a browsing session.
• Restore a saved session.
• Close a group.
• Move a tab between groups.

Important distinction

A tab group is an organizational collection.

A session is a restorable browsing context.

They are related but not interchangeable.

Restoration depends on what the browser engine can actually preserve.

───

B05 — Browser AI Home

Purpose

Provide a dedicated control center for AI-assisted browsing.

Main areas

AI Overview

• Current AI permission level.
• Active browsing tasks.
• Connected page context.
• Recent AI activities.

Quick tasks

• Summarize a page.
• Explain selected content.
• Extract structured information.
• Compare several pages.
• Research a subject.
• Prepare a form.
• Review a page for relevant information.

AI permissions

• Observe.
• Assist.
• Autonomous.
• Full Control.

Model and execution information

• Active model.
• Provider.
• Local, Cloud, or Hybrid execution.
• Current task role.
• Status.
• Available contribution details.

Permission levels

Observe

The AI can inspect the authorized page context.

It cannot click, type, submit, or perform actions.

Assist

The AI can analyze the page and prepare suggested actions.

The user performs the actions or explicitly authorizes a specific interaction.

Autonomous

The AI can execute an approved task within its authorized scope.

Sensitive actions remain subject to approval.

Full Control

A higher-privilege mode that requires explicit user authorization and clear scope.

It must never be enabled silently.

The user must be able to revoke access immediately.

───

B06 — AI Page Assistant

Purpose

Provide contextual AI assistance for the current website.

Layout

Header

• Current page identity.
• AI permission indicator.
• Close panel.
• Task status.

Context area

• Current page.
• Selected text.
• Relevant visible content.
• Available page structure.
• Referenced sources.

Assistant conversation

• Ask a question.
• Explain selected content.
• Summarize the page.
• Find a specific item.
• Compare content.
• Prepare a page interaction.

Action preview

• What the AI intends to do.
• Which page elements are involved.
• What information will be entered.
• Whether the action is reversible.
• Whether user approval is required.

Interaction model

1. User asks for an action.
2. AI interprets the request.
3. AI inspects the authorized page context.
4. AI prepares a plan.
5. AI shows the relevant action preview when needed.
6. User approves sensitive or consequential actions.
7. AI executes only within the granted scope.
8. Result is reported with provenance and status.

Supported interaction categories

Subject to actual engine and site capabilities:

• Read visible page content.
• Inspect page structure.
• Locate elements.
• Click.
• Type.
• Fill forms.
• Select controls.
• Copy information.
• Navigate.
• Scroll.
• Extract structured information.

The UI must not advertise an operation as available when the underlying engine cannot perform it.

───

B07 — Multi-Model Workspace

Purpose

Coordinate work across multiple AI websites opened in separate browser tabs.

This is a central capability of the Mimir Browser vision.

Example workspace

• ChatGPT tab.
• Claude tab.
• Grok tab.
• Other user-authorized AI websites.

The user logs into each website using their own account.

Layout

Model tabs

• Website identity.
• Tab status.
• Current conversation.
• Permission status.

Task panel

• Shared objective.
• Assigned model.
• Task status.
• Dependencies.
• Expected output.

Coordination area

• Shared task description.
• Model contributions.
• Comparison.
• Conflicts or disagreements.
• Consolidated result.

Example workflow

1. The user creates a research task.
2. Mimir prepares the task instructions.
3. The user chooses which AI websites participate.
4. Each model receives its assigned task through the authorized browser interaction.
5. Mimir tracks the responses.
6. The outputs are collected with their source and model attribution.
7. Mimir compares the results.
8. The user reviews the combined result.

Essential controls

• Add participating tab.
• Remove participant.
• Assign task.
• Pause coordination.
• Resume where supported.
• Stop all active coordination.
• Review each model's contribution.
• Compare outputs.
• Approve transfer of information between tabs.

Privacy boundary

A conversation in one provider's tab must not be transferred to another provider without the applicable authorization.

Mimir must not assume that access to one tab grants access to every other tab.

Website restrictions, login state, anti-automation controls, and other limitations must be respected.

───

B08 — AI Task Monitor

Purpose

Show the progress of AI-assisted browsing tasks.

Task list

Each task displays:

• Task name.
• Associated page or tab.
• Assigned model.
• Permission level.
• Current status.
• Start time.
• Last activity.
• Completion or failure state.

Task states

• Draft.
• Awaiting Authorization.
• Ready.
• Running.
• Waiting for User.
• Paused.
• Completed.
• Failed.
• Cancelled.

These states belong to the task itself and must not be confused with tab loading or browser process states.

Task details

• Original user request.
• Execution plan.
• Completed steps.
• Pending steps.
• Actions performed.
• Approval events.
• Model and provider attribution.
• Output.
• Error details.

Controls

• Pause.
• Resume where supported.
• Cancel.
• Review pending action.
• Open associated tab.
• View provenance.

A completed task must not be shown as successful if the final result was not verified.

───

B09 — Permissions & Security Center

Purpose

Provide visibility and control over Browser-specific permissions.

Global authorization remains owned by Mimir's shared Permissions & Authorization service.

Main sections

AI Permissions

• Current permission level.
• Scope.
• Authorized tabs.
• Authorized sites.
• Expiration or revocation state where applicable.

Site Permissions

• Page access.
• Notifications.
• Camera and microphone.
• Location.
• Clipboard.
• Downloads.
• Other browser-supported permissions.

AI interaction permissions

• Observe.
• Assist.
• Autonomous.
• Full Control.

Security

• Suspicious page warnings.
• Untrusted content indicators.
• Prompt injection warnings.
• Sensitive action approval.
• Emergency stop.

Kill Switch

A prominent Stop All AI Activity action must be available.

It should:

• Stop active AI-controlled browsing actions where technically possible.
• Revoke active interaction authorization.
• Preserve the page and browsing session where possible.
• Report any action that could not be stopped immediately.

Prompt Injection Protection

Page content is untrusted input.

Instructions found inside websites must not automatically become user instructions.

Browser AI must distinguish:

• User instructions.
• System and platform policies.
• Website content.
• Third-party prompts.
• Tool results.

A website must not be allowed to silently expand AI permissions.

───

B10 — History

Purpose

Search and review previously visited pages.

Layout

• Search history.
• Date grouping.
• Website identity.
• Page title.
• Visit time.
• Associated tab or session when available.

Actions

• Open page.
• Open in new tab.
• Copy address.
• Remove an entry.
• Clear selected history.
• Filter by date.
• Filter by website.

Privacy

History visibility must respect the active profile and browsing mode.

Private browsing must not be represented as ordinary persistent history.

Deleting a history record does not automatically delete independent saved captures or files.

───

B11 — Bookmarks & Reading List

Purpose

Save and organize pages for future access.

Sections

• Bookmarks.
• Folders.
• Reading List.
• Pinned Pages.
• Recently Added.

Bookmark details

• Page title.
• Address.
• Website identity.
• Folder.
• Added date.
• Optional user note.

Actions

• Add bookmark.
• Edit.
• Move.
• Organize.
• Search.
• Open.
• Copy.
• Remove.

Reading List

A reading-list entry is not the same as an archived page.

The UI must distinguish a saved link from a captured copy of page content.

───

B12 — Downloads

Purpose

Provide a unified view of browser downloads.

Main layout

• Active downloads.
• Completed downloads.
• Failed downloads.
• Paused downloads, where supported.

Download item

• File name.
• Source website.
• File type.
• File size where available.
• Progress where available.
• Status.
• Destination.
• Date.

Actions

• Open.
• Reveal in Mimir Files.
• Copy source address.
• Retry where supported.
• Cancel where supported.
• Remove from list.
• Inspect file information.

Important distinction

Removing an item from the download list is not necessarily the same as deleting the underlying file.

The UI must communicate which action is being performed.

───

B13 — Saved Pages & Captures

Purpose

Organize content captured from websites.

This screen connects Browser with Mimir Intelligence without merging their ownership responsibilities.

Sections

• Saved pages.
• Page snapshots.
• Selected text captures.
• Research collections.
• Captured references.

Capture details

• Original page.
• Capture time.
• Capture type.
• Source URL.
• Available content.
• Extraction status.
• Associated Mimir Brain reference.

Actions

• Open original page.
• View capture.
• Save to Mimir Files.
• Send knowledge to Mimir Brain.
• Copy source reference.
• Remove capture.

Ownership

Browser manages the browsing capture workflow.

Mimir Files owns file-management responsibilities.

Mimir Brain owns organized knowledge and its references.

A reference between these domains does not automatically grant access to the underlying content.

───

B14 — Site Information & Controls

Purpose

Show information and controls for the current website.

Sections

Website identity

• Domain.
• Security connection information.
• Page title.
• Current address.

Permissions

• Site access.
• Notifications.
• Clipboard.
• Camera and microphone.
• Other supported site permissions.

AI access

• Whether AI may observe the page.
• Whether interaction is authorized.
• Current scope.
• Revoke access.

Page actions

• Bookmark.
• Copy URL.
• Share.
• Save page.
• Inspect page information.

The interface must avoid implying that a secure connection means the website itself is trustworthy.

───

B15 — Browser Profiles

Purpose

Separate browsing contexts.

Profile card

• Profile name.
• Visual identity.
• Active status.
• Associated browsing context.
• Relevant privacy settings.

Actions

• Create profile.
• Switch profile.
• Edit profile.
• Close profile.
• Review profile data.

Isolation requirements

Profiles must not silently share:

• Browsing history.
• Cookies or authentication state.
• Session information.
• Site permissions.
• AI browsing authorization.

Any supported sharing must be explicit.

Credentials remain managed through Mimir Secure Vault or the appropriate browser integration.

───

B16 — Browser Settings

Main sections

General

• Startup behavior.
• New tab behavior.
• Default search provider where supported.
• Address bar behavior.

Appearance

• Theme.
• Toolbar layout.
• Tab presentation.
• Page zoom.
• Compact or expanded controls.

Privacy

• History behavior.
• Site data.
• Tracking and privacy controls where supported.
• Private browsing behavior.

Downloads

• Default download location where supported.
• Download confirmation preferences.

AI

• Default permission level.
• AI panel behavior.
• Approved site scope.
• Multi-model coordination preferences.
• Confirmation behavior.

Accessibility

• Text scaling.
• Keyboard navigation.
• Screen reader compatibility.
• RTL and LTR support.

Advanced

• Browser engine diagnostics.
• Supported experimental capabilities.
• Reset controls.

Settings must distinguish user preferences from capabilities that depend on the underlying browser engine.

───

B17 — Browser Command Palette

Purpose

Provide fast access to Browser actions.

It must not replace ordinary navigation.

Categories

• Navigation.
• Tabs.
• Sessions.
• Bookmarks.
• History.
• Downloads.
• Browser AI.
• Permissions.
• Settings.

Examples

• New Tab.
• Switch Tab.
• Close Current Tab.
• Restore Recent Session.
• Open Downloads.
• Ask AI About This Page.
• Summarize Current Page.
• Stop AI Activity.
• Open Browser Settings.

Interaction

• Search commands.
• Keyboard shortcuts on Desktop.
• Contextual suggestions.
• Recent actions.
• Permission-aware action availability.

Unavailable actions must appear disabled or clearly unavailable, not as if they were executable.

───

5. Main User Journeys

Journey 1 — Ordinary browsing

Browser Home → Address/Search → Browser Workspace → Open links → New Tab → Return to previous tab.

The browsing experience must remain familiar and fast.

Journey 2 — Ask AI about a page

Browser Workspace → AI Page Assistant → Authorize page observation → Ask question → Review answer and source → Continue browsing.

Journey 3 — AI fills a form

Browser Workspace → AI Page Assistant → Identify form → Prepare values → Show action preview → Request required authorization → Fill approved fields → Present result.

Submitting a consequential form requires explicit confirmation.

Journey 4 — Multi-model research

Browser Home → Multi-Model Workspace → Select authorized AI tabs → Define task → Assign contributions → Review each response → Compare → Consolidate → Save result.

Journey 5 — Save research

Browser Workspace → Capture Page → Review captured content → Choose destination → Mimir Brain or Mimir Files → Confirm → Preserve source reference.

Journey 6 — Download a file

Browser Workspace → Download → Downloads → Inspect file → Open or reveal in Mimir Files.

Journey 7 — Revoke AI access

Browser AI → Permissions & Security → Select active authorization → Revoke or Stop All AI Activity → Show resulting status.

Journey 8 — Restore browsing work

Browser Home → Recent Sessions → Select session → Review available tabs → Restore → Continue.

───

6. Responsive Design System

Android

Primary priorities

• One-handed use.
• Clear address/search field.
• Page content receives maximum space.
• Simple tab overview.
• Contextual AI bottom sheet.
• Shared Mimir Composer.
• Global navigation remains accessible.
• Essential actions are reachable without excessive menus.

Navigation

• Browser controls are contextual to the current browsing workspace.
• Global Mimir navigation remains separate.
• Browser AI is opened from the current page or Browser AI Home.

Interaction

• Touch-first.
• Long-press context menus.
• Clear confirmation surfaces.
• Bottom sheets for secondary controls.
• Avoid dense toolbars.

Tablet

Primary priorities

• More visible tab information.
• Split browsing and AI panels.
• Expanded tab manager.
• Better multi-page comparison.
• More visible contextual controls.

Interaction

• Touch and pointer.
• Resizable panels where supported.
• Side-by-side content.
• Optional keyboard shortcuts.

Desktop

Primary priorities

• Full tab strip.
• Expanded address and navigation controls.
• Optional AI side panel.
• Multi-tab workflows.
• Multi-model coordination.
• Keyboard shortcuts.
• Drag-and-drop organization where supported.

Interaction

• Mouse and keyboard.
• Context menus.
• Multiple windows where supported.
• Split view.
• Efficient task switching.

───

7. Shared Mimir Component Integration

Browser must reuse the global design system and shared services.

Shared visual components

• Global application shell.
• Global navigation.
• Contextual navigation.
• Shared Composer.
• Buttons and icon buttons.
• Cards and panels.
• Menus and popovers.
• Dialogs and bottom sheets.
• Tabs.
• Search fields.
• Status indicators.
• Progress indicators.
• Notifications.
• Empty states.
• Error states.
• Loading states.
• Permission prompts.
• Provenance indicators.

Shared services

Service| Browser usage
Unified Search| Search and cross-space discovery
Permissions & Authorization| Site and AI authorization
Provenance & Source References| AI and page source attribution
Sync & Conflict Resolution| Browser preferences and supported session data
Activity & Audit Trail| Browsing-related authorized actions
Notifications| Task and download notifications
Import & Export| Supported browsing data exchange
Encryption & Key Management| Protected browser-related data
Retention & Deletion Policies| History, captures, and related records

Browser must not create duplicate global implementations of these services.

───

8. Model Provenance & Execution Transparency

Every model-produced result must preserve available information:

• Model identity.
• Provider.
• Model version where available.
• Local, Cloud, or Hybrid execution.
• Task role.
• Contribution.
• Start and completion timestamps where available.
• Status.
• Sources and page references.
• Participating models.
• Handoffs between models.

The default UI should show a compact attribution indicator.

Detailed provenance must be available on demand.

The metadata must survive supported handoffs, synchronization, storage, restore, export, and copy operations.

───

9. Security & Trust Requirements

1. Browser AI must operate only within user-authorized scope.
2. Website content is untrusted input.
3. A page cannot grant itself new AI permissions.
4. Sensitive information must not be shared between AI providers without authorization.
5. Passwords and secrets must not be exposed to general AI context by default.
6. Autofill must use the minimum required information.
7. Sensitive actions must require appropriate confirmation.
8. The user must be able to stop AI activity.
9. Permission changes must be auditable.
10. Unsupported interactions must fail honestly.
11. CAPTCHA, authentication, DRM, site restrictions, and access controls must not be bypassed.
12. Browser must clearly distinguish an AI suggestion from an action that was actually executed.
13. Browser history deletion must not silently delete unrelated captures or files.
14. Closing a tab must not silently revoke or destroy unrelated user data.

───

10. UI States & Error Handling

Every primary screen must account for:

• Initial loading.
• Normal content.
• Empty state.
• Offline state.
• Network failure.
• Permission denied.
• Authorization required.
• Site unavailable.
• Unsupported operation.
• AI task paused.
• AI task failed.
• AI task completed.
• Partial result.
• Session restoration failure.
• Download failure.
• Security warning.

Error messages must explain:

• What happened.
• Which operation was affected.
• Whether the page or session remains usable.
• What the user can do next.

Never show a success state for an operation that did not succeed.

───

11. Acceptance Criteria

The Browser design is ready for visual review only when:

• All 17 screens have distinct and appropriate layouts.
• The Browser Workspace is clearly recognizable as a browser.
• Browser AI is contextual rather than replacing the web page.
• Tab and session management are understandable.
• Multi-model coordination has a clear task and contribution model.
• Permissions are visible and understandable.
• Sensitive actions have explicit approval flows.
• Android, Tablet, and Desktop have intentionally designed layouts.
• Shared Mimir components are reused.
• Global services are not duplicated.
• Provenance is available for model-produced outputs.
• Empty, loading, failure, and restricted states are specified.
• Navigation between all primary screens and contextual panels is defined.
• The design does not present unverified runtime capabilities as working features.

───

12. Design Delivery Sequence

The Browser visual design should be delivered in this order:

1. Browser Home.
2. Browser Workspace.
3. Tabs & Windows Manager.
4. Tab Groups & Sessions.
5. Browser AI Home.
6. AI Page Assistant.
7. Multi-Model Workspace.
8. AI Task Monitor.
9. Permissions & Security Center.
10. History.
11. Bookmarks & Reading List.
12. Downloads.
13. Saved Pages & Captures.
14. Site Information & Controls.
15. Browser Profiles.
16. Browser Settings.
17. Browser Command Palette.

Each screen must be designed for Android, Tablet, and Desktop.

The visual review must inspect the actual screen layout, component hierarchy, navigation, and interaction states—not merely the page title or a generic card arrangement.

Final status: Complete Browser UX and interaction specification draft. Visual mockups and runtime implementation remain separate deliverables and are not claimed as completed by this document.

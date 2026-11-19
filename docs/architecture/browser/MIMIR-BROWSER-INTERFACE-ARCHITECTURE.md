Mimir Browser — Detailed Interface Architecture & Interaction Specification

Part 2 — Screen Composition, Navigation, Components & Responsive Behavior

Status: Design Specification Draft — Companion to Browser UX Specification
Applies to: Android · Tablet · Desktop
Design System: Mimir Liquid Design — Balanced
Direction: RTL / LTR
Principle: Simple by default, powerful when needed.

───

1. The Browser Visual Structure

Browser must have three distinct interface layers.

Layer A — Global Mimir Shell

This layer belongs to Mimir, not Browser.

It contains:

• Global navigation.
• Global Composer.
• Global notifications.
• Global search.
• Shared activity and approval access.
• Global settings access.

Browser cannot duplicate these elements.

Layer B — Browser Workspace

This layer belongs to Browser.

It contains:

• Browser header.
• Address and search field.
• Tabs.
• Navigation controls.
• Current website viewport.
• Browser-specific menus.
• Contextual Browser tools.

Layer C — Contextual Intelligence

This layer appears when needed.

It contains:

• Browser AI.
• Page analysis.
• Selected-text analysis.
• Multi-model coordination.
• Task progress.
• Permission requests.
• Action previews.

The contextual layer must not permanently occupy the website's viewing area.

2. Global Visual Hierarchy

2.1 Visual priority

The priority order is:

1. Current website content.
2. Address and navigation controls.
3. Tab management.
4. Contextual AI access.
5. Secondary browser controls.
6. Advanced settings and diagnostics.

This order prevents the browser from feeling like an administration dashboard.

2.2 Visual language

Use:

• Mimir's established color tokens.
• Pearl Light and Hybrid Dark themes.
• Balanced glass surfaces.
• Controlled transparency.
• Independent shadow levels.
• Rounded but restrained surfaces.
• Clear typography hierarchy.
• Consistent iconography.
• Subtle motion and transitions.

Avoid:

• Excessive floating cards.
• Unnecessary gradients.
• Too many permanently visible buttons.
• Excessive borders.
• Repeated headings.
• Dense metadata on mobile.
• Multiple competing AI entry points.

The original Mimir logo must be inserted when supplied. Until then, use the Mimir wordmark as a temporary placeholder.

3. Android — Complete Layout Architecture

Android is the primary design reference.

3.1 Browser Home

Top

• Browser title.
• Profile selector.
• More menu.

Main

• Prominent address/search field.
• Quick-access websites.
• Recent browsing sessions.
• Continue browsing.
• AI research shortcut.

Bottom

• Shared Mimir Composer when relevant to the current context.
• Global Mimir bottom navigation.

The address field must be the most visually prominent browser control.

Quick-access websites must not compete with it.

3.2 Browser Workspace

The layout is divided into four areas.

Area 1 — Browser header

• Current page title or address.
• Tab count.
• Profile indicator.

Area 2 — Website viewport

• Actual website.
• Full available width.
• Normal page scrolling.
• Website interaction.

Area 3 — Browser toolbar

• Back.
• Forward.
• New tab or tab overview.
• Reload/stop.
• More.

Area 4 — Mimir contextual Composer

• Available when Browser AI is active or when the user starts a contextual task.
• Adapts to the keyboard.
• Never duplicates the global Composer.

The website receives maximum available space.

3.3 Browser AI bottom sheet

When opened:

Collapsed

• Small AI handle.
• Current AI permission indicator.
• Short prompt.

Expanded

• Conversation.
• Page context.
• Suggested actions.
• Current task state.
• Action preview when applicable.

Full screen

• Detailed task planning.
• Multi-step review.
• Long AI responses.
• Multi-model coordination.

The user can collapse the sheet and return to the exact browsing position.

3.4 Tabs Manager

Use a grid of tab cards.

Each card contains:

• Website icon or favicon.
• Page title.
• Short domain.
• Preview where available.
• Close control.
• Group indicator.

The active tab receives a subtle distinction, not an oversized visual treatment.

3.5 Menus and secondary panels

Use bottom sheets for:

• Tab actions.
• Page actions.
• Site information.
• Bookmark actions.
• Download actions.
• Contextual permissions.

Use full-screen pages for:

• History.
• Bookmarks.
• Downloads.
• Browser Settings.
• Profiles.

4. Tablet — Complete Layout Architecture

Tablet should feel like an expanded professional browser.

4.1 Browser Workspace

Top row

• Browser title and profile.
• Window controls where supported.

Tab row

• Horizontal tabs.
• New tab.
• Tab group access.
• Tab overflow.

Address row

• Back and forward.
• Reload.
• Address/search.
• Site information.
• Bookmark.
• Downloads.
• Browser AI.
• More.

Main area

• Website viewport.
• Optional contextual AI panel.

4.2 Split view

The user can choose:

• Website only.
• Website + AI panel.
• Two authorized pages side by side.
• Website + task monitor.

Split view must be optional.

The default is a single website viewport.

4.3 Tab overview

Use a flexible grid with larger previews.

Support:

• Drag and drop where supported.
• Group organization.
• Multi-selection.
• Tab details.

4.4 Multi-Model Workspace

Use a three-part layout:

1. Participating AI tabs.
2. Shared task and instructions.
3. Contributions and comparison.

Each section must have a clear visual identity.

The comparison area must distinguish the original response from Mimir's consolidated output.

5. Desktop — Complete Layout Architecture

Desktop is the advanced browsing workspace.

5.1 Browser Workspace

Global Mimir shell

• Compact global navigation.
• Browser workspace.

Browser tab strip

• Multiple tabs.
• Pinned tabs.
• Tab groups.
• New tab.

Browser toolbar

• Navigation.
• Address/search.
• Site information.
• Bookmark.
• Downloads.
• Browser AI.
• More.

Main content

• Website viewport.

Optional side panel

• Browser AI.
• Page outline.
• Page information.
• Task monitor.

The side panel must be resizable where supported.

5.2 Desktop panel behavior

The user can:

• Open the AI panel.
• Close the AI panel.
• Change panel width.
• Switch between AI and page information.
• Keep the panel visible while changing tabs, subject to context and privacy rules.

A panel must not unexpectedly retain sensitive content from a previous website after the active context changes.

5.3 Multi-window behavior

Where the underlying browser engine supports multiple windows:

• Create window.
• Move tabs between windows.
• Restore windows.
• Manage window sessions.

Unsupported operations must not be displayed as available.

6. Complete Component Inventory

6.1 Browser Foundation Components

Component| Responsibility
BrowserHeader| Browser identity and profile
AddressBar| URL and search input
NavigationControls| Back, forward, reload
TabStrip| Open tabs
TabItem| Individual tab
TabOverview| Visual tab management
TabGroup| Tab organization
WebsiteViewport| Actual page display
BrowserToolbar| Browser-specific actions
BrowserMenu| Secondary actions
SiteIdentity| Domain and connection details

6.2 AI Components

Component| Responsibility
BrowserAIEntry| Open AI assistance
BrowserAIPanel| Contextual AI surface
AIConversation| Page-related conversation
PageContext| Authorized page information
SelectionContext| Selected text and related context
ActionPreview| Proposed page actions
ApprovalRequest| Authorization request
TaskProgress| AI task state
ModelAttribution| Model and provider identity
ContributionCard| Individual model output
MultiModelCoordinator| Cross-tab coordination

6.3 Management Components

Component| Responsibility
HistoryEntry| Visited page record
BookmarkItem| Saved link
ReadingListItem| Reading entry
DownloadItem| Download record
CaptureItem| Saved page or content
ProfileCard| Browsing profile
PermissionRow| Site or AI permission
SecurityAlert| Security warning
EmptyState| No available content
ErrorState| Failed operation
LoadingState| Pending operation

7. Navigation Map

The Browser navigation hierarchy is:

Browser Home

• Browser Workspace
◦ Current Page
◦ Site Information
◦ Browser AI
▪ AI Page Assistant
▪ Action Preview
▪ AI Task Monitor
◦ Tabs Manager
▪ Tab Details
▪ Tab Groups
▪ Sessions
◦ Multi-Model Workspace
▪ Participants
▪ Task Instructions
▪ Contributions
▪ Comparison
◦ History
◦ Bookmarks & Reading List
◦ Downloads
◦ Saved Pages & Captures
◦ Profiles
◦ Browser Settings
◦ Command Palette
◦ Permissions & Security

Contextual panels are not always full navigation destinations.

For example:

• Site Information opens as a contextual panel.
• AI Page Assistant can open as a bottom sheet on Android.
• Action Preview opens as a confirmation surface.
• Tab Details can appear as a contextual sheet or full detail screen depending on device.

8. Interaction Matrix

User action| Result| Required behavior
Tap address field| Enter URL/search| Keyboard and suggestions
Tap tab counter| Open Tabs Manager| Preserve active tab
Tap tab| Activate tab| Restore page state where supported
Long-press tab| Tab menu| Show relevant actions
Tap Browser AI| Open AI assistant| Show current permission
Select text| Selection menu| Copy, search, explain
Tap site identity| Site information| Show security and permissions
Tap download| Download details| Show actual status
Tap bookmark| Save page| Confirm saved state
Tap AI action| Prepare operation| Show preview when needed
Approve action| Execute authorized operation| Record result
Stop AI| Stop active AI activity| Report stop status
Change profile| Switch browsing context| Preserve isolation

9. Action and Approval Architecture

Every AI action follows this sequence:

Request → Interpretation → Authorization Check → Plan → Preview → Approval if required → Execution → Verification → Result

The UI must distinguish these states:

• Suggested.
• Prepared.
• Awaiting approval.
• Authorized.
• Executing.
• Completed.
• Partially completed.
• Failed.
• Cancelled.

A prepared action is not an executed action.

An approved action is not necessarily a successful action.

A successful action must have a verified result where verification is possible.

10. Contextual Menus

Link menu

• Open.
• Open in new tab.
• Copy link.
• Bookmark.
• Share.
• Ask AI about this link.

Selected text menu

• Copy.
• Search.
• Translate where supported.
• Explain with AI.
• Save selection.
• Add to research.

Tab menu

• Activate.
• Duplicate.
• Pin.
• Move to group.
• Copy address.
• Close.
• Close other tabs where appropriate.

Page menu

• Reload.
• Bookmark.
• Share.
• Save page.
• Page information.
• Ask AI.
• Report issue.

AI action menu

• Review plan.
• Edit instructions.
• Approve.
• Reject.
• Pause.
• Cancel.
• View execution details.

11. State Preservation

Browser must preserve the following where technically supported:

• Active tab.
• Open tabs.
• Tab groups.
• Scroll position.
• Page navigation state.
• Browser profile.
• Relevant session state.
• AI task references.
• Pending drafts.

State restoration must never expose one profile's private content to another profile.

If restoration is incomplete, Browser must report what was restored and what was not.

12. Accessibility and Internationalization

RTL

• Arabic layouts use right-to-left alignment.
• Browser navigation controls remain visually consistent.
• URL fields retain appropriate LTR text handling.
• Mixed Arabic and English content must not break layout.
• Icons must not reverse when direction changes unless their meaning is directional.

LTR

• English layouts use left-to-right alignment.
• Browser controls retain consistent hierarchy.

Accessibility

• Clear focus indicators.
• Screen reader labels.
• Keyboard navigation.
• Adequate contrast.
• Scalable text.
• Reduced motion option.
• Touch targets appropriate for mobile.
• Meaningful status labels rather than color alone.

13. Responsive Acceptance Checklist

Android

• Address field is easy to reach.
• Website receives maximum viewing space.
• Tab overview is understandable.
• AI panel does not obscure essential page content.
• Menus are reachable one-handed.
• Composer and keyboard do not overlap.
• Back behavior is predictable.

Tablet

• Tabs remain readable.
• Split view is optional.
• AI panel has useful space.
• Website retains adequate width.
• Multi-model workspace is understandable.

Desktop

• Tab strip is efficient.
• Address bar is clear.
• Side panel is optional.
• Keyboard shortcuts are discoverable.
• Multi-tab workflows are manageable.
• Advanced controls do not overwhelm ordinary browsing.

14. Visual Review Requirements

The visual review must evaluate:

1. Browser Home hierarchy.
2. Address field prominence.
3. Website viewport size.
4. Tab strip density.
5. AI panel behavior.
6. Permission clarity.
7. Multi-model workspace readability.
8. Navigation consistency.
9. Theme consistency.
10. RTL/LTR quality.
11. Responsive behavior.
12. Accessibility.

A screen cannot be approved merely because all its components are present.

The composition must be visually understandable.

15. Final Design Rule

Mimir Browser must feel like a professional browser first, an intelligent browsing assistant second, and a multi-model coordination environment when the user needs it.

These are complementary responsibilities, not competing interfaces.

The user should be able to browse normally without being forced to interact with AI features.

When AI is needed, it should become available in the context of the current page, tab, task, or selected content.

Part 2 status: Interface architecture, component inventory, navigation, interaction matrix, state preservation, and responsive behavior specified. Visual mockups and implementation verification remain outstanding.

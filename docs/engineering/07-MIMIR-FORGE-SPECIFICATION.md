---
document_id: MIMIR-ENG-FORGE-001
title: Mimir Forge Engineering Workspace Specification
document_type: Engine Product and UX Specification
status: Approved product direction — implementation and verification pending
language: en
scope: Forge lifecycle, eleven pages, AI assistance, environments, execution, review, release
review_date: 2026-11-09
---

# Mimir Forge Engineering Workspace Specification

## 1. Purpose

Forge is Mimir's AI-assisted software engineering workspace. It enables users with limited or no programming experience to describe an outcome in natural language and receive planning, implementation assistance, explanations, verification, and controlled delivery.

Forge is not merely a code editor or chat window. It manages a software project's engineering lifecycle.

## 2. Product principles

- AI is the central assistant across all Forge pages, not a separate chat-only feature.
- Natural-language requests are first-class.
- Explain technical concepts in simple language, with technical detail available on demand.
- The user remains the authority for scope, permissions, approvals, and consequential changes.
- No success claim without evidence.
- Technology-neutral: Forge is not limited to Flutter or any one language/framework.
- Mobile-first: phone experience prioritizes AI assistance, project visibility, task control, and review; Desktop provides a full engineering workspace.
- Progressive disclosure: simple user view, contextual engineering view, advanced developer details.
- Preserve project history, provenance, and recoverability.
- Forge and Terminal are independent workspaces with different responsibilities.

## 3. Forge versus Terminal

**Forge** manages software engineering: project understanding, planning, code changes, review, build/test orchestration, artifacts, and releases.

**Terminal** provides direct command-line interaction and process execution.

Rules:
- Forge may request command execution through a generic execution/tool contract.
- Terminal is not embedded as a Forge sub-engine and does not become Forge.
- Forge must not duplicate Terminal's command-line interface.
- Terminal must have its own independent specification.
- Both may use shared permission, provenance, connectivity, and execution contracts.
- Code zoom/readability requirements apply independently to Forge Code Workspace and Terminal.

## 4. AI engineering roles

Functional roles may be performed by one model or multiple models:
- Architect AI: architecture and design.
- Developer AI: code generation and modification.
- Reviewer AI: independent change review.
- Tester AI: test design and execution.
- Debugger AI: diagnosis and repair.
- Explainer AI: user-facing explanations.
- Security AI: security review and risk identification.

A role is not automatically a separate model or agent. Record actual model/provider identity and contribution using Mimir's global provenance contract.

## 5. Autonomy levels

1. **Assist only:** explain and propose; do not change files without a user request.
2. **Collaborative assistance:** plan and execute permitted work; ask approval for sensitive or consequential actions.
3. **Supervised autonomy:** execute within explicitly granted limits and escalate exceptions.

Autonomy level does not override operation-specific permission checks, security policy, or user approvals.

## 6. Forge navigation and shared page structure

Forge Header:
- Project Switcher.
- Search within Forge/project.
- Create Project.
- Notifications/attention.
- Connectivity and synchronization status.
- Forge Settings.

Shared across pages:
- Active project context.
- AI Engineering Assistant.
- Task/agent activity.
- Source/model provenance.
- Permission and approval surface.
- Logs and diagnostic access when relevant.

## 7. Page specifications

### 7.1 Forge Home — approved

Purpose: engineering overview and entry point.

Sections:
- Engineering Overview.
- Projects.
- Active Engineering Tasks.
- Build & Test Monitor.
- Agents & Workers.
- Changes & Reviews.
- Artifacts.
- Needs Attention.
- Recent Engineering Activity.

The home page prioritizes current work, blockers, and next actions. Progress indicators must be based on real task/build/test state.

### 7.2 Projects — approved

Forge Projects contains software projects and is distinct from Mimir's general Projects area.

Project cards show:
- Name, type, description.
- Repository and branch where connected.
- Last activity.
- Build status.
- Open tasks.
- Last modification.

Creation/import:
- Start from a natural-language idea with AI assistance.
- Open an existing project.
- Import a Git repository.
- Import project files/archive.

On opening an existing project, run a read-only Project Intelligence Scan first:
- Explain project purpose and structure.
- Identify languages/frameworks and dependencies.
- Detect likely issues and missing tools.
- Summarize architecture and available tests.
- Do not modify files during the initial scan.

Project Workspace sections may include Overview, Files & Code, Tasks & Plan, AI Team, Build & Tests, Changes & Reviews, Artifacts, Documentation, History, and Project Settings. These may be tabs or contextual panels rather than separate pages.

Project operations: open, rename, move, copy, archive, export, connect repository, share, and delete with confirmation and recovery policy.

### 7.3 Code Workspace — approved

Core layout:
- Project Explorer.
- Code Editor.
- AI Engineering Assistant.
- Live Preview & Results.

Three interaction modes:
1. Natural-language conversation with the engineering assistant.
2. Select code and ask for explanation/change.
3. Manual editing.

AI capabilities:
- Write and modify code.
- Explain code.
- Repair errors.
- Review code.
- Modify related files when the plan and impact are visible.

Change review must show changed files, diff, reason, expected impact, verification, and actions to accept/reject/request changes/restore/compare.

Preview must label whether the result is Preview, Simulation, or real execution. Availability depends on the target platform and actual environment.

Logs and Diagnostics are distinct from Terminal. Show simple explanations first and technical logs on demand.

#### Code readability and zoom (approved cross-workspace requirement)

Applies to Forge Code Workspace and independently to Terminal:
- Plus and minus controls.
- Current editor font size display.
- Reset to default.
- Pinch-to-Zoom on touch devices where supported.
- Keyboard shortcuts on desktop.
- Scale code font, line height, line numbers, and editor text without changing global Mimir typography.
- Keep settings separate for Forge and Terminal; project-level overrides may be supported.
- Preserve code content and formatting; zoom must not rewrite source.
- Support horizontal scrolling for long lines and RTL/LTR.
- Optional comfortable reading mode and temporary panel collapse.

Safety:
- Real save state, versions, and restore points.
- Sensitive-file protection.
- Isolated execution where available.
- Consequential operations require appropriate approval.
- AI-generated code is not considered correct until verified.

### 7.4 Toolchains & Environments — approved

Purpose: discover and prepare the tools needed by a project.

Coverage:
- Languages and runtimes.
- Compilers and SDKs.
- Frameworks.
- Libraries and packages.
- Build tools.
- Emulators/simulators.
- Testing tools.
- Static analysis and formatting tools.
- Local, Cloud, and Hybrid environments.

Sections:
- Environment Overview.
- Installed Tools.
- Missing Requirements.
- Libraries & Dependencies.
- Environment Configuration.
- AI Environment Assistant.

Natural-language examples:
- "Prepare a Flutter environment."
- "What is missing to build this Android project?"
- "Check the installed tools."
- "Install the required libraries."
- "Find a compatible version for this SDK."

For each recommendation show purpose, reason needed, required/installed version, approximate size only when verified, official/trusted source, project/device compatibility, permissions, and expected impact.

Installation workflow:
1. Inspect project and target environment.
2. Detect installed/missing tools.
3. Recommend versions and explain compatibility.
4. Show plan, download size, permissions, and impact.
5. Obtain user approval.
6. Download/install through a suitable package manager/installer.
7. Verify version, path, and actual execution.
8. Bind the toolchain to the project.

Do not assume a phone can run every compiler, SDK, or emulator locally. Offer compatible Cloud/Hybrid alternatives where appropriate.

Distinguish device toolchains from project dependencies. Do not update dependencies arbitrarily or change lockfiles without showing the impact and obtaining the required approval.

### 7.5 Tasks & Planning — approved

Purpose: convert a natural-language goal into a structured engineering plan.

Planning flow:
1. Understand the requested outcome and current project.
2. Identify affected areas/files and prerequisites.
3. Break work into tasks and milestones.
4. Define dependencies and parallelizable work.
5. Identify tools, environments, tests, risks, and approvals.
6. Present the plan.
7. Start only after required approval.
8. Track evidence and revise plan when conditions change.

Views:
- Project Plan.
- Kanban Task Board.
- Simple list.
- Timeline/dependency graph.
- AI Planning Assistant.

Suggested task states:
Backlog, Ready, In Progress, Blocked, Waiting for Approval, In Review, Done.

Task detail:
- Purpose and expected outcome.
- Work steps.
- Dependencies.
- Affected files.
- Assignee (user, agent, or executor).
- Priority.
- Actual state.
- Verification method.
- Results and notes.

Three planning levels:
- Simple.
- Detailed.
- Advanced engineering plan.

Estimate is not a guarantee. Completion requires evidence. Plan approval does not automatically approve all execution actions.

### 7.6 Agents & Workers — approved

Purpose: manage the AI team working on the project.

Functional roles: Architect, Developer, Reviewer, Tester, Debugger, Explainer, Security.

Sections:
- Team Overview.
- Agent Profiles.
- Active Workers.
- Task Assignments.
- Collaboration and handoffs.
- Performance & History.
- Agent Configuration.

Controls:
- Start/stop.
- Assign/reassign tasks.
- Set scoped permissions.
- Inspect activity.
- Request explanation.
- Set resource/time/cost limits.
- Review each agent's contribution.

Prevent two workers from applying conflicting changes to the same target without coordination/lease control. An executor is not the sole judge of its own completion. Agent permissions cannot expand themselves.

### 7.7 Build & Test Center — approved

Purpose: build the project and validate outcomes.

Sections:
- Build Overview.
- Build Profiles.
- Test Center.
- Results & Diagnostics.
- AI Build & Test Assistant.

Profiles may include Android Debug/Release, iOS, Web, Desktop, and Custom where supported by the environment.

Test categories:
- Unit.
- Integration.
- UI.
- End-to-End.
- Regression.
- Performance.
- Security.

Keep separate statuses for build, each test suite, and independent review. A successful build does not mean tests passed. Show logs, failure cause, affected files, possible fixes, and what remains unverified.

### 7.8 Changes & Code Review — approved

Purpose: inspect and govern all project changes.

Sections:
- Changes Overview.
- Diff Viewer.
- AI Review.
- Review Decisions.
- Change History.
- Impact Analysis.

Track added, modified, deleted, renamed, moved, and uncommitted files.

Review actions:
- Accept.
- Reject.
- Request Changes.
- Compare.
- Restore.

Record actor (user/agent/tool), model/provider/execution provenance where available, timestamp, reason, files affected, test results, review result, and final decision.

Large or risky changes should have restore points. Never accept AI changes merely because generation finished.

### 7.9 Artifacts & Releases — approved

Purpose: manage build outputs and product releases.

Artifacts may include APK/AAB, iOS packages where supported, Web/Desktop outputs, libraries, documentation, distribution packages, reports, and project archives.

Sections:
- Artifacts Library.
- Release Manager.
- Distribution.
- Version Comparison.
- AI Release Assistant.

Track name, type, project, version, creation time, size when verified, build environment, and verification status.

Distinguish:
- Created.
- Build-verified.
- Test-verified.
- Reviewed.
- Approved for release.
- Published/distributed.

Protect signing keys and certificates in Secrets Vault. Do not upload, publish, or distribute externally without approval.

### 7.10 Sandbox & Execution — approved

Purpose: run code and operations in a suitably isolated environment before allowing effects on project/device resources.

Sections:
- Sandbox Overview.
- Execution Sessions.
- Permission & Isolation Controls.
- Execution Results.
- AI Execution Assistant.

Every session records project, source revision, command/operation, environment, permissions, filesystem access, network access, resource limits, timestamps, state, and outputs.

Suggested isolation profiles:
- Restricted: minimal read/analysis.
- Standard Sandbox: scoped files/resources.
- Extended Sandbox: broader access only with explicit approval and platform support.

Do not describe ordinary local execution as a secure sandbox unless isolation is actually enforced. Show whether the environment is local, remote, or simulated.

### 7.11 Forge Settings — approved

Settings groups:
- General.
- AI & Models.
- Autonomy & Permissions.
- Development Preferences.
- Editor Appearance.
- Toolchains & Environments.
- Storage & Backup.
- Security & Privacy.
- Advanced.

Support separate Forge editor zoom/font settings, reset-one and reset-all, safe defaults, and clear explanations. Do not expose unsupported provider capabilities.

## 8. AI assistant behavior across Forge

The assistant must understand the current project, selected files, plan, tasks, environment, changes, build/test status, and relevant Knowledge Vault context.

It should:
- Explain before acting when the action is consequential.
- Provide a plan and expected impact.
- Use read-only inspection before proposing modifications.
- Work on multiple related files only when the change set is visible.
- Ask approval for destructive, external, privileged, or irreversible actions.
- Summarize results in plain language and link to technical evidence.
- Preserve model/provider/local-cloud provenance.
- Distinguish proposal, execution, verification, and acceptance.

## 9. Optional external integrations

### 9.1 Cognee
Cognee is an accepted optional Knowledge & Memory provider. Forge may consume code-graph, project-memory, and retrieval capabilities through Mimir contracts if useful. Forge must not depend on Cognee and must retain native project/file/task identity.

### 9.2 Vane Search candidate
The reviewed `BunsDev/perplexica-search-engine-ai` repository currently identifies its application as Vane. It may be evaluated as an optional research provider for technical documentation discovery, source comparison, and external research. It is not a Forge engine and not a required dependency. External research results must preserve citations and must be explicitly saved to Knowledge Vault when desired.

## 10. Architecture boundaries

Forge owns the software engineering product workflow and presentation. It does not own:
- Kernel capability registry.
- Agent Runtime lifecycle.
- Global Orchestration.
- Workflow Runtime.
- Terminal engine.
- Secrets Vault.
- Global Connectivity & Synchronization.
- Global Model Provenance contract.
- Knowledge Vault persistence.

Forge consumes these through contracts and may expose Forge-specific workflows without duplicating the underlying services.

## 11. Reliability, safety, and privacy

- No success without evidence.
- Read-only scan before modification of an imported project.
- Show affected files and expected impact.
- Preserve restore points and history.
- Enforce scoped permissions.
- Isolate untrusted code.
- Protect signing credentials and secrets.
- Do not upload project content without policy and consent.
- Prevent duplicate external side effects after uncertain results.
- Keep task/agent/workflow/tool states separate.
- Do not let agents self-expand permissions.
- Do not claim unavailable preview/build/test platforms are supported.

## 12. Responsive UX

Mobile:
- Assistant, project/task overview, progress, review, approvals, and execution controls.
- Code editor remains usable with zoom and focused reading.
- Avoid forcing desktop-density panels onto a phone.

Tablet:
- Expandable explorer and assistant panels.
- More simultaneous project context.

Desktop/Web:
- Full editor and multi-panel engineering workspace.
- Keyboard shortcuts, hover affordances, split diff, timeline, and diagnostics.

All platforms:
- RTL/LTR.
- Accessibility.
- Theme and Liquid material contracts.
- Reduced motion/transparency.
- Keyboard/safe-area handling.

## 13. Acceptance criteria

- [ ] All eleven approved Forge pages are represented.
- [ ] Forge and Terminal remain independent.
- [ ] AI assistance spans every Forge page.
- [ ] Natural-language project planning works.
- [ ] Toolchain recommendations show compatibility and impact.
- [ ] No installation occurs without the required approval.
- [ ] Task and agent states remain distinct.
- [ ] Build and test results are evidence-backed.
- [ ] Code review shows diffs and impact.
- [ ] Artifacts preserve build and provenance links.
- [ ] Sandbox isolation claims match actual enforcement.
- [ ] Code zoom applies to Forge and Terminal separately.
- [ ] Cognee/Vane remain optional integrations, not Forge dependencies.
- [ ] Android, Tablet, Desktop, RTL/LTR, accessibility, and performance are validated.

## 14. Status

The complete Forge product direction and all eleven page concepts are approved. This document records the approved design specification. Implementation, integration, build/test verification, security review, and device validation remain pending.

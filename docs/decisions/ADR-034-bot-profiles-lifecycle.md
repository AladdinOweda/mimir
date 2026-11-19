# ADR-034 — Bot Profiles, Lifecycle & Execution Scope

Status: **Accepted (directional)** — 2026-11-04
Sources: AnyModelBot audit (selective); Architecture 53

## Context
Bot Engine was a playbook runner. Product needs multi-bot identities with persona, model binding, workspace, memory scope, permissions, budget — without a Multi-Bot Engine.

## Decision
- Extend **existing Bot Engine** with `BotProfile` (identity, persona, modelRef, workspaceId, memoryScope, permissionProfile, budget).
- Bot ≠ Agent Runtime. Bot is policy/identity; execution goes through Agent Runtime / capabilities.
- Isolation: bot cannot read another bot’s memory or workspace without explicit grant.
- No Modelflare hard dependency; model binding is a ModelId/policy reference only.

## Consequences
Update bot-schema, BotEngine register/list/get, tests BOT-001…004. No new Engine.

# ADR-035 — Durable Delivery, Outbox & Idempotency

Status: **Accepted (directional)** — 2026-11-04
Sources: AnyModelBot outbox pattern (design only, no code copy)

## Context
Notifications, approvals, and channel messages can be lost on crash if sent best-effort.

## Decision
- Shared **DurableOutbox** service (not a Kernel Engine, not channel-specific).
- States: Pending → Sending → Sent | Held | Scheduled | Gone.
- Deterministic ids, dedupe keys, exponential backoff, restart recovery.
- Channels are adapters; outbox does not embed Telegram/Slack.
- Exactly-once is not promised without channel idempotency; at-least-once + dedupe.

## Consequences
`DurableOutbox` API + tests DEL-001/002 style. Integrates with bots/workflow notifications later.

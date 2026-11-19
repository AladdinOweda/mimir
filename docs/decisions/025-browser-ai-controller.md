# ADR-025: Browser Engine vs Browser AI Controller

- **Status:** Accepted
- **Date:** 2026-11-01

## Decision
Split **Browser Engine** (render/tabs/profiles) from **Browser AI Controller** (observe/act/orchestrate multi-tab multi-model sessions). AI Hub remains independent of Browser.

## Acceptance scenario
User-authenticated sessions on multiple AI web UIs in tabs; Controller reads, types, sends, and transfers tasks under Observe→FullControl permissions with Kill Switch and audit.

## Engine
GeckoView Android first; desktop engine adapter separate; automation via DOM+a11y (+ visual fallback), not “CDP string” alone.

# ADR-019: AI Terminal — Three Modes + Graduated Fallback

- **Status:** Accepted
- **Date:** 2026-10-30
- **Related:** ADR-002 (Hybrid Terminal), ADR-016 (AI Terminal), Notes_261030

## Context

Users need both natural-language command generation and a classic shell. Device capability and preference differ (mobile vs desktop).

## Decision

1. **UI modes:** Chat (default) · Classic · Split
2. **Selection:** Onboarding choice + Settings change + short explanation of each mode
3. **Automation level:** Assisted by default (Manual / Assisted / Full) — onboarding + Settings
4. **Auto-invoke:** Natural language may propose commands; Assisted requires confirmation before run
5. **Graduated fallback on failure:**
   1. Show “لم أفهم” / “I didn’t understand”
   2. Suggest **three** interpretations
   3. Offer to open **Classic Mode**
6. **Adaptive defaults:** Desktop/High → Classic or Split acceptable; Mobile/Low → Chat preferred — user can always override

## Consequences

- TerminalService exposes execute + executeAI
- All process execution via OpenShell / TerminalService — never ad-hoc ProcessBuilder
- Phase **03.3** delivers core modes + fallback; Phase **06** adds advanced features

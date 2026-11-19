# ADR-023: Terminal Blocks + CLI↔GUI adapters (Termonster-inspired)

- **Status:** Accepted (design backlog)
- **Date:** 2026-10-31
- **Phase target:** 03.3 core metadata; richer GUI adapters later (06+)

## Decision

### Terminal Blocks
Each command becomes a **Block** with: command, cwd, user/host, start/end, exit code, stdout/stderr, rerun, fold.

### CLI ↔ GUI (optional adapters)
Map tool output to interactive views (file list, process list, git status, JSON/table) while **always** showing the underlying command in the Block log. UI actions emit real commands, never silent side effects.

### Smart Off
User can disable AI features and keep a classic terminal (aligns with Classic mode).

### Out of scope for 03.3
Full Multiverse agent orchestra, Skills marketplace clone, proprietary J.A.R.V.I.S kernel.

## Research pointers (public)
FrugalGPT cascades, LLMLingua-style compression — evaluate for ModelRouter cost path; do not invent unverified % savings.

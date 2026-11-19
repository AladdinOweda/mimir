# ADR-043 — Model Provider and Capability Adapter Contract

Status: **Proposed** — 2026-11-03

## Context
ModelRouter was a minimal complete() placeholder. LangChain shows useful provider abstractions; Mimir is KMP-first.

## Decision
Mimir owns provider-neutral model contracts. Adapters declare capabilities (stream, tools, structured output, embeddings, usage). Router applies policy. No LangChain Engine. No Python in commonMain.

## Consequences
Extend ModelRouter; conformance tests; optional isolated adapters only.

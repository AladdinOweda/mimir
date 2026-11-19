# ADR-044 — Portable Workflow and Agent Runtime Adapter Contract

Status: **Proposed** — 2026-11-03

## Context
Flows/Loops/Swarm exist conceptually; formal Workflow + Run/Checkpoint contracts were missing. Seven frameworks overlap but must not own public schemas.

## Decision
Mimir owns Agent Definition, Workflow Definition, Workflow Run, Checkpoint, Approval. External frameworks are optional adapters. LangGraph = first durable-runtime POC candidate. No framework Engines.

## Consequences
agent/workflow/workflow-run schemas; native Flow path required; optional LangGraph adapter behind contract.

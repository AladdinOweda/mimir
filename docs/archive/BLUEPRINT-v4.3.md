# Mimir Universal Architecture Blueprint v4.3
**Canonical — 2026-11-03** · Replaces v4.2 + scattered addenda as the single narrative source.

## 1. Product
Mimir is a Kotlin Multiplatform (Android + Desktop) AI workspace: Kernel-first, independent engines, adapter-based external systems. License: Apache-2.0. Name: **Mimir only** (not QUMP).

## 2. Core rules
1. Kernel owns lifecycle, engine registry, permissions, jobs, events, config — **no domain logic**.
2. Engines are independent; cross-engine calls only via KernelHost capabilities.
3. External systems are **adapters/providers**, never new engines solely because a repo exists.
4. Honest stubs preferred over fake production success.
5. Discovery ≠ evaluation ≠ approval ≠ enablement.

## 3. Architecture layers
```
Kernel
Shared: Provider Registry · Process Runtime · Verification/Evidence · Secrets/Vault hooks
Engines: Terminal · Forge · Browser · Computer Use · Orchestration · IE · Vault ·
         Agent Connectivity/Session/Runtime · Bot · Task Isolation · SPE · AI/Model
Adapters: Transformers · vLLM · SGLang · ACP · MCP · UI-TARS · Apify · browser drivers
Apps: shared UI shell + android + desktop
```

## 4. Engine summary
| Engine | Role |
|--------|------|
| Terminal | Commands, risk gates, executor ports |
| Forge | Templates, scaffold, **sandbox gated by Provider** |
| Browser | Tabs, navigation, chat extract contracts |
| Computer Use | Observe/act ports, verification claims |
| Orchestration | Multi-model delegation (not model load) |
| IE | Thin extract → orch → type pipelines |
| Vault | Local envelopes, secrets boundary |
| Agent * | ACP/MCP sessions, isolation, bots |
| Task Isolation | Git worktree for code tasks |
| SPE | Spec/plan/tasks/analyze/converge |
| Process Runtime | Frozen workflow steps, live capability invoke |
| Provider Registry | External tool/MCP lifecycle |
| Verification | Claims, evidence, provider/engine checks |
| **AI/Model** | Model registry, router, **runtime adapters** (no TransformersEngine) |

## 5. Locked external-repo decisions
| Source | Place |
|--------|--------|
| UI-TARS | Browser/Computer Use **adapter only** (ADR-028) |
| Codeg | Agent connectivity + task patterns (ADR-029/030) |
| Spec Kit | SPE + Process (ADR-031) — no unrestricted shell |
| Agentic AI APIs | Provider discovery only (ADR-032) |
| HF Transformers | **ModelRuntimeContract** adapter (ADR-033) |

## 6. AI / Model (ADR-033)
- ModelManifest + lifecycle + **per-model license**
- `trust_remote_code` default **deny**
- Adapters: Noop · Transformers(stub) · vLLM(stub) · SGLang(stub)
- Router ≠ Orchestration

## 7. UX / Product
- Adaptive nav (phone/tablet/desktop)
- Feature flags, onboarding, i18n En/Ar contracts
- Product readiness + ReleaseChecklist (`canShip` gated)
- Full Compose UI still incomplete

## 8. Security (global)
Untrusted web content · MCP tool scopes · no secret leakage to models · shell only via permissions · provider/model enablement gates · audit evidence on sensitive actions.

## 9. Non-goals (current)
Live Python Transformers in-process · production vLLM · real WebView bind · GitHub CI green · store release.

## 10. References
UNIFIED-ARCHITECTURE-DECISIONS-1.0.md · ADR-027…033 · STATE.md · REPO-STRUCTURE.md · CHECKLIST-MASTER.md · DASHBOARD.md

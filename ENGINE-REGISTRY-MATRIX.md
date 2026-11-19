# ENGINE-REGISTRY-MATRIX
**Authority:** ADR-037 paths · ProductContext.attachDefaultEngines · 2026-11-10

| Path | Gradle coordinate | ProductContext attach |
|------|-------------------|------------------------|
| engines/terminal | :shared:engine:terminal | yes |
| engines/process | :shared:engine:process | yes |
| engines/forge | :shared:engine:forge | yes |
| engines/browser | :shared:engine:browser | yes |
| engines/computer | :shared:engine:computer | yes |
| engines/vault | :shared:engine:vault | yes |
| engines/agent-runtime | :shared:engine:agent-runtime | yes |
| engines/orchestration | :shared:engine:orchestration | yes |
| engines/workflow-runtime | :shared:engine:workflow | yes |
| engines/intelligent-execution | :shared:engine:ie | yes |
| engines/bot-profiles | :shared:engine:bot | yes |
| engines/loop-runtime | :engines:loop-runtime | LoopRuntime companion |
| engines/provider | :shared:engine:provider | optional |
| adapters/browser | :adapters:browser | dependency of browser/computer |

Privileged invoke: KernelHost only. Layout: ADR-034.

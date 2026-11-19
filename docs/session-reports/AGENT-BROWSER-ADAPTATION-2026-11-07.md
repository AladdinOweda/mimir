# agent-browser → Mimir Browser adaptation (2026-11-07)

Source inspiration: https://github.com/vercel-labs/agent-browser (Apache-2.0)
**Not** vendored as dependency — patterns ported into Kotlin contracts.

## Mapping

| agent-browser | Mimir capability / type |
|---------------|-------------------------|
| `open` + session | `browser.tab.open` / navigate |
| `snapshot -i` (a11y + refs) | `browser.snapshot.a11y` → tree + refs `e1…` |
| `click @e1` | `browser.act.ref` action=click ref=e1 |
| `fill @eN` | `browser.act.ref` action=type/fill |
| click/type CSS | `browser.click` / `browser.type` |
| `wait` | `browser.wait` |
| hover / scroll / press | `browser.hover` / `scroll` / `press` |
| `eval` | `browser.eval` (**always** ApprovalToken) |
| domain allowlists | `BrowserPolicy` + `browser.policy.set/get` |
| screenshot | `browser.screenshot.meta` (honest availability) |
| tabs | `browser.tab.list` / open / close |
| MCP/skills CLI | future **adapter** process, not Engine |

## Extra beyond CLI inspiration
- Kernel ApprovalToken risk classes (High/Critical)
- Pluggable WebViewPort (noop, http-fetch, chromium-dom, native Android/iOS)
- ComputerUseEngine shares same ports
- Chat extract capability for multi-LLM page workflows

## Honesty
HTML parser refs ≠ full Chrome a11y tree. Chromium driver improves act fidelity; parser still labels snapshot for agents when only HTML is available.

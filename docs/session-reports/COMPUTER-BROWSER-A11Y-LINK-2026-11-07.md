# Computer Use ↔ Browser a11y link

## Contract
- `computer.observe` returns `tree`, `refs`, `nodeCount` from the same `HtmlA11yParser` / `WebViewPort.a11ySnapshot` as `browser.snapshot.a11y`.
- `computer.act` accepts `ref` (preferred) or `selector`; parity field documents which path.
- `SharedBrowserSession(port)` wires one `WebViewPort` into both engines.

## Agent loop
1. navigate / open
2. observe → read `tree` + `refs`
3. act with `ref=eN` + ApprovalToken when required
4. observe again

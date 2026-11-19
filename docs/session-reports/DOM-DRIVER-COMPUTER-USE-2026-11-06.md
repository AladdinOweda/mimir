# Chromium DOM driver + Computer Use — 2026-11-06

## Browser drivers
| kind | class | real | DOM snapshot | click/type |
|------|-------|------|--------------|------------|
| noop | NoopWebViewPort | no | no | no |
| http-fetch | HttpFetchWebViewPort | yes HTTP | HTML body | no |
| **chromium-dom** | **ChromiumDomWebViewPort** | **yes** | **Chrome --dump-dom** | CDP best-effort |

```kotlin
BrowserEngine(driverKind = "chromium-dom")
ComputerUseEngine(uiKind = "chromium-dom")
```

## Computer Use
- `UiAutomationPort` + `BrowserBackedUiPort` wraps WebViewPort
- Levels: Observe / Assist / Autonomous / FullControl
- ACT requires approvalToken; executed=true only if driver click/type succeeds
- Smoke: navigate+observe on example.com with realDriver=true, title Example Domain

## Still open
Stable CDP page-target click in all environments; Android WebView port; CEF embed

# Browser real driver — 2026-11-06

## Drivers
| Kind | Class | realDriver | navigate | snapshot | DOM click/type |
|------|-------|------------|----------|----------|----------------|
| noop | NoopWebViewPort | false | local state | empty | no |
| http-fetch | HttpFetchWebViewPort | true | real HTTP GET | HTML prefix + text | no (honest) |

## Usage
```kotlin
BrowserEngine(driverKind = "http-fetch")
// or
BrowserEngine(driverFactory = { HttpFetchWebViewPort() })
```

## Not yet
- Chromium/CEF / JavaFX WebView / Android WebView DOM automation
- Full chat extract from SPA (needs JS runtime)

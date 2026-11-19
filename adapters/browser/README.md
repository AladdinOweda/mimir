# adapters/browser

Isolates rendering backends from `engines/browser`.

**Current implementation location (code):**
- `engines/browser/.../webview/ChromiumDomWebViewPort.kt`
- `engines/browser/.../webview/HttpFetchWebViewPort.kt`
- `engines/browser/.../webview/MobileWebViewPorts.kt`

Target: move provider-specific drivers behind this adapter package without changing BrowserEngine contracts.
Status: documented bridge — physical move of .kt optional later ADR.

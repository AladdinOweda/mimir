# Mimir iOS (real WKWebView)

Swift package with `WkWebViewHost` mirroring `NativeWebViewHost`.

```bash
# open in Xcode or
swift build  # on macOS with iOS SDK / simulator toolchains as applicable
```

Bridge to shared Kotlin:
1. Expose WkWebViewHost methods to Kotlin/Native `NativeWebViewHost`
2. `NativeWebViewPort(host, "ios-wkwebview")`
3. `WebViewDrivers.register { port }` before starting BrowserEngine

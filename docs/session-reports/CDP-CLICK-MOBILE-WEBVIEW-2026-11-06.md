# CDP page-target click + Android / iOS WebView — 2026-11-06

## CDP
- Page targets only; Runtime/Page/DOM enabled; wait readyState
- Smoke: file HTML #btn → out=clicked PASS

## Mobile
- NativeWebViewHost + NativeWebViewPort for AGP Android / iOS WKWebView
- JVM placeholders: android-webview-missing, ios-wkwebview-missing
- WebViewDrivers.register for injection

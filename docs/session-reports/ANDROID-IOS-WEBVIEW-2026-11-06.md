# Real Android WebView + iOS WKWebView — 2026-11-06

## Android (`apps/androidApp`)
- AGP application module, `minSdk 26`, Internet permission
- `AndroidWebViewHost` implements `NativeWebViewHost` on live `android.webkit.WebView`
- `MainActivity` registers `NativeWebViewPort(..., "android-webview")` into `WebViewDrivers`
- Starts `BrowserEngine` + `ComputerUseEngine` on the same port
- Module included from `settings.gradle.kts` **only when** `local.properties`/`ANDROID_HOME` present (keeps JVM CI green without SDK)

## iOS (`apps/iosApp`)
- Swift package `MimirIOS` with `WkWebViewHost` (WKWebView, JS click/type/snapshot)
- `ContentView` embeds WKWebView and loads example.com
- Bridge to shared Kotlin: map host → `NativeWebViewPort(..., "ios-wkwebview")`

## Shared
- `androidWebViewPortFromHost` / `iosWebViewPortFromBridge` helpers
- Placeholders remain for pure JVM (`android-webview-missing` / `ios-wkwebview-missing`)

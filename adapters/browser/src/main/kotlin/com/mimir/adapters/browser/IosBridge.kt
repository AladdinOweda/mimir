package com.mimir.adapters.browser

/**
 * Expected to be satisfied by Kotlin/Native or a JNI/Swift bridge layer.
 * Product iOS app creates [NativeWebViewPort] with a host that delegates to WkWebViewHost.swift.
 */
fun iosWebViewPortFromBridge(host: NativeWebViewHost): WebViewPort =
    NativeWebViewPort(host, driverName = "ios-wkwebview")

fun androidWebViewPortFromHost(host: NativeWebViewHost): WebViewPort =
    NativeWebViewPort(host, driverName = "android-webview")

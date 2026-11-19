import SwiftUI
import WebKit

struct ContentView: View {
    @StateObject private var model = BrowserModel()

    var body: some View {
        VStack(spacing: 0) {
            WebViewContainer(webView: model.host.webView)
                .edgesIgnoringSafeArea(.bottom)
        }
        .task {
            await model.bootstrap()
        }
    }
}

struct WebViewContainer: UIViewRepresentable {
    let webView: WKWebView
    func makeUIView(context: Context) -> WKWebView { webView }
    func updateUIView(_ uiView: WKWebView, context: Context) {}
}

@MainActor
final class BrowserModel: ObservableObject {
    let host = WkWebViewHost()

    func bootstrap() async {
        await host.loadUrl("https://example.com")
        // Shared Kotlin BrowserEngine is linked via KMP framework in full product build.
        // Bridge maps WkWebViewHost → NativeWebViewHost → NativeWebViewPort(driverName: "ios-wkwebview").
    }
}

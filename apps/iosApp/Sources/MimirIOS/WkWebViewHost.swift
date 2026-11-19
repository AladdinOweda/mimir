import Foundation
import WebKit

/// Live iOS host bridging WKWebView → shared NativeWebViewHost contract (via Kotlin/Native or JSON RPC).
/// Product can expose these methods to shared Kotlin through a thin bridge.
@MainActor
public final class WkWebViewHost: NSObject, WKNavigationDelegate {
    public let webView: WKWebView
    private var loadContinuation: CheckedContinuation<Void, Never>?

    public override init() {
        let config = WKWebViewConfiguration()
        config.preferences.javaScriptEnabled = true
        self.webView = WKWebView(frame: .zero, configuration: config)
        super.init()
        self.webView.navigationDelegate = self
    }

    public func loadUrl(_ url: String) async {
        guard let u = URL(string: url) else { return }
        await withCheckedContinuation { (cont: CheckedContinuation<Void, Never>) in
            self.loadContinuation = cont
            self.webView.load(URLRequest(url: u))
        }
    }

    public func currentUrl() -> String {
        webView.url?.absoluteString ?? "about:blank"
    }

    public func title() -> String {
        webView.title ?? ""
    }

    public func htmlSnapshot() async -> String {
        await evaluateJavascript("(function(){try{return document.documentElement.outerHTML;}catch(e){return '';}})();")
    }

    public func evaluateJavascript(_ script: String) async -> String {
        await withCheckedContinuation { cont in
            webView.evaluateJavaScript(script) { result, _ in
                if let s = result as? String {
                    cont.resume(returning: s)
                } else if let r = result {
                    cont.resume(returning: String(describing: r))
                } else {
                    cont.resume(returning: "")
                }
            }
        }
    }

    public func clickCss(_ selector: String) async -> Bool {
        let esc = selector.replacingOccurrences(of: "\\", with: "\\\\")
            .replacingOccurrences(of: "\"", with: "\\\"")
        let js = """
        (function(){
          var el = document.querySelector("\(esc)");
          if(!el) return 'missing';
          el.click();
          return 'ok';
        })();
        """
        return await evaluateJavascript(js) == "ok"
    }

    public func typeCss(_ selector: String, text: String) async -> Bool {
        let escSel = selector.replacingOccurrences(of: "\\", with: "\\\\")
            .replacingOccurrences(of: "\"", with: "\\\"")
        let escText = text.replacingOccurrences(of: "\\", with: "\\\\")
            .replacingOccurrences(of: "\"", with: "\\\"")
            .replacingOccurrences(of: "\n", with: "\\n")
        let js = """
        (function(){
          var el = document.querySelector("\(escSel)");
          if(!el) return 'missing';
          el.focus();
          el.value = "\(escText)";
          el.dispatchEvent(new Event('input', {bubbles:true}));
          el.dispatchEvent(new Event('change', {bubbles:true}));
          return 'ok';
        })();
        """
        return await evaluateJavascript(js) == "ok"
    }

    public func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        loadContinuation?.resume()
        loadContinuation = nil
    }

    public func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        loadContinuation?.resume()
        loadContinuation = nil
    }
}

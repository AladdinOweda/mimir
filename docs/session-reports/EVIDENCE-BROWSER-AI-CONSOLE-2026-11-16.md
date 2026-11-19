# Browser AI + Console — 2026-11-16

## Implemented
- browser.console.read / clear (contract + engine)
- WebViewPort.consoleLogs; HttpFetch host buffer; Chromium CDP Runtime.consoleAPICalled + Console.enable
- BrowserAiController (ADR-025): Observe/Assist/Full, kill switch, multi-tab open, observe (snap+a11y+console+chat), act, captureContext, transferContext
- Tests: BrowserAiControlTest 4 + BrowserConsoleTest 2 = 6 OK

## Remaining for full product console/AI control
- Live CDP on device with Chrome installed (CI may lack chrome)
- Android WebChromeClient / iOS WK console bridge
- DOM fill/send chat on real AI sites (selectors per provider)
- Kernel policies for browser-ai session principal

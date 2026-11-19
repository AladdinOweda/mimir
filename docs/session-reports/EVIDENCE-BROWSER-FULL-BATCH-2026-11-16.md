# Browser full batch 2026-11-16

## Delivered
1. AiSiteCatalog — DeepSeek, Perplexity, Qwen, Kimi, Gemini, Copilot, Poe, HF, … + runtime register any URL/tool
2. AdBlock policy + navigate/open block
3. Popup heuristics + dismiss (label/role/ref) gated by mimir-popup-guard extension
4. Extension registry (virtual Chrome-like): adblock, popup-guard, secrets-fill
5. Credential fill via secretRef only — AI never sees password values
6. BrowserAiController multi-site + prior AI control/console

## Tests OK (14)
BrowserProtectionAndAiSitesTest + BrowserAiControlTest + BrowserConsoleTest

## Honest remaining
- Real Chromium WebExtension (CRX) host not in-process
- Network-level filter lists need WebView/Chromium extension layer on device
- Site-specific chat selectors still best-effort

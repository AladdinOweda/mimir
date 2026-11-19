# Known limitations — 2026-11-10

1. **canShip = false** — not a consumer product release.
2. Compose Material3 screens not shipped; shell is contract/controller level.
3. Browser WebView ports are partial (HTTP/noop/Chromium stubs); not full multi-tab product UX.
4. Durable memory/activity use TSV files under `~/.mimir` — not multi-device sync.
5. Secrets vault is process-local AES key — not hardware keystore.
6. ProductContext.attachDefaultEngines requires full multi-module classpath (Gradle).
7. CI structure checks may soft-fail missing scaffold build scripts.
8. Provider/model runtime not required for vertical security path tests.
9. LoopRuntime is companion object, not always registered as MimirEngine capability.
10. Some engine capability string IDs predate CapabilityCatalog aliases — both must stay aligned.

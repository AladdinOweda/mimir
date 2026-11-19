# Priority Batch 1 — Browser driver + Kernel smoke

## Done
1. ProductContext.attachSmokeWithHttpBrowser() (terminal+process+forge+BrowserEngine http-fetch)
2. KernelEngineSmokeTest: KernelHost attach terminal + http-fetch browser — OK (1)
3. HttpFetchWebViewPortTest: about:blank + https://example.com — OK (2)
4. Http-fetch is realDriver=true (network HTML); CDP/Chromium remains optional next

## Gradle
- Wrapper works (Gradle 8.11.1) via `bash gradlew`
- Full :kernel:compileKotlinJvm timed out / memory constrained in sandbox
- kotlinc path remains green for smoke tests

## Next priority batches
2. ProductContext full attach smoke with durable dirs (when classpath complete)
3. Gradle CI green on kernel+terminal+browser
4. Compose one screen
5. canShip gate

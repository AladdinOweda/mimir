# Evidence — VerticalInvokePathTest

- **Date (UTC):** see log
- **Runner:** kotlinc + JUnitCore (not Gradle — no full Gradle daemon run in this environment for the full multi-module graph)
- **Class:** `com.mimir.kernel.host.VerticalInvokePathTest`
- **Source path:** `kernel/src/test/kotlin/com/mimir/kernel/host/VerticalInvokePathTest.kt`

## Why not Gradle
Sandbox/Drive workspace may lack a reliable `./gradlew` network+daemon run for the entire graph in this session. This evidence is **kotlinc+JUnit** only.

## Raw log
```
=== date: 2026-11-10T17:57:53Z ===
=== kotlinc: info: kotlinc-jvm 2.0.21 (JRE 21.0.10+7-Ubuntu-124.04) ===
=== java: openjdk version "21.0.10" 2026-01-20 ===
=== compile kernel ===
kernel_compile_exit=0
=== compile VerticalInvokePathTest ===
test_compile_exit=0
=== junit ===
JUnit version 4.13.2
..
Time: 0.157

OK (2 tests)

junit_exit=0

```

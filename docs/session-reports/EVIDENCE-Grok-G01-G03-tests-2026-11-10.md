# Evidence — Grok G-01/G-02/G-03 tests — 2026-11-10

## Environment
- DATE_UTC: 2026-11-10T19:13:02Z
- kotlinc-jvm 2.0.21 (JRE 21.0.10)
- openjdk 21.0.10
- Runner: JUnitCore 4.13.2 via kotlinc (not Gradle)

## Why not Gradle
No full `./gradlew` multi-module run log in this session (same constraint as prior evidence). CI workflow updated to compile/test these modules when Actions runs.

## Commands (abbreviated; full classpath in log)
```
kotlinc -jvm-target 17 -d kernel.jar $(find kernel/src/main -name '*.kt')
kotlinc ... -d kernel-tests.jar VerticalInvokePathTest.kt KernelNeutralityTest.kt
kotlinc ... -d tasks.jar $(find services/tasks/src/main -name '*.kt')
kotlinc ... -d tasks-tests.jar TaskContractsTest.kt
java -cp kernel-tests.jar:kernel.jar:... org.junit.runner.JUnitCore com.mimir.kernel.host.VerticalInvokePathTest
java -cp ... com.mimir.kernel.KernelNeutralityTest   # cwd=kernel/
java -cp tasks-tests.jar:tasks.jar:... com.mimir.service.tasks.TaskContractsTest
```

## Results
| Suite | Result |
|-------|--------|
| VerticalInvokePathTest | OK (2 tests) |
| KernelNeutralityTest | OK (1 test) |
| TaskContractsTest | OK (4 tests) |

## Raw log
```
DATE_UTC=2026-11-10T19:13:02Z
KOTLINC=info: kotlinc-jvm 2.0.21 (JRE 21.0.10+7-Ubuntu-124.04)
JAVA=openjdk version "21.0.10" 2026-01-20
kernel_compile=0
tasks_compile=0
kernel_tests_compile=0
tasks_tests_compile=0
=== CMD VerticalInvokePathTest ===
java -cp /tmp/mimir-compile/kernel-tests.jar:/tmp/mimir-compile/kernel.jar:/tmp/mimir-compile/tasks.jar:/tmp/coroutines.jar:/tmp/kotlin-test.jar:/tmp/kotlin-test-junit.jar:/tmp/junit.jar:/tmp/hamcrest.jar:/tmp/kotlinc/lib/kotlin-stdlib.jar org.junit.runner.JUnitCore com.mimir.kernel.host.VerticalInvokePathTest
JUnit version 4.13.2
..
Time: 0.134

OK (2 tests)

=== CMD KernelNeutralityTest ===
JUnit version 4.13.2
.
Time: 0.134

OK (1 test)

=== CMD TaskContractsTest ===
JUnit version 4.13.2
....
Time: 0.025

OK (4 tests)

```

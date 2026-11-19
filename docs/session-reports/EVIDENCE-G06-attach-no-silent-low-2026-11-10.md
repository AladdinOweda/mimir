# Evidence — G-06 + G-04 accepted — 2026-11-10

DATE_UTC=2026-11-10T19:22:07Z
kotlinc 2.0.21 · JUnitCore

## Change
- KernelHost.attach no longer auto-registers RiskLevel.Low policies.
- ProductContext.attachDefaultEngines calls DefaultCapabilityPolicies.seed.
- ADR-040 accepted. ADR-039 G-04 accepted by owner.

## Tests
```
DATE_UTC=2026-11-10T19:22:07Z
kernel_compile=0
tasks_compile=0
ktest_c=0
ttest_c=0
JUnit version 4.13.2
..
Time: 0.202

OK (2 tests)

JUnit version 4.13.2
.
Time: 0.225

OK (1 test)

JUnit version 4.13.2
....
Time: 0.063

OK (4 tests)
```

VerticalInvokePathTest OK (2) · KernelNeutralityTest OK (1) · TaskContractsTest OK (4)

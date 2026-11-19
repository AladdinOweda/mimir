# Browser + Process + Kernel integration

## Process
- processMetrics (execCount, failCount, lastExit)
- Compiles with terminal ProcessExecutorPort
- Kernel integration: argv echo OK, shell High risk needs token OK

## Browser
- browserMetrics + listTabSummaries
- Compiles with adapters

## Integration tests KernelEngineIntegrationTest: 4 OK
- process argv through kernel
- process shell approval gate
- terminal open+list through kernel
- events allow/deny

## Kernel "100%" honesty
Designed core surface complete (host, policy, approval, audit, outbox, events, permissions).
Not: multi-node, hardware keystore, full production SRE, every future capability.

# ProcessRuntimeEngine compile fix — 2026-11-05

## Cause
Compile of `process` module without `terminal` on the classpath left `ProcessExecResult` unresolved when mixed with other broken variants.

## Fix
- Single clean `ProcessRuntimeEngine` (v0.5.1) importing `ProcessExecRequest` / `ProcessExecResult` / `ProcessExecutorPort`
- argv via `argv` (NUL), `argvCsv`, or `argvLine`
- path_jail → Err; real exitCode/stdout/stderr/timedOut on Ok

## Tests
4/4 PASS: empty argv · echo argv · path jail · shell echo

# R5 Process Runtime & Terminal — 2026-11-04

## Delivered
- `JvmProcessExecutor`: argv-first, parallel stream drain, timeout, workspace path jail
- `ProcessRuntimeEngine`: `process.exec.argv` + `process.exec.shell`
- Capability policies for process exec
- Terminal uses named ProcessExecRequest (shell still for session commands)
- Smoke **7/7 PASS**

## Not closed
- OpenShell backend
- Full Android process port
- KernelHost-only path for all process product calls

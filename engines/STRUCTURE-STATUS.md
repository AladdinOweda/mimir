# engines/ — structure status (MIMIR-REPO-002 v1.0)

Holds **all** specialized capability modules and former `runtimes/*` sources:
agent-runtime, orchestration, workflow-runtime, bot-profiles, intelligent-execution,
browser, terminal, forge, computer, vault, process, provider, verification, tools, ai-model, …

Gradle coordinates remain `:shared:engine:*` with `projectDir = engines/<name>`.

Scaffold-only slots: ai-hub, loop-runtime, knowledge-workspace, unified-file-transfer, collaboration.

# MODULE_GRAPH — 2026-11-09 (structure-matched)

Dependency direction (allowed):

```
apps → packages/app-shell + packages/design-tokens + kernel contracts
engines/* → kernel only (no engine→engine private imports)
adapters → contracts / kernel types (not product UI)
shared services → kernel contracts (not engines)
```

## projectDir map

See `settings.gradle.kts`. All engine modules resolve under `engines/`.
`runtimes/` is a pointer only.

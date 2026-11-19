# Structure match review — 2026-11-09 (continued)

## Closed this pass
- apps/mobile + shared-app documented as target aliases
- platform/{android,desktop,ios,web,common} slots
- adapters/browser + process + ai bridge READMEs → real code paths
- contracts/{common,capability,browser,approval} slots
- tests/{architecture,contracts,integration,security,acceptance,fixtures}
- MODULE-CATALOG + MODULE_GRAPH + REPO-STRUCTURE updated
- Confirmed: no .kt under shared/ engine trees; engines hold sources

## Still open
- Rename Gradle coordinates from :shared:engine:* to :engines:* (optional ADR)
- Physical move of WebView ports into adapters/browser (optional)
- Implement shared/memory, shared/secrets, …
- Compose UI in apps
- Drive full source upload / new zip
- canShip false

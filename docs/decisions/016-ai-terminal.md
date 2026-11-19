# ADR 016 — AI Terminal (v4.1)
**Status:** Accepted
**Decision:** Three modes — Chat (default for new/weaker), Classic, Split. Pipeline: IntentParser → Planner → Safety → Executor → Explainer. Auto-invoke from all features. Automation: Manual / Assisted (default) / Auto.
**Phase:** 03.3 before Forge.

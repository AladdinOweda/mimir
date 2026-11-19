# RELEASE-GATE
**canShip: false**

| Gate | Status | Evidence |
|------|--------|----------|
| Constitution + ADRs coherent | partial | D-7…D-10 locked; ADR renumber done |
| Single target structure narrative | yes (principles) | ADR-046; competing docs superseded |
| Kernel neutrality | yes (static) | KernelNeutralityTest baseline |
| No engine→engine private deps (Forge/Process/Chat) | partial | ProcessExecutorPort + Chat pipeline; TaskBoard pending |
| All modules compile in Gradle | no | F0 includes added; full CI not green |
| Module unit + contract tests | partial | selected deepen batches |
| Full CI green | no | — |
| Product UX acceptance | no | screen specs incomplete |
| Privacy / security review | no | — |
| Human sign-off | no | — |

**Rule:** never set canShip true until every mandatory gate has linked evidence and human sign-off.

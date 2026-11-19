# Capability catalog

**Code:** `com.mimir.kernel.contract.CapabilityCatalog`

| Family | IDs | Module |
|--------|-----|--------|
| browser.* | navigate, snapshot, a11y, act.ref, tab.* | engines/browser + adapters/browser |
| computer.* | observe, act, navigate | engines/computer |
| terminal / process / forge / vault | exec, spawn, build, test, get/put | engines/* |
| agent / orch / workflow / loop | session, plan, run | engines/* |
| ie.* | plan, execute, relay | engines/intelligent-execution |
| bot.profile.* | create, get, list | engines/bot-profiles |
| model.complete | | engines/provider |

All privileged invokes: **KernelHost only**.

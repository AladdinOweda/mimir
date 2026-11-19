package com.mimir.contracts.capability

/**
 * Product capability ID constants — **not** in kernel (ADR G-05 / Constitution §3.2).
 * Kernel only sees opaque capability strings via policies + invoke.
 */
object CapabilityCatalog {
    const val BROWSER_TAB_OPEN = "browser.tab.open"
    const val BROWSER_TAB_CLOSE = "browser.tab.close"
    const val BROWSER_TAB_LIST = "browser.tab.list"
    const val BROWSER_NAVIGATE = "browser.navigate"
    const val BROWSER_SNAPSHOT = "browser.snapshot"
    const val BROWSER_A11Y = "browser.snapshot.a11y"
    const val BROWSER_CLICK = "browser.click"
    const val BROWSER_TYPE = "browser.type"
    const val BROWSER_ACT_REF = "browser.act.ref"

    const val COMPUTER_OBSERVE = "computer.observe"
    const val COMPUTER_ACT = "computer.act"
    const val COMPUTER_NAVIGATE = "computer.navigate"

    /** Coarse id; fine-grained: contracts.terminal.TerminalCapabilityIds */
    const val TERMINAL_EXEC = "terminal.exec"
    const val PROCESS_SPAWN = "process.spawn"
    const val FORGE_BUILD = "forge.build"
    const val FORGE_TEST = "forge.test"
    const val VAULT_GET = "vault.get"
    const val VAULT_PUT = "vault.put"

    const val AGENT_SESSION_START = "agent.session.start"
    const val ORCH_PLAN = "orchestration.plan"
    const val WORKFLOW_RUN = "workflow.run"
    const val LOOP_RUN = "loop.run"

    const val IE_PLAN = "ie.plan"
    const val IE_EXECUTE = "ie.execute"
    const val IE_RELAY = "ie.relay"

    const val BOT_PROFILE_CREATE = "bot.profile.create"
    const val BOT_PROFILE_GET = "bot.profile.get"
    const val BOT_PROFILE_LIST = "bot.profile.list"

    const val MODEL_COMPLETE = "model.complete"
}

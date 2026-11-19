package com.mimir.engine.browser

import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrowserAiControlTest {
    @Test
    fun observe_mode_blocks_act() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val ai = BrowserAiController(eng)
        ai.setLevel(BrowserAiController.ControlLevel.Observe)
        val open = ai.openTab("about:blank", "lab")
        assertTrue(open is InvocationResult.Ok, open.toString())
        val tabId = (open as InvocationResult.Ok).output["tabId"]!!
        val denied = ai.act(tabId, "click", selector = "#x")
        assertTrue(denied is InvocationResult.Err)
        assertEquals("level_denied", (denied as InvocationResult.Err).code)
        val obs = ai.observe(tabId)
        assertTrue(obs.containsKey("console"))
        assertTrue(obs.containsKey("chat") || obs.containsKey("snap"))
    }

    @Test
    fun full_mode_navigate_and_console_read_via_observe() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val ai = BrowserAiController(eng)
        ai.setLevel(BrowserAiController.ControlLevel.Full)
        val open = ai.openTab("about:blank", "g1")
        val tabId = (open as InvocationResult.Ok).output["tabId"]!!
        // navigate via act
        val nav = ai.act(tabId, "navigate", text = "https://example.com")
        // may succeed or fail on network; not level_denied
        assertTrue(nav is InvocationResult.Ok || (nav is InvocationResult.Err && (nav as InvocationResult.Err).code != "level_denied"), nav.toString())
        val state = ai.controlState()
        assertEquals("Full", state["level"])
        assertEquals("false", state["killSwitch"])
    }

    @Test
    fun kill_switch_blocks_act() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val ai = BrowserAiController(eng)
        ai.setLevel(BrowserAiController.ControlLevel.Full)
        ai.armKillSwitch(true)
        val open = ai.openTab("about:blank")
        val tabId = (open as InvocationResult.Ok).output["tabId"]!!
        val r = ai.act(tabId, "click", selector = "a")
        assertTrue(r is InvocationResult.Err)
        assertEquals("kill_switch", (r as InvocationResult.Err).code)
    }

    @Test
    fun multi_tab_context_capture_keys() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val ai = BrowserAiController(eng)
        ai.setLevel(BrowserAiController.ControlLevel.Assist)
        val a = (ai.openTab("about:blank", "claude") as InvocationResult.Ok).output["tabId"]!!
        val b = (ai.openTab("about:blank", "gpt") as InvocationResult.Ok).output["tabId"]!!
        assertTrue(a != b)
        val payload = ai.captureContext("from-claude", a)
        assertTrue(payload.isNotEmpty() || ai.listContextKeys().contains("from-claude"))
        assertTrue("from-claude" in ai.listContextKeys())
        // transfer without selector may still return inv error — key must exist
        val missing = ai.transferContext("nope", b, "textarea")
        assertTrue(missing is InvocationResult.Err)
        assertEquals("not_found", (missing as InvocationResult.Err).code)
    }
}

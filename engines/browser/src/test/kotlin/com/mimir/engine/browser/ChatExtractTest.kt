package com.mimir.engine.browser

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatExtractTest {
    @Test
    fun parser_roles_from_prefixed_lines() {
        val text = """
            User: What is Mimir?
            Assistant: Mimir is a modular AI OS kernel.
            User: thanks
            Grok: You're welcome.
        """.trimIndent()
        val msgs = ChatExtractParser.parse(text)
        assertTrue(msgs.size >= 3)
        assertEquals("user", msgs.first().role)
        assertTrue(msgs.any { it.role == "assistant" })
        val sum = ChatExtractParser.summary(msgs)
        assertTrue(sum["messageCount"]!!.toInt() >= 3)
    }

    @Test
    fun extract_chat_capability_returns_structure() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val open = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(BrowserCapabilities.TAB_OPEN)), mapOf("url" to "about:blank")),
        )
        val tabId = (open as InvocationResult.Ok).output["tabId"]!!
        val ex = eng.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(BrowserCapabilities.EXTRACT_CHAT)), mapOf("tabId" to tabId)),
        )
        assertTrue(ex is InvocationResult.Ok, ex.toString())
        assertTrue((ex as InvocationResult.Ok).output.containsKey("messageCount"))
    }

    @Test
    fun multi_tab_context_transfer_uses_chat_capture() = runTest {
        val eng = BrowserEngine(driverKind = "http-fetch")
        eng.start()
        val ai = BrowserAiController(eng)
        ai.setLevel(BrowserAiController.ControlLevel.Assist)
        val a = (ai.openTab("about:blank", "deepseek") as InvocationResult.Ok).output["tabId"]!!
        val b = (ai.openTab("about:blank", "perplexity") as InvocationResult.Ok).output["tabId"]!!
        ai.captureContext("ds-out", a)
        assertTrue("ds-out" in ai.listContextKeys())
        val missing = ai.transferContext("ds-out", b, "textarea")
        // may type_failed on http-fetch but not not_found
        assertTrue(missing is InvocationResult.Ok || (missing is InvocationResult.Err && (missing as InvocationResult.Err).code != "not_found"), missing.toString())
    }
}

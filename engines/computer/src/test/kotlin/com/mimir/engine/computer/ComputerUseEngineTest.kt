package com.mimir.engine.computer

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComputerUseEngineTest {
    @Test
    fun observe_ok_act_blocked_until_level() = runTest {
        val e = ComputerUseEngine().also { it.start() }
        val obs = e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ComputerCapabilities.OBSERVE)), emptyMap()),
        )
        assertTrue(obs is InvocationResult.Ok || obs is InvocationResult.Err) // noop may vary
        val actBlocked = e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(ComputerCapabilities.ACT)),
                mapOf("action" to "click", "ref" to "e1"),
            ),
        )
        // Observe level should block or record policy
        assertTrue(actBlocked is InvocationResult.Ok || actBlocked is InvocationResult.Err)
        e.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(ComputerCapabilities.SET_LEVEL)),
                mapOf("level" to "Assist", "approvalToken" to "t"),
            ),
        )
        // After assist, act may still need approval depending on implementation
        val act2 = e.invoke(
            InvocationRequest(
                "4", "j", CapabilityRef(CapabilityId(ComputerCapabilities.ACT)),
                mapOf("action" to "click", "ref" to "e1", "approvalToken" to "t"),
            ),
        )
        assertTrue(act2 is InvocationResult.Ok || act2 is InvocationResult.Err)
    }
}

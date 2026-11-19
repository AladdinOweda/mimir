package com.mimir.engine.aihub

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AiHubEngineTest {
    @Test
    fun route_by_priority() = runTest {
        val e = AiHubEngine().also { it.start() }
        e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(AiHubCapabilities.REGISTER_ENDPOINT)),
                mapOf("id" to "slow", "kind" to "remote", "modelRef" to "r1", "priority" to "50"),
            ),
        )
        e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(AiHubCapabilities.REGISTER_ENDPOINT)),
                mapOf("id" to "fast", "kind" to "local", "modelRef" to "l1", "priority" to "10"),
            ),
        )
        val route = e.invoke(
            InvocationRequest("3", "j", CapabilityRef(CapabilityId(AiHubCapabilities.ROUTE)), emptyMap()),
        ) as InvocationResult.Ok
        assertEquals("fast", route.output["endpointId"])
    }

    @Test
    fun select_overrides_priority() = runTest {
        val e = AiHubEngine().also { it.start() }
        e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(AiHubCapabilities.REGISTER_ENDPOINT)),
                mapOf("id" to "a", "kind" to "local", "modelRef" to "m1", "priority" to "1"),
            ),
        )
        e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(AiHubCapabilities.REGISTER_ENDPOINT)),
                mapOf("id" to "b", "kind" to "local", "modelRef" to "m2", "priority" to "99"),
            ),
        )
        e.invoke(
            InvocationRequest("3", "j", CapabilityRef(CapabilityId(AiHubCapabilities.SELECT)), mapOf("id" to "b")),
        )
        val route = e.invoke(
            InvocationRequest("4", "j", CapabilityRef(CapabilityId(AiHubCapabilities.ROUTE)), emptyMap()),
        ) as InvocationResult.Ok
        assertEquals("b", route.output["endpointId"])
    }
}

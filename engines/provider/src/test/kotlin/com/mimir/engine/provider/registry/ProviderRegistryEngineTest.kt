package com.mimir.engine.provider.registry

import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProviderRegistryEngineTest {
    @Test
    fun registerAndList() = runTest {
        val e = ProviderRegistryEngine().also { it.start() }
        val reg = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ProviderCapabilities.REGISTER)),
                mapOf("id" to "local.test", "name" to "Local"),
            ),
        )
        assertTrue(reg is InvocationResult.Ok, reg.toString())
        val list = e.invoke(
            InvocationRequest("2", "j", CapabilityRef(CapabilityId(ProviderCapabilities.LIST)), emptyMap()),
        )
        assertTrue(list is InvocationResult.Ok)
        assertTrue((list as InvocationResult.Ok).output["ids"].orEmpty().contains("local.test"))
        assertEquals("1", list.output["count"])
    }

    @Test
    fun registerRequiresId() = runTest {
        val e = ProviderRegistryEngine().also { it.start() }
        val reg = e.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ProviderCapabilities.REGISTER)), emptyMap()),
        )
        assertTrue(reg is InvocationResult.Err)
        assertEquals("invalid", (reg as InvocationResult.Err).code)
    }

    @Test
    fun authorizeWithinBudget() = runTest {
        val e = ProviderRegistryEngine().also { it.start() }
        val ok = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ProviderCapabilities.AUTHORIZE_CALL)),
                mapOf(
                    "principalId" to "u1",
                    "budgetUsd" to "1.00",
                    "estimatedUsd" to "0.10",
                    "allowSpend" to "true", // must be ignored as authorization source
                ),
            ),
        )
        assertTrue(ok is InvocationResult.Ok, ok.toString())
        assertEquals("true", (ok as InvocationResult.Ok).output["authorized"])
        assertEquals("true", ok.output["ignoredAllowSpend"])
    }

    @Test
    fun authorizeExceedsBudget() = runTest {
        val e = ProviderRegistryEngine().also { it.start() }
        val r = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ProviderCapabilities.AUTHORIZE_CALL)),
                mapOf(
                    "principalId" to "u2",
                    "budgetUsd" to "0.05",
                    "estimatedUsd" to "1.00",
                ),
            ),
        )
        assertTrue(r is InvocationResult.Err)
        assertEquals("budget_exceeded", (r as InvocationResult.Err).code)
    }

    @Test
    fun authorizeRequiresPrincipal() = runTest {
        val e = ProviderRegistryEngine().also { it.start() }
        val r = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ProviderCapabilities.AUTHORIZE_CALL)),
                mapOf("estimatedUsd" to "0.01"),
            ),
        )
        assertTrue(r is InvocationResult.Err)
        assertEquals("invalid", (r as InvocationResult.Err).code)
    }


    @Test
    fun cumulativeSpendHitsLimit() = runTest {
        val e = ProviderRegistryEngine().also { it.start() }
        val a1 = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ProviderCapabilities.AUTHORIZE_CALL)),
                mapOf("principalId" to "u3", "budgetUsd" to "0.15", "estimatedUsd" to "0.10"),
            ),
        )
        assertTrue(a1 is InvocationResult.Ok)
        val a2 = e.invoke(
            InvocationRequest(
                "2", "j", CapabilityRef(CapabilityId(ProviderCapabilities.AUTHORIZE_CALL)),
                mapOf("principalId" to "u3", "estimatedUsd" to "0.10"),
            ),
        )
        assertTrue(a2 is InvocationResult.Err)
        assertEquals("budget_exceeded", (a2 as InvocationResult.Err).code)
    }

    @Test
    fun authorizeUnknownProviderFails() = runTest {
        val e = ProviderRegistryEngine().also { it.start() }
        val r = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ProviderCapabilities.AUTHORIZE_CALL)),
                mapOf("principalId" to "u4", "providerId" to "missing.p", "estimatedUsd" to "0.01"),
            ),
        )
        assertTrue(r is InvocationResult.Err)
        assertEquals("provider_not_registered", (r as InvocationResult.Err).code)
    }
}

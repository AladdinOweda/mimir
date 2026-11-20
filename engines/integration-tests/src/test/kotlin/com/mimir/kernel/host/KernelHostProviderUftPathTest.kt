package com.mimir.kernel.host

import com.mimir.contracts.capability.DefaultCapabilityPolicies
import com.mimir.engine.provider.registry.ProviderCapabilities
import com.mimir.engine.provider.registry.ProviderRegistryEngine
import com.mimir.engine.uft.UftCapabilities
import com.mimir.engine.uft.UnifiedFileTransferEngine
import com.mimir.kernel.contract.*
import com.mimir.kernel.permission.PermissionContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class KernelHostProviderUftPathTest {
    @Test
    fun host_routes_provider_and_uft() = runTest {
        val host = KernelHost()
        DefaultCapabilityPolicies.seed(host.capabilitySecurity)
        val provider = ProviderRegistryEngine()
        val uft = UnifiedFileTransferEngine()
        val pMan = provider.describe()
        val uMan = uft.describe()
        host.attach(provider, pMan)
        host.attach(uft, uMan)
        host.registerAndStart(pMan.id)
        host.registerAndStart(uMan.id)
        host.permissions.grantAll(
            "tester",
            listOf("provider.admin", "provider.call", "uft.write", "uft.read"),
        )
        val principal = PermissionContext("tester")
        val reg = host.invoke(
            InvocationRequest(
                "1", "tester", CapabilityRef(CapabilityId(ProviderCapabilities.REGISTER)),
                mapOf("id" to "p1", "kind" to "local"),
            ),
            principal,
        )
        assertTrue(reg is InvocationResult.Ok, reg.toString())
        val create = host.invoke(
            InvocationRequest(
                "2", "tester", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_CREATE)),
                mapOf("direction" to "Copy", "source" to "a", "destination" to "b"),
            ),
            principal,
        )
        assertTrue(create is InvocationResult.Ok, create.toString())
    }
}

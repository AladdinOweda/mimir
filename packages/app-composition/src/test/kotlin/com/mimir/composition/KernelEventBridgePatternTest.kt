package com.mimir.composition

import com.mimir.kernel.contract.EventEnvelope
import com.mimir.kernel.contract.KernelEventHandler
import com.mimir.kernel.host.KernelHost
import com.mimir.platform.events.EventBus
import com.mimir.platform.events.EventHandler
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Pattern used by ProductContext.installKernelEventBridge — host.events → product EventBus.
 * Full ProductContext needs engine classpath; this proves the bridge contract.
 */
class KernelEventBridgePatternTest {
    @Test
    fun host_events_forward_to_product_bus() {
        val host = KernelHost()
        val product = EventBus()
        val seen = mutableListOf<String>()
        product.subscribe(EventHandler { seen += it.type })
        host.events.subscribe(KernelEventHandler { env: EventEnvelope -> product.publish(env) })
        host.events.publish(EventEnvelope("kernel.engine.attached", "KernelHost", payload = mapOf("engineId" to "x")))
        assertTrue("kernel.engine.attached" in seen)
    }
}

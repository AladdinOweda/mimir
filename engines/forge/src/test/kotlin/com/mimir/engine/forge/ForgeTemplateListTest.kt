package com.mimir.engine.forge

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ForgeTemplateListTest {
    @Test
    fun list_templates() = runTest {
        val e = ForgeEngine().also { it.start() }
        val r = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ForgeCapabilities.TEMPLATE_LIST)),
                emptyMap(),
            ),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
        val ok = r as InvocationResult.Ok
        assertTrue(ok.output.isNotEmpty() || ok.output.containsKey("count") || ok.output.containsKey("templates") || ok.output.containsKey("ids"))
    }
}

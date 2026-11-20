package com.mimir.engine.forge

import com.mimir.contracts.forge.ForgeBuildRequest
import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ForgeEngineTest {
    @Test
    fun templateList() = runTest {
        val eng = ForgeEngine().also { it.start() }
        val r = eng.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId(ForgeCapabilities.TEMPLATE_LIST)), emptyMap()),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
    }

    @Test
    fun toolchainDetect() = runTest {
        val eng = ForgeEngine().also { it.start() }
        val r = eng.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(ForgeCapabilities.TOOLCHAIN_DETECT)),
                mapOf("kind" to "java"),
            ),
        )
        assertTrue(r is InvocationResult.Ok, r.toString())
    }

    @Test
    fun port_register_lane_and_owner() {
        val eng = ForgeEngine(allowRealFs = false)
        val lane = eng.registerLane("/tmp/mimir-proj", "proj")
        assertEquals(lane.id.value, eng.ownerOf("/tmp/mimir-proj/src"))
        assertTrue(eng.listLanes().isNotEmpty())
    }

    @Test
    fun port_build_without_gradlew_is_honest_failure() {
        val dir = File.createTempFile("forge-proj", null).apply { delete(); mkdirs() }
        try {
            val eng = ForgeEngine(allowRealFs = true)
            val r = eng.build(ForgeBuildRequest(dir.absolutePath))
            assertTrue(!r.success)
            assertTrue(r.output.contains("gradlew_missing") || r.exitCode != 0)
        } finally {
            dir.deleteRecursively()
        }
    }
}

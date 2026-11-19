package com.mimir.kernel

import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.EngineId
import com.mimir.kernel.contract.EngineManifest
import com.mimir.kernel.contract.EngineState
import com.mimir.kernel.contract.HealthReport
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import com.mimir.kernel.contract.MimirEngine
import com.mimir.kernel.host.KernelHost
import com.mimir.kernel.permission.PermissionContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KernelHostTest {
    private class EchoEngine : MimirEngine {
        private var state = EngineState.Discovered
        private val manifest = EngineManifest(
            id = EngineId("echo"),
            name = "Echo",
            version = "0.1.0",
            provides = listOf(CapabilityRef(CapabilityId("echo.ping"))),
            requestedPermissions = listOf("echo.run"),
        )
        override suspend fun describe() = manifest
        override suspend fun configure(config: Map<String, String>): EngineState {
            state = EngineState.Configured; return state
        }
        override suspend fun start(): EngineState {
            state = EngineState.Running; return state
        }
        override suspend fun stop(reason: String): EngineState {
            state = EngineState.Stopped; return state
        }
        override suspend fun health() = HealthReport(manifest.id, true, state)
        override suspend fun invoke(request: InvocationRequest) =
            InvocationResult.Ok(request.requestId, mapOf("pong" to "true"))
    }

    @Test
    fun registerStartAndInvoke() = runTest {
        val host = KernelHost()
        val eng = EchoEngine()
        host.attach(eng, eng.describe())
        assertEquals(EngineState.Running, host.registerAndStart(EngineId("echo")))
        host.permissions.grantAll("u1", listOf("echo.run"))
        val res = host.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId("echo.ping"))),
            PermissionContext("u1"),
            "echo.run",
        )
        assertTrue(res is InvocationResult.Ok)
    }

    @Test
    fun permissionDenied() = runTest {
        val host = KernelHost()
        val eng = EchoEngine()
        host.attach(eng, eng.describe())
        host.registerAndStart(EngineId("echo"))
        val res = host.invoke(
            InvocationRequest("1", "j", CapabilityRef(CapabilityId("echo.ping"))),
            PermissionContext("u1"),
            "echo.run",
        )
        assertTrue(res is InvocationResult.Err)
    }
}

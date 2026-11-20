package com.mimir.kernel.host

import com.mimir.kernel.contract.*
import com.mimir.kernel.permission.PermissionContext
import com.mimir.kernel.security.CapabilityPolicy
import com.mimir.kernel.security.RiskLevel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class VerticalInvokePathTest {

    private class EchoEngine : MimirEngine {
        private val manifest = EngineManifest(
            id = EngineId("echo-test"),
            name = "Echo",
            version = "0.1.0",
            provides = listOf(CapabilityRef(CapabilityId("echo.ping"))),
            requestedPermissions = listOf("echo.run"),
        )
        private var state = EngineState.Discovered
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
        override suspend fun health() =
            HealthReport(manifest.id, state == EngineState.Running, state)
        override suspend fun invoke(request: InvocationRequest): InvocationResult {
            if (request.input.containsKey("approvalGranted")) {
                return InvocationResult.Err(request.requestId, "spoof", "approvalGranted must be stripped")
            }
            return InvocationResult.Ok(
                request.requestId,
                mapOf("echo" to (request.input["msg"] ?: "")),
            )
        }
    }

    private class DangerEngine : MimirEngine {
        private val m = EngineManifest(
            id = EngineId("danger"),
            name = "D",
            version = "1",
            provides = listOf(CapabilityRef(CapabilityId("echo.danger"))),
            requestedPermissions = listOf("echo.danger"),
        )
        private var st = EngineState.Discovered
        override suspend fun describe() = m
        override suspend fun configure(c: Map<String, String>): EngineState {
            st = EngineState.Configured; return st
        }
        override suspend fun start(): EngineState {
            st = EngineState.Running; return st
        }
        override suspend fun stop(r: String): EngineState {
            st = EngineState.Stopped; return st
        }
        override suspend fun health() = HealthReport(m.id, true, st)
        override suspend fun invoke(request: InvocationRequest) =
            InvocationResult.Ok(request.requestId, mapOf("ok" to "1"))
    }

    @Test
    fun attach_start_invoke_records_audit() {
        runBlocking {
            val host = KernelHost()
            host.capabilitySecurity.register(
                CapabilityPolicy("echo.ping", "echo.run", RiskLevel.Low),
            )
            val eng = EchoEngine()
            host.attach(eng, eng.describe())
            assertEquals(EngineState.Running, host.registerAndStart(EngineId("echo-test")))
            host.permissions.grantAll("user-1", listOf("echo.run"))
            val result = host.invoke(
                InvocationRequest(
                    requestId = "req-1",
                    jobId = "job-1",
                    capability = CapabilityRef(CapabilityId("echo.ping")),
                    input = mapOf("msg" to "hello", "approvalGranted" to "true"),
                ),
                PermissionContext(principalId = "user-1"),
            )
            assertIs<InvocationResult.Ok>(result)
            assertEquals("hello", result.output["echo"])
            assertTrue(host.audit.recent().any { it.capabilityId == "echo.ping" && it.decision == "ALLOW" })
        }
    }

    @Test
    fun high_risk_requires_token_not_boolean() {
        runBlocking {
            val host = KernelHost()
            host.capabilitySecurity.register(
                CapabilityPolicy("echo.danger", "echo.danger", RiskLevel.High, "echo.danger"),
            )
            val d = DangerEngine()
            host.attach(d, d.describe())
            host.registerAndStart(EngineId("danger"))
            host.permissions.grantAll("user-1", listOf("echo.danger"))
            val principal = PermissionContext(principalId = "user-1")
            val denied = host.invoke(
                InvocationRequest(
                    requestId = "r2",
                    jobId = "j2",
                    capability = CapabilityRef(CapabilityId("echo.danger")),
                    input = mapOf("approvalGranted" to "true"),
                ),
                principal,
            )
            assertIs<InvocationResult.Err>(denied)
            assertEquals("approval_required", denied.code)

            val token = host.approvals.issue(
                principalId = "user-1",
                actionId = "echo.danger",
                targetResource = "echo.danger",
                grantedScope = "echo.danger",
            )
            val ok = host.invoke(
                InvocationRequest(
                    requestId = "r3",
                    jobId = "j3",
                    capability = CapabilityRef(CapabilityId("echo.danger")),
                    input = mapOf("approvalToken" to token.id),
                ),
                principal,
            )
            assertIs<InvocationResult.Ok>(ok)
        }
    }
}

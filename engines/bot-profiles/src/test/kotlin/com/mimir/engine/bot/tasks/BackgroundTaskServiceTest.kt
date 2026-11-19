package com.mimir.engine.bot.tasks

import com.mimir.kernel.contract.CapabilityId
import com.mimir.kernel.contract.CapabilityRef
import com.mimir.kernel.contract.InvocationRequest
import com.mimir.kernel.contract.InvocationResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BackgroundTaskServiceTest {
    @Test
    fun lifecycle() = runTest {
        val e = BackgroundTaskService().also { it.start() }
        val id = (e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(BgTaskCapabilities.TASK_CREATE)),
                mapOf("botId" to "b1", "title" to "job"),
            ),
        ) as InvocationResult.Ok).output.getValue("id")
        e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(BgTaskCapabilities.TASK_START)), mapOf("id" to id)))
        e.invoke(
            InvocationRequest(
                "3", "j", CapabilityRef(CapabilityId(BgTaskCapabilities.TASK_PROGRESS)),
                mapOf("id" to id, "progress" to "40"),
            ),
        )
        val done = e.invoke(
            InvocationRequest("4", "j", CapabilityRef(CapabilityId(BgTaskCapabilities.TASK_COMPLETE)), mapOf("id" to id)),
        ) as InvocationResult.Ok
        assertEquals("Succeeded", done.output["status"])
    }
}

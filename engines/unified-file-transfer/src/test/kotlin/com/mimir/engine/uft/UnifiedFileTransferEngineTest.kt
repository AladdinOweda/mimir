package com.mimir.engine.uft

import com.mimir.kernel.contract.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UnifiedFileTransferEngineTest {
    @Test
    fun transfer_lifecycle() = runTest {
        val e = UnifiedFileTransferEngine().also { it.start() }
        val c = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_CREATE)),
                mapOf("direction" to "Upload", "source" to "/a", "destination" to "/b", "bytesTotal" to "100"),
            ),
        )
        assertTrue(c is InvocationResult.Ok, c.toString())
        val id = (c as InvocationResult.Ok).output.getValue("id")
        e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_PROGRESS)), mapOf("id" to id, "bytesDone" to "50")))
        val done = e.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_COMPLETE)), mapOf("id" to id)))
        assertTrue(done is InvocationResult.Ok, done.toString())
        assertEquals("Completed", (done as InvocationResult.Ok).output["status"])
    }

    @Test
    fun resume_and_checksum() = runTest {
        val e = UnifiedFileTransferEngine().also { it.start() }
        val c = e.invoke(
            InvocationRequest(
                "1", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_CREATE)),
                mapOf("direction" to "Download", "source" to "http://x", "destination" to "/tmp/f", "bytesTotal" to "1000", "resumable" to "true"),
            ),
        )
        assertTrue(c is InvocationResult.Ok, "create=" + c.toString())
        val id = (c as InvocationResult.Ok).output.getValue("id")
        e.invoke(InvocationRequest("2", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_PROGRESS)), mapOf("id" to id, "bytesDone" to "200")))
        val cancel = e.invoke(InvocationRequest("3", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_CANCEL)), mapOf("id" to id)))
        assertTrue(cancel is InvocationResult.Ok, "cancel=" + cancel.toString())
        val resumed = e.invoke(InvocationRequest("4", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_RESUME)), mapOf("id" to id)))
        assertTrue(resumed is InvocationResult.Ok, "resume=" + resumed.toString())
        e.invoke(InvocationRequest("5", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_CHECKSUM)), mapOf("id" to id, "checksum" to "abc123")))
        val st = e.invoke(InvocationRequest("6", "j", CapabilityRef(CapabilityId(UftCapabilities.TRANSFER_STATUS)), mapOf("id" to id)))
        assertTrue(st is InvocationResult.Ok, "status=" + st.toString())
        assertEquals("InProgress", (st as InvocationResult.Ok).output["status"])
    }
}

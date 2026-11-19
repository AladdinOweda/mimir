package com.mimir.contracts.workflow
import kotlin.test.Test
import kotlin.test.assertEquals
class WorkflowContractTest {
    @Test fun ids() { assertEquals("workflow.run.start", WorkflowCapabilityIds.RUN_START) }
}

package com.mimir.contracts.agent
import kotlin.test.Test
import kotlin.test.assertEquals
class AgentContractTest {
    @Test fun ids() { assertEquals("agent.session.start", AgentCapabilityIds.SESSION_START) }
}

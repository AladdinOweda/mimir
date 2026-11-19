package com.mimir.contracts.agent

object AgentCapabilityIds {
    const val AGENT_CREATE = "agent.create"
    const val SESSION_START = "agent.session.start"
    const val SESSION_STOP = "agent.session.stop"
    const val TOOL_INVOKE = "agent.tool.invoke"
    const val SESSION_STATUS = "agent.session.status"
    val ALL = listOf(AGENT_CREATE, SESSION_START, SESSION_STOP, TOOL_INVOKE, SESSION_STATUS)
}

data class AgentId(val value: String)
data class AgentSessionId(val value: String)

data class AgentInfo(
    val id: AgentId,
    val displayName: String,
    val modelId: String,
)

data class AgentSessionInfo(
    val id: AgentSessionId,
    val agentId: AgentId,
    val status: String,
)

interface AgentPort {
    fun createAgent(displayName: String, modelId: String, toolAllowlist: Set<String> = emptySet()): AgentInfo
    fun startSession(agentId: AgentId): AgentSessionInfo
    fun stopSession(sessionId: AgentSessionId): Boolean
    fun sessionStatus(sessionId: AgentSessionId): AgentSessionInfo?
}

interface AgentObservability {
    fun agentCount(): Int
    fun activeSessionCount(): Int
}

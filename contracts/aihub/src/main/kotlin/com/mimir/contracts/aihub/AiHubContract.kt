package com.mimir.contracts.aihub

object AiHubCapabilityIds {
    const val ROUTE = "aihub.route"
    const val REGISTER_ENDPOINT = "aihub.endpoint.register"
    const val LIST_ENDPOINTS = "aihub.endpoint.list"
    const val SELECT = "aihub.select"
    val ALL = listOf(ROUTE, REGISTER_ENDPOINT, LIST_ENDPOINTS, SELECT)
}

data class AiEndpointId(val value: String)

interface AiHubPort {
    fun register(id: String, kind: String, modelRef: String, priority: Int = 100): AiEndpointId
    fun select(id: AiEndpointId): Boolean
    fun route(kind: String? = null): AiEndpointId?
}

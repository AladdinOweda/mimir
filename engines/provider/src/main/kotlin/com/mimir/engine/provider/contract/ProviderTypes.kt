package com.mimir.engine.provider.contract

/**
 * Provider & Capability Registry — shared discovery/evaluation layer.
 * External catalogs (e.g. agentic-ai-apis) are **discovery sources only**, not trusted truth.
 */

@JvmInline value class ProviderId(val value: String)
@JvmInline value class CapabilityKey(val value: String)

enum class ProviderType {
    AiModel,
    AgentPlatform,
    McpServer,
    CodeSandbox,
    WebScraper,
    ResearchTool,
    Other,
}

enum class IntegrationKind {
    HttpRest,
    Mcp,
    Cli,
    Sdk,
    Unknown,
}

enum class ProviderLifecycle {
    Discovered,
    Evaluated,
    SecurityReviewed,
    TechnicallyVerified,
    Approved,
    Enabled,
    Disabled,
    Rejected,
}

enum class TrustStatus {
    Unverified,
    Community,
    Official,
    Rejected,
}

data class ProviderSource(
    val discoveredFrom: String,
    val officialUrl: String? = null,
    val lastCheckedEpochMs: Long? = null,
    val notes: String? = null,
)

data class AuthSpec(
    val methods: List<String> = listOf("none"),
    val secretRefHint: String? = null, // vault key hint — never store secrets here
)

data class PricingSpec(
    val model: String = "unknown", // free|usage_based|subscription|unknown
    val verified: Boolean = false,
    val notes: String? = null,
)

data class CompatibilitySpec(
    val status: String = "pending", // pending|compatible|incompatible
    val testsPassed: List<String> = emptyList(),
)

data class ProviderRecord(
    val id: ProviderId,
    val name: String,
    val type: ProviderType,
    val source: ProviderSource,
    val capabilities: List<CapabilityKey> = emptyList(),
    val integration: IntegrationKind = IntegrationKind.Unknown,
    val adapterId: String? = null,
    val authentication: AuthSpec = AuthSpec(),
    val pricing: PricingSpec = PricingSpec(),
    val compatibility: CompatibilitySpec = CompatibilitySpec(),
    val trust: TrustStatus = TrustStatus.Unverified,
    val lifecycle: ProviderLifecycle = ProviderLifecycle.Discovered,
    val version: String? = null,
    val enabledForEngines: List<String> = emptyList(), // engine ids allowed to use when Enabled
    val metadata: Map<String, String> = emptyMap(),
)

data class CapabilityDescriptor(
    val key: CapabilityKey,
    val description: String,
    val requiredPermissions: List<String> = emptyList(),
    val providerIds: List<String> = emptyList(),
)

object ProviderCapabilities {
    const val REGISTER = "provider.register"
    const val GET = "provider.get"
    const val LIST = "provider.list"
    const val SEARCH = "provider.search"
    const val SET_LIFECYCLE = "provider.lifecycle.set"
    const val SET_TRUST = "provider.trust.set"
    const val BIND_ADAPTER = "provider.adapter.bind"
    const val ENABLE = "provider.enable"
    const val DISABLE = "provider.disable"
    const val CAPABILITY_LIST = "provider.capability.list"
    val ALL = listOf(
        REGISTER, GET, LIST, SEARCH, SET_LIFECYCLE, SET_TRUST,
        BIND_ADAPTER, ENABLE, DISABLE, CAPABILITY_LIST,
    )
}

object ProviderPermissions {
    const val READ = "provider.read"
    const val WRITE = "provider.write"
    const val ADMIN = "provider.admin"
}

object ProviderErrorCodes {
    const val NOT_FOUND = "provider_not_found"
    const val INVALID = "invalid_input"
    const val LIFECYCLE = "lifecycle_violation"
    const val NOT_ENABLED = "provider_not_enabled"
    const val UNSUPPORTED = "unsupported_capability"
}

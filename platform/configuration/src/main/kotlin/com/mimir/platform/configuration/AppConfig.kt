package com.mimir.platform.configuration

/**
 * Non-secret application configuration (Blueprint shared/configuration).
 * Secrets never live here — use shared/secrets.
 */
data class AppConfig(
    val dataDir: String = "${System.getProperty("user.home")}/.mimir",
    val locale: String = "en",
    val appearanceMode: String = "Adaptive",
    val durableStores: Boolean = true,
    val enableBrowserEngine: Boolean = true,
    val enableComputerUse: Boolean = true,
)

object AppConfigLoader {
    fun defaults() = AppConfig()
    fun fromMap(map: Map<String, String>) = AppConfig(
        dataDir = map["dataDir"] ?: AppConfig().dataDir,
        locale = map["locale"] ?: "en",
        appearanceMode = map["appearanceMode"] ?: "Adaptive",
        durableStores = map["durableStores"]?.toBooleanStrictOrNull() ?: true,
        enableBrowserEngine = map["enableBrowserEngine"]?.toBooleanStrictOrNull() ?: true,
        enableComputerUse = map["enableComputerUse"]?.toBooleanStrictOrNull() ?: true,
    )
}

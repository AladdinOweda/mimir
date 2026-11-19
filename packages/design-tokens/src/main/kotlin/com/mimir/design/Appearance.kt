package com.mimir.design

/**
 * MIMIR-UX-DTC-001 appearance modes.
 * Adaptive is default for new installs; never overwrite an existing saved preference silently.
 */
enum class AppearanceMode {
    Light,
    Dark,
    System,
    Adaptive,
    ;

    companion object {
        val DefaultForNewUser = Adaptive
        fun fromContract(value: String): AppearanceMode = when (value.lowercase()) {
            "light", "نهاري" -> Light
            "dark", "ليلي" -> Dark
            "system", "حسب النظام" -> System
            "adaptive", "تكيفي" -> Adaptive
            else -> DefaultForNewUser
        }
    }
}

/** Resolved visual theme after preference + environment inputs. */
enum class ResolvedTheme { Light, Dark }

data class AppearancePreferences(
    val mode: AppearanceMode = AppearanceMode.DefaultForNewUser,
    val fontFamilyId: String = "default",
    val fontScale: Float = 1.0f,
    val glassIntensity: Float = 0.35f,
    val shadowLevel1: Float = 0.4f,
    val shadowLevel2: Float = 0.55f,
    val shadowLevel3: Float = 0.7f,
) {
    init {
        require(fontScale in 0.85f..1.6f)
        require(glassIntensity in 0f..1f)
        require(shadowLevel1 in 0f..1f && shadowLevel2 in 0f..1f && shadowLevel3 in 0f..1f)
    }
}

/**
 * Theme resolution — presentation layer only (never Kernel).
 * System uses [osIsDark]; Adaptive uses [dayFraction] 0=night .. 1=day (caller supplies solar model).
 */
object ThemeResolver {
    fun resolve(
        prefs: AppearancePreferences,
        osIsDark: Boolean,
        dayFraction: Float = 1f,
    ): ResolvedTheme = when (prefs.mode) {
        AppearanceMode.Light -> ResolvedTheme.Light
        AppearanceMode.Dark -> ResolvedTheme.Dark
        AppearanceMode.System -> if (osIsDark) ResolvedTheme.Dark else ResolvedTheme.Light
        AppearanceMode.Adaptive -> if (dayFraction >= 0.5f) ResolvedTheme.Light else ResolvedTheme.Dark
    }

    /**
     * Intermediate blend 0=Dark .. 1=Light for Adaptive gradual transition.
     * Non-Adaptive modes return 0 or 1 only.
     */
    fun adaptiveBlend(
        prefs: AppearancePreferences,
        dayFraction: Float,
    ): Float = when (prefs.mode) {
        AppearanceMode.Adaptive -> dayFraction.coerceIn(0f, 1f)
        AppearanceMode.Light -> 1f
        AppearanceMode.Dark -> 0f
        AppearanceMode.System -> -1f // caller uses OS; no time blend
    }
}

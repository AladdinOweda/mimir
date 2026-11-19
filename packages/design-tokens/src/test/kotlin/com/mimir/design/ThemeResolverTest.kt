package com.mimir.design

import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeResolverTest {
    @Test
    fun adaptiveDefaultForNewUser() {
        assertEquals(AppearanceMode.Adaptive, AppearanceMode.DefaultForNewUser)
    }

    @Test
    fun lightIgnoresOsAndDay() {
        val p = AppearancePreferences(mode = AppearanceMode.Light)
        assertEquals(ResolvedTheme.Light, ThemeResolver.resolve(p, osIsDark = true, dayFraction = 0f))
    }

    @Test
    fun adaptiveUsesDayFraction() {
        val p = AppearancePreferences(mode = AppearanceMode.Adaptive)
        assertEquals(ResolvedTheme.Light, ThemeResolver.resolve(p, false, 0.8f))
        assertEquals(ResolvedTheme.Dark, ThemeResolver.resolve(p, false, 0.2f))
    }
}

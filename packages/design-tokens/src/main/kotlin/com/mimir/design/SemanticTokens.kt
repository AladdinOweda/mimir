package com.mimir.design

/**
 * Semantic token *names* (MIMIR-UX-DTC-001). Values stay platform-specific;
 * this module is the single registry of roles so UI never hard-codes palette literals.
 */
object SemanticTokenIds {
    // Color surfaces
    const val COLOR_CANVAS = "color.surface.canvas"
    const val COLOR_SURFACE_PRIMARY = "color.surface.primary"
    const val COLOR_SURFACE_ELEVATED = "color.surface.elevated"
    const val COLOR_TEXT_PRIMARY = "color.text.primary"
    const val COLOR_TEXT_SECONDARY = "color.text.secondary"
    const val COLOR_BORDER_SUBTLE = "color.border.subtle"
    const val COLOR_ACTION_PRIMARY = "color.action.primary.background"
    const val COLOR_ACTION_DESTRUCTIVE = "color.action.destructive.background"
    const val COLOR_STATUS_ERROR = "color.status.error.foreground"
    const val COLOR_STATUS_SUCCESS = "color.status.success.foreground"
    const val COLOR_FOCUS_RING = "color.focus.ring"
    const val COLOR_OVERLAY_SCRIM = "color.overlay.scrim"

    // Material
    const val MATERIAL_SOLID = "material.glass.solid"
    const val MATERIAL_SUBTLE = "material.glass.subtle"
    const val MATERIAL_REGULAR = "material.glass.regular"
    const val MATERIAL_PROMINENT = "material.glass.prominent"

    // Shadow levels (independently adjustable)
    const val SHADOW_L1 = "shadow.level.1"
    const val SHADOW_L2 = "shadow.level.2"
    const val SHADOW_L3 = "shadow.level.3"

    // Motion
    const val MOTION_DURATION_STANDARD = "motion.duration.standard"
    const val MOTION_EASING_STANDARD = "motion.easing.standard"
}

enum class MaterialTier { Solid, Subtle, Regular, Prominent }

/** When to prefer solid over liquid glass (spec). */
object MaterialPolicy {
    fun preferredTier(contentKind: String): MaterialTier = when (contentKind.lowercase()) {
        "code", "table", "form", "approval", "long_text", "settings" -> MaterialTier.Solid
        "nav", "composer", "floating" -> MaterialTier.Regular
        "hero" -> MaterialTier.Prominent
        else -> MaterialTier.Subtle
    }
}

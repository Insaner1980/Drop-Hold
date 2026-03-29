package com.finntek.dropandhold.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Centralized color palette based on the Skyfall theme from the game spec.
 * All UI colors should reference these constants.
 */
object AppColors {
    // Primary palette (Skyfall theme)
    val SkyGradientLight = Color(0xFF87CEEB)
    val SkyGradientDark = Color(0xFF4A90D9)
    val CloudWhite = Color(0xFFF0F4F8)

    // Accents
    val AccentWarm = Color(0xFFFF8C42) // highlights, score pop, buttons
    val AccentCool = Color(0xFF5B9BD5) // secondary interactive elements

    // Text
    val TextDark = Color(0xFF1A1A2E)
    val TextLight = Color(0xFFFFFFFF)

    // Feedback
    val Danger = Color(0xFFE74C3C) // tilt warning, near-flip
    val Success = Color(0xFF2ECC71) // achievements, positive feedback

    // Surface
    val PlatformMarble = Color(0xFFE8E0D8)
}

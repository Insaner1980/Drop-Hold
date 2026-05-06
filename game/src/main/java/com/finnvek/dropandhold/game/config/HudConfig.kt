package com.finnvek.dropandhold.game.config

import com.badlogic.gdx.graphics.Color

/**
 * Visual configuration for the in-game HUD overlay (Scene2D).
 * Centralizes fonts, colors, paddings, and pre-game / pause / game-over status messages.
 */
object HudConfig {
    // Fonts
    const val FONT_SCALE = 1.6f
    const val BIG_FONT_SCALE = 3.2f

    // Padding (Scene2D units = pixels with ScreenViewport)
    const val EDGE_PAD = 32f
    const val LABEL_PAD = 16f
    const val ROW_SPACING = 8f

    // Colors
    val TEXT_PRIMARY: Color = Color.WHITE
    val TEXT_SECONDARY: Color = Color(0.85f, 0.85f, 0.85f, 1f)
    val TEXT_ACCENT: Color = Color(1f, 0.55f, 0.26f, 1f) // ~#FF8C42
    val HUD_BACKGROUND: Color = Color(0f, 0f, 0f, 0.30f)

    // Frame timing
    const val MAX_DELTA = 1f / 30f

    // Status messages
    const val STATUS_CALIBRATING = "Hold your phone steady…"
    const val STATUS_COUNTDOWN = "Get ready"
    const val STATUS_PAUSED = "PAUSED"
    const val STATUS_GAME_OVER = "GAME OVER"
}

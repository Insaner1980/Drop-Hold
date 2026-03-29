package com.finntek.dropandhold.game.config

/**
 * Gameplay tuning: drop intervals, game-over conditions, scoring.
 * Single source of truth for all balance/difficulty parameters.
 */
object GameplayConfig {
    // Drop intervals (seconds)
    const val INITIAL_DROP_INTERVAL = 3.0f
    const val MIN_DROP_INTERVAL = 0.8f

    /** Drop interval decreases by this amount every REDUCTION_INTERVAL_SECONDS. */
    const val DROP_INTERVAL_REDUCTION = 0.05f
    const val DROP_REDUCTION_EVERY_SECONDS = 10f

    // Game over conditions

    /** Platform tilt beyond this angle → game over. */
    const val FLIP_THRESHOLD_DEGREES = 70f

    /** Empty-platform game-over only triggers after this much play time. */
    const val MIN_ELAPSED_BEFORE_EMPTY_CHECK = 5f

    /** If platform stays empty this long, game over. */
    const val EMPTY_PLATFORM_TIMEOUT = 3f

    // Countdown
    const val COUNTDOWN_DURATION = 3f
    const val MAX_DELTA_TIME = 1f / 30f

    // Scoring

    /** Base points multiplier per active object. Formula: objects * (1 + objects * this). */
    const val POINTS_PER_OBJECT_FACTOR = 0.15f

    /** Multiplier grows by this every MULTIPLIER_GROWTH_INTERVAL seconds without losing an object. */
    const val MULTIPLIER_GROWTH = 0.2f
    const val MULTIPLIER_GROWTH_INTERVAL = 10f

    /** Multiplier penalty per lost object. */
    const val MULTIPLIER_LOSS_PENALTY = 0.3f
    const val MULTIPLIER_MINIMUM = 1f

    // Default settings
    const val DEFAULT_SENSITIVITY = 5
}

package com.finntek.dropandhold.game.config

/**
 * Sensor input and platform control parameters.
 */
object ControlConfig {
    // PD controller (platform response feel)
    const val DEFAULT_SPRING_K = 8f
    const val DEFAULT_DAMPING_D = 4f

    // Sensitivity mapping: game setting 1-10 → multiplier
    const val SENSITIVITY_MIN = 0.3f
    const val SENSITIVITY_MAX = 3.0f
    const val SENSITIVITY_STEPS = 9 // 10 levels, 9 steps between them
    const val SENSITIVITY_STEP = (SENSITIVITY_MAX - SENSITIVITY_MIN) / SENSITIVITY_STEPS

    // Sensor calibration

    /** Number of samples to average for neutral position (~1.2s at 20ms interval). */
    const val CALIBRATION_SAMPLES = 60

    // Input filtering

    /** Angles below this are treated as zero (prevents jitter). */
    const val DEAD_ZONE_DEGREES = 2f

    /** Low-pass filter factor (0 = no change, 1 = instant). Lower = smoother. */
    const val SMOOTHING_FACTOR = 0.15f

    /** Clamp tilt output to this range. */
    const val MAX_TILT_DEGREES = 60f
}

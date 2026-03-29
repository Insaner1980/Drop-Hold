package com.finntek.dropandhold.game.config

/**
 * All physics-related constants in one place.
 * Changing these values tunes the "feel" of the game.
 */
object PhysicsConfig {
    // World
    const val GRAVITY = -9.81f
    const val FIXED_TIMESTEP = 1f / 60f
    const val MAX_SUBSTEPS = 5

    // Platform geometry
    const val PLATFORM_RADIUS = 3f
    const val PLATFORM_HALF_HEIGHT = 0.15f
    const val PLATFORM_Y = 0f
    const val PLATFORM_MASS = 5f
    const val PLATFORM_FRICTION = 0.6f
    const val PLATFORM_RESTITUTION = 0.1f

    /** Max tilt allowed by constraint (degrees). Beyond this Bullet locks the axis. */
    const val PLATFORM_MAX_CONSTRAINT_DEGREES = 80f

    // Ground catch-plane (invisible, destroys fallen objects)
    const val GROUND_Y = -20f
    const val GROUND_HALF_EXTENT = 50f

    // Object spawning position
    const val SPAWN_Y = 6f
    const val SPAWN_OFFSET_RANGE = 0.5f // fraction of platform radius

    /** Objects below this Y are considered fallen and get removed. */
    const val FALL_THRESHOLD_Y = -5f

    // Collision user values for identification
    const val PLATFORM_USER_VALUE = 1
    const val GROUND_USER_VALUE = 2
    const val FIRST_OBJECT_ID = 100

    // Material properties
    object Wood {
        const val MASS = 0.5f
        const val FRICTION = 0.6f
        const val RESTITUTION = 0.2f
    }

    object Metal {
        const val MASS = 1.5f
        const val FRICTION = 0.3f
        const val RESTITUTION = 0.3f
    }

    object Stone {
        const val MASS = 2.0f
        const val FRICTION = 0.5f
        const val RESTITUTION = 0.1f
    }

    /** Weight multiplier ramp: objects get heavier over time. */
    const val WEIGHT_RAMP_SECONDS = 60f
    const val WEIGHT_RAMP_MAX = 2f
}

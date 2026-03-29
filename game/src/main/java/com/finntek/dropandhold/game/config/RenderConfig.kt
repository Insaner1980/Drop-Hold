package com.finntek.dropandhold.game.config

import com.badlogic.gdx.graphics.Color

/**
 * All rendering constants: colors, camera, lighting, object sizes.
 */
object RenderConfig {
    // Camera
    const val CAMERA_FOV = 60f
    const val CAMERA_X = 0f
    const val CAMERA_Y = 8f
    const val CAMERA_Z = 10f
    const val CAMERA_LOOK_X = 0f
    const val CAMERA_LOOK_Y = 0f
    const val CAMERA_LOOK_Z = 0f
    const val CAMERA_NEAR = 0.1f
    const val CAMERA_FAR = 100f

    // Mesh detail
    const val PLATFORM_SEGMENTS = 32
    const val SPHERE_DIVISIONS = 16

    // Sky / clear color (Skyfall theme default)
    val SKY_COLOR = Color(0.53f, 0.81f, 0.92f, 1f)

    // Lighting
    val AMBIENT_COLOR = Color(0.4f, 0.4f, 0.4f, 1f)

    object MainLight {
        const val R = 0.8f
        const val G = 0.8f
        const val B = 0.8f
        const val DIR_X = -1f
        const val DIR_Y = -0.8f
        const val DIR_Z = -0.2f
    }

    object FillLight {
        const val R = 0.3f
        const val G = 0.3f
        const val B = 0.4f
        const val DIR_X = 1f
        const val DIR_Y = -0.5f
        const val DIR_Z = 0.5f
    }

    // Material colors
    val PLATFORM_COLOR = Color(0.91f, 0.88f, 0.85f, 1f) // warm marble
    val WOOD_COLOR = Color(0.72f, 0.53f, 0.34f, 1f)
    val METAL_COLOR = Color(0.7f, 0.7f, 0.75f, 1f)
    val STONE_COLOR = Color(0.55f, 0.55f, 0.53f, 1f)

    // Object visual sizes (diameter)
    const val WOOD_SPHERE_SIZE = 0.6f
    const val WOOD_BOX_SIZE = 0.5f
    const val METAL_SPHERE_SIZE = 0.5f
    const val METAL_BOX_SIZE = 0.45f
    const val STONE_SPHERE_SIZE = 0.55f
    const val STONE_BOX_SIZE = 0.5f
}

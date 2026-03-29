package com.finntek.dropandhold.game.physics

import com.finntek.dropandhold.game.config.ControlConfig
import com.finntek.dropandhold.game.input.SensorInputManager

/**
 * Reads sensor tilt values and applies torque to the platform via PD controller.
 */
class PlatformController(
    private val physicsWorld: PhysicsWorld,
    private val sensorInput: SensorInputManager,
) {
    var springK: Float = ControlConfig.DEFAULT_SPRING_K
    var dampingD: Float = ControlConfig.DEFAULT_DAMPING_D

    fun setSensitivity(level: Int) {
        val clamped = level.coerceIn(1, 10)
        sensorInput.sensitivity = ControlConfig.SENSITIVITY_MIN + (clamped - 1) * ControlConfig.SENSITIVITY_STEP
    }

    fun update() {
        physicsWorld.applyPlatformTorque(
            targetPitch = sensorInput.tiltPitch,
            targetRoll = sensorInput.tiltRoll,
            springK = springK,
            dampingD = dampingD,
        )
    }
}

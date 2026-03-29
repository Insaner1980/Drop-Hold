package com.finntek.dropandhold.game.input

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.badlogic.gdx.math.MathUtils
import com.finntek.dropandhold.game.config.ControlConfig

/**
 * Manages gyroscope/accelerometer input for platform tilt control.
 *
 * Priority: TYPE_GAME_ROTATION_VECTOR → TYPE_ROTATION_VECTOR → TYPE_ACCELEROMETER
 * Outputs filtered pitch/roll angles in degrees relative to calibrated neutral.
 */
class SensorInputManager(
    context: Context,
) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorType: Int
    private val sensor: Sensor?

    var tiltPitch: Float = 0f
        private set
    var tiltRoll: Float = 0f
        private set

    // Calibration
    private var isCalibrating = false
    private var calibrationSamples = 0
    private val calibrationRotation = FloatArray(9)
    private val neutralRotation = FloatArray(9)
    private val inverseNeutral = FloatArray(9)
    private var isCalibrated = false

    var sensitivity: Float = 1f
    var deadZoneDegrees: Float = ControlConfig.DEAD_ZONE_DEGREES

    private var filteredPitch = 0f
    private var filteredRoll = 0f

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private val relativeRotation = FloatArray(9)

    init {
        val gameRotation = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        val rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        when {
            gameRotation != null -> {
                sensorType = Sensor.TYPE_GAME_ROTATION_VECTOR
                sensor = gameRotation
            }

            rotation != null -> {
                sensorType = Sensor.TYPE_ROTATION_VECTOR
                sensor = rotation
            }

            accelerometer != null -> {
                sensorType = Sensor.TYPE_ACCELEROMETER
                sensor = accelerometer
            }

            else -> {
                sensorType = -1
                sensor = null
            }
        }
    }

    fun register() {
        sensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    fun unregister() {
        sensorManager.unregisterListener(this)
    }

    fun startCalibration() {
        isCalibrating = true
        isCalibrated = false
        calibrationSamples = 0
        calibrationRotation.fill(0f)
        filteredPitch = 0f
        filteredRoll = 0f
        tiltPitch = 0f
        tiltRoll = 0f
    }

    val calibrationProgress: Float
        get() =
            when {
                isCalibrating -> (calibrationSamples.toFloat() / ControlConfig.CALIBRATION_SAMPLES).coerceIn(0f, 1f)
                isCalibrated -> 1f
                else -> 0f
            }

    override fun onSensorChanged(event: SensorEvent) {
        when (sensorType) {
            Sensor.TYPE_GAME_ROTATION_VECTOR, Sensor.TYPE_ROTATION_VECTOR -> handleRotationVector(event)
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event)
        }
    }

    private fun handleRotationVector(event: SensorEvent) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        if (isCalibrating) {
            accumulateCalibration(rotationMatrix)
            return
        }
        if (!isCalibrated) return

        multiplyMatrices3x3(inverseNeutral, rotationMatrix, relativeRotation)
        SensorManager.getOrientation(relativeRotation, orientationAngles)

        applyFiltering(
            Math.toDegrees(orientationAngles[1].toDouble()).toFloat(),
            Math.toDegrees(orientationAngles[2].toDouble()).toFloat(),
        )
    }

    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        if (isCalibrating) {
            if (SensorManager.getRotationMatrix(rotationMatrix, null, event.values, null)) {
                accumulateCalibration(rotationMatrix)
            } else {
                calibrationSamples++
                if (calibrationSamples >= ControlConfig.CALIBRATION_SAMPLES) {
                    isCalibrating = false
                    isCalibrated = true
                }
            }
            return
        }
        if (!isCalibrated) return

        applyFiltering(
            Math.toDegrees(Math.atan2(x.toDouble(), Math.sqrt((y * y + z * z).toDouble()))).toFloat(),
            Math.toDegrees(Math.atan2(y.toDouble(), Math.sqrt((x * x + z * z).toDouble()))).toFloat(),
        )
    }

    private fun accumulateCalibration(rotation: FloatArray) {
        for (i in rotation.indices) calibrationRotation[i] += rotation[i]
        calibrationSamples++
        if (calibrationSamples >= ControlConfig.CALIBRATION_SAMPLES) {
            for (i in calibrationRotation.indices) neutralRotation[i] = calibrationRotation[i] / calibrationSamples
            transposeMatrix3x3(neutralRotation, inverseNeutral)
            isCalibrating = false
            isCalibrated = true
        }
    }

    private fun applyFiltering(
        rawPitch: Float,
        rawRoll: Float,
    ) {
        val pitch = if (Math.abs(rawPitch) < deadZoneDegrees) 0f else rawPitch
        val roll = if (Math.abs(rawRoll) < deadZoneDegrees) 0f else rawRoll

        filteredPitch += ControlConfig.SMOOTHING_FACTOR * (pitch - filteredPitch)
        filteredRoll += ControlConfig.SMOOTHING_FACTOR * (roll - filteredRoll)

        tiltPitch = MathUtils.clamp(filteredPitch * sensitivity, -ControlConfig.MAX_TILT_DEGREES, ControlConfig.MAX_TILT_DEGREES)
        tiltRoll = MathUtils.clamp(filteredRoll * sensitivity, -ControlConfig.MAX_TILT_DEGREES, ControlConfig.MAX_TILT_DEGREES)
    }

    override fun onAccuracyChanged(
        sensor: Sensor,
        accuracy: Int,
    ) {
        // Not used — sensor accuracy changes don't affect tilt calculation
    }

    companion object {
        private fun multiplyMatrices3x3(
            a: FloatArray,
            b: FloatArray,
            result: FloatArray,
        ) {
            for (row in 0..2) {
                for (col in 0..2) {
                    result[row * 3 + col] = a[row * 3] * b[col] + a[row * 3 + 1] * b[3 + col] + a[row * 3 + 2] * b[6 + col]
                }
            }
        }

        private fun transposeMatrix3x3(
            src: FloatArray,
            dst: FloatArray,
        ) {
            for (row in 0..2) for (col in 0..2) dst[col * 3 + row] = src[row * 3 + col]
        }
    }
}

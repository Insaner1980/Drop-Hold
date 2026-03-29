package com.finntek.dropandhold.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.finntek.dropandhold.game.input.SensorInputManager

/**
 * Android host for the LibGDX game. Launched from Compose UI via Intent,
 * returns GameResult via ActivityResult.
 */
class GameActivity : AndroidApplication() {
    private lateinit var sensorInput: SensorInputManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorInput = SensorInputManager(this)

        val config =
            AndroidApplicationConfiguration().apply {
                useAccelerometer = false // We handle sensors ourselves
                useGyroscope = false
                useCompass = false
                numSamples = 2 // MSAA
            }

        val game = DropAndHoldGame(sensorInput)
        initialize(game, config)
    }

    override fun onResume() {
        super.onResume()
        if (::sensorInput.isInitialized) {
            sensorInput.register()
        }
    }

    override fun onPause() {
        if (::sensorInput.isInitialized) {
            sensorInput.unregister()
        }
        super.onPause()
    }
}

package com.finnvek.dropandhold.game

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.finnvek.dropandhold.game.input.SensorInputManager
import com.finnvek.dropandhold.game.model.GameResult

/**
 * Android host for the LibGDX game. Launched from Compose UI via Intent
 * (use [createIntent]); returns GameResult via [setResult] when the game ends.
 */
class GameActivity : AndroidApplication() {
    private lateinit var sensorInput: SensorInputManager
    private lateinit var game: DropAndHoldGame
    private var resultDelivered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorInput = SensorInputManager(this)
        val mode = intent.getStringExtra(EXTRA_MODE) ?: GameMode.CLASSIC

        val config =
            AndroidApplicationConfiguration().apply {
                useAccelerometer = false
                useGyroscope = false
                useCompass = false
                numSamples = 2
            }

        game =
            DropAndHoldGame(sensorInput, mode).also {
                it.onGameEnded = ::deliverResult
            }
        initialize(game, config)
    }

    private fun deliverResult(result: GameResult) {
        if (resultDelivered) return
        resultDelivered = true
        val data =
            Intent().apply {
                putExtra(RESULT_GAME_RESULT, result)
            }
        setResult(Activity.RESULT_OK, data)
        runOnUiThread { finish() }
    }

    override fun onResume() {
        super.onResume()
        if (::sensorInput.isInitialized) sensorInput.register()
    }

    override fun onPause() {
        if (::sensorInput.isInitialized) sensorInput.unregister()
        super.onPause()
    }

    companion object {
        const val EXTRA_MODE = "extra_game_mode"
        const val RESULT_GAME_RESULT = "result_game_result"

        fun createIntent(
            context: Context,
            mode: String,
        ): Intent =
            Intent(context, GameActivity::class.java).apply {
                putExtra(EXTRA_MODE, mode)
            }

        @Suppress("DEPRECATION")
        fun parseResult(data: Intent?): GameResult? =
            if (data == null) {
                null
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data.getParcelableExtra(RESULT_GAME_RESULT, GameResult::class.java)
            } else {
                data.getParcelableExtra(RESULT_GAME_RESULT)
            }
    }
}

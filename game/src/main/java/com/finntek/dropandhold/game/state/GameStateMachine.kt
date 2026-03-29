package com.finntek.dropandhold.game.state

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.finntek.dropandhold.game.DropAndHoldGame
import com.finntek.dropandhold.game.config.GameplayConfig

class GameStateMachine(
    private val game: DropAndHoldGame,
) : Disposable {
    var currentState: GameState = GameState.INITIALIZING
        private set

    private var countdownTimer = 0f
    private var timeSinceLastObjectOnPlatform = 0f

    fun transition(newState: GameState) {
        Gdx.app?.log("GameState", "${currentState.name} → ${newState.name}")
        currentState = newState
        when (newState) {
            GameState.CALIBRATING -> {
                game.sensorInput.startCalibration()
            }

            GameState.COUNTDOWN -> {
                countdownTimer = GameplayConfig.COUNTDOWN_DURATION
            }

            GameState.PLAYING -> {
                game.resetGameState()
                timeSinceLastObjectOnPlatform = 0f
            }

            else -> {}
        }
    }

    fun update(delta: Float) {
        when (currentState) {
            GameState.INITIALIZING -> {
                transition(GameState.CALIBRATING)
            }

            GameState.CALIBRATING -> {
                if (game.sensorInput.calibrationProgress >= 1f) transition(GameState.COUNTDOWN)
            }

            GameState.COUNTDOWN -> {
                countdownTimer -= delta
                if (countdownTimer <= 0f) transition(GameState.PLAYING)
            }

            GameState.PLAYING -> {
                game.platformController.update()
                game.physicsWorld.step(delta)
                game.objectSpawner.update(delta)

                val fallen = game.objectSpawner.cleanupFallen()
                if (fallen > 0) game.onObjectsLost(fallen)

                game.updateScoring(delta)

                if (checkGameOver(delta)) transition(GameState.GAME_OVER)
            }

            GameState.PAUSED -> {}

            GameState.GAME_OVER -> {}
        }
    }

    private fun checkGameOver(delta: Float): Boolean {
        if (game.physicsWorld.isFlipped(GameplayConfig.FLIP_THRESHOLD_DEGREES)) return true

        if (game.physicsWorld.activeObjectCount == 0 && game.elapsedTime > GameplayConfig.MIN_ELAPSED_BEFORE_EMPTY_CHECK) {
            timeSinceLastObjectOnPlatform += delta
            if (timeSinceLastObjectOnPlatform > GameplayConfig.EMPTY_PLATFORM_TIMEOUT) return true
        } else {
            timeSinceLastObjectOnPlatform = 0f
        }
        return false
    }

    override fun dispose() {}
}

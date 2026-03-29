package com.finntek.dropandhold.game.mode

/**
 * Strategy pattern for different game modes.
 * Each mode defines its own spawning rules, game-over conditions, and scoring.
 */
interface GameModeStrategy {
    val config: GameModeConfig

    /** Check whether the game should end. */
    fun checkGameOver(context: GameModeContext): Boolean

    /** Get the drop interval at the given elapsed time. */
    fun getDropInterval(elapsedSeconds: Float): Float
}

data class GameModeConfig(
    val name: String,
    val initialDropInterval: Float,
    val minDropInterval: Float,
    val hasTimeLimit: Boolean = false,
    val timeLimitSeconds: Float = 0f,
    val canGameOver: Boolean = true,
    val scoreMultiplier: Float = 1f,
)

data class GameModeContext(
    val elapsedSeconds: Float,
    val platformTiltAngle: Float,
    val activeObjectCount: Int,
    val objectsLostTotal: Int,
    val timeSinceLastObject: Float,
)

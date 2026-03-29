package com.finntek.dropandhold.game.mode

class ClassicModeStrategy : GameModeStrategy {
    override val config =
        GameModeConfig(
            name = "Classic",
            initialDropInterval = 3.0f,
            minDropInterval = 0.8f,
        )

    override fun checkGameOver(context: GameModeContext): Boolean {
        // Flip: platform tilt exceeds 70 degrees
        if (context.platformTiltAngle > 70f) return true
        // Empty platform for too long after objects started dropping
        if (context.activeObjectCount == 0 && context.elapsedSeconds > 5f && context.timeSinceLastObject > 3f) return true
        return false
    }

    override fun getDropInterval(elapsedSeconds: Float): Float {
        // Decrease by 0.05s every 10 seconds, clamped to minimum
        val reduction = (elapsedSeconds / 10f).toInt() * 0.05f
        return (config.initialDropInterval - reduction).coerceAtLeast(config.minDropInterval)
    }
}

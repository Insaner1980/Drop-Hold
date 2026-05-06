package com.finnvek.dropandhold.game

/**
 * Game mode identifiers passed via Intent extra and returned in GameResult.
 * Strategies are looked up from this string ID at runtime.
 */
object GameMode {
    const val CLASSIC = "classic"
    const val DAILY = "daily"
    const val QUICK = "quick"
    const val ZEN = "zen"
    const val EXTREME = "extreme"
    const val CHALLENGE = "challenge"
}

package com.finnvek.dropandhold.domain

import com.finnvek.dropandhold.game.model.GameResult

/**
 * State emitted by PlayViewModel to PlayScreen.
 */
data class PlayUiState(
    val bestScore: Int = 0,
    val bestSimultaneous: Int = 0,
    val totalRuns: Int = 0,
    val lastResult: GameResult? = null,
)

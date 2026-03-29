package com.finntek.dropandhold.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Returned from GameActivity to Compose via ActivityResult.
 */
@Parcelize
data class GameResult(
    val score: Int,
    val bestScore: Int,
    val timeSeconds: Float,
    val objectsBalanced: Int,
    val maxSimultaneous: Int,
    val bestChainSeconds: Float,
    val shardsEarned: Int,
    val isNewBest: Boolean,
    val mode: String,
    val achievementsUnlocked: List<String>,
) : Parcelable

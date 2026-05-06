package com.finnvek.dropandhold.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finnvek.dropandhold.data.db.dao.RunDao
import com.finnvek.dropandhold.data.db.entity.RunEntity
import com.finnvek.dropandhold.domain.PlayUiState
import com.finnvek.dropandhold.game.model.GameResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayViewModel
    @Inject
    constructor(
        private val runDao: RunDao,
    ) : ViewModel() {
        private val lastResult = MutableStateFlow<GameResult?>(null)

        val uiState: StateFlow<PlayUiState> =
            combine(
                runDao.bestScoreOverall(),
                runDao.bestSimultaneousObjects(),
                runDao.totalRunCount(),
                lastResult,
            ) { best, simul, runs, result ->
                PlayUiState(
                    bestScore = best ?: 0,
                    bestSimultaneous = simul ?: 0,
                    totalRuns = runs,
                    lastResult = result,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), PlayUiState())

        /**
         * Persist a finished run to Room and surface it as the latest result for
         * the Game Over bottom sheet. Determines isNewBest from current best score.
         */
        fun onGameFinished(result: GameResult) {
            viewModelScope.launch {
                val currentBest = uiState.value.bestScore
                val isNewBest = result.score > currentBest
                val enriched = result.copy(isNewBest = isNewBest, bestScore = maxOf(currentBest, result.score))
                runDao.insertRun(
                    RunEntity(
                        mode = enriched.mode,
                        score = enriched.score,
                        durationSeconds = enriched.timeSeconds,
                        objectsBalanced = enriched.objectsBalanced,
                        maxSimultaneous = enriched.maxSimultaneous,
                        bestChainSeconds = enriched.bestChainSeconds,
                        shardsEarned = enriched.shardsEarned,
                    ),
                )
                lastResult.value = enriched
            }
        }

        fun dismissResult() {
            lastResult.update { null }
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }

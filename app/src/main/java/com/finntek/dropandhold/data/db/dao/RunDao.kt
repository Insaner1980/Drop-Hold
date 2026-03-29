package com.finntek.dropandhold.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.finntek.dropandhold.data.db.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {
    @Query("SELECT MAX(score) FROM runs WHERE mode = :mode")
    fun bestScoreForMode(mode: String): Flow<Int?>

    @Query("SELECT MAX(score) FROM runs")
    fun bestScoreOverall(): Flow<Int?>

    @Query("SELECT MAX(maxSimultaneous) FROM runs")
    fun bestSimultaneousObjects(): Flow<Int?>

    @Query("SELECT * FROM runs ORDER BY timestamp DESC LIMIT 30")
    fun recentRuns(): Flow<List<RunEntity>>

    @Query("SELECT COUNT(*) FROM runs")
    fun totalRunCount(): Flow<Int>

    @Insert
    suspend fun insertRun(run: RunEntity)
}

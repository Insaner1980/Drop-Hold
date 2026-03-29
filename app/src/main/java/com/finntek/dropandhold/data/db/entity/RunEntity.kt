package com.finntek.dropandhold.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "runs")
data class RunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val score: Int,
    val durationSeconds: Float,
    val objectsBalanced: Int,
    val maxSimultaneous: Int,
    val bestChainSeconds: Float,
    val shardsEarned: Int,
    val timestamp: Long = System.currentTimeMillis(),
)

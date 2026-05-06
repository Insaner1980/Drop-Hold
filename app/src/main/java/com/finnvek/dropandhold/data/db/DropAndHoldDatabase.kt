package com.finnvek.dropandhold.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finnvek.dropandhold.data.db.dao.RunDao
import com.finnvek.dropandhold.data.db.entity.RunEntity

@Database(
    entities = [RunEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class DropAndHoldDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao
}

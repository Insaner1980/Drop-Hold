package com.finnvek.dropandhold.di

import android.content.Context
import androidx.room.Room
import com.finnvek.dropandhold.data.db.DropAndHoldDatabase
import com.finnvek.dropandhold.data.db.dao.RunDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): DropAndHoldDatabase =
        Room
            .databaseBuilder(
                context,
                DropAndHoldDatabase::class.java,
                context.getString(com.finnvek.dropandhold.R.string.db_name),
            ).build()

    @Provides
    fun provideRunDao(database: DropAndHoldDatabase): RunDao = database.runDao()
}

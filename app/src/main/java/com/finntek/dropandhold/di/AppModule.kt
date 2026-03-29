package com.finntek.dropandhold.di

import android.content.Context
import androidx.room.Room
import com.finntek.dropandhold.data.db.DropAndHoldDatabase
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
                context.getString(com.finntek.dropandhold.R.string.db_name),
            ).build()
}

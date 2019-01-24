package com.citymapper.codingchallenge.common

import android.content.Context
import androidx.room.Room
import com.citymapper.codingchallenge.stoppoints.StopPointDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideEngieDatabase(context: Context): StopPointDatabase =
        Room.databaseBuilder(context, StopPointDatabase::class.java, "citymapper-database")
            .fallbackToDestructiveMigration()
            .build()
}

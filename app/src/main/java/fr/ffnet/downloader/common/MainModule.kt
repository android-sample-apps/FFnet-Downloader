package fr.ffnet.downloader.common

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.room.Room
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import javax.inject.Singleton

@Module
class MainModule(private val application: MainApplication) {

    @Singleton
    @Provides
    fun provideContext(): Context = application

    @Singleton
    @Provides
    fun provideResources(context: Context): Resources = context.resources

    @Singleton
    @Provides
    fun provideFanfictionDatabase(context: Context): FanfictionDownloaderDatabase {
        return Room.databaseBuilder(
            context, FanfictionDownloaderDatabase::class.java, "fanfiction-db"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("ffnetdownloader", Context.MODE_PRIVATE)
    }
}

package fr.ffnet.downloader.common

import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import javax.inject.Singleton

@Module
class ApplicationMainModule {
    @Singleton
    @Provides
    fun provideContext(application: MainApplication): Context = application

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
}

package fr.ffnet.downloader.common

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.room.Room
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.RecipeDownloaderDatabase
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
    fun provideFanfictionDatabase(context: Context): RecipeDownloaderDatabase {
        return Room.databaseBuilder(
            context, RecipeDownloaderDatabase::class.java, "recipe-db"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("ffnetdownloader", Context.MODE_PRIVATE)
    }
}

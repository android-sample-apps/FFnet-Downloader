package fr.ffnet.downloader.common

import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class MainModule(private val context: Context) {
    @Singleton
    @Provides
    fun provideBackgroundExecutor(): Executor = Executors.newFixedThreadPool(1)

    @Singleton
    @Provides
    fun provideHandlerExecutor(): MainThreadExecutor = MainThreadExecutor()

    @Provides
    fun providesContext(): Context = context

    @Provides
    fun providesResources(): Resources = context.resources

    @Singleton
    @Provides
    fun provideFanfictionDatabase(context: Context): FanfictionDownloaderDatabase {
        return Room.databaseBuilder(context, FanfictionDownloaderDatabase::class.java, "fanfiction-db")
                .fallbackToDestructiveMigration()
                .build()
    }
}

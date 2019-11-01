package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.RecipeDownloaderDatabase
import fr.ffnet.downloader.fanfictionutils.RecipeBuilder
import fr.ffnet.downloader.repository.dao.RecipeDao
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun provideRecipeDao(database: RecipeDownloaderDatabase): RecipeDao =
        database.recipeDao()

    @Provides
    fun provideCrawlService(retrofit: Retrofit): CrawlService = retrofit.create(
        CrawlService::class.java
    )

    @Provides
    fun provideDatabaseRepository(dao: RecipeDao): DatabaseRepository = DatabaseRepository(dao)

    @Provides
    @Singleton
    fun provideDownloaderRepository(
        service: CrawlService,
        recipeDao: RecipeDao,
        recipeBuilder: RecipeBuilder,
        context: Context,
        workerFactory: DownloaderWorkerFactory
    ): DownloaderRepository {
        val configuration = Configuration.Builder().setWorkerFactory(workerFactory).build()
        WorkManager.initialize(context, configuration)
        return DownloaderRepository(
            service,
            recipeBuilder,
            recipeDao,
            WorkManager.getInstance(context)
        )
    }

    @Provides
    @Singleton
    fun provideWorkerFactory(
        service: CrawlService,
        recipeBuilder: RecipeBuilder,
        recipeDao: RecipeDao
    ): DownloaderWorkerFactory {
        return DownloaderWorkerFactory(service, recipeBuilder, recipeDao)
    }
}

package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionConverter
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.fanfictionutils.ProfileBuilder
import fr.ffnet.downloader.repository.dao.ErrorDao
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.dao.ProfileDao
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao =
        database.fanfictionDao()

    @Provides
    fun provideProfileDao(database: FanfictionDownloaderDatabase): ProfileDao =
        database.profileDao()

    @Provides
    fun provideErrorDao(database: FanfictionDownloaderDatabase): ErrorDao = database.errorDao()

    @Provides
    fun provideCrawlService(retrofit: Retrofit): CrawlService = retrofit.create(
        CrawlService::class.java
    )

    @Provides
    fun provideErrorRepository(errorDao: ErrorDao): ErrorRepository = ErrorRepository(errorDao)

    @Provides
    fun provideDatabaseRepository(
        dao: FanfictionDao,
        fanfictionConverter: FanfictionConverter
    ): DatabaseRepository = DatabaseRepository(dao, fanfictionConverter)

    @Provides
    @Singleton
    fun provideDownloaderRepository(
        service: CrawlService,
        fanfictionDao: FanfictionDao,
        fanfictionBuilder: FanfictionBuilder,
        fanfictionConverter: FanfictionConverter,
        context: Context,
        workerFactory: DownloaderWorkerFactory
    ): DownloaderRepository {
        val configuration = Configuration.Builder().setWorkerFactory(workerFactory).build()
        WorkManager.initialize(context, configuration)
        return DownloaderRepository(
            service,
            fanfictionBuilder,
            fanfictionDao,
            fanfictionConverter,
            WorkManager.getInstance(context)
        )
    }

    @Provides
    @Singleton
    fun provideWorkerFactory(
        service: CrawlService,
        fanfictionBuilder: FanfictionBuilder,
        fanfictionDao: FanfictionDao
    ): DownloaderWorkerFactory {
        return DownloaderWorkerFactory(service, fanfictionBuilder, fanfictionDao)
    }

    @Provides
    fun provideProfileRepository(
        service: CrawlService,
        profileDao: ProfileDao,
        profileBuilder: ProfileBuilder,
        fanfictionConverter: FanfictionConverter,
        fanfictionDao: FanfictionDao
    ): ProfileRepository = ProfileRepository(
        service,
        profileDao,
        fanfictionDao,
        profileBuilder,
        fanfictionConverter
    )
}

package fr.ffnet.downloader.repository

import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.fanfictionutils.FanfictionTransformer
import fr.ffnet.downloader.fanfictionutils.ProfileBuilder
import fr.ffnet.downloader.repository.dao.ErrorDao
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.dao.ProfileDao
import retrofit2.Retrofit

@Module
class RepositoryModule {

    @Provides
    fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao = database.fanfictionDao()

    @Provides
    fun provideProfileDao(database: FanfictionDownloaderDatabase): ProfileDao = database.profileDao()

    @Provides
    fun provideErrorDao(database: FanfictionDownloaderDatabase): ErrorDao = database.errorDao()

    @Provides
    fun provideCrawlService(retrofit: Retrofit): CrawlService = retrofit.create(
        CrawlService::class.java
    )

    @Provides
    fun provideErrorRepository(errorDao: ErrorDao): ErrorRepository = ErrorRepository(errorDao)

    @Provides
    fun provideDatabaseRepository(dao: FanfictionDao): DatabaseRepository = DatabaseRepository(dao)

    @Provides
    fun provideDownloaderRepository(
        service: CrawlService,
        fanfictionDao: FanfictionDao,
        fanfictionBuilder: FanfictionBuilder,
        fanfictionTransformer: FanfictionTransformer
    ): DownloaderRepository = DownloaderRepository(
        service,
        fanfictionBuilder,
        fanfictionDao,
        fanfictionTransformer
    )

    @Provides
    fun provideProfileRepository(
        service: CrawlService,
        profileDao: ProfileDao,
        profileBuilder: ProfileBuilder,
        fanfictionTransformer: FanfictionTransformer,
        fanfictionDao: FanfictionDao
    ): ProfileRepository = ProfileRepository(
        service,
        profileDao,
        fanfictionDao,
        profileBuilder,
        fanfictionTransformer
    )
}

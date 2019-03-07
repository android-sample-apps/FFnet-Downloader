package fr.ffnet.downloader.repository

import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.fanfictionutils.FanfictionTransformer
import fr.ffnet.downloader.fanfictionutils.ProfileBuilder
import retrofit2.Retrofit

@Module
class RepositoryModule {

    @Provides
    fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao = database.fanfictionDao()

    @Provides
    fun provideProfileDao(database: FanfictionDownloaderDatabase): ProfileDao = database.profileDao()

    @Provides
    fun provideDownloaderService(retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Provides
    fun provideDownloaderRepository(
        service: SearchService,
        fanfictionDao: FanfictionDao,
        fanfictionBuilder: FanfictionBuilder,
        fanfictionTransformer: FanfictionTransformer
    ): DownloaderRepository {
        return DownloaderRepository(
            service,
            fanfictionBuilder,
            fanfictionDao,
            fanfictionTransformer
        )
    }

    @Provides
    fun provideProfileRepository(
        service: SearchService,
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

    @Provides
    fun provideDatabaseRepository(
        dao: FanfictionDao
    ): DatabaseRepository = DatabaseRepository(dao)
}

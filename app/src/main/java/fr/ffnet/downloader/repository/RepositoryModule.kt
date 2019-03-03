package fr.ffnet.downloader.repository

import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.fanfictionutils.ProfileBuilder
import retrofit2.Retrofit

@Module
class RepositoryModule {

    @Provides
    fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao = database.fanfictionDao()

    @Provides
    fun provideHistoryDao(database: FanfictionDownloaderDatabase): HistoryDao = database.historyDao()

    @Provides
    fun provideDownloaderService(retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Provides
    fun provideDownloaderRepository(
        service: SearchService,
        fanfictionDao: FanfictionDao,
        historyDao: HistoryDao,
        fanfictionBuilder: FanfictionBuilder
    ): DownloaderRepository {
        return DownloaderRepository(service, fanfictionBuilder, fanfictionDao, historyDao)
    }

    @Provides
    fun provideProfileRepository(
        service: SearchService,
        profileDao: ProfileDao,
        profileBuilder: ProfileBuilder
    ): ProfileRepository = ProfileRepository(service, profileDao, profileBuilder)

    @Provides
    fun provideDatabaseRepository(
        dao: FanfictionDao
    ): DatabaseRepository = DatabaseRepository(dao)
}

package fr.ffnet.downloader.repository

import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import retrofit2.Retrofit

@Module
class RepositoryModule {
    @Provides
    fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao = database.fanfictionDao()

    @Provides
    fun provideDownloaderService(retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Provides
    fun provideDownloaderRepository(
        service: SearchService,
        dao: FanfictionDao,
        fanfictionBuilder: FanfictionBuilder
    ): DownloaderRepository {
        return DownloaderRepository(service, fanfictionBuilder, dao)
    }
}

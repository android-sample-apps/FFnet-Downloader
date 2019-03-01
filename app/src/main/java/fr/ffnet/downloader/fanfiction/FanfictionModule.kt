package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.common.ActivityScope
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.repository.FanfictionDao
import fr.ffnet.downloader.repository.SearchService
import retrofit2.Retrofit

@Module
class FanfictionModule {

    @ActivityScope
    @Provides
    fun provideSearchViewModel(
        resources: Resources,
        dao: FanfictionDao,
        repository: DownloaderRepository
    ): FanfictionViewModel = FanfictionViewModel(resources, dao, repository)

    @ActivityScope
    @Provides
    fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao = database.fanfictionDao()

    @ActivityScope
    @Provides
    fun provideDownloaderService(retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @ActivityScope
    @Provides
    fun provideDownloaderRepository(
        service: SearchService,
        dao: FanfictionDao,
        fanfictionBuilder: FanfictionBuilder
    ): DownloaderRepository {
        return DownloaderRepository(service, fanfictionBuilder, dao)
    }
}

package fr.ffnet.downloader.downloader

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.utils.UrlTransformer
import retrofit2.Retrofit

@Module
class DownloaderModule {

    @Provides
    fun provideDownloaderViewModel(
        urlTransformer: UrlTransformer,
        resources: Resources,
        dao: FanfictionDao,
        repository: DownloaderRepository
    ): DownloaderViewModel {
        return DownloaderViewModel(
            urlTransformer, resources, dao, repository
        )
    }

    @Provides
    fun provideDownloaderService(retrofit: Retrofit): DownloaderService {
        return retrofit.create(DownloaderService::class.java)
    }

    @Provides
    fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao = database.fanfictionDao()

    @Provides
    fun provideDownloaderRepository(
        service: DownloaderService,
        dao: FanfictionDao,
        fanfictionBuilder: FanfictionBuilder
    ): DownloaderRepository {
        return DownloaderRepository(service, fanfictionBuilder, dao)
    }
}

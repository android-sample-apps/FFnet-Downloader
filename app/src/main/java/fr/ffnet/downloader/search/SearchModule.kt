package fr.ffnet.downloader.search

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.FanfictionDownloaderDatabase
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.utils.UrlTransformer
import retrofit2.Retrofit

@Module
abstract class SearchModule {
    @Module
    companion object {
        @JvmStatic
        @FragmentScope
        @Provides
        fun provideSearchViewModel(
            urlTransformer: UrlTransformer,
            resources: Resources,
            dao: FanfictionDao,
            repository: DownloaderRepository
        ): SearchViewModel = SearchViewModel(urlTransformer, resources, dao, repository)

        @JvmStatic
        @FragmentScope
        @Provides
        fun provideDownloaderService(retrofit: Retrofit): SearchService {
            return retrofit.create(SearchService::class.java)
        }

        @JvmStatic
        @FragmentScope
        @Provides
        fun provideFanfictionDao(database: FanfictionDownloaderDatabase): FanfictionDao = database.fanfictionDao()

        @JvmStatic
        @FragmentScope
        @Provides
        fun provideDownloaderRepository(
            service: SearchService,
            dao: FanfictionDao,
            fanfictionBuilder: FanfictionBuilder
        ): DownloaderRepository {
            return DownloaderRepository(service, fanfictionBuilder, dao)
        }
    }
}

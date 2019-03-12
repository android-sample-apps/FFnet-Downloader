package fr.ffnet.downloader.search

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.ErrorRepository
import fr.ffnet.downloader.utils.DateFormatter

@Module
class SearchModule {

    @FragmentScope
    @Provides
    fun provideSearchViewModel(
        urlTransformer: UrlTransformer,
        resources: Resources,
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository,
        errorRepository: ErrorRepository,
        dateFormatter: DateFormatter
    ): SearchViewModel = SearchViewModel(
        urlTransformer,
        resources,
        apiRepository,
        dbRepository,
        errorRepository,
        dateFormatter
    )
}

package fr.ffnet.downloader.search

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter

@Module
class SearchModule {
    @Provides
    fun provideSearchViewModel(
        fragment: SearchFragment,
        viewModelCreator: () -> SearchViewModel
    ): SearchViewModel {
        val factory = SearchViewModelFactory(viewModelCreator)
        return ViewModelProvider(fragment.viewModelStore, factory)[SearchViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        urlTransformer: UrlTransformer,
        resources: Resources,
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository,
        dateFormatter: DateFormatter
    ): () -> SearchViewModel = {
        SearchViewModel(
            urlTransformer,
            resources,
            apiRepository,
            dbRepository,
            dateFormatter
        )
    }
}

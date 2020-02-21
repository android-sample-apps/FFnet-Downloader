package fr.ffnet.downloader.search

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class SearchModule {
    @Provides
    fun provideViewModel(
        fragment: SearchFragment,
        urlTransformer: UrlTransformer,
        resources: Resources,
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository,
        dateFormatter: DateFormatter
    ): SearchViewModel {
        val factory = ViewModelFactory {
            SearchViewModel(
                urlTransformer,
                resources,
                apiRepository,
                dbRepository,
                dateFormatter
            )
        }
        return ViewModelProvider(fragment, factory)[SearchViewModel::class.java]
    }
}

package fr.ffnet.downloader.search.injection

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.search.SearchViewModel
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class SearchModule(private val fragment: Fragment) {

    @Provides
    fun provideViewModel(
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

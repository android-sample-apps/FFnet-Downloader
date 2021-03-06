package fr.ffnet.downloader.search.injection

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionoptions.OptionsViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.SearchRepository
import fr.ffnet.downloader.search.SearchFragment
import fr.ffnet.downloader.search.SearchViewModel
import fr.ffnet.downloader.synced.OptionsController
import fr.ffnet.downloader.synced.SyncedViewModel
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class SearchModule(private val fragment: SearchFragment) {

    @Provides
    fun provideSearchViewModel(
        urlTransformer: UrlTransformer,
        resources: Resources,
        searchRepository: SearchRepository,
        dbRepository: DatabaseRepository,
        dateFormatter: DateFormatter,
        UIBuilder: UIBuilder
    ): SearchViewModel {
        val factory = ViewModelFactory {
            SearchViewModel(
                urlTransformer,
                resources,
                searchRepository,
                dbRepository,
                dateFormatter,
                UIBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[SearchViewModel::class.java]
    }

    @Provides
    fun provideSyncedViewModel(
        resources: Resources,
        downloaderRepository: DownloaderRepository,
        databaseRepository: DatabaseRepository,
        UIBuilder: UIBuilder
    ): SyncedViewModel {
        val factory = ViewModelFactory {
            SyncedViewModel(
                resources,
                downloaderRepository,
                databaseRepository,
                UIBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[SyncedViewModel::class.java]
    }

    @Provides
    fun provideFanfictionOpener(): FanfictionOpener = FanfictionOpener(fragment.requireContext())

    @Provides
    fun provideOptionsViewModel(
        resources: Resources,
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository,
        pdfBuilder: PdfBuilder,
        epubBuilder: EpubBuilder
    ): OptionsViewModel {
        val factory = ViewModelFactory {
            OptionsViewModel(
                resources = resources,
                databaseRepository = databaseRepository,
                downloaderRepository = downloaderRepository,
                pdfBuilder = pdfBuilder,
                epubBuilder = epubBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[OptionsViewModel::class.java]
    }

    @Provides
    fun provideOptionsController(
        optionsViewModel: OptionsViewModel,
        fanfictionOpener: FanfictionOpener
    ): OptionsController {
        return OptionsController(
            context = fragment.requireContext(),
            lifecycleOwner = fragment.viewLifecycleOwner,
            parentListener = fragment,
            optionsViewModel = optionsViewModel,
            fanfictionOpener = fanfictionOpener
        )
    }
}

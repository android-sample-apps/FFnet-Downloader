package fr.ffnet.downloader.synced.injection

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionoptions.OptionsViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.synced.OptionsController
import fr.ffnet.downloader.synced.SyncedFragment
import fr.ffnet.downloader.synced.SyncedViewModel
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.FanfictionUIBuilder
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class SyncedModule(private val fragment: SyncedFragment) {

    @Provides
    fun provideSyncedViewModel(
        downloaderRepository: DownloaderRepository,
        databaseRepository: DatabaseRepository,
        fanfictionUIBuilder: FanfictionUIBuilder
    ): SyncedViewModel {
        val factory = ViewModelFactory {
            SyncedViewModel(
                downloaderRepository,
                databaseRepository,
                fanfictionUIBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[SyncedViewModel::class.java]
    }

    @Provides
    fun provideFanfictionOpener(): FanfictionOpener = FanfictionOpener(fragment.requireContext())

    @Provides
    fun provideOptionsViewModel(
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository,
        pdfBuilder: PdfBuilder,
        epubBuilder: EpubBuilder
    ): OptionsViewModel {
        val factory = ViewModelFactory {
            OptionsViewModel(
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
            permissionListener = fragment,
            optionsViewModel = optionsViewModel,
            fanfictionOpener = fanfictionOpener
        )
    }
}

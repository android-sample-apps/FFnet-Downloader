package fr.ffnet.downloader.fanfictionoptions

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionutils.EpubBuilder
import fr.ffnet.downloader.fanfictionutils.PdfBuilder
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter

@Module
class OptionsModule {
    @Provides
    fun provideOptionsViewModel(
        fragment: OptionsFragment,
        viewModelCreator: () -> OptionsViewModel
    ): OptionsViewModel {
        val factory = OptionsViewModelFactory(viewModelCreator)
        return ViewModelProvider(fragment.viewModelStore, factory)[OptionsViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        databaseRepository: DatabaseRepository,
        dateFormatter: DateFormatter,
        downloaderRepository: DownloaderRepository,
        pdfBuilder: PdfBuilder,
        epubBuilder: EpubBuilder
    ): () -> OptionsViewModel = {
        OptionsViewModel(
            databaseRepository,
            dateFormatter,
            downloaderRepository,
            pdfBuilder,
            epubBuilder
        )
    }
}

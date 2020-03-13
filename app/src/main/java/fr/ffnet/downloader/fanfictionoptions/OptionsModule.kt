package fr.ffnet.downloader.fanfictionoptions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionutils.EpubBuilder
import fr.ffnet.downloader.fanfictionutils.PdfBuilder
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class OptionsModule(private val fragment: Fragment) {

    @Provides
    fun provideOptionsViewModel(
        databaseRepository: DatabaseRepository,
        dateFormatter: DateFormatter,
        downloaderRepository: DownloaderRepository,
        pdfBuilder: PdfBuilder,
        epubBuilder: EpubBuilder
    ): OptionsViewModel {
        val factory = ViewModelFactory {
            OptionsViewModel(
                databaseRepository = databaseRepository,
                dateFormatter = dateFormatter,
                downloaderRepository = downloaderRepository,
                pdfBuilder = pdfBuilder,
                epubBuilder = epubBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[OptionsViewModel::class.java]
    }
}

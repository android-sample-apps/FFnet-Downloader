package fr.ffnet.downloader.profile.fanfiction.injection

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionoptions.OptionsViewModel
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.synced.OptionsController
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class ProfileFanfictionModule(private val fragment: Fragment) {
    @Provides
    fun provideProfileFanfictionViewModel(
        resources: Resources,
        databaseRepository: DatabaseRepository,
        dateFormatter: DateFormatter
    ): ProfileFanfictionViewModel {
        val factory = ViewModelFactory {
            ProfileFanfictionViewModel(
                resources,
                databaseRepository,
                dateFormatter
            )
        }
        return ViewModelProvider(fragment, factory)[ProfileFanfictionViewModel::class.java]
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
            fragment,
            optionsViewModel,
            fanfictionOpener
        )
    }
}

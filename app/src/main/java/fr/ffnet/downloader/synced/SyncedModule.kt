package fr.ffnet.downloader.synced

import android.content.res.Resources
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionutils.EpubBuilder
import fr.ffnet.downloader.fanfictionutils.PdfBuilder
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.DateFormatter

@Module
class SyncedModule {
    @Provides
    fun provideSyncedViewModel(
        fragment: SyncedFragment,
        viewModelCreator: () -> SyncedViewModel
    ): SyncedViewModel {
        val factory = SyncedViewModelFactory(viewModelCreator)
        return ViewModelProviders.of(fragment, factory)[SyncedViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        resources: Resources,
        repository: DatabaseRepository,
        dateFormatter: DateFormatter,
        pdfBuilder: PdfBuilder,
        epubBuilder: EpubBuilder
    ): () -> SyncedViewModel = {
        SyncedViewModel(
            resources,
            repository,
            dateFormatter,
            pdfBuilder,
            epubBuilder
        )
    }
}

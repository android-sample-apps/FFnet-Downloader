package fr.ffnet.downloader.fanfiction.info

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter

@Module
class FanfictionInfoModule {

    @Provides
    fun provideFanfictionInfoViewModel(
        fragment: FanfictionInfoFragment,
        viewModelCreator: () -> FanfictionInfoViewModel
    ): FanfictionInfoViewModel {
        val factory = FanfictionInfoViewModelFactory(viewModelCreator)
        return ViewModelProvider(
            fragment.viewModelStore,
            factory
        )[FanfictionInfoViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        resources: Resources,
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository,
        dateFormatter: DateFormatter
    ): () -> FanfictionInfoViewModel = {
        FanfictionInfoViewModel(
            resources,
            apiRepository,
            dbRepository,
            dateFormatter
        )
    }
}

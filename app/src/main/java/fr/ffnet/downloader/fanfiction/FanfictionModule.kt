package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter

@Module
class FanfictionModule {

    @Provides
    fun provideFanfictionViewModel(
        activity: FanfictionActivity,
        viewModelCreator: () -> FanfictionViewModel
    ): FanfictionViewModel {
        val factory = FanfictionViewModelFactory(viewModelCreator)
        return ViewModelProvider(activity.viewModelStore, factory)[FanfictionViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        resources: Resources,
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository,
        dateFormatter: DateFormatter
    ): () -> FanfictionViewModel = {
        FanfictionViewModel(
            resources,
            apiRepository,
            dbRepository,
            dateFormatter
        )
    }
}

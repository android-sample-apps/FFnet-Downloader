package fr.ffnet.downloader.profile.fanfiction

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter

@Module
class ProfileFanfictionModule {
    @Provides
    fun provideProfileFanfictionViewModel(
        fragment: ProfileFanfictionFragment,
        viewModelCreator: () -> ProfileFanfictionViewModel
    ): ProfileFanfictionViewModel {
        val factory = ProfileFanfictionViewModelFactory(viewModelCreator)
        return ViewModelProvider(
            fragment.viewModelStore,
            factory
        )[ProfileFanfictionViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        resources: Resources,
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository,
        dateFormatter: DateFormatter
    ): () -> ProfileFanfictionViewModel = {
        ProfileFanfictionViewModel(
            resources,
            databaseRepository,
            downloaderRepository,
            dateFormatter
        )
    }
}

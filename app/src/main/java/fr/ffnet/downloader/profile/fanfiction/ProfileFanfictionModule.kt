package fr.ffnet.downloader.profile.fanfiction

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository

@Module
class ProfileFanfictionModule {
    @FragmentScope
    @Provides
    fun provideProfileFanfictionViewModel(
        resources: Resources,
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository
    ): ProfileFanfictionViewModel = ProfileFanfictionViewModel(
        resources,
        databaseRepository,
        downloaderRepository
    )

}

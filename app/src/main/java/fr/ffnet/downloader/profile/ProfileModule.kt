package fr.ffnet.downloader.profile

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.repository.RepositoryModule

@Module(includes = [RepositoryModule::class])
class ProfileModule {
    @FragmentScope
    @Provides
    fun provideProfileViewModel(
        urlTransformer: UrlTransformer,
        resources: Resources,
        databaseRepository: DatabaseRepository,
        profileRepository: ProfileRepository,
        downloaderRepository: DownloaderRepository
    ): ProfileViewModel = ProfileViewModel(
        urlTransformer,
        resources,
        databaseRepository,
        profileRepository,
        downloaderRepository
    )
}

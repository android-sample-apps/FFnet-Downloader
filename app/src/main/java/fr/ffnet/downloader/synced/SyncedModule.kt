package fr.ffnet.downloader.synced

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.RepositoryModule

@Module(includes = [RepositoryModule::class])
class SyncedModule {
    @FragmentScope
    @Provides
    fun provideSearchViewModel(
        resources: Resources,
        repository: DownloaderRepository
    ): SyncedViewModel = SyncedViewModel(resources, repository)
}

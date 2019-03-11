package fr.ffnet.downloader.synced

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.repository.DatabaseRepository

@Module
class SyncedModule {
    @FragmentScope
    @Provides
    fun provideSearchViewModel(
        resources: Resources,
        repository: DatabaseRepository
    ): SyncedViewModel = SyncedViewModel(resources, repository)
}

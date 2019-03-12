package fr.ffnet.downloader.synced

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.DateFormatter

@Module
class SyncedModule {
    @FragmentScope
    @Provides
    fun provideSearchViewModel(
        resources: Resources,
        repository: DatabaseRepository,
        dateFormatter: DateFormatter
    ): SyncedViewModel = SyncedViewModel(resources, repository, dateFormatter)
}

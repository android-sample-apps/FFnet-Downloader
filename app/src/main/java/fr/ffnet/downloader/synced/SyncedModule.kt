package fr.ffnet.downloader.synced

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class SyncedModule {
    @Provides
    fun provideSyncedViewModel(
        fragment: SyncedFragment,
        resources: Resources,
        repository: DatabaseRepository,
        dateFormatter: DateFormatter
    ): SyncedViewModel {
        val factory = ViewModelFactory {
            SyncedViewModel(
                resources,
                repository,
                dateFormatter
            )
        }
        return ViewModelProvider(fragment, factory)[SyncedViewModel::class.java]
    }
}

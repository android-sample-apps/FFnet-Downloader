package fr.ffnet.downloader.synced.injection

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.synced.SyncedViewModel
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class SyncedModule(private val fragment: Fragment) {

    @Provides
    fun provideSyncedViewModel(
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

package fr.ffnet.downloader.profile.fanfiction

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class ProfileFanfictionModule {
    @Provides
    fun provideProfileFanfictionViewModel(
        fragment: ProfileFanfictionFragment,
        resources: Resources,
        databaseRepository: DatabaseRepository,
        dateFormatter: DateFormatter
    ): ProfileFanfictionViewModel {
        val factory = ViewModelFactory {
            ProfileFanfictionViewModel(
                resources,
                databaseRepository,
                dateFormatter
            )
        }
        return ViewModelProvider(fragment, factory)[ProfileFanfictionViewModel::class.java]
    }
}

package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class FanfictionModule {

    @Provides
    fun provideFanfictionViewModel(
        activity: FanfictionActivity,
        resources: Resources,
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository,
        dateFormatter: DateFormatter
    ): FanfictionViewModel {
        val factory = ViewModelFactory {
            FanfictionViewModel(
                resources,
                apiRepository,
                dbRepository,
                dateFormatter
            )
        }
        return ViewModelProvider(activity, factory)[FanfictionViewModel::class.java]
    }
}

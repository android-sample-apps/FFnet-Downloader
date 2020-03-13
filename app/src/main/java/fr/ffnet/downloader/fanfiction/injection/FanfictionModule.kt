package fr.ffnet.downloader.fanfiction.injection

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfiction.FanfictionViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class FanfictionModule(private val activity: AppCompatActivity) {

    @Provides
    fun provideFanfictionViewModel(
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

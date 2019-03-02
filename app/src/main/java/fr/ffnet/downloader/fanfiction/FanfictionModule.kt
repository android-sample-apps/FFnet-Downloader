package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.ActivityScope
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.FanfictionDao
import fr.ffnet.downloader.repository.RepositoryModule

@Module(includes = [RepositoryModule::class])
class FanfictionModule {

    @ActivityScope
    @Provides
    fun provideSearchViewModel(
        resources: Resources,
        dao: FanfictionDao,
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository
    ): FanfictionViewModel = FanfictionViewModel(resources, dao, apiRepository, dbRepository)
}

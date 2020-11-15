package fr.ffnet.downloader.common

import dagger.Component
import fr.ffnet.downloader.fanfiction.injection.FanfictionComponent
import fr.ffnet.downloader.fanfiction.injection.FanfictionModule
import fr.ffnet.downloader.profile.fanfiction.injection.ProfileFanfictionComponent
import fr.ffnet.downloader.profile.fanfiction.injection.ProfileFanfictionModule
import fr.ffnet.downloader.profile.injection.ProfileComponent
import fr.ffnet.downloader.profile.injection.ProfileModule
import fr.ffnet.downloader.repository.DownloaderWorker
import fr.ffnet.downloader.repository.PeriodicCheckWorker
import fr.ffnet.downloader.repository.RepositoryModule
import fr.ffnet.downloader.search.injection.SearchComponent
import fr.ffnet.downloader.search.injection.SearchModule
import fr.ffnet.downloader.synced.injection.SyncedComponent
import fr.ffnet.downloader.synced.injection.SyncedModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainModule::class,
        NetworkModule::class,
        RepositoryModule::class
    ]
)
interface MainComponent {

    fun inject(application: MainApplication)
    fun inject(downloaderWorker: DownloaderWorker)
    fun inject(periodicCheckWorker: PeriodicCheckWorker)

    fun plus(module: FanfictionModule): FanfictionComponent
    fun plus(module: ProfileModule): ProfileComponent
    fun plus(module: ProfileFanfictionModule): ProfileFanfictionComponent
    fun plus(module: SearchModule): SearchComponent
    fun plus(module: SyncedModule): SyncedComponent
}

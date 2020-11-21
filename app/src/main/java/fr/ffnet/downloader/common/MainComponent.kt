package fr.ffnet.downloader.common

import dagger.Component
import fr.ffnet.downloader.fanfiction.injection.FanfictionComponent
import fr.ffnet.downloader.fanfiction.injection.FanfictionModule
import fr.ffnet.downloader.profile.AuthorModule
import fr.ffnet.downloader.profile.fanfiction.injection.ProfileFanfictionComponent
import fr.ffnet.downloader.profile.fanfiction.injection.ProfileFanfictionModule
import fr.ffnet.downloader.profile.injection.AuthorComponent
import fr.ffnet.downloader.repository.DownloaderWorker
import fr.ffnet.downloader.repository.RepositoryModule
import fr.ffnet.downloader.search.injection.SearchComponent
import fr.ffnet.downloader.search.injection.SearchModule
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

    fun plus(module: FanfictionModule): FanfictionComponent
    fun plus(module: ProfileFanfictionModule): ProfileFanfictionComponent
    fun plus(module: AuthorModule): AuthorComponent
    fun plus(module: SearchModule): SearchComponent
}

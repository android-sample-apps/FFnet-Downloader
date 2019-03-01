package fr.ffnet.downloader.search

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.RepositoryModule

@Module(includes = [RepositoryModule::class])
class SearchModule {

    @FragmentScope
    @Provides
    fun provideSearchViewModel(
        urlTransformer: UrlTransformer,
        resources: Resources,
        repository: DownloaderRepository
    ): SearchViewModel = SearchViewModel(urlTransformer, resources, repository)
}

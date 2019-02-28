package fr.ffnet.downloader.search

import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.FragmentScope

@Module
abstract class SearchModule {
    @Module
    companion object {
        @JvmStatic
        @FragmentScope
        @Provides
        fun provideSearchViewModel(): SearchViewModel = SearchViewModel()
    }
}

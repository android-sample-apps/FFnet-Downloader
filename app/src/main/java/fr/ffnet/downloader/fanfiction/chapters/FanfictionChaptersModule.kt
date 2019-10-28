package fr.ffnet.downloader.fanfiction.chapters

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository

@Module
class FanfictionChaptersModule {

    @Provides
    fun provideFanfictionChaptersViewModel(
        fragment: FanfictionChaptersFragment,
        viewModelCreator: () -> FanfictionChaptersViewModel
    ): FanfictionChaptersViewModel {
        val factory = FanfictionChaptersViewModelFactory(viewModelCreator)
        return ViewModelProvider(
            fragment.viewModelStore,
            factory
        )[FanfictionChaptersViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        resources: Resources,
        dbRepository: DatabaseRepository
    ): () -> FanfictionChaptersViewModel = {
        FanfictionChaptersViewModel(resources, dbRepository)
    }

}

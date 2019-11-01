package fr.ffnet.downloader.categories

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository

@Module
class CategoriesModule {
    @Provides
    fun provideCategoriesViewModel(
        fragment: CategoriesFragment,
        viewModelCreator: () -> CategoriesViewModel
    ): CategoriesViewModel {
        val factory = CategoriesViewModelFactory(viewModelCreator)
        return ViewModelProvider(fragment.viewModelStore, factory)[CategoriesViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository
    ): () -> CategoriesViewModel = {
        CategoriesViewModel(
            apiRepository,
            dbRepository
        )
    }
}

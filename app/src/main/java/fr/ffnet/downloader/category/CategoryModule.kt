package fr.ffnet.downloader.category

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository

@Module
class CategoryModule {
    @Provides
    fun provideCategoryViewModel(
        activity: CategoryActivity,
        viewModelCreator: () -> CategoryViewModel
    ): CategoryViewModel {
        val factory = CategoryViewModelFactory(viewModelCreator)
        return ViewModelProvider(activity.viewModelStore, factory)[CategoryViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository
    ): () -> CategoryViewModel = {
        CategoryViewModel(
            apiRepository,
            dbRepository
        )
    }
}

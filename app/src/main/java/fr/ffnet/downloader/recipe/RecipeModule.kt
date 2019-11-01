package fr.ffnet.downloader.recipe

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository

@Module
class RecipeModule {
    @Provides
    fun provideCategoryViewModel(
        activity: RecipeActivity,
        viewModelCreator: () -> RecipeViewModel
    ): RecipeViewModel {
        val factory = RecipeViewModelFactory(viewModelCreator)
        return ViewModelProvider(activity.viewModelStore, factory)[RecipeViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        resources: Resources,
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository
    ): () -> RecipeViewModel = {
        RecipeViewModel(
            resources,
            apiRepository,
            dbRepository
        )
    }
}

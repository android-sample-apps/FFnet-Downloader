package fr.ffnet.downloader.recipe

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository

class RecipeViewModel(
    private val resources: Resources,
    private val downloaderRepository: DownloaderRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private lateinit var recipeTitle: LiveData<String>
    fun getRecipeTitle() = recipeTitle

    fun loadRecipe(recipeUrl: String) {

    }

}

class RecipeViewModelFactory(
    private val creator: () -> RecipeViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

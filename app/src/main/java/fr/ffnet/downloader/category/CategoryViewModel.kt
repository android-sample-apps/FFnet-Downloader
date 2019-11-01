package fr.ffnet.downloader.category

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.BuildConfig
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val resources: Resources,
    private val downloaderRepository: DownloaderRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private lateinit var recipeList: LiveData<List<RecipeDisplayItem>>
    fun getRecipeList(): LiveData<List<RecipeDisplayItem>> = recipeList

    private lateinit var categoryTitle: LiveData<String>
    fun getCategoryTitle(): LiveData<String> = categoryTitle

    fun refreshRecipeList(categoryUrl: String) {
        loadRecipeList(categoryUrl)
        categoryTitle = Transformations.map(databaseRepository.loadCategory(categoryUrl)) {
            it.title
        }
        viewModelScope.launch(Dispatchers.IO) {
            downloaderRepository.loadRecipes(categoryUrl)
        }
    }

    private fun loadRecipeList(categoryUrl: String) {
        recipeList = Transformations.map(databaseRepository.loadRecipeList(categoryUrl)) {
            it.map { recipe ->
                RecipeDisplayModel(
                    url = recipe.url,
                    imageUrl = BuildConfig.API_BASE_URL + recipe.imageUrl,
                    title = recipe.title
                )
            } + RecipeLoadMoreDisplayModel(resources.getString(R.string.category_load_more_recipes))
        }
    }

    fun loadMore(categoryUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            downloaderRepository.loadRecipes(categoryUrl)
        }
    }
}

class CategoryViewModelFactory(
    private val creator: () -> CategoryViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

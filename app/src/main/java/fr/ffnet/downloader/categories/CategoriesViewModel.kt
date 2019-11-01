package fr.ffnet.downloader.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val downloaderRepository: DownloaderRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private lateinit var categoryList: LiveData<List<CategoryDisplayModel>>
    fun getCategoryList(): LiveData<List<CategoryDisplayModel>> = categoryList

    init {
        loadCategoryList()
    }

    fun refreshCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            downloaderRepository.loadCategories()
        }
    }

    private fun loadCategoryList() {
        categoryList = Transformations.map(databaseRepository.loadCategoryList()) {
            it.map { categoryEntity ->
                CategoryDisplayModel(
                    url = categoryEntity.url,
                    title = categoryEntity.title
                )
            }
        }
    }
}

class CategoriesViewModelFactory(
    private val creator: () -> CategoriesViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

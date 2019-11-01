package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import fr.ffnet.downloader.repository.dao.RecipeDao
import fr.ffnet.downloader.repository.entities.CategoryEntity
import fr.ffnet.downloader.repository.entities.RecipeEntity

class DatabaseRepository(private val dao: RecipeDao) {

    fun loadCategoryList(): LiveData<List<CategoryEntity>> = dao.getCategoryList()
    fun loadCategory(categoryUrl: String): LiveData<CategoryEntity> = dao.getCategory(categoryUrl)

    fun loadRecipeList(categoryUrl: String): LiveData<List<RecipeEntity>> =
        dao.getRecipeList(categoryUrl)
}

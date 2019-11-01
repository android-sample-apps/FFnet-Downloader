package fr.ffnet.downloader.repository

import androidx.work.WorkManager
import fr.ffnet.downloader.fanfictionutils.RecipeBuilder
import fr.ffnet.downloader.repository.dao.RecipeDao
import java.io.IOException
import java.net.SocketTimeoutException

class DownloaderRepository(
    private val service: CrawlService,
    private val recipeBuilder: RecipeBuilder,
    private val recipeDao: RecipeDao,
    private val workManager: WorkManager
) {

    fun loadRecipe() {
        val recipe = service.getRecipe("boeuf-bourguignon-tout-simplement_108_4")
        val response = recipe.execute()
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                println(responseBody.string())
            }
        }
    }

    fun loadRecipes(categoryUrl: String) {
        try {
            val response = service.getRecipes(categoryUrl).execute()
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val recipeList = recipeBuilder.buildRecipes(
                        categoryUrl,
                        responseBody.string()
                    )
                    recipeDao.insertRecipes(recipeList)
                }
            } else {
                println("Not successful")
            }
        } catch (e: IOException) {
            if (e is SocketTimeoutException) {
                println("Response time out!")
            } else {
                println(e.message)
            }
        }
    }

    fun loadCategories() {
        try {
            val response = service.getCategories().execute()
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    recipeDao.insertCategories(recipeBuilder.buildCategories(responseBody.string()))
                }
            } else {
                println("Not successful")
            }
        } catch (e: IOException) {
            if (e is SocketTimeoutException) {
                println("Response time out!")
            } else {
                println(e.message)
            }
        }
    }
}

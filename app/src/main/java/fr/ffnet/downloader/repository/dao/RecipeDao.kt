package fr.ffnet.downloader.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ffnet.downloader.repository.entities.CategoryEntity
import fr.ffnet.downloader.repository.entities.RecipeEntity

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(categoryList: List<CategoryEntity>)

    @Query("SELECT * FROM categoryentity")
    fun getCategoryList(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM categoryentity WHERE url = :categoryUrl")
    fun getCategory(categoryUrl: String): LiveData<CategoryEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipes(recipeList: List<RecipeEntity>)

    @Query("SELECT * FROM recipeentity WHERE categoryUrl = :categoryUrl")
    fun getRecipeList(categoryUrl: String): LiveData<List<RecipeEntity>>
}

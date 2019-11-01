package fr.ffnet.downloader

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.ffnet.downloader.repository.dao.RecipeDao
import fr.ffnet.downloader.repository.entities.CategoryEntity
import fr.ffnet.downloader.repository.entities.RecipeEntity
import fr.ffnet.downloader.utils.Converters

@Database(
    entities = [
        CategoryEntity::class,
        RecipeEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RecipeDownloaderDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}

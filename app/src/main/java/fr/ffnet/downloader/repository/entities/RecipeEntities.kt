package fr.ffnet.downloader.repository.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.joda.time.LocalDateTime
import java.util.*

@Entity
data class CategoryEntity(
    @PrimaryKey val url: String,
    val title: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["url"],
        childColumns = ["categoryUrl"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class RecipeEntity(
    @PrimaryKey val url: String,
    val categoryUrl: String,
    val imageUrl: String,
    val title: String
)

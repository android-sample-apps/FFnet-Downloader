package fr.ffnet.downloader.downloader

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class FanfictionEntity(
    @PrimaryKey val id: String,
    var title: String,
    var words: Int,
    var summary: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = FanfictionEntity::class,
        parentColumns = ["id"],
        childColumns = ["fanfictionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val fanfictionId: String,
    val chapterId: String,
    val title: String,
    val content: String = "",
    val isSynced: Boolean = false
)

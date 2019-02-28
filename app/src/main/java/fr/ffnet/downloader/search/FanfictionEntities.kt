package fr.ffnet.downloader.search

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class FanfictionEntity(
    @PrimaryKey val id: String,
    var title: String,
    var words: Int,
    var summary: String,
    var publishedDate: Date,
    var updatedDate: Date
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = FanfictionEntity::class,
        parentColumns = ["id"],
        childColumns = ["fanfictionId"],
        onDelete = ForeignKey.NO_ACTION
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

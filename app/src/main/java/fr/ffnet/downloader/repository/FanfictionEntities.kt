package fr.ffnet.downloader.repository

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
    var updatedDate: Date,
    var syncedDate: Date?,
    var profileType: Int = 0,
    var nbChapters: Int = 0,
    var nbSyncedChapters: Int = 0
)

@Entity
data class HistoryEntity(
    @PrimaryKey val id: String,
    var title: String,
    var fetchedDate: Date
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

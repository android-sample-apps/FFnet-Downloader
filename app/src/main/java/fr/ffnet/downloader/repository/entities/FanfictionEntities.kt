package fr.ffnet.downloader.repository.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.joda.time.LocalDateTime
import java.util.*

@Entity
data class FanfictionEntity(
    @PrimaryKey val id: String,
    var title: String,
    var author: String,
    var words: Int,
    var summary: String,
    var publishedDate: Date,
    var updatedDate: Date,
    var fetchedDate: LocalDateTime?,
    var profileType: Int = 0,
    var nbChapters: Int = 0,
    var nbSyncedChapters: Int = 0
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
    val content: String = ""
)

package fr.ffnet.downloader.repository.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDateTime

data class AuthorSearchResult(
    val id: String,
    val name: String,
    val nbStories: String
)

data class Author(
    val id: String,
    val name: String,
    val nbStories: String,
    val nbFavorites: String,
    val fetchedDate: LocalDateTime
)

@Entity
data class AuthorEntity(
    @PrimaryKey val authorId: String,
    var name: String,
    var fetchedDate: LocalDateTime,
    var nbstories: String,
    var nbFavorites: String
)

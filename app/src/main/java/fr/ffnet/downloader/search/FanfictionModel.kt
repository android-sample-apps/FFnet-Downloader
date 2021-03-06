package fr.ffnet.downloader.search

import org.joda.time.LocalDateTime
import java.util.*

data class Fanfiction(
    val id: String,
    var title: String,
    var words: Int,
    val author: String,
    var summary: String,
    val publishedDate: Date,
    val updatedDate: Date,
    val fetchedDate: LocalDateTime?,
    val profileType: Int,
    val nbChapters: Int,
    val nbSyncedChapters: Int,
    val chapterList: List<Chapter> = emptyList()
)

data class Chapter(
    val id: String,
    val title: String,
    var content: String = ""
)

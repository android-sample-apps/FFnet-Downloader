package fr.ffnet.downloader.search

import java.util.*

data class Fanfiction(
    val id: String,
    var title: String,
    var words: Int,
    var summary: String,
    val publishedDate: Date,
    val updatedDate: Date,
    val syncedDate: Date?,
    val nbChapters: Int,
    val nbSyncedChapters: Int,
    val chapterList: List<Chapter> = emptyList()
)

data class Chapter(
    val id: String,
    val title: String,
    val content: String = "",
    val status: Boolean = false
)

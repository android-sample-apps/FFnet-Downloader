package fr.ffnet.downloader.downloader

import java.util.*

data class Fanfiction(
    val id: String,
    var title: String,
    var words: Int,
    var summary: String,
    val publishedDate: Date,
    val updatedDate: Date,
    val chapterList: List<Chapter> = emptyList()
)

data class Chapter(
    val id: String,
    val title: String,
    val content: String = "",
    val status: Boolean = false
)

package fr.ffnet.downloader

import java.util.*

data class Fanfiction(
    val id: String,
    val title: String,
    val words: Int,
    val summary: String,
    val publishedDate: Date,
    val updatedDate: Date,
    val chapterList: List<Chapter> = emptyList()
)

data class Chapter(
    val id: Int,
    val title: String,
    val content: String = ""
)

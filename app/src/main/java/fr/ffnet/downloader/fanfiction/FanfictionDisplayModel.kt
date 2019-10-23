package fr.ffnet.downloader.fanfiction

data class FanfictionDisplayModel(
    val id: String,
    val title: String,
    val words: String,
    val summary: String,
    val publishedDate: String,
    val updatedDate: String,
    val syncedDate: String,
    val progression: String
)

data class ChapterUIModel(
    val id: String,
    val title: String,
    val status: String
)
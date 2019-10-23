package fr.ffnet.downloader.fanfiction

data class FanfictionDisplayModel(
    val id: String,
    val title: String,
    val words: String,
    val summary: String,
    val publishedDate: String,
    val updatedDate: String,
    val syncedDate: String,
    val progressionText: String,
    val progression: Int,
    val nbChapters: Int
)

data class ChapterUIModel(
    val id: String,
    val title: String,
    val status: String
)
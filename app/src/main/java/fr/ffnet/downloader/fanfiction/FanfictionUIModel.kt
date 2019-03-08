package fr.ffnet.downloader.fanfiction

sealed class FanfictionInfoUIModel {

    data class FanfictionUIModel(
        val id: String,
        val title: String,
        val words: String,
        val summary: String,
        val publishedDate: String,
        val updatedDate: String,
        val syncedDate: String,
        val progression: String
    ) : FanfictionInfoUIModel()

    data class ChapterUIModel(
        val id: String,
        val title: String,
        val status: String
    ) : FanfictionInfoUIModel()
}
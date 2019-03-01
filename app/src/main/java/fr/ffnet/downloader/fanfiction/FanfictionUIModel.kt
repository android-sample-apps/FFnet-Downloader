package fr.ffnet.downloader.fanfiction

data class FanfictionUIModel(
    val id: String,
    val title: String,
    val words: String,
    val summary: String,
    val publishedDate: String,
    val updatedDate: String,
    val chapterList: List<ChapterUIModel> = emptyList()
)

data class ChapterUIModel(
    val id: String,
    val title: String,
    val status: String
)
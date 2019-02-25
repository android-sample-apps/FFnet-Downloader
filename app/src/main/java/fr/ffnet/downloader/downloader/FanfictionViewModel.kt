package fr.ffnet.downloader.downloader

data class FanfictionViewModel(
    val id: String,
    val title: String,
    val words: String,
    val summary: String,
    val publishedDate: String,
    val updatedDate: String,
    val chapterList: List<ChapterViewModel> = emptyList()
)

data class ChapterViewModel(
    val id: String,
    val title: String
)
package fr.ffnet.downloader.synced

data class FanfictionSyncedUIModel(
    val id: String,
    val title: String,
    val words: String,
    val summary: String,
    val progressionText: String,
    val updatedDate: String,
    val publishedDate: String,
    val fetchedDate: String,
    val isDownloadComplete: Boolean,
    val exportPdfImage: Int,
    val exportEpubImage: Int
)

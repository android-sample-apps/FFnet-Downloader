package fr.ffnet.downloader.synced

data class FanfictionSyncedUIModel(
    val id: String,
    val title: String,
    val chapters: String,
    val syncedChapters: Int,
    val nbChapters: Int,
    val updatedDate: String,
    val publishedDate: String,
    val fetchedDate: String,
    val isDownloadComplete: Boolean
)

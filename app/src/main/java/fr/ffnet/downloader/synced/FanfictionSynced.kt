package fr.ffnet.downloader.synced

data class FanfictionSyncedUIModel(
    val id: String,
    val title: String,
    val chapters: String,
    val updatedDate: String,
    val publishedDate: String,
    val fetchedDate: String?
)

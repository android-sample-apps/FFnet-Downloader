package fr.ffnet.downloader.synced

sealed class FanfictionUIItem {
    data class FanfictionUITitle(
        val title: String
    ): FanfictionUIItem()
    data class FanfictionUI(
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
    ): FanfictionUIItem()
}

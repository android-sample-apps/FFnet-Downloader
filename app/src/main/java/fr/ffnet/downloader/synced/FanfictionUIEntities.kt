package fr.ffnet.downloader.synced

sealed class FanfictionUIItem {
    data class FanfictionUITitle(
        val shouldShowSyncAllButton: Boolean,
        val title: String
    ): FanfictionUIItem()
    data class FanfictionUI(
        val id: String,
        val title: String,
        val words: String?,
        val summary: String,
        val progressionText: String,
        val chaptersNb: Int,
        val updatedDate: String,
        val publishedDate: String,
        val fetchedDate: String,
        val isDownloadComplete: Boolean,
        val shouldShowAuthor: Boolean,
        val shouldShowWords: Boolean,
        val shouldShowChapterSync: Boolean,
        val shouldShowExport: Boolean,
        val author: String?,
        val exportPdfImage: Int,
        val exportEpubImage: Int
    ): FanfictionUIItem()

    data class HistoryUI(
        val fanfictionId: String,
        val url: String,
        val title: String,
        val date: String
    ) : FanfictionUIItem()
}

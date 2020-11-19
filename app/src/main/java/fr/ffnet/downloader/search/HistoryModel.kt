package fr.ffnet.downloader.search

sealed class HistoryItem {
    data class HistoryUITitle(val title: String) : HistoryItem()
    data class HistoryUI(
        val fanfictionId: String,
        val url: String,
        val title: String,
        val date: String
    ) : HistoryItem()
}

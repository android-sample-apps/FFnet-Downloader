package fr.ffnet.downloader.utils

enum class FanfictionAction {
    GOTO_FANFICTION, EXPORT_PDF, EXPORT_EPUB, DELETE_FANFICTION
}

interface OnFanfictionOptionsListener {
    fun onOptionsClicked(
        fanfictionId: String,
        title: String,
        publishedDate: String,
        updatedDate: String,
        fetchedDate: String
    )
}

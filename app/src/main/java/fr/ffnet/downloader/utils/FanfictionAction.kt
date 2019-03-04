package fr.ffnet.downloader.utils

enum class FanfictionAction {
    GOTO_FANFICTION, EXPORT_PDF, EXPORT_EPUB, DELETE_FANFICTION
}

interface OnActionsClickListener {
    fun onActionClicked(fanfictionId: String, action: FanfictionAction)
}

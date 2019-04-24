package fr.ffnet.downloader.utils

import fr.ffnet.downloader.synced.FanfictionSyncedUIModel

enum class FanfictionAction {
    GOTO_FANFICTION, EXPORT_PDF, EXPORT_EPUB, DELETE_FANFICTION
}

interface OnFanfictionOptionsListener {
    fun onOptionsClicked(fanfiction: FanfictionSyncedUIModel)
}

package fr.ffnet.downloader.utils

import fr.ffnet.downloader.synced.FanfictionSyncedUIModel

interface OnFanfictionActionsListener {
    fun onFetchInformation(fanfiction: FanfictionSyncedUIModel)
    fun onExportPdf(fanfictionId: String)
    fun onExportEpub(fanfictionId: String)
    fun onUnsync(fanfiction: FanfictionSyncedUIModel)
}

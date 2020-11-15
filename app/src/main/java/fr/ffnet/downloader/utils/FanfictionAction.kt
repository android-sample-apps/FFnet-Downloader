package fr.ffnet.downloader.utils

import fr.ffnet.downloader.synced.FanfictionSyncedUIModel

interface OnFanfictionActionsListener {
    fun onFetchInformation(fanfiction: FanfictionSyncedUIModel)
    fun onExportPdf(fanfiction: FanfictionSyncedUIModel)
    fun onExportEpub(fanfiction: FanfictionSyncedUIModel)
    fun onUnsync(fanfiction: FanfictionSyncedUIModel)
}

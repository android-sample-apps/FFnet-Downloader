package fr.ffnet.downloader.utils

import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUI

interface OnFanfictionActionsListener {
    fun onFetchInformation(fanfiction: FanfictionUI)
    fun onExportPdf(fanfictionId: String)
    fun onExportEpub(fanfictionId: String)
    fun onUnsync(fanfiction: FanfictionUI)
}

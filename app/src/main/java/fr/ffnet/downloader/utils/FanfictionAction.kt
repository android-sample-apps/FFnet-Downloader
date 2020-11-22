package fr.ffnet.downloader.utils

import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUI

interface OnFanfictionActionsListener {
    fun onFetchInformation(fanfictionId: String)
    fun onExportPdf(fanfictionId: String)
    fun onExportEpub(fanfictionId: String)
    fun onUnsync(fanfiction: FanfictionUI)
}

package fr.ffnet.downloader.utils

import fr.ffnet.downloader.synced.FanfictionSyncedUIModel

interface OnFanfictionOptionsListener {
    fun onOptionsClicked(fanfiction: FanfictionSyncedUIModel)
}

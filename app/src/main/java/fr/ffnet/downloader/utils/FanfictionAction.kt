package fr.ffnet.downloader.utils

import fr.ffnet.downloader.profile.FanfictionSyncedUIModel

interface OnFanfictionOptionsListener {
    fun onOptionsClicked(fanfiction: FanfictionSyncedUIModel)
}

package fr.ffnet.downloader

import javax.inject.Inject

class DownloaderInteractor @Inject constructor(
    private val repository: DownloaderRepository
) {
    fun loadFanfiction(id: String) {
        repository.loadFanfictionInfo(id)
    }
}

package fr.ffnet.downloader

import fr.ffnet.downloader.DownloaderRepository.FanfictionResult.FanfictionResultSuccess
import javax.inject.Inject

class DownloaderInteractor @Inject constructor(
    private val repository: DownloaderRepository,
    private val fanfictionBuilder: FanfictionBuilder
) {
    fun loadFanfictionInfo(id: String) {

        val fanfictionResult = repository.loadFanfictionInfo(id)

        if (fanfictionResult is FanfictionResultSuccess) {
            fanfictionBuilder.buildFanfiction(id, fanfictionResult.html)
        } else {

        }
    }
}

package fr.ffnet.downloader.downloader

import fr.ffnet.downloader.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import javax.inject.Inject

class DownloaderInteractor @Inject constructor(
    private val repository: DownloaderRepository,
    private val fanfictionBuilder: FanfictionBuilder
) {

    fun loadFanfictionInfo(id: String): FanfictionResult {

        val fanfictionResult = repository.loadFanfictionInfo(id)

        return if (fanfictionResult is FanfictionRepositoryResultSuccess) {
            val fanfictionInfo = fanfictionBuilder.buildFanfiction(id, fanfictionResult.html)
            FanfictionResult.FanfictionResultSuccess(
                fanfictionInfo
            )
        } else {
            FanfictionResult.FanfictionResultFailure
        }
    }

    sealed class FanfictionResult {
        data class FanfictionResultSuccess(val fanfictionInfo: Fanfiction) : FanfictionResult()
        object FanfictionResultFailure : FanfictionResult()
    }
}

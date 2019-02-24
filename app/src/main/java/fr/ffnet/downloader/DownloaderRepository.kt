package fr.ffnet.downloader

import fr.ffnet.downloader.DownloaderRepository.FanfictionResult.FanfictionResultFailure
import fr.ffnet.downloader.DownloaderRepository.FanfictionResult.FanfictionResultSuccess
import javax.inject.Inject

class DownloaderRepository @Inject constructor(
    private val service: DownloaderService
) {

    fun loadFanfictionInfo(id: String): FanfictionResult {

        val response = service.getPage(id).execute()
        return if (response.isSuccessful) {
            response.body()?.let {
                FanfictionResultSuccess(it.string())
            } ?: FanfictionResultFailure
        } else {
            FanfictionResultFailure
        }
    }

    sealed class FanfictionResult {
        data class FanfictionResultSuccess(val html: String) : FanfictionResult()
        object FanfictionResultFailure : FanfictionResult()
    }
}

package fr.ffnet.downloader

import fr.ffnet.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import javax.inject.Inject

class DownloaderRepository @Inject constructor(
    private val service: DownloaderService
) {

    fun loadFanfictionInfo(id: String): FanfictionRepositoryResult {

        val response = service.getPage(id).execute()
        return if (response.isSuccessful) {
            response.body()?.let {
                FanfictionRepositoryResultSuccess(it.string())
            } ?: FanfictionRepositoryResultFailure
        } else {
            FanfictionRepositoryResultFailure
        }
    }

    sealed class FanfictionRepositoryResult {
        data class FanfictionRepositoryResultSuccess(val html: String) : FanfictionRepositoryResult()
        object FanfictionRepositoryResultFailure : FanfictionRepositoryResult()
    }
}

package fr.ffnet.downloader.downloader

import fr.ffnet.downloader.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import javax.inject.Inject

class DownloaderInteractor @Inject constructor(
    private val repository: DownloaderRepository
) {

    fun loadFanfictionInfo(id: String): FanfictionResult {

        val fanfictionResult = repository.loadFanfictionInfo(id)

        if (fanfictionResult is FanfictionRepositoryResultSuccess) {

            with(fanfictionResult) {
                if (fanfictionInfo.chapterList.size > 1) {
                    repository.loadAllChapters(fanfictionInfo.id, fanfictionInfo.chapterList)
                }
                return FanfictionResult.FanfictionResultSuccess(fanfictionInfo)
            }
        } else {
            return FanfictionResult.FanfictionResultFailure
        }
    }

    sealed class FanfictionResult {
        data class FanfictionResultSuccess(val fanfictionInfo: Fanfiction) : FanfictionResult()
        object FanfictionResultFailure : FanfictionResult()
    }
}

package fr.ffnet.downloader.downloader

import fr.ffnet.downloader.downloader.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import javax.inject.Inject

class DownloaderInteractor @Inject constructor(
    private val repository: DownloaderRepository,
    private val fanfictionBuilder: FanfictionBuilder
) : DownloaderRepository.ChapterLoadedListener {

    override fun onChapterLoaded(chapter: Chapter, html: String) {
        val chapterContent = fanfictionBuilder.extractChapter(html)
        println("Chapter ${chapter.id} loaded. Length : ${chapterContent.length}")
    }

    fun loadFanfictionInfo(id: String): FanfictionResult {

        val fanfictionResult = repository.loadFanfictionInfo(id)

        if (fanfictionResult is FanfictionRepositoryResultSuccess) {
            val fanfictionInfo = fanfictionBuilder.buildFanfiction(id, fanfictionResult.html)

            if (fanfictionInfo.chapterList.size > 1) {
                repository.loadAllChapters(fanfictionInfo.id, fanfictionInfo.chapterList, this)
            }

            return FanfictionResult.FanfictionResultSuccess(fanfictionInfo)
        } else {
            return FanfictionResult.FanfictionResultFailure
        }
    }

    sealed class FanfictionResult {
        data class FanfictionResultSuccess(val fanfictionInfo: Fanfiction) : FanfictionResult()
        object FanfictionResultFailure : FanfictionResult()
    }
}

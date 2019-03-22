package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.fanfictionutils.FanfictionTransformer
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.search.Fanfiction
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloaderRepository(
    private val service: CrawlService,
    private val fanfictionBuilder: FanfictionBuilder,
    private val fanfictionDao: FanfictionDao,
    private val fanfictionTransformer: FanfictionTransformer
) {

    private val chaptersDownloadState = MutableLiveData<ChaptersDownloadResult>()

    fun getDownloadState(): LiveData<ChaptersDownloadResult> = chaptersDownloadState

    fun loadFanfictionInfo(fanfictionId: String): FanfictionRepositoryResult {
        val response = service.getFanfiction(fanfictionId).execute()
        return if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                val existingChapters = fanfictionDao.getChaptersIds(fanfictionId)
                val (firstChapter, fanfictionInfo) = fanfictionBuilder.buildFanfiction(
                    fanfictionId, responseBody.string()
                )

                fanfictionDao.insertFanfiction(
                    fanfictionTransformer.toFanfictionEntity(fanfictionInfo)
                )

                val chapterList = if (existingChapters.isNotEmpty()) {
                    fanfictionInfo.chapterList.filter { it.id !in existingChapters }
                } else {
                    fanfictionInfo.chapterList
                }
                fanfictionDao.insertChapterList(
                    fanfictionTransformer.toChapterEntityList(fanfictionId, chapterList)
                )
                fanfictionDao.updateFirstChapter(fanfictionId, firstChapter)
                FanfictionRepositoryResultSuccess(fanfictionInfo)
            } ?: FanfictionRepositoryResultFailure
        } else {
            FanfictionRepositoryResultFailure
        }
    }

    fun downloadChapters(fanfictionId: String) {
        chaptersDownloadState.postValue(ChaptersDownloadResult.DownloadOngoing)
        val chapterList = fanfictionDao.getChapters(fanfictionId)
        chapterList.forEach { chapter ->
            service.getFanfiction(
                fanfictionId, chapter.chapterId
            ).enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    chaptersDownloadState.postValue(ChaptersDownloadResult.RepositoryException)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val chapterContent = fanfictionBuilder.extractChapter(it.string())
                            Thread {
                                fanfictionDao.updateChapter(
                                    content = chapterContent,
                                    isSynced = true,
                                    chapterId = chapter.chapterId,
                                    fanfictionId = fanfictionId
                                )
                            }.start()
                            if (chapter.chapterId.toInt() == chapterList.size) {
                                chaptersDownloadState.postValue(ChaptersDownloadResult.DownloadSuccessful)
                            }
                        } ?: setDownloadStateChapterEmpty()
                    } else {
                        chaptersDownloadState.postValue(ChaptersDownloadResult.ResponseNotSuccessful)
                    }
                }
            })
        }
    }

    private fun setDownloadStateChapterEmpty() {
        chaptersDownloadState.postValue(ChaptersDownloadResult.ChapterEmpty)
    }

    sealed class ChaptersDownloadResult {
        object DownloadOngoing : ChaptersDownloadResult()
        object DownloadSuccessful : ChaptersDownloadResult()
        object ChapterEmpty : ChaptersDownloadResult()
        object RepositoryException : ChaptersDownloadResult()
        object ResponseNotSuccessful : ChaptersDownloadResult()
    }

    sealed class FanfictionRepositoryResult {
        data class FanfictionRepositoryResultSuccess(val fanfictionInfo: Fanfiction) : FanfictionRepositoryResult()
        object FanfictionRepositoryResultFailure : FanfictionRepositoryResult()
    }
}

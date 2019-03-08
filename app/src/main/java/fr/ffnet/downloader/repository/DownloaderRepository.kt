package fr.ffnet.downloader.repository

import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.fanfictionutils.FanfictionTransformer
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.search.Fanfiction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloaderRepository(
    private val service: SearchService,
    private val fanfictionBuilder: FanfictionBuilder,
    private val fanfictionDao: FanfictionDao,
    private val fanfictionTransformer: FanfictionTransformer
) {

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

    fun loadAllChapters(fanfictionId: String, listener: RetrofitIsDoneListener) {
        CoroutineScope(Dispatchers.IO).launch {
            val chapterList = fanfictionDao.getChapters(fanfictionId)
            chapterList.forEach { chapter ->
                service.getFanfiction(
                    fanfictionId, chapter.chapterId
                ).enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        println("onFailure Nope")
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
                                if (chapter.chapterId == chapterList.last().chapterId) {
                                    listener.onChapterDownloadState(ChapterDownloadState.DONE)
                                }
                            } ?: println("onResponse Nope")
                        } else {
                            println("onResponse Nope")
                        }
                    }
                })
            }
        }
    }

    sealed class FanfictionRepositoryResult {
        data class FanfictionRepositoryResultSuccess(val fanfictionInfo: Fanfiction) : FanfictionRepositoryResult()
        object FanfictionRepositoryResultFailure : FanfictionRepositoryResult()
    }

    enum class ChapterDownloadState {
        PENDING, DOWNLOADING, FAILURE, DONE
    }

    interface RetrofitIsDoneListener {
        fun onChapterDownloadState(state: ChapterDownloadState)
    }
}

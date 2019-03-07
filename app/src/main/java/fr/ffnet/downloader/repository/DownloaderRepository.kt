package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.fanfictionutils.FanfictionTransformer
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.search.Fanfiction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloaderRepository(
    private val service: SearchService,
    private val fanfictionBuilder: FanfictionBuilder,
    private val fanfictionDao: FanfictionDao,
    private val historyDao: HistoryDao,
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
                historyDao.insertInHistory(
                    HistoryEntity(
                        fanfictionInfo.id,
                        fanfictionInfo.title,
                        DateTime.now().toDate()
                    )
                )
                val chapterList = if (existingChapters.isNotEmpty()) {
                    fanfictionInfo.chapterList[0].apply {
                        if (!this.status) {
                            content = firstChapter
                            status = true
                        }
                    }
                    fanfictionInfo.chapterList.filter { it.id !in existingChapters }
                } else {
                    fanfictionInfo.chapterList[0].apply {
                        content = firstChapter
                        status = true
                    }
                    fanfictionInfo.chapterList
                }
                fanfictionDao.insertChapterList(
                    fanfictionTransformer.toChapterEntityList(fanfictionId, chapterList)
                )
                FanfictionRepositoryResultSuccess(fanfictionInfo)
            } ?: FanfictionRepositoryResultFailure
        } else {
            FanfictionRepositoryResultFailure
        }
    }

    fun loadHistory(): LiveData<List<HistoryEntity>> = historyDao.getHistory()

    fun loadAllChapters(fanfictionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            fanfictionDao.getChapters(fanfictionId).forEach { chapter ->
                println("Adding request for chapter ${chapter.chapterId}")
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
}

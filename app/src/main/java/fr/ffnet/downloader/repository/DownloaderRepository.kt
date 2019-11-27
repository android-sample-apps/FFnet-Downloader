package fr.ffnet.downloader.repository

import androidx.lifecycle.*
import fr.ffnet.downloader.fanfictionutils.*
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.*
import fr.ffnet.downloader.repository.dao.*
import fr.ffnet.downloader.search.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class DownloaderRepository(
    private val service: CrawlService,
    private val fanfictionBuilder: FanfictionBuilder,
    private val fanfictionDao: FanfictionDao,
    private val fanfictionTransformer: FanfictionTransformer
) {

    private val chaptersDownloadState = MutableLiveData<ChaptersDownloadResult>()
    fun getDownloadState(): LiveData<ChaptersDownloadResult> = chaptersDownloadState

    fun downloadChapters(fanfictionId: String) {
        val chapterList = fanfictionDao.getChaptersToSync(fanfictionId)
        if (chapterList.isNotEmpty()) {
            chaptersDownloadState.postValue(ChaptersDownloadResult.DownloadOngoing)
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
                                    chaptersDownloadState.postValue(
                                        ChaptersDownloadResult.DownloadSuccessful
                                    )
                                }
                            } ?: chaptersDownloadState.postValue(
                                ChaptersDownloadResult.ChapterEmpty
                            )
                        } else {
                            chaptersDownloadState.postValue(
                                ChaptersDownloadResult.ResponseNotSuccessful
                            )
                        }
                    }
                })
            }
        } else {
            chaptersDownloadState.postValue(ChaptersDownloadResult.NothingToDownload)
        }
    }

    fun loadFanfictionInfo(fanfictionId: String): FanfictionRepositoryResult {
        return try {
            val response = service.getFanfiction(fanfictionId).execute()
            if (response.isSuccessful) {
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
                    fanfictionDao.updateChapter(
                        content = firstChapter,
                        isSynced = true,
                        fanfictionId = fanfictionId,
                        chapterId = "1"
                    )
                    FanfictionRepositoryResultSuccess(fanfictionInfo)
                } ?: FanfictionRepositoryResultFailure
            } else {
                FanfictionRepositoryResultServerFailure
            }
        } catch (exception: IOException) {
            FanfictionRepositoryResultInternetFailure
        }
    }

    sealed class ChaptersDownloadResult {
        object DownloadOngoing : ChaptersDownloadResult()
        object DownloadSuccessful : ChaptersDownloadResult()
        object NothingToDownload : ChaptersDownloadResult()
        object ChapterEmpty : ChaptersDownloadResult()
        object RepositoryException : ChaptersDownloadResult()
        object ResponseNotSuccessful : ChaptersDownloadResult()
    }

    sealed class FanfictionRepositoryResult {
        data class FanfictionRepositoryResultSuccess(val fanfictionInfo: Fanfiction) : FanfictionRepositoryResult()
        object FanfictionRepositoryResultFailure : FanfictionRepositoryResult()
        object FanfictionRepositoryResultServerFailure : FanfictionRepositoryResult()
        object FanfictionRepositoryResultInternetFailure : FanfictionRepositoryResult()
    }
}

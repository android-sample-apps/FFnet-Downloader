package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import androidx.work.ExistingWorkPolicy.*
import fr.ffnet.downloader.fanfictionutils.FanfictionBuilder
import fr.ffnet.downloader.fanfictionutils.FanfictionTransformer
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.*
import fr.ffnet.downloader.repository.DownloaderWorker.Companion.FANFICTION_ID_KEY
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.search.Fanfiction
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DownloaderRepository(
        private val service: CrawlService,
        private val fanfictionBuilder: FanfictionBuilder,
        private val fanfictionDao: FanfictionDao,
        private val fanfictionTransformer: FanfictionTransformer,
        private val workManager: WorkManager
) {

    private val chaptersDownloadState = MutableLiveData<ChaptersDownloadResult>()

    fun getDownloadState(): LiveData<ChaptersDownloadResult> = chaptersDownloadState

    fun downloadChaptersWithWorkManager(fanfictionId: String) {
        val chapterList = fanfictionDao.getChaptersToSync(fanfictionId)
        if (chapterList.isNotEmpty()) {
            workManager.enqueueUniqueWork(
                    fanfictionId,
                    KEEP,
                    OneTimeWorkRequest.Builder(DownloaderWorker::class.java)
                            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                            .addTag(fanfictionId)
                            .setInputData(Data.Builder().putString(FANFICTION_ID_KEY, fanfictionId).build())
                            .build()
            )
        }
    }

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
                    fanfictionDao.updateFirstChapter(fanfictionId, firstChapter)
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

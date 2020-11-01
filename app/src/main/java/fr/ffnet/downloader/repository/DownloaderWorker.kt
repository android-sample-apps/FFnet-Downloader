package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.utils.FanfictionBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DownloaderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val FANFICTION_ID_KEY = "FANFICTION_ID"
    }

    lateinit var service: CrawlService
    lateinit var fanfictionBuilder: FanfictionBuilder
    lateinit var fanfictionDao: FanfictionDao

    override suspend fun doWork(): Result {
        val downloadResult = withContext(Dispatchers.IO) {
            downloadFanfictionChapters()
        }
        return when (downloadResult) {
            FanfictionDownloadResult.DownloadFailure -> Result.failure()
            FanfictionDownloadResult.DownloadSuccess -> Result.success()
        }
    }

    private suspend fun downloadFanfictionChapters(): FanfictionDownloadResult {
        return suspendCoroutine { continuation ->
            val fanfictionId = inputData.getString(FANFICTION_ID_KEY) ?: ""
            val chapterList = fanfictionDao.getChaptersToSync(fanfictionId)
            chapterList.forEachIndexed { index, chapter ->
                service.getFanfiction(
                    fanfictionId, chapter.chapterId
                ).enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        continuation.resume(FanfictionDownloadResult.DownloadFailure)
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
                            }
                            if (index == chapterList.lastIndex) {
                                continuation.resume(FanfictionDownloadResult.DownloadSuccess)
                            }
                        }
                    }
                })
            }
        }
    }
}

sealed class FanfictionDownloadResult {
    object DownloadSuccess : FanfictionDownloadResult()
    object DownloadFailure : FanfictionDownloadResult()
}

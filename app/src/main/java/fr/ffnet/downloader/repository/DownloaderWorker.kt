package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.utils.FanfictionBuilder
import javax.inject.Inject

class DownloaderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val FANFICTION_ID_KEY = "FANFICTION_ID"
        const val CHAPTER_ID_KEY = "CHAPTER_ID"
        const val MAX_RETRY_AUTHORIZED = 2
    }

    @Inject lateinit var serviceRegular: RegularCrawlService
    @Inject lateinit var fanfictionBuilder: FanfictionBuilder
    @Inject lateinit var fanfictionDao: FanfictionDao

    override suspend fun doWork(): Result {

        MainApplication.getComponent(applicationContext).inject(this)

        val fanfictionId = inputData.getString(FANFICTION_ID_KEY) ?: ""
        val chapterId = inputData.getString(CHAPTER_ID_KEY) ?: ""

        val request = serviceRegular.getFanfiction(fanfictionId, chapterId)
        val response = request.execute()
        if (response.isSuccessful) {
            response.body()?.let {
                val chapterContent = fanfictionBuilder.extractChapter(it.string())
                Thread {
                    fanfictionDao.updateChapter(
                        content = chapterContent,
                        chapterId = chapterId,
                        fanfictionId = fanfictionId
                    )
                }.start()
            }
            FFLogger.d(FFLogger.EVENT_KEY, "$fanfictionId/$chapterId success")
            return Result.success()
        } else {
            return if (runAttemptCount > MAX_RETRY_AUTHORIZED) {
                FFLogger.d(FFLogger.EVENT_KEY, "$fanfictionId/$chapterId failure")
                Result.failure()
            } else {
                FFLogger.d(FFLogger.EVENT_KEY, "$fanfictionId/$chapterId retry")
                Result.retry()
            }
        }
    }
}

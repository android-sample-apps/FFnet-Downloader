package fr.ffnet.downloader.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.common.MainApplication
import javax.inject.Inject

class PeriodicCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject lateinit var repository: DownloaderRepository

    override suspend fun doWork(): Result {

        MainApplication.getComponent(applicationContext).inject(this)

        val allWatchingFanfictions = repository.getAllWatchingFanfictions()

        if (allWatchingFanfictions.isNotEmpty()) {

            allWatchingFanfictions.forEach { fanfiction ->
                repository.loadFanfictionInfo(fanfiction.id)
                repository.downloadChapters(fanfiction.id)
            }

        } else {
            FFLogger.d(EVENT_KEY, "Nothing to check")
        }
        return Result.success()
    }
}

package fr.ffnet.downloader.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultInternetFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultServerFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.search.Fanfiction
import javax.inject.Inject

class PeriodicCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "ffnet_periodic"
        private const val NOTIFICATION_ID = 1000
    }

    @Inject lateinit var repository: DownloaderRepository

    override suspend fun doWork(): Result {

        MainApplication.getComponent(applicationContext).inject(this)

        val allWatchingFanfictions = repository.getAllWatchingFanfictions()

        if (allWatchingFanfictions.isNotEmpty()) {

            allWatchingFanfictions.forEach { watchedFanfiction ->
                when (val fanfictionResult = repository.loadFanfictionInfo(watchedFanfiction.id)) {
                    is FanfictionRepositoryResultSuccess -> {
                        val nbChaptersToSync = fanfictionResult.fanfictionInfo.nbChapters - fanfictionResult.fanfictionInfo.nbSyncedChapters
                        if (nbChaptersToSync > 0) {
                            showNotification(fanfictionResult.fanfictionInfo, nbChaptersToSync)
                            FFLogger.d(
                                EVENT_KEY,
                                "$nbChaptersToSync chapters to sync for fanfiction ${watchedFanfiction.id}"
                            )
                            repository.downloadChapters(watchedFanfiction.id)
                        } else {
                            FFLogger.d(
                                EVENT_KEY,
                                "No chapters to sync for fanfiction ${watchedFanfiction.id}"
                            )
                        }
                    }
                    FanfictionRepositoryResultFailure,
                    FanfictionRepositoryResultServerFailure,
                    FanfictionRepositoryResultInternetFailure -> {
                        FFLogger.d(
                            EVENT_KEY,
                            "Failed periodic check for fanfiction ${watchedFanfiction.id}"
                        )
                        return Result.failure()
                    }
                }
            }

        } else {
            FFLogger.d(EVENT_KEY, "Nothing to check")
        }
        return Result.success()
    }

    private fun showNotification(fanfictionInfo: Fanfiction, nbChaptersToSync: Int) {
        FFLogger.d(EVENT_KEY, "Showing notification")
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Fanfiction syncing")
            .setContentText("Downloading $nbChaptersToSync chapters for ${fanfictionInfo.title}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.resources.getString(R.string.notification_channel_name)
            val descriptionText = applicationContext.resources.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            NotificationManagerCompat
                .from(applicationContext)
                .notify(NOTIFICATION_ID, builder.build())
        }
    }
}

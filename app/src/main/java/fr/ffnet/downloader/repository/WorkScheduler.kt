package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY

class WorkScheduler(
    private val workManager: WorkManager
) {

    companion object {
        private const val PERIODIC_CHECK = "PERIODIC_UPDATE_CHECK"
        private const val PERIODIC_INTERVAL = 5L
    }

    private fun getUniqueWorkName(fanfictionId: String, chapterId: String): String {
        return "$fanfictionId-$chapterId"
    }

    fun getWorkInfosForUniqueWorkLiveData(
        fanfictionId: String
    ): LiveData<List<WorkInfo>> {
        return workManager.getWorkInfosByTagLiveData(fanfictionId)
    }

    fun downloadChapter(fanfictionId: String, chapterId: String) {
        FFLogger.d(EVENT_KEY, "Scheduling chapter download for $fanfictionId/$chapterId")
        workManager.enqueueUniqueWork(
            getUniqueWorkName(fanfictionId, chapterId),
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequest.Builder(DownloaderWorker::class.java)
                .addTag(fanfictionId)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putString(DownloaderWorker.FANFICTION_ID_KEY, fanfictionId)
                        .putString(DownloaderWorker.CHAPTER_ID_KEY, chapterId)
                        .build()
                )
                .build()
        )
    }

    fun stopSyncing(fanfictionId: String) {
        workManager.getWorkInfosByTag(fanfictionId).cancel(true)
    }
}

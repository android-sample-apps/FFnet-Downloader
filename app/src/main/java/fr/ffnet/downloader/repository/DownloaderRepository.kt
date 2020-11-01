package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy.KEEP
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultInternetFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultServerFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.repository.DownloaderWorker.Companion.FANFICTION_ID_KEY
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.utils.FanfictionBuilder
import fr.ffnet.downloader.utils.FanfictionConverter
import java.io.IOException

class DownloaderRepository(
    private val service: CrawlService,
    private val fanfictionBuilder: FanfictionBuilder,
    private val fanfictionDao: FanfictionDao,
    private val converter: FanfictionConverter,
    private val workManager: WorkManager
) {

    fun getDownloadState(fanfictionId: String): LiveData<List<WorkInfo>> {
        return workManager.getWorkInfosForUniqueWorkLiveData(fanfictionId)
    }

    fun downloadChapters(fanfictionId: String) {
        val chapterList = fanfictionDao.getChaptersToSync(fanfictionId)
        if (chapterList.isNotEmpty()) {
            workManager.enqueueUniqueWork(
                fanfictionId,
                KEEP,
                OneTimeWorkRequest.Builder(DownloaderWorker::class.java)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .setInputData(Data.Builder().putString(FANFICTION_ID_KEY, fanfictionId).build())
                    .build()
            )
        }
    }

    fun loadFanfictionInfo(fanfictionId: String): FanfictionRepositoryResult {
        return try {
            val response = service.getFanfiction(fanfictionId).execute()
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->

                    val existingFanfiction = fanfictionDao.getFanfiction(fanfictionId)
                    val existingChapters = fanfictionDao.getChaptersIds(fanfictionId)

                    val (firstChapter, remoteFanfiction) = fanfictionBuilder.buildFanfiction(
                        fanfictionId, responseBody.string()
                    )

                    val localFanfiction = existingFanfiction?.let {
                        remoteFanfiction.copy(
                            isWatching = it.isWatching
                        )
                    } ?: remoteFanfiction

                    fanfictionDao.insertFanfiction(
                        converter.toFanfictionEntity(localFanfiction)
                    )

                    val chapterList = if (existingChapters.isNotEmpty()) {
                        localFanfiction.chapterList.filter { it.id !in existingChapters }
                    } else {
                        localFanfiction.chapterList
                    }
                    fanfictionDao.insertChapterList(
                        converter.toChapterEntityList(fanfictionId, chapterList)
                    )
                    fanfictionDao.updateChapter(
                        content = firstChapter,
                        isSynced = true,
                        fanfictionId = fanfictionId,
                        chapterId = "1"
                    )
                    FanfictionRepositoryResultSuccess(localFanfiction)
                } ?: FanfictionRepositoryResultFailure
            } else {
                FanfictionRepositoryResultServerFailure
            }
        } catch (exception: IOException) {
            FanfictionRepositoryResultInternetFailure
        }
    }

    fun schedulePeriodicJob() {

    }

    sealed class FanfictionRepositoryResult {
        data class FanfictionRepositoryResultSuccess(val fanfictionInfo: Fanfiction) : FanfictionRepositoryResult()
        object FanfictionRepositoryResultFailure : FanfictionRepositoryResult()
        object FanfictionRepositoryResultServerFailure : FanfictionRepositoryResult()
        object FanfictionRepositoryResultInternetFailure : FanfictionRepositoryResult()
    }
}

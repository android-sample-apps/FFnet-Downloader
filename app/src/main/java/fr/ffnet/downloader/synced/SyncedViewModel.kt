package fr.ffnet.downloader.synced

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.synced.SyncedViewModel.SyncedFanfictionsResult.NoSyncedFanfictions
import fr.ffnet.downloader.synced.SyncedViewModel.SyncedFanfictionsResult.SyncedFanfictions
import fr.ffnet.downloader.utils.FanfictionUIBuilder
import fr.ffnet.downloader.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncedViewModel(
    private val resources: Resources,
    private val downloaderRepository: DownloaderRepository,
    private val databaseRepository: DatabaseRepository,
    private val fanfictionUIBuilder: FanfictionUIBuilder
) : ViewModel() {

    private lateinit var fanfictionResult: LiveData<SyncedFanfictionsResult>
    var fanfictionRefreshResult: SingleLiveEvent<FanfictionRefreshResult> = SingleLiveEvent()

    fun loadSyncedFanfictions(): LiveData<SyncedFanfictionsResult> {
        fanfictionResult = Transformations.map(databaseRepository.getSyncedFanfictions()) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                val fanfictionUIList = fanfictionList.map {
                    fanfictionUIBuilder.buildFanfictionUI(
                        fanfiction = it,
                        shouldShowAuthor = true
                    )
                }
                val title = FanfictionUIItem.FanfictionUITitle(
                    shouldShowSyncAllButton = fanfictionUIList.any { it.isDownloadComplete.not() },
                    title = resources.getString(R.string.synced_title)
                )
                SyncedFanfictions(
                    fanfictionUIItemList = listOf(title).plus(fanfictionUIList)
                )
            } else {
                NoSyncedFanfictions
            }
        }
        return fanfictionResult
    }

    fun refreshSyncedInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            FFLogger.d(EVENT_KEY, "Refreshing synced fanfictions")
            val fanfictionResult = fanfictionResult.value
            if (fanfictionResult is SyncedFanfictions) {
                val allFanfictionResult = fanfictionResult.fanfictionUIItemList
                    .filterIsInstance<FanfictionUIItem.FanfictionUI>()
                    .map { fanfiction ->
                        downloaderRepository.loadFanfictionInfo(fanfiction.id)
                    }
                if (allFanfictionResult.all { it is FanfictionRepositoryResultSuccess }) {
                    fanfictionRefreshResult.postValue(FanfictionRefreshResult.Refreshed)
                } else {
                    fanfictionRefreshResult.postValue(FanfictionRefreshResult.NotRefreshed)
                }
            }
        }
    }

    fun syncAllUnsyncedChapters() {
        viewModelScope.launch(Dispatchers.IO) {
            FFLogger.d(EVENT_KEY, "Syncing all unsynced chapters")
            val fanfictionResult = fanfictionResult.value
            if (fanfictionResult is SyncedFanfictions) {
                val fanfictionsToSync = fanfictionResult.fanfictionUIItemList
                    .filterIsInstance<FanfictionUIItem.FanfictionUI>()
                    .filter { it.isDownloadComplete.not() }
                    .map {
                        downloaderRepository.downloadChapters(it.id)
                    }
                FFLogger.d(EVENT_KEY, "Found ${fanfictionsToSync.size} fanfictions to sync")
            }
        }
    }

    sealed class SyncedFanfictionsResult {
        data class SyncedFanfictions(
            val fanfictionUIItemList: List<FanfictionUIItem>
        ) : SyncedFanfictionsResult()

        object NoSyncedFanfictions : SyncedFanfictionsResult()
    }

    sealed class FanfictionRefreshResult {
        object Refreshed : FanfictionRefreshResult()
        object NotRefreshed : FanfictionRefreshResult()
    }
}

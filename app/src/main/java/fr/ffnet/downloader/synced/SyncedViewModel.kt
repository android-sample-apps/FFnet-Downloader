package fr.ffnet.downloader.synced

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val downloaderRepository: DownloaderRepository,
    private val databaseRepository: DatabaseRepository,
    private val fanfictionUIBuilder: FanfictionUIBuilder
) : ViewModel() {

    private lateinit var fanfictionResult: LiveData<SyncedFanfictionsResult>
    var fanfictionRefreshResult: SingleLiveEvent<FanfictionRefreshResult> = SingleLiveEvent()

    fun getFanfictionList(): LiveData<SyncedFanfictionsResult> = fanfictionResult

    fun loadFanfictions() {
        fanfictionResult = Transformations.map(databaseRepository.getSyncedFanfictions()) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                SyncedFanfictions(
                    fanfictionList.map {
                        fanfictionUIBuilder.buildFanfictionUI(it)
                    }
                )
            } else {
                NoSyncedFanfictions
            }
        }
    }

    fun refreshSyncedInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            FFLogger.d(EVENT_KEY, "Refreshing synced fanfictions")
            val fanfictionResult = fanfictionResult.value
            if (fanfictionResult is SyncedFanfictions) {
                val allFanfictionResult = fanfictionResult.fanfictionList.map { fanfiction ->
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
                val fanfictionsToSync = fanfictionResult.fanfictionList.filter {
                    it.isDownloadComplete.not()
                }
                FFLogger.d(EVENT_KEY, "Found ${fanfictionsToSync.size} fanfictions to sync")
                fanfictionsToSync.map { fanfiction ->
                    downloaderRepository.downloadChapters(fanfiction.id)
                }
            }
        }
    }

    sealed class SyncedFanfictionsResult {
        data class SyncedFanfictions(val fanfictionList: List<FanfictionSyncedUIModel>) : SyncedFanfictionsResult()
        object NoSyncedFanfictions : SyncedFanfictionsResult()
    }

    sealed class FanfictionRefreshResult {
        object Refreshed : FanfictionRefreshResult()
        object NotRefreshed : FanfictionRefreshResult()
    }
}

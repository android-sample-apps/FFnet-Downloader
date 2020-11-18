package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.RUNNING
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.utils.FanfictionUIBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FanfictionViewModel(
    private val resources: Resources,
    private val downloaderRepository: DownloaderRepository,
    private val databaseRepository: DatabaseRepository,
    private val fanfictionUIBuilder: FanfictionUIBuilder
) : ViewModel() {

    private lateinit var fanfictionInfo: LiveData<FanfictionSyncedUIModel>
    private lateinit var chapterList: LiveData<List<ChapterUIModel>>
    private lateinit var jobRunning: LiveData<Boolean>
    private val chapterSyncStateMediator: MediatorLiveData<ChapterSyncState> by lazy {
        MediatorLiveData<ChapterSyncState>()
    }

    fun getChapterSyncState(): MediatorLiveData<ChapterSyncState> = chapterSyncStateMediator

    fun getFanfictionInfo(): LiveData<FanfictionSyncedUIModel> = fanfictionInfo

    fun loadFanfictionInfo(fanfictionId: String) {
        fanfictionInfo = Transformations.map(databaseRepository.getFanfictionInfo(fanfictionId)) {
            fanfictionUIBuilder.buildFanfictionUI(it)
        }
    }

    fun syncChapters(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            downloaderRepository.downloadChapters(fanfictionId)
        }
    }

    fun loadChapterInfo(fanfictionId: String) {
        loadChapters(fanfictionId)
        loadSyncButtonState(fanfictionId)
        chapterSyncStateMediator.apply {
            addSource(chapterList) {
                chapterSyncStateMediator.value = combineLatestData(
                    chapterList = chapterList.value ?: emptyList(),
                    isJobRunning = jobRunning.value ?: false
                )
            }
            addSource(jobRunning) {
                chapterSyncStateMediator.value = combineLatestData(
                    chapterList = chapterList.value ?: emptyList(),
                    isJobRunning = jobRunning.value ?: false
                )
            }
        }
    }

    private fun combineLatestData(
        chapterList: List<ChapterUIModel>,
        isJobRunning: Boolean
    ): ChapterSyncState {
        val syncButtonState = when {
            chapterList.all { it.isSynced } -> SyncButtonState.Synced
            isJobRunning -> SyncButtonState.Syncing(resources.getString(R.string.download_button_downloading))
            else -> SyncButtonState.Unsynced(resources.getString(R.string.download_button_title))
        }
        return ChapterSyncState(
            syncButtonState = syncButtonState,
            chapterList = chapterList
        )
    }

    private fun loadSyncButtonState(fanfictionId: String) {
        jobRunning = Transformations.map(downloaderRepository.getDownloadState(fanfictionId)) { workInfo ->
            workInfo.any {
                it.state in listOf(
                    ENQUEUED,
                    RUNNING
                )
            }
        }
    }

    private fun loadChapters(fanfictionId: String) {
        chapterList = Transformations.map(databaseRepository.getChapters(fanfictionId)) { chapterList ->
            chapterList.map {
                ChapterUIModel(
                    id = it.chapterId,
                    title = it.title,
                    status = resources.getString(
                        when (it.content.isNotEmpty()) {
                            true -> R.string.download_info_chapter_status_synced
                            false -> R.string.download_info_chapter_status_not_synced
                        }
                    ),
                    isSynced = it.content.isNotEmpty()
                )
            }
        }
    }

    data class ChapterSyncState(
        val syncButtonState: SyncButtonState,
        val chapterList: List<ChapterUIModel>
    )

    sealed class SyncButtonState {
        object Synced : SyncButtonState()
        data class Syncing(val buttonTitle: String) : SyncButtonState()
        data class Unsynced(val buttonTitle: String) : SyncButtonState()
    }
}

package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.utils.FanfictionUIBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FanfictionViewModel(
    private val resources: Resources,
    private val apiRepository: DownloaderRepository,
    private val dbRepository: DatabaseRepository,
    private val fanfictionUIBuilder: FanfictionUIBuilder
) : ViewModel() {

    private lateinit var chapterList: LiveData<List<ChapterUIModel>>
    private lateinit var fanfictionInfo: LiveData<FanfictionSyncedUIModel>
    private lateinit var downloadButtonState: LiveData<SyncState>

    fun getChapterList(): LiveData<List<ChapterUIModel>> = chapterList

    fun getFanfictionInfo(): LiveData<FanfictionSyncedUIModel> = fanfictionInfo

    fun getDownloadButtonState(): LiveData<SyncState> = downloadButtonState

    fun loadFanfictionInfo(fanfictionId: String) {
        downloadButtonState = Transformations.map(apiRepository.getDownloadState(fanfictionId)) { workInfo ->
            val isDownloading = workInfo.any {
                it.state in listOf(
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.RUNNING
                )
            }
            if (isDownloading) {
                SyncState.Syncing(resources.getString(R.string.download_button_downloading))
            } else {
                SyncState.Sync(resources.getString(R.string.download_button_title))
            }
        }

        fanfictionInfo = Transformations.map(dbRepository.getFanfictionInfo(fanfictionId)) {
            fanfictionUIBuilder.buildFanfictionUI(it)
        }
    }

    fun syncChapters(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.downloadChapters(fanfictionId)
        }
    }

    fun loadChapters(fanfictionId: String) {
        chapterList = Transformations.map(dbRepository.getChapters(fanfictionId)) { chapterList ->
            chapterList.map {
                ChapterUIModel(
                    id = it.chapterId,
                    title = it.title,
                    status = resources.getString(
                        when (it.content.isNotEmpty()) {
                            true -> R.string.download_info_chapter_status_synced
                            false -> R.string.download_info_chapter_status_not_synced
                        }
                    )
                )
            }
        }
    }

    sealed class SyncState {
        abstract val buttonTitle: String
        data class Syncing(override val buttonTitle: String) : SyncState()
        data class Sync(override val buttonTitle: String) : SyncState()
    }
}

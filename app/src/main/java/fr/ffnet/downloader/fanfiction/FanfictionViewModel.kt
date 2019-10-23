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
import fr.ffnet.downloader.utils.DateFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FanfictionViewModel(
    private val resources: Resources,
    private val apiRepository: DownloaderRepository,
    private val dbRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {

    private lateinit var chapterList: LiveData<List<ChapterUIModel>>

    private lateinit var fanfictionInfo: LiveData<FanfictionUIModel>

    private lateinit var downloadButtonState: LiveData<String>

    fun getChapterList(): LiveData<List<ChapterUIModel>> = chapterList

    fun getFanfictionInfo(): LiveData<FanfictionUIModel> = fanfictionInfo

    fun getDownloadButtonState(): LiveData<String> = downloadButtonState

    fun loadFanfictionInfo(fanfictionId: String) {
        downloadButtonState = Transformations.map(apiRepository.getWorkManagerStateForFanfiction(fanfictionId)) {
            it.firstOrNull()?.let { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> "ENQUEUED"
                    WorkInfo.State.RUNNING  -> "RUNNING "
                    WorkInfo.State.SUCCEEDED -> "SUCCEEDED"
                    WorkInfo.State.FAILED -> "FAILED"
                    WorkInfo.State.BLOCKED -> "BLOCKED"
                    WorkInfo.State.CANCELLED -> "CANCELLED"
                }
            } ?: "Not found"
        }

        fanfictionInfo = Transformations.map(dbRepository.getFanfictionInfo(fanfictionId)) {
            FanfictionUIModel(
                id = it.id,
                title = it.title,
                words = it.words.toString(),
                summary = it.summary,
                updatedDate = dateFormatter.format(it.updatedDate),
                publishedDate = dateFormatter.format(it.publishedDate),
                syncedDate = dateFormatter.format(it.fetchedDate),
                progression = resources.getString(
                    R.string.download_info_chapters_value,
                    it.nbSyncedChapters,
                    it.nbChapters
                )
            )
        }
    }

    fun syncChapters(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.downloadChaptersWithWorkManager(fanfictionId)
        }
    }

    fun loadChapters(fanfictionId: String) {
        chapterList = Transformations.map(
            dbRepository.getChapters(fanfictionId)
        ) { chapterList ->
            chapterList.map {
                ChapterUIModel(
                    id = it.chapterId,
                    title = it.title,
                    status = resources.getString(
                        when (it.isSynced) {
                            true  -> R.string.download_info_chapter_status_synced
                            false -> R.string.download_info_chapter_status_not_synced
                        }
                    )
                )
            }
        }
    }

//    fun loadChapterDownloadingState() {
//        downloadButtonState = Transformations.map(
//            apiRepository.getDownloadState()
//        ) { downloadStatus ->
//            when (downloadStatus) {
//                DownloadOngoing -> ChapterStatusState.ChapterSyncing
//                ChaptersDownloadResult.DownloadSuccessful -> ChapterStatusState.ChapterSynced
//                ChaptersDownloadResult.ChapterEmpty,
//                ChaptersDownloadResult.RepositoryException,
//                ChaptersDownloadResult.ResponseNotSuccessful -> ChapterStatusState.ChapterSyncError(
//                    resources.getString(R.string.download_chapter_error)
//                )
//                ChaptersDownloadResult.NothingToDownload -> ChapterStatusState.ChaptersAlreadySynced(
//                    resources.getString(R.string.download_chapter_already_done)
//                )
//            }
//        }
//    }

    sealed class ChapterStatusState {
        object ChapterSynced : ChapterStatusState()
        object ChapterSyncing : ChapterStatusState()
        data class ChapterSyncError(val message: String) : ChapterStatusState()
        data class ChaptersAlreadySynced(val message: String) : ChapterStatusState()
    }
}

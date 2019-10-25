package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

    // TODO
    // Put chapter list in an other place
    // Display author
    // Display fanfiction picture
    // Remove "SYNC CHAPTERS"
    // Add button with icon EPUB
    // Add button with icon PDF
    // When clicking on an export button, disable both buttons until task completed
    // Remove fanfiction from database when done
    // Remove Synced information
    // Show number of chapters
    // Show number of downloaded chapters

    private lateinit var chapterList: LiveData<List<ChapterUIModel>>
    private lateinit var fanfictionInfo: LiveData<FanfictionDisplayModel>
    private lateinit var downloadButtonState: LiveData<Pair<String, Boolean>>

    fun getChapterList(): LiveData<List<ChapterUIModel>> = chapterList

    fun getFanfictionInfo(): LiveData<FanfictionDisplayModel> = fanfictionInfo

    fun getDownloadButtonState(): LiveData<Pair<String, Boolean>> = downloadButtonState

    fun loadFanfictionInfo(fanfictionId: String) {
        downloadButtonState = Transformations.map(apiRepository.getDownloadState(fanfictionId)) {
            it.firstOrNull()?.let { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.RUNNING -> resources.getString(R.string.download_button_downloading) to false
                    else -> resources.getString(R.string.download_button_title) to true
                }
            } ?: resources.getString(R.string.download_button_title) to true
        }

        fanfictionInfo = Transformations.map(dbRepository.getFanfictionInfo(fanfictionId)) {
            FanfictionDisplayModel(
                id = it.id,
                title = it.title,
                words = it.words.toString(),
                summary = it.summary,
                updatedDate = dateFormatter.format(it.updatedDate),
                publishedDate = dateFormatter.format(it.publishedDate),
                syncedDate = dateFormatter.format(it.fetchedDate),
                progressionText = resources.getString(
                    R.string.download_info_chapters_value,
                    it.nbSyncedChapters,
                    it.nbChapters
                ),
                progression = it.nbSyncedChapters,
                nbChapters = it.nbChapters
            )
        }
    }

    fun syncChapters(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.downloadChapters(fanfictionId)
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
                            true -> R.string.download_info_chapter_status_synced
                            false -> R.string.download_info_chapter_status_not_synced
                        }
                    )
                )
            }
        }
    }
}

class FanfictionViewModelFactory(
    private val creator: () -> FanfictionViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

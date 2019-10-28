package fr.ffnet.downloader.fanfiction.info

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionDisplayModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.DateFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FanfictionInfoViewModel(
    private val resources: Resources,
    private val apiRepository: DownloaderRepository,
    private val dbRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {

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

    private lateinit var fanfictionInfo: LiveData<FanfictionDisplayModel>
    private lateinit var downloadButtonState: LiveData<Pair<String, Boolean>>

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
                fanfictionTitleAndAuthor = resources.getString(
                    R.string.fanfiction_title_author,
                    it.title,
                    it.authorName
                ),
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
                nbChapters = it.nbChapters,
                imageUrl = it.imageUrl
            )
        }
    }

    fun syncChapters(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.downloadChapters(fanfictionId)
        }
    }
}

class FanfictionInfoViewModelFactory(
    private val creator: () -> FanfictionInfoViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

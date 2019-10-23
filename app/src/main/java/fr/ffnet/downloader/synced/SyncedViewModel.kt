package fr.ffnet.downloader.synced

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfictionutils.EpubBuilder
import fr.ffnet.downloader.fanfictionutils.PdfBuilder
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.LiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncedViewModel(
    private val resources: Resources,
    private val repository: DatabaseRepository,
    private val dateFormatter: DateFormatter,
    private val pdfBuilder: PdfBuilder,
    private val epubBuilder: EpubBuilder
) : ViewModel() {

    private val _getFile = MutableLiveData<LiveEvent<String>>()
    val getFile: LiveData<LiveEvent<String>> get() = _getFile

    private lateinit var fanfictionResult: LiveData<SyncedFanfictionsResult>

    fun getFanfictionList(): LiveData<SyncedFanfictionsResult> = fanfictionResult

    fun loadFanfictionsFromDb() {
        fanfictionResult = Transformations.map(
            repository.getSyncedFanfictions()
        ) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                SyncedFanfictionsResult.SyncedFanfictions(fanfictionList.map { fanfiction ->
                    FanfictionSyncedUIModel(
                        id = fanfiction.id,
                        title = fanfiction.title,
                        updatedDate = dateFormatter.format(fanfiction.updatedDate),
                        publishedDate = dateFormatter.format(fanfiction.publishedDate),
                        fetchedDate = dateFormatter.format(fanfiction.fetchedDate),
                        chapters = resources.getString(
                            R.string.synced_fanfictions_chapters,
                            fanfiction.nbSyncedChapters,
                            fanfiction.nbChapters
                        )
                    )
                })
            } else {
                SyncedFanfictionsResult.NoSyncedFanfictions
            }
        }
    }

    fun unsyncFanfiction(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unsyncFanfiction(fanfictionId)
        }
    }

    fun buildPdf(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
                val file = pdfBuilder.buildPdf(fanfiction)
                _getFile.postValue(LiveEvent(file))
            }
        }
    }

    fun buildEpub(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
                val fileName = epubBuilder.buildEpub(fanfiction)
                _getFile.postValue(LiveEvent(fileName))
            }
        }
    }

    sealed class SyncedFanfictionsResult {
        data class SyncedFanfictions(val fanfictionList: List<FanfictionSyncedUIModel>) : SyncedFanfictionsResult()
        object NoSyncedFanfictions : SyncedFanfictionsResult()
    }
}

class SyncedViewModelFactory(
    private val creator: () -> SyncedViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

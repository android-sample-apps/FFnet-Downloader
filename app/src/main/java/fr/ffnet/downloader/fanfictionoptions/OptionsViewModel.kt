package fr.ffnet.downloader.fanfictionoptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.fanfictionutils.EpubBuilder
import fr.ffnet.downloader.fanfictionutils.PdfBuilder
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.*
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter,
    private val downloaderRepository: DownloaderRepository,
    private val pdfBuilder: PdfBuilder,
    private val epubBuilder: EpubBuilder
) : ViewModel() {

    private val _getFile: SingleLiveEvent<String> = SingleLiveEvent()
    val getFile: SingleLiveEvent<String> get() = _getFile

    private val navigateToFanfictionActivity: SingleLiveEvent<Unit> = SingleLiveEvent()
    val navigateToFanfiction: SingleLiveEvent<Unit>
        get() = navigateToFanfictionActivity

    private lateinit var displayModel: LiveData<OptionsDisplayModel>
    fun getDisplayModel() = displayModel

    fun load(fanfictionId: String) {
        displayModel = Transformations.map(databaseRepository.getFanfictionInfo(fanfictionId)) { fanfiction ->
            OptionsDisplayModel(
                title = fanfiction.title,
                publishedDate = dateFormatter.format(fanfiction.publishedDate),
                updatedDate = dateFormatter.format(fanfiction.updatedDate),
                fetchedDate = dateFormatter.format(fanfiction.fetchedDate),
                shouldShowFetchedDate = fanfiction.fetchedDate != null,
                shouldShowPdfExport = fanfiction.nbSyncedChapters > 0,
                shouldShowEpubExport = fanfiction.nbSyncedChapters > 0
            )
        }
    }

    fun loadFanfictionInfo(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fanfictionResult = downloaderRepository.loadFanfictionInfo(fanfictionId)
            if (fanfictionResult is FanfictionRepositoryResultSuccess) {
                navigateToFanfictionActivity.postValue(Unit)
            }
        }
    }

    fun unsyncFanfiction(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.unsyncFanfiction(fanfictionId)
        }
    }

    fun buildPdf(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
                val file = pdfBuilder.buildPdf(fanfiction)
                _getFile.postValue(file)
            }
        }
    }

    fun buildEpub(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
                val fileName = epubBuilder.buildEpub(fanfiction)
                _getFile.postValue(fileName)
            }
        }
    }
}

data class OptionsDisplayModel(
    val title: String,
    val publishedDate: String,
    val updatedDate: String,
    val fetchedDate: String,
    val shouldShowFetchedDate: Boolean,
    val shouldShowPdfExport: Boolean,
    val shouldShowEpubExport: Boolean
)

class OptionsViewModelFactory(
    private val creator: () -> OptionsViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

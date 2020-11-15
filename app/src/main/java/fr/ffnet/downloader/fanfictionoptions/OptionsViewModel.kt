package fr.ffnet.downloader.fanfictionoptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val downloaderRepository: DownloaderRepository,
    private val pdfBuilder: PdfBuilder,
    private val epubBuilder: EpubBuilder,
    private val _getFile: SingleLiveEvent<Pair<String, String>> = SingleLiveEvent(),
    private val navigateToFanfictionActivity: SingleLiveEvent<String> = SingleLiveEvent()
) : ViewModel() {

    val getFile: LiveData<Pair<String, String>>
        get() = _getFile

    val navigateToFanfiction: SingleLiveEvent<String>
        get() = navigateToFanfictionActivity

    fun loadFanfictionInfo(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFanfictionInDatabase = databaseRepository.isFanfictionInDatabase(fanfictionId)
            if (isFanfictionInDatabase) {
                navigateToFanfictionActivity.postValue(fanfictionId)
            } else {
                val fanfictionResult = downloaderRepository.loadFanfictionInfo(fanfictionId)
                if (fanfictionResult is FanfictionRepositoryResultSuccess) {
                    navigateToFanfictionActivity.postValue(fanfictionId)
                }
            }
        }
    }

    fun unsyncFanfiction(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.unsyncFanfiction(fanfictionId)
        }
    }

    fun buildPdf(absolutePath: String, fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
                val fileName = pdfBuilder.buildPdf(absolutePath, fanfiction)
                _getFile.postValue(fileName to absolutePath)
            }
        }
    }

    fun buildEpub(absolutePath: String, fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
                val fileName = epubBuilder.buildEpub(absolutePath, fanfiction)
                _getFile.postValue(fileName to absolutePath)
            }
        }
    }
}

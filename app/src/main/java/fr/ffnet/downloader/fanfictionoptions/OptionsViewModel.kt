package fr.ffnet.downloader.fanfictionoptions

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultInternetFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultServerFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val resources: Resources,
    private val databaseRepository: DatabaseRepository,
    private val downloaderRepository: DownloaderRepository,
    private val pdfBuilder: PdfBuilder,
    private val epubBuilder: EpubBuilder
) : ViewModel() {

    val getFile: MutableLiveData<Pair<String, String>> = SingleLiveEvent()
    val navigateToFanfiction: SingleLiveEvent<String> = SingleLiveEvent()
    val error: SingleLiveEvent<SearchError> = SingleLiveEvent()

    fun loadFanfictionInfo(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFanfictionInDatabase = databaseRepository.isFanfictionInDatabase(fanfictionId)
            if (isFanfictionInDatabase) {
                FFLogger.d(EVENT_KEY, "Fanfiction $fanfictionId is already in database")
                navigateToFanfiction.postValue(fanfictionId)
                downloaderRepository.loadFanfictionInfo(fanfictionId)
            } else {
                when (downloaderRepository.loadFanfictionInfo(fanfictionId)) {
                    is FanfictionRepositoryResultSuccess -> {
                        FFLogger.d(EVENT_KEY, "Fanfiction $fanfictionId loaded successfully")
                        navigateToFanfiction.postValue(fanfictionId)
                    }
                    FanfictionRepositoryResultFailure -> TODO()
                    FanfictionRepositoryResultServerFailure -> TODO()
                    FanfictionRepositoryResultInternetFailure -> TODO()
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
                getFile.postValue(fileName to absolutePath)
            }
        }
    }

    fun buildEpub(absolutePath: String, fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
                val fileName = epubBuilder.buildEpub(absolutePath, fanfiction)
                getFile.postValue(fileName to absolutePath)
            }
        }
    }

    private fun displayErrorMessage(messageResource: Int) {
        val errorMessage = resources.getString(messageResource)
        FFLogger.d(EVENT_KEY, errorMessage)
        error.postValue(
            SearchError.InfoFetchingFailed(errorMessage)
        )
    }

    sealed class SearchError(val message: String) {
        data class UrlNotValid(val msg: String) : SearchError(msg)
        data class InfoFetchingFailed(val msg: String) : SearchError(msg)
    }
}

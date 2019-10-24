package fr.ffnet.downloader.search

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.BuildConfig
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultInternetFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val downloaderRepository: DownloaderRepository,
    private val databaseRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {

    private val navigateToFanfictionActivity: SingleLiveEvent<String> = SingleLiveEvent()
    val navigateToFanfiction: SingleLiveEvent<String>
        get() = navigateToFanfictionActivity

    private val errorPresent: SingleLiveEvent<SearchError> = SingleLiveEvent()
    val sendError: SingleLiveEvent<SearchError>
        get() = errorPresent

    private lateinit var historyList: LiveData<List<HistoryUIModel>>

    fun loadFanfictionInfos(url: String?) {
        val urlTransformationResult = urlTransformer.getFanfictionIdFromUrl(url)
        when (urlTransformationResult) {
            is UrlTransformSuccess -> loadFanfictionInfo(
                urlTransformationResult.id
            )
            is UrlTransformFailure -> errorPresent.postValue(
                SearchError.UrlNotValid(
                    resources.getString(R.string.search_fanfiction_url_error)
                )
            )
        }
    }

    fun loadHistory(): LiveData<List<HistoryUIModel>> {
        historyList = Transformations.map(databaseRepository.loadHistory()) { historyList ->
            historyList.map {
                HistoryUIModel(
                    fanfictionId = it.id,
                    url = "${BuildConfig.API_BASE_URL}s/${it.id}",
                    title = it.title,
                    date = resources.getString(
                        R.string.search_fetched_date,
                        dateFormatter.format(it.fetchedDate)
                    )
                )
            }
        }
        return historyList
    }

    private fun loadFanfictionInfo(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fanfictionResult = downloaderRepository.loadFanfictionInfo(fanfictionId)
            if (fanfictionResult is FanfictionRepositoryResultSuccess
                || databaseRepository.isFanfictionInDatabase(fanfictionId)
            ) {
                navigateToFanfictionActivity.postValue(fanfictionId)
            } else {
                if (fanfictionResult is FanfictionRepositoryResultInternetFailure) {
                    displayErrorMessage(R.string.search_fanfiction_info_server_error)
                } else {
                    displayErrorMessage(R.string.search_fanfiction_info_fetching_error)
                }
            }
        }
    }

    private fun displayErrorMessage(messageResource: Int) {
        errorPresent.postValue(
            SearchError.InfoFetchingFailed(resources.getString(messageResource))
        )
    }

    sealed class SearchError(val message: String) {
        data class UrlNotValid(val msg: String) : SearchError(msg)
        data class InfoFetchingFailed(val msg: String) : SearchError(msg)
    }
}

class SearchViewModelFactory(
    private val creator: () -> SearchViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

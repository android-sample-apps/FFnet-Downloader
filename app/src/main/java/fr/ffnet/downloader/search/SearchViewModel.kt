package fr.ffnet.downloader.search

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.BuildConfig
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultInternetFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.repository.SearchRepository
import fr.ffnet.downloader.synced.FanfictionUIItem
import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUITitle
import fr.ffnet.downloader.synced.FanfictionUIItem.HistoryUI
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.FanfictionUIBuilder
import fr.ffnet.downloader.utils.SingleLiveEvent
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val searchRepository: SearchRepository,
    private val downloaderRepository: DownloaderRepository,
    private val databaseRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter,
    private val fanfictionUIBuilder: FanfictionUIBuilder
) : ViewModel() {

    val navigateToFanfiction: SingleLiveEvent<String> = SingleLiveEvent()
    val sendError: SingleLiveEvent<SearchError> = SingleLiveEvent()


    val searchHistoryResult: MediatorLiveData<List<FanfictionUIItem>> by lazy {
        MediatorLiveData<List<FanfictionUIItem>>()
    }

    private lateinit var historyResult: LiveData<List<FanfictionUIItem>>
    private var searchResult: MutableLiveData<List<FanfictionUIItem>> = MutableLiveData()

    fun emptySearchResult() {
        searchResult.postValue(emptyList())
    }

    fun searchFanfiction(searchText: String?) {
        when (val urlTransformationResult = urlTransformer.getFanfictionIdFromUrl(searchText)) {
            is UrlTransformSuccess -> loadFanfictionInfo(
                urlTransformationResult.id
            )
            is UrlTransformFailure -> {
                FFLogger.d(FFLogger.EVENT_KEY, "Could not get fanfictionId from search $searchText")
                if (searchText != null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val searchList = searchRepository.searchKeywords(searchText.split(" "))
                        if (searchList.isNotEmpty()) {
                            val title = FanfictionUITitle(
                                shouldShowSyncAllButton = false,
                                title = resources.getString(R.string.search_result_title)
                            )
                            val fanfictionResult = searchList.map {
                                fanfictionUIBuilder.buildFanfictionUI(
                                    fanfiction = it,
                                    shouldShowAuthor = true,
                                    shouldShowWords = true,
                                    shouldShowChapterSync = false,
                                    shouldShowExport = false
                                )
                            }
                            searchResult.postValue(listOf(title).plus(fanfictionResult))
                        } else {
                            searchResult.postValue(emptyList())
                        }
                    }
                }
            }
        }
    }

    private fun loadHistory() {
        historyResult = Transformations.map(databaseRepository.loadHistory()) { historyList ->
            val title = FanfictionUITitle(
                shouldShowSyncAllButton = false,
                title = resources.getString(R.string.history_title)
            )
            val historyUIList = historyList.map {
                HistoryUI(
                    fanfictionId = it.id,
                    url = "${BuildConfig.API_BASE_URL}s/${it.id}",
                    title = it.title,
                    date = dateFormatter.format(it.fetchedDate)
                )
            }
            listOf(title).plus(historyUIList)
        }
    }

    fun loadSearchAndHistory() {
        loadHistory()
        searchHistoryResult.apply {
            addSource(historyResult) {
                searchHistoryResult.value = combineLatestData(
                    historyList = historyResult.value ?: emptyList(),
                    searchList = searchResult.value ?: emptyList()
                )
            }
            addSource(searchResult) {
                searchHistoryResult.value = combineLatestData(
                    historyList = historyResult.value ?: emptyList(),
                    searchList = searchResult.value ?: emptyList()
                )
            }
        }
    }

    private fun combineLatestData(
        historyList: List<FanfictionUIItem>,
        searchList: List<FanfictionUIItem>
    ): List<FanfictionUIItem> {
        return searchList.plus(historyList)
    }

    private fun loadFanfictionInfo(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FFLogger.d(FFLogger.EVENT_KEY, "Loading fanfiction info for $fanfictionId")
            val isFanfictionInDatabase = databaseRepository.isFanfictionInDatabase(fanfictionId)
            if (isFanfictionInDatabase) {
                FFLogger.d(FFLogger.EVENT_KEY, "Fanfiction $fanfictionId is already in database")
                navigateToFanfiction.postValue(fanfictionId)
                downloaderRepository.loadFanfictionInfo(fanfictionId)
            } else {
                when (downloaderRepository.loadFanfictionInfo(fanfictionId)) {
                    is FanfictionRepositoryResultSuccess -> {
                        FFLogger.d(
                            FFLogger.EVENT_KEY,
                            "Fanfiction $fanfictionId loaded successfully"
                        )
                        navigateToFanfiction.postValue(fanfictionId)
                    }
                    FanfictionRepositoryResultInternetFailure -> displayErrorMessage(R.string.search_fanfiction_info_server_error)
                    else -> displayErrorMessage(R.string.search_fanfiction_info_fetching_error)
                }
            }
        }
    }

    private fun displayErrorMessage(messageResource: Int) {
        val errorMessage = resources.getString(messageResource)
        FFLogger.d(FFLogger.EVENT_KEY, errorMessage)
        sendError.postValue(
            SearchError.InfoFetchingFailed(errorMessage)
        )
    }

    sealed class SearchError(val message: String) {
        data class UrlNotValid(val msg: String) : SearchError(msg)
        data class InfoFetchingFailed(val msg: String) : SearchError(msg)
    }
}

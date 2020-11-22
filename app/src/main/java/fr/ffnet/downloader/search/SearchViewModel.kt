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
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.SearchRepository
import fr.ffnet.downloader.synced.FanfictionUIItem
import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUITitle
import fr.ffnet.downloader.synced.FanfictionUIItem.HistoryUI
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.SingleLiveEvent
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val searchRepository: SearchRepository,
    private val databaseRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter,
    private val uiBuilder: UIBuilder
) : ViewModel() {

    val navigateToFanfiction: SingleLiveEvent<String> = SingleLiveEvent()

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
            is UrlTransformSuccess -> navigateToFanfiction.postValue(urlTransformationResult.id)
            is UrlTransformFailure -> {
                FFLogger.d(
                    EVENT_KEY,
                    "Could not get fanfictionId from url $searchText, trying to search keywords"
                )
                if (searchText != null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val searchList = searchRepository.searchKeywords(searchText.split(" "))
                        if (searchList.isNotEmpty()) {
                            val title = FanfictionUITitle(
                                shouldShowSyncAllButton = false,
                                title = resources.getString(R.string.search_result_title)
                            )
                            val fanfictionResult = searchList.map {
                                uiBuilder.buildFanfictionUI(
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
            if (historyList.isNotEmpty()) {
                val title = FanfictionUITitle(
                    shouldShowSyncAllButton = false,
                    title = resources.getString(R.string.history_title)
                )
                val historyUIList = historyList.map {
                    HistoryUI(
                        fanfictionId = it.id,
                        url = "${BuildConfig.API_REGULAR_BASE_URL}s/${it.id}",
                        title = it.title,
                        date = dateFormatter.format(it.fetchedDate)
                    )
                }
                listOf(title).plus(historyUIList)
            } else emptyList()
        }
    }

    fun loadSearchAndHistory() {
        loadHistory()

        searchHistoryResult.removeSource(historyResult)
        searchHistoryResult.removeSource(searchResult)

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
}

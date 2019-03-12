package fr.ffnet.downloader.search

import android.content.res.Resources
import androidx.lifecycle.*
import fr.ffnet.downloader.BuildConfig
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.LiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val apiRepository: DownloaderRepository,
    private val dbRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {

    private val navigateToFanfictionActivity = MutableLiveData<LiveEvent<String>>()
    val navigateToFanfiction: LiveData<LiveEvent<String>>
        get() = navigateToFanfictionActivity

    private val presentError = MutableLiveData<LiveEvent<Int>>()
    val displayError: LiveData<LiveEvent<Int>>
        get() = presentError

    private lateinit var historyList: LiveData<List<HistoryUIModel>>

    fun loadFanfictionInfos(url: String?) {
        if (!url.isNullOrEmpty()) {
            val urlTransformationResult = urlTransformer.getFanfictionIdFromUrl(url)
            when (urlTransformationResult) {
                is UrlTransformSuccess -> loadFanfictionInfo(
                    urlTransformationResult.id
                )
                is UrlTransformFailure -> {
                    presentError.value = LiveEvent(R.string.search_fanfiction_url_error)
                }
            }
        }
    }

    fun loadHistory(): LiveData<List<HistoryUIModel>> {
        historyList = Transformations.map(dbRepository.loadHistory()) { historyList ->
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
            val fanfictionResult = apiRepository.loadFanfictionInfo(fanfictionId)
            if (fanfictionResult is FanfictionRepositoryResultSuccess) {
                viewModelScope.launch(Dispatchers.Main) {
                    navigateToFanfictionActivity.value = LiveEvent(fanfictionId)
                }
            }
        }
    }
}

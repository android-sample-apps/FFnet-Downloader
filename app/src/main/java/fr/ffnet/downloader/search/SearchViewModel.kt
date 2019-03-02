package fr.ffnet.downloader.search

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.BuildConfig
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.utils.LiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class SearchViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val repository: DownloaderRepository
) : ViewModel() {

    private val navigateToFanfictionActivity = MutableLiveData<LiveEvent<String>>()
    val navigateToFanfiction: LiveData<LiveEvent<String>>
        get() = navigateToFanfictionActivity

    private lateinit var historyList: LiveData<List<HistoryUIModel>>
//    fun getHistoryList(): LiveData<List<HistoryUIModel>> = historyList

    fun loadFanfictionInfos(url: String?) {
        if (!url.isNullOrEmpty()) {
            val urlTransformationResult = urlTransformer.getIdFromUrl(url)
            CoroutineScope(Dispatchers.IO).launch {
                when (urlTransformationResult) {
                    is UrlTransformSuccess -> loadFanfictionInfo(
                        urlTransformationResult.fanfictionId
                    )
                    is UrlTransformFailure -> TODO()
                }
            }
        }
    }

    fun loadHistory(): LiveData<List<HistoryUIModel>> {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        historyList = Transformations.map(repository.loadHistory()) { historyList ->
            historyList.map {
                HistoryUIModel(
                    fanfictionId = it.id,
                    url = "${BuildConfig.API_BASE_URL}s/${it.id}",
                    title = it.title,
                    date = formatter.format(it.fetchedDate)
                )
            }
        }
        return historyList
    }

    private fun loadFanfictionInfo(fanfictionId: String) {
        val fanfictionResult = repository.loadFanfictionInfo(fanfictionId)
        if (fanfictionResult is FanfictionRepositoryResultSuccess) {
            CoroutineScope(Dispatchers.Main).launch {
                navigateToFanfictionActivity.value = LiveEvent(fanfictionId)
            }
        }
    }
}

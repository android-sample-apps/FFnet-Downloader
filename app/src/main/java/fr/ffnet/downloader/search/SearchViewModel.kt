package fr.ffnet.downloader.search

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.utils.LiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val repository: DownloaderRepository
) : ViewModel() {

    private val navigateToFanfictionActivity = MutableLiveData<LiveEvent<String>>()
    val navigateToFanfiction: LiveData<LiveEvent<String>>
        get() = navigateToFanfictionActivity

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

    private fun loadFanfictionInfo(fanfictionId: String) {
        val fanfictionResult = repository.loadFanfictionInfo(fanfictionId)
        if (fanfictionResult is FanfictionRepositoryResultSuccess) {
            CoroutineScope(Dispatchers.Main).launch {
                navigateToFanfictionActivity.value = LiveEvent(fanfictionId)
            }
        }
    }
}

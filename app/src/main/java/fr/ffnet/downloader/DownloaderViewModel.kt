package fr.ffnet.downloader

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.DownloaderInteractor.FanfictionResult
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat


class DownloaderViewModel(
    private val interactor: DownloaderInteractor,
    private val urlTransformer: UrlTransformer,
    private val resources: Resources
) : ViewModel() {

    val currentFanfiction: MutableLiveData<FanfictionViewModel> by lazy {
        MutableLiveData<FanfictionViewModel>()
    }

    fun loadFanfictionInfos(url: String?) {
        if (!url.isNullOrEmpty()) {
            val urlTransformationResult = urlTransformer.getIdFromUrl(url)
            GlobalScope.launch {
                when (urlTransformationResult) {
                    is UrlTransformSuccess -> loadFanfictionInfo(urlTransformationResult.fanfictionId)
                    is UrlTransformFailure -> TODO()
                }
            }
        }
    }

    private fun loadFanfictionInfo(fanfictionId: String) {
        val fanfictionInfoResult = interactor.loadFanfictionInfo(fanfictionId)
        when (fanfictionInfoResult) {
            is FanfictionResult.FanfictionResultSuccess -> presentFanfictionInfo(fanfictionInfoResult.fanfictionInfo)
            is FanfictionResult.FanfictionResultFailure -> presentFanfictionInfoError()
        }
    }

    private fun presentFanfictionInfo(fanfictionInfo: Fanfiction) {
        with(fanfictionInfo) {
            val fanfictionViewModel = FanfictionViewModel(
                id = id,
                title = title,
                words = resources.getString(R.string.download_info_words, words),
                summary = summary,
                updatedDate = SimpleDateFormat("yyyy-mm-dd").format(updatedDate),
                publishedDate = SimpleDateFormat("yyyy-mm-dd").format(publishedDate),
                chapterList = chapterList.map {
                    ChapterViewModel(
                        id = it.id,
                        title = it.title
                    )
                }
            )
            CoroutineScope(Dispatchers.Main).launch {
                currentFanfiction.value = fanfictionViewModel
            }
        }
    }

    private fun presentFanfictionInfoError() {

    }
}

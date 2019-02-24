package fr.ffnet.downloader

import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DownloaderViewModel(
    private val interactor: DownloaderInteractor,
    private val urlTransformer: UrlTransformer
) : ViewModel() {

    fun loadFanfictionInfos(url: String?) {
        if (!url.isNullOrEmpty()) {
            val urlTransformationResult = urlTransformer.getIdFromUrl(url)
            GlobalScope.launch {
                when (urlTransformationResult) {
                    is UrlTransformSuccess -> interactor.loadFanfictionInfo(urlTransformationResult.id)
                    is UrlTransformFailure -> TODO()
                }
            }
        }
    }
}

package fr.ffnet.downloader.fanfictionutils

import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import javax.inject.Inject

class UrlTransformer @Inject constructor() {

    companion object {
        private const val ID_MATCHER = "\\/s\\/(\\d+)"
    }

    fun getIdFromUrl(url: String): UrlTransformationResult {
        val result = ID_MATCHER.toRegex().find(url)
        return result?.value?.let {
            UrlTransformSuccess(it.replace("/s/", ""))
        } ?: UrlTransformFailure
    }

    sealed class UrlTransformationResult {
        data class UrlTransformSuccess(val fanfictionId: String) : UrlTransformationResult()
        object UrlTransformFailure : UrlTransformationResult()
    }
}

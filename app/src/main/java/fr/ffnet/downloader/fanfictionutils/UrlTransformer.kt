package fr.ffnet.downloader.fanfictionutils

import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.fanfictionutils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import javax.inject.Inject

class UrlTransformer @Inject constructor() {

    companion object {
        private const val FANFICTION_ID_MATCHER = "\\/s\\/(\\d+)"
        private const val PROFILE_ID_MATCHER = "\\/u\\/(\\d+)"
    }

    fun getFanfictionIdFromUrl(url: String): UrlTransformationResult {
        val result = FANFICTION_ID_MATCHER.toRegex().find(url)
        return result?.value?.let {
            UrlTransformSuccess(it.replace("/s/", ""))
        } ?: UrlTransformFailure
    }

    fun getProfileIdFromUrl(url: String): UrlTransformationResult {
        val result = PROFILE_ID_MATCHER.toRegex().find(url)
        return result?.value?.let {
            UrlTransformSuccess(it.replace("/u/", ""))
        } ?: UrlTransformFailure
    }

    sealed class UrlTransformationResult {
        data class UrlTransformSuccess(val id: String) : UrlTransformationResult()
        object UrlTransformFailure : UrlTransformationResult()
    }
}

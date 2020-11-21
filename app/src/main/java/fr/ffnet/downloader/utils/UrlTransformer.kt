package fr.ffnet.downloader.utils

import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import javax.inject.Inject

class UrlTransformer @Inject constructor() {

    companion object {
        private const val FANFICTION_ID_MATCHER = "\\/s\\/(\\d+)"
        private const val PROFILE_ID_MATCHER = "\\/u\\/(\\d+)"
    }

    fun getFanfictionIdFromUrl(url: String?): UrlTransformationResult {
        if (url.isNullOrEmpty()) {
            return UrlTransformFailure
        }
        val result = FANFICTION_ID_MATCHER.toRegex().find(url)
        return result?.value?.let {
            UrlTransformSuccess(it.replace("/s/", ""))
        } ?: UrlTransformFailure
    }

    fun getProfileIdFromUrl(url: String?): UrlTransformationResult {
        if (url.isNullOrEmpty()) {
            return UrlTransformFailure
        }
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

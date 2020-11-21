package fr.ffnet.downloader.repository

import fr.ffnet.downloader.repository.entities.AuthorSearchResult
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.utils.SearchBuilder

class SearchRepository(
    private val mobileServiceRegular: MobileCrawlService,
    private val searchBuilder: SearchBuilder
) {

    companion object {
        private const val SEARCH_TYPE_STORY = "story"
        private const val SEARCH_TYPE_WRITER = "writer"
    }

    fun searchKeywords(keywordList: List<String>): List<Fanfiction> {
        val keywords = keywordList.joinToString("+")
        val response = mobileServiceRegular.search(keywords, SEARCH_TYPE_STORY).execute()

        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                return searchBuilder.buildFanfictionSearchResult(responseBody.string())
            }
        }
        return emptyList()
    }

    fun searchAuthor(keywordList: List<String>): List<AuthorSearchResult> {
        val keywords = keywordList.joinToString("+")
        val response = mobileServiceRegular.search(keywords, SEARCH_TYPE_WRITER).execute()

        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                return searchBuilder.builAuthorSearchResult(responseBody.string())
            }
        }
        return emptyList()
    }
}

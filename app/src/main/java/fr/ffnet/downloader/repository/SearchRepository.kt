package fr.ffnet.downloader.repository

import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.utils.SearchBuilder

class SearchRepository(
    private val service: CrawlService,
    private val searchBuilder: SearchBuilder
) {

    fun searchKeywords(keywordList: List<String>): List<Fanfiction> {
        val keywords = keywordList.joinToString("+")
        val search = service.getSearch(keywords)
        val response = search.execute()

        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                return searchBuilder.buildSearchResult(responseBody.string())
            }
        }
        return emptyList()
    }
}

package fr.ffnet.downloader.utils

import fr.ffnet.downloader.repository.entities.AuthorSearchResult
import fr.ffnet.downloader.search.Fanfiction
import org.joda.time.DateTime
import javax.inject.Inject

class SearchBuilder @Inject constructor(
    private val jsoupParser: JsoupParser
) {

    fun buildFanfictionSearchResult(html: String): List<Fanfiction> {
        val document = jsoupParser.parseHtml(html)
        val selector = document.select(".z-list")
        return selector.map { fanfiction ->
            val fanfictionStuff = fanfiction.select(".z-padtop2").first().text()
            val words = fanfictionStuff.split("Words: ")[1].split(" ")[0].replace(",", "")
            val chapters = fanfictionStuff.split("Chapters: ")[1].split(" ")[0]
            val dates = fanfiction.select("span[data-xutime]")
            val updatedDate = DateTime(dates[0].attr("data-xutime").toLong() * 1000).toDate()
            val publishedDate = DateTime(
                dates[dates.size - 1].attr("data-xutime")
                    .toLong() * 1000
            ).toDate()
            val fanfictionId = fanfiction.select("a.stitle").first().attr("href").split("/")[2]
            Fanfiction(
                id = fanfictionId,
                title = fanfiction.select("a.stitle").text(),
                words = words.toInt(),
                author = fanfiction.select("a").first { it.attr("href").contains("/u/") }.text(),
                summary = "N/A",
                publishedDate = publishedDate,
                updatedDate = updatedDate,
                fetchedDate = null,
                profileType = 0,
                nbChapters = chapters.toInt(),
                nbSyncedChapters = 0,
                chapterList = emptyList()
            )
        }
    }

    fun builAuthorSearchResult(html: String): List<AuthorSearchResult> {
        val document = jsoupParser.parseHtml(html)
        val selector = document.select("form>div>div.bs")
        return selector.map {
            val link = it.select("a")
            val span = it.select("span")
            AuthorSearchResult(
                id = link.attr("href").split("/")[2],
                name = link.text(),
                nbStories = span.select("span").text()
            )
        }
    }
}

package fr.ffnet.downloader.utils

import fr.ffnet.downloader.search.Fanfiction
import org.joda.time.DateTime
import javax.inject.Inject

class SearchBuilder @Inject constructor(
    private val jsoupParser: JsoupParser
) {

    fun buildSearchResult(html: String): List<Fanfiction> {
        val document = jsoupParser.parseHtml(html)
        val selector = document.select(".z-list")
        return selector.map {
            val fanfictionStuff = it.select(".z-padtop2").first().text()
            val words = fanfictionStuff.split("Words: ")[1].split(" ")[0].replace(",", "")
            val chapters = fanfictionStuff.split("Chapters: ")[1].split(" ")[0]
            val dates = it.select("span[data-xutime]")
            val updatedDate = DateTime(dates[0].attr("data-xutime").toLong() * 1000).toDate()
            val publishedDate = DateTime(dates[dates.size - 1].attr("data-xutime").toLong() * 1000).toDate()
            Fanfiction(
                id = it.select("a.stitle").first().attr("href").split("/")[2],
                title = it.select("a.stitle").text(),
                words = words.toInt(),
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
}

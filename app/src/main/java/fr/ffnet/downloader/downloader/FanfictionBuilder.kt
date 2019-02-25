package fr.ffnet.downloader.downloader

import fr.ffnet.downloader.JsoupParser
import org.jsoup.select.Elements
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import javax.inject.Inject

class FanfictionBuilder @Inject constructor(
    private val jsoupParser: JsoupParser
) {

    fun buildFanfiction(id: String, html: String): Fanfiction {

        val document = jsoupParser.parseHtml(html)
        val profileTop = document.select("div#profile_top")
        val dates = profileTop.select("span[data-xutime]")

        val title = profileTop.select("b").first()?.text() ?: "N/A"
        val words = extractWordsNb(profileTop.text())
        val summary = profileTop.select("div").last()?.text() ?: "N/A"
        val published = dates[if (dates.size > 1) 1 else 0]?.attr("data-xutime")?.toLong() ?: 0
        val updated = dates[0]?.attr("data-xutime")?.toLong() ?: 0
        val chapterList = extractChapterList(document.select("#chap_select"), title)

        return Fanfiction(
            id = id,
            title = title,
            words = words,
            summary = summary,
            publishedDate = DateTimeUtils.toDate(Instant.ofEpochMilli(published)),
            updatedDate = DateTimeUtils.toDate(Instant.ofEpochMilli(updated)),
            chapterList = chapterList
        )
    }

    fun extractChapter(html: String): String {
        val document = jsoupParser.parseHtml(html)
        return document.select("#storytext").first().text()
    }

    private fun extractChapterList(select: Elements, title: String): List<Chapter> {
        return if (select.isNotEmpty()) {
            select.first().select("option").map {
                val chapterId = it.attr("value")
                Chapter(
                    id = chapterId,
                    title = it.text().replace("$chapterId. ", "")
                )
            }
        } else {
            listOf(
                Chapter(
                    id = "1",
                    title = title
                )
            )
        }
    }

    private fun extractWordsNb(text: String): Int {
        val split = text.split("Words: ")
        if (split[1].isNotEmpty()) {
            val split2 = split[1].split(" ")
            if (split2.isNotEmpty()) {
                return split2[0].replace(",", "").trim().toInt()
            }
        }
        return 0
    }
}

package fr.ffnet.downloader.fanfictionutils

import fr.ffnet.downloader.profile.Profile
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.utils.JsoupParser
import org.joda.time.DateTime
import org.jsoup.select.Elements
import javax.inject.Inject

class ProfileBuilder @Inject constructor(
    private val jsoupParser: JsoupParser
) {
    fun buildProfile(profileId: String, html: String): Profile {
        val document = jsoupParser.parseHtml(html)

        val name = document.select("#content_wrapper_inner span").first()?.text() ?: "N/A"
        val favoriteStoriesSelector = document.select(".z-list.favstories")
        val myStoriesSelector = document.select(".z-list.mystories")

        return Profile(
            profileId = profileId,
            name = name,
            favoriteFanfictionList = extractFanfictionsFromList(favoriteStoriesSelector),
            myFanfictionList = extractFanfictionsFromList(myStoriesSelector)
        )
    }

    private fun extractFanfictionsFromList(selector: Elements): List<Fanfiction> {
        return selector.map {
            Fanfiction(
                id = it.attr("data-storyId"),
                title = it.attr("data-title"),
                words = it.attr("data-wordcount").toInt(),
                summary = "N/A",
                publishedDate = DateTime(it.attr("data-datesubmit").toLong() * 1000).toDate(),
                updatedDate = DateTime(it.attr("data-dateupdate").toLong() * 1000).toDate(),
                syncedDate = DateTime.now().toDate(),
                nbChapters = it.attr("data-chapters").toInt(),
                nbSyncedChapters = 0,
                chapterList = emptyList()
            )
        }
    }
}
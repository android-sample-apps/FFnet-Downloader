package fr.ffnet.downloader.utils

import fr.ffnet.downloader.profile.Profile
import fr.ffnet.downloader.repository.AuthorRepository.Companion.PROFILE_TYPE_FAVORITE
import fr.ffnet.downloader.repository.AuthorRepository.Companion.PROFILE_TYPE_MY_STORY
import fr.ffnet.downloader.search.Fanfiction
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
            favoriteFanfictionList = extractFanfictionsFromList(
                favoriteStoriesSelector,
                PROFILE_TYPE_FAVORITE
            ),
            myFanfictionList = extractFanfictionsFromList(
                myStoriesSelector,
                PROFILE_TYPE_MY_STORY,
                name
            )
        )
    }

    private fun extractFanfictionsFromList(
        selector: Elements,
        profileType: Int,
        defaultAuthor: String? = null
    ): List<Fanfiction> {
        return selector.map { fanfiction ->
            val fanfictionId = fanfiction.attr("data-storyId")
            println(fanfictionId)
            Fanfiction(
                id = fanfictionId,
                title = fanfiction.attr("data-title"),
                words = fanfiction.attr("data-wordcount").toInt(),
                author = defaultAuthor ?: fanfiction
                    .select("a")
                    .first { it.attr("href").contains("/u/") }
                    .text(),
                summary = "N/A",
                publishedDate = DateTime(
                    fanfiction.attr("data-datesubmit")
                        .toLong() * 1000
                ).toDate(),
                updatedDate = DateTime(fanfiction.attr("data-dateupdate").toLong() * 1000).toDate(),
                fetchedDate = null,
                profileType = profileType,
                nbChapters = fanfiction.attr("data-chapters").toInt(),
                nbSyncedChapters = 0,
                chapterList = emptyList()
            )
        }
    }
}

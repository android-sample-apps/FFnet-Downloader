package fr.ffnet.downloader.fanfictionutils

import fr.ffnet.downloader.profile.Profile
import fr.ffnet.downloader.utils.JsoupParser
import javax.inject.Inject

class ProfileBuilder @Inject constructor(
    private val jsoupParser: JsoupParser
) {
    fun buildProfile(profileId: String, html: String): Profile {
        val document = jsoupParser.parseHtml(html)

        val name = document.select("#content_wrapper_inner span").first()?.text() ?: "N/A"
        val favoritesSelector = document.select(".z-list.favstories")

        return Profile(
            profileId = profileId,
            name = "",
            favoriteFanfictionList = emptyList(),
            myFanfictionList = emptyList()
        )
    }
}
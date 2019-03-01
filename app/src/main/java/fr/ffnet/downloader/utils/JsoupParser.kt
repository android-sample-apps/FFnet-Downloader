package fr.ffnet.downloader.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class JsoupParser @Inject constructor() {

    fun parseHtml(html: String): Document = Jsoup.parse(html)
}

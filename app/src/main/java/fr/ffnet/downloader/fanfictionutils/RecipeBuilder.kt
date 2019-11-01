package fr.ffnet.downloader.fanfictionutils

import fr.ffnet.downloader.repository.entities.CategoryEntity
import fr.ffnet.downloader.repository.entities.RecipeEntity
import fr.ffnet.downloader.utils.JsoupParser
import javax.inject.Inject

class RecipeBuilder @Inject constructor(
    private val jsoupParser: JsoupParser
) {
    companion object {
        private const val REPLACE = "https://www.academiedugout.fr/recettes/"
    }

    fun buildCategories(html: String): List<CategoryEntity> {
        val document = jsoupParser.parseHtml(html)
        val htmlItemList = document.select(".recipe-categorie__item")
        return htmlItemList.map {
            val url = it.attr("href").replace(REPLACE, "")
            val title = it.select("span span").text()
            CategoryEntity(url, title)
        }
    }

    fun buildRecipes(categoryUrl: String, html: String): List<RecipeEntity> {
        val document = jsoupParser.parseHtml(html)
        val htmlItemList = document.select(".mod-recipe")
        return htmlItemList.map {
            val url = it.select("a").first().attr("href").replace(REPLACE, "")
            val title = it.select(".mod-recipe__body-content a.js-link-a").text().removePrefix("/")
            val imageUrl = it.select(".mod-recipe__media img").attr("src")
            RecipeEntity(url, categoryUrl, imageUrl, title)
        }
    }
}

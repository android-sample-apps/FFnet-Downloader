package fr.ffnet.downloader.category

sealed class RecipeDisplayItem

data class RecipeLoadMoreDisplayModel(
    val title: String
): RecipeDisplayItem()

data class RecipeDisplayModel(
    val url: String,
    val imageUrl: String,
    val title: String
): RecipeDisplayItem()

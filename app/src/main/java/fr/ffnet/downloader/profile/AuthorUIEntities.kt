package fr.ffnet.downloader.profile

sealed class AuthorUIItem {

    data class SearchAuthorNotResultUIItem(
        val message: String
    ) : AuthorUIItem()

    data class AuthorTitleUIItem(
        val title: String
    ) : AuthorUIItem()

    data class SyncedAuthorUIItem(
        val id: String,
        val name: String,
        val fetchedDate: String,
        val nbStories: String,
        val nbFavorites: String
    ) : AuthorUIItem()

    data class SearchAuthorUIItem(
        val id: String,
        val name: String,
        val nbStories: String
    ) : AuthorUIItem()
}
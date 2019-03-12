package fr.ffnet.downloader.profile

import fr.ffnet.downloader.search.Fanfiction

data class Profile(
    val profileId: String,
    val name: String,
    val favoriteFanfictionList: List<Fanfiction>,
    val myFanfictionList: List<Fanfiction>
)

data class ProfileUIModel(
    val profileId: String,
    val name: String,
    val fetchedDate: String,
    val url: String
)

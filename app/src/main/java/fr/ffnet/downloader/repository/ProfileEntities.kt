package fr.ffnet.downloader.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfileEntity(
    @PrimaryKey val profileId: String,
    var name: String,
    var myFavoritesList: String,
    var myStoriesList: String
)

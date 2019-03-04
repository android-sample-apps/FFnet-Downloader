package fr.ffnet.downloader.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfileEntity(
    @PrimaryKey val profileId: String,
    var name: String,
    var isAssociated: Boolean
)

@Entity
data class ProfileFanfictionEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var profileId: String,
    var fanfictionId: String,
    var isFavorite: Boolean
)

package fr.ffnet.downloader.repository.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfileFanfictionEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var profileId: String,
    var fanfictionId: String,
    var profileType: Int
)

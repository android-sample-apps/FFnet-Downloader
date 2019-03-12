package fr.ffnet.downloader.repository.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDateTime

@Entity
data class ProfileEntity(
    @PrimaryKey val profileId: String,
    var name: String,
    var fetchedDate: LocalDateTime,
    var isAssociated: Boolean
)

@Entity
data class ProfileFanfictionEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var profileId: String,
    var fanfictionId: String,
    var profileType: Int
)

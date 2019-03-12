package fr.ffnet.downloader.repository.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDateTime

@Entity
data class ErrorEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var message: String,
    var date: LocalDateTime,
    var shouldDisplaySnackbar: Boolean,
    var shouldSendToAnalytics: Boolean
)

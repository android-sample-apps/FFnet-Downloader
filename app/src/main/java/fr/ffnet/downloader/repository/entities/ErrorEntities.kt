package fr.ffnet.downloader.repository.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDateTime

@Entity
data class ErrorEntity(
    @PrimaryKey(autoGenerate = true) val id: String,
    val message: String,
    val date: LocalDateTime,
    val shouldDisplaySnackbar: Boolean,
    val shouldSendToAnalytics: Boolean
)

package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import fr.ffnet.downloader.repository.dao.ErrorDao
import fr.ffnet.downloader.repository.entities.ErrorEntity
import org.joda.time.LocalDateTime

class ErrorRepository(
    private val errorDao: ErrorDao
) {
    fun getErrors(): LiveData<ErrorEntity> = errorDao.getErrors()

    fun addError(message: String, shouldDisplaySnackBar: Boolean, shouldSendToAnalytics: Boolean) {
        errorDao.insertError(
            ErrorEntity(
                message = message,
                date = LocalDateTime.now(),
                shouldDisplaySnackbar = shouldDisplaySnackBar,
                shouldSendToAnalytics = shouldSendToAnalytics
            )
        )
    }

    fun deleteError(errorId: Int) {
        errorDao.deleteError(errorId)
    }
}

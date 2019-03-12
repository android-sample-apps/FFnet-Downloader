package fr.ffnet.downloader

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.repository.ErrorRepository
import fr.ffnet.downloader.repository.entities.ErrorEntity

class MainViewModel(private val errorRepository: ErrorRepository) : ViewModel() {

    private lateinit var errorResult: LiveData<ErrorEntity>
    fun getErrors(): LiveData<ErrorEntity> = errorResult

    fun loadErrors() {
        errorResult = errorRepository.getErrors()
    }

    fun consumeError(errorId: Int) {
        errorRepository.deleteError(errorId)
    }
}

package fr.ffnet.downloader

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.repository.ErrorRepository
import fr.ffnet.downloader.repository.entities.ErrorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val errorRepository: ErrorRepository) : ViewModel() {

    private lateinit var errorResult: LiveData<ErrorEntity>
    fun getErrors(): LiveData<ErrorEntity> = errorResult

    fun loadErrors() {
        errorResult = errorRepository.getErrors()
    }

    fun consumeError(errorId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            errorRepository.deleteError(errorId)
        }
    }
}

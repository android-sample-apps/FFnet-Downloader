package fr.ffnet.downloader.fanfictionoptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.DateFormatter

class OptionsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {

    private lateinit var displayModel: LiveData<OptionsDisplayModel>

    fun getDisplayModel() = displayModel

    fun load(fanfictionId: String) {
        displayModel = Transformations.map(databaseRepository.getFanfictionInfo(fanfictionId)) { fanfiction ->
            OptionsDisplayModel(
                title = fanfiction.title,
                publishedDate = dateFormatter.format(fanfiction.publishedDate),
                updatedDate = dateFormatter.format(fanfiction.updatedDate),
                fetchedDate = dateFormatter.format(fanfiction.fetchedDate),
                shouldShowFetchedDate = fanfiction.fetchedDate != null,
                shouldShowPdfExport = fanfiction.nbSyncedChapters > 0,
                shouldShowEpubExport = fanfiction.nbSyncedChapters > 0
            )
        }
    }
}

data class OptionsDisplayModel(
    val title: String,
    val publishedDate: String,
    val updatedDate: String,
    val fetchedDate: String,
    val shouldShowFetchedDate: Boolean,
    val shouldShowPdfExport: Boolean,
    val shouldShowEpubExport: Boolean
)

class OptionsViewModelFactory(
    private val creator: () -> OptionsViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

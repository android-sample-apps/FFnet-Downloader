package fr.ffnet.downloader.synced

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.DateFormatter

class SyncedViewModel(
    private val resources: Resources,
    private val repository: DatabaseRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {

    private lateinit var fanfictionResult: LiveData<SyncedFanfictionsResult>

    fun getFanfictionList(): LiveData<SyncedFanfictionsResult> = fanfictionResult

    fun loadFanfictionsFromDb() {
        fanfictionResult = Transformations.map(repository.getSyncedFanfictions()) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                SyncedFanfictionsResult.SyncedFanfictions(fanfictionList.map { fanfiction ->
                    FanfictionSyncedUIModel(
                        id = fanfiction.id,
                        title = fanfiction.title,
                        updatedDate = dateFormatter.format(fanfiction.updatedDate),
                        publishedDate = dateFormatter.format(fanfiction.publishedDate),
                        fetchedDate = dateFormatter.format(fanfiction.fetchedDate),
                        chapters = resources.getString(
                            R.string.synced_fanfictions_chapters,
                            fanfiction.nbSyncedChapters,
                            fanfiction.nbChapters
                        )
                    )
                })
            } else {
                SyncedFanfictionsResult.NoSyncedFanfictions
            }
        }
    }

    sealed class SyncedFanfictionsResult {
        data class SyncedFanfictions(val fanfictionList: List<FanfictionSyncedUIModel>) : SyncedFanfictionsResult()
        object NoSyncedFanfictions : SyncedFanfictionsResult()
    }
}

class SyncedViewModelFactory(
    private val creator: () -> SyncedViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

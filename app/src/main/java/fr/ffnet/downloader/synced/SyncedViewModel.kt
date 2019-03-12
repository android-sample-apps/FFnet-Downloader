package fr.ffnet.downloader.synced

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.DateFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncedViewModel(
    private val resources: Resources,
    private val repository: DatabaseRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {

    private lateinit var fanfictionResult: LiveData<SyncedFanfictionsResult>

    fun getFanfictionList(): LiveData<SyncedFanfictionsResult> = fanfictionResult

    fun loadFanfictionsFromDb() {
        fanfictionResult = Transformations.map(
            repository.getSyncedFanfictions()
        ) { fanfictionList ->
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

    fun deleteFanfiction(fanfictionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.unsyncFanfiction(fanfictionId)
        }
    }

    sealed class SyncedFanfictionsResult {
        data class SyncedFanfictions(val fanfictionList: List<FanfictionSyncedUIModel>) : SyncedFanfictionsResult()
        object NoSyncedFanfictions : SyncedFanfictionsResult()
    }
}

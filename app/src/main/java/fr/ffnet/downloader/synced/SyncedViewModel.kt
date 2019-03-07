package fr.ffnet.downloader.synced

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.DatabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SyncedViewModel(
    private val resources: Resources,
    private val repository: DatabaseRepository
) : ViewModel() {

    private lateinit var fanfictionResult: LiveData<SyncedFanfictionsResult>

    fun getFanfictionList(): LiveData<SyncedFanfictionsResult> = fanfictionResult

    fun loadFanfictionsFromDb() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fanfictionResult = Transformations.map(
            repository.getFanfictionsFromDbLiveData()
        ) { fanfictionList ->
            if (fanfictionList.isNotEmpty()) {
                SyncedFanfictionsResult.SyncedFanfictions(fanfictionList.map { fanfiction ->
                    FanfictionSyncedUIModel(
                        id = fanfiction.id,
                        title = fanfiction.title,
                        updatedDate = formatter.format(fanfiction.updatedDate),
                        publishedDate = formatter.format(fanfiction.publishedDate),
                        syncedDate = fanfiction.syncedDate?.toString("yyyy-MM-dd HH:mm") ?: "N/A",
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

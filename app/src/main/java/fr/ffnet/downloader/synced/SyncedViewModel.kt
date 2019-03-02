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

    private lateinit var fanfictionList: LiveData<List<FanfictionSyncedUIModel>>

    fun getFanfictionList(): LiveData<List<FanfictionSyncedUIModel>> = fanfictionList

    fun loadFanfictionsFromDb() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fanfictionList = Transformations.map(
            repository.getFanfictionsFromDbLiveData()
        ) { fanfictionList ->
            fanfictionList.map { fanfiction ->
                FanfictionSyncedUIModel(
                    id = fanfiction.id,
                    title = fanfiction.title,
                    updatedDate = formatter.format(fanfiction.updatedDate),
                    publishedDate = formatter.format(fanfiction.publishedDate),
                    syncedDate = formatter.format(fanfiction.publishedDate),
                    chapters = resources.getString(
                        R.string.synced_fanfictions_chapters,
                        fanfiction.nbSyncedChapters,
                        fanfiction.nbChapters
                    )
                )
            }
        }
    }

    fun deleteFanfiction(fanfictionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteFanfiction(fanfictionId)
        }
    }
}

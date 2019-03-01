package fr.ffnet.downloader.synced

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.search.Chapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SyncedViewModel(
    private val resources: Resources,
    private val repository: DownloaderRepository
) : ViewModel() {

    private val fanfictionList: MutableLiveData<List<FanfictionSyncedUIModel>> by lazy {
        MutableLiveData<List<FanfictionSyncedUIModel>>()
    }

    fun getFanfictionList(): LiveData<List<FanfictionSyncedUIModel>> = fanfictionList

    fun loadFanfictionsFromDb() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        CoroutineScope(Dispatchers.IO).launch {
            val fanfictions = repository.getFanfictionsFromDb().map { fanfiction ->
                FanfictionSyncedUIModel(
                    id = fanfiction.id,
                    title = fanfiction.title,
                    updatedDate = formatter.format(fanfiction.updatedDate),
                    publishedDate = formatter.format(fanfiction.publishedDate),
                    syncedDate = formatter.format(fanfiction.publishedDate),
                    chapters = resources.getString(
                        R.string.synced_fanfictions_chapters,
                        fanfiction.chapterList.filter(Chapter::status).size,
                        fanfiction.chapterList.size
                    )
                )
            }
            CoroutineScope(Dispatchers.Main).launch {
                fanfictionList.value = fanfictions
            }
        }
    }
}

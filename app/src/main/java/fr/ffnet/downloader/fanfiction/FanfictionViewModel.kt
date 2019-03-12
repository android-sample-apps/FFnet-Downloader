package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.FanfictionDao
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.utils.DateFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FanfictionViewModel(
    private val resources: Resources,
    private val dao: FanfictionDao,
    private val apiRepository: DownloaderRepository,
    private val dbRepository: DatabaseRepository,
    private val dateFormatter: DateFormatter
) : ViewModel() {

    private lateinit var currentFanfiction: Fanfiction

    private lateinit var chapterList: LiveData<List<ChapterUIModel>>

    private lateinit var fanfictionInfo: LiveData<FanfictionUIModel>

    fun getChapterList(): LiveData<List<ChapterUIModel>> = chapterList

    fun getFanfictionInfo(): LiveData<FanfictionUIModel> = fanfictionInfo

    fun refreshFanfictionInfo(fanfictionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            apiRepository.loadFanfictionInfo(fanfictionId)
        }
    }

    fun loadFanfictionInfo(fanfictionId: String) {
        fanfictionInfo = Transformations.map(dbRepository.getFanfictionInfo(fanfictionId)) {
            currentFanfiction = it
            FanfictionUIModel(
                id = it.id,
                title = it.title,
                words = it.words.toString(),
                summary = it.summary,
                updatedDate = dateFormatter.format(it.updatedDate),
                publishedDate = dateFormatter.format(it.publishedDate),
                syncedDate = dateFormatter.format(it.fetchedDate),
                progression = resources.getString(
                    R.string.download_info_chapters_value,
                    it.nbSyncedChapters,
                    it.nbChapters
                )
            )
        }
    }

    fun syncChapters(fanfictionId: String) {
        apiRepository.loadAllChapters(fanfictionId)
    }

    fun loadChapters(fanfictionId: String) {
        chapterList = Transformations.map(
            dao.getChaptersLivedata(fanfictionId)
        ) { chapterList ->
            chapterList.map {
                ChapterUIModel(
                    id = it.chapterId,
                    title = it.title,
                    status = resources.getString(
                        when (it.isSynced) {
                            true -> R.string.download_info_chapter_status_synced
                            false -> R.string.download_info_chapter_status_not_synced
                        }
                    )
                )
            }
        }
    }
}

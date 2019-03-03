package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.R
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.FanfictionDao
import fr.ffnet.downloader.search.Chapter
import fr.ffnet.downloader.search.Fanfiction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FanfictionViewModel(
    private val resources: Resources,
    private val dao: FanfictionDao,
    private val apiRepository: DownloaderRepository,
    private val dbRepository: DatabaseRepository
) : ViewModel() {

    private lateinit var currentFanfiction: Fanfiction

    private val fanfiction: MutableLiveData<FanfictionUIModel> by lazy {
        MutableLiveData<FanfictionUIModel>()
    }
    private lateinit var chapterList: LiveData<List<ChapterUIModel>>
    private val chapterProgression: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getCurrentFanfiction(): LiveData<FanfictionUIModel> = fanfiction

    fun getChapterList(): LiveData<List<ChapterUIModel>> = chapterList

    fun getChapterSyncingProgression(): LiveData<String> = chapterProgression

    fun loadFanfictionInfoFromDatabase(fanfictionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fanfictionInfo = dbRepository.getFanfictionFromDb(fanfictionId)
            presentFanfictionInfo(fanfictionInfo)
        }
    }

    fun loadChapters() {
        if (currentFanfiction.chapterList.size > 1) {
            chapterList = Transformations.map(
                dao.getChaptersLivedata(currentFanfiction.id)
            ) { chapterList ->
                chapterProgression.value = resources.getString(
                    R.string.download_info_chapters_value,
                    chapterList.filter { it.isSynced }.size,
                    currentFanfiction.chapterList.size
                )
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
            apiRepository.loadAllChapters(currentFanfiction.id, currentFanfiction.chapterList)
        }
    }

    private fun presentFanfictionInfo(fanfictionInfo: Fanfiction) {
        currentFanfiction = fanfictionInfo
        with(fanfictionInfo) {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fanfictionUiModel = FanfictionUIModel(
                id = id,
                title = title,
                words = words.toString(),
                summary = summary,
                updatedDate = formatter.format(updatedDate),
                publishedDate = formatter.format(publishedDate),
                chapterList = chapterList.map {
                    ChapterUIModel(
                        id = it.id,
                        title = it.title,
                        status = resources.getString(
                            when (it.status) {
                                true -> R.string.download_info_chapter_status_synced
                                false -> R.string.download_info_chapter_status_not_synced
                            }
                        )
                    )
                }
            )
            CoroutineScope(Dispatchers.Main).launch {
                chapterProgression.value = resources.getString(
                    R.string.download_info_chapters_value,
                    fanfictionInfo.chapterList.filter(Chapter::status).size,
                    fanfictionInfo.chapterList.size
                )
                fanfiction.value = fanfictionUiModel
            }
        }
    }
}

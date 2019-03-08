package fr.ffnet.downloader.fanfiction

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.fanfiction.FanfictionInfoUIModel.ChapterUIModel
import fr.ffnet.downloader.fanfiction.FanfictionInfoUIModel.FanfictionUIModel
import fr.ffnet.downloader.fanfictionutils.FanfictionTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.ChapterDownloadState
import fr.ffnet.downloader.repository.DownloaderRepository.ChapterDownloadState.PENDING
import fr.ffnet.downloader.repository.FanfictionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FanfictionViewModel(
    private val resources: Resources,
    private val dao: FanfictionDao,
    private val fanfictionTransformer: FanfictionTransformer,
    private val apiRepository: DownloaderRepository,
    private val dbRepository: DatabaseRepository
) : ViewModel(), DownloaderRepository.RetrofitIsDoneListener {

    private lateinit var chapterList: LiveData<List<ChapterUIModel>>
    private lateinit var fanfictionInfo: LiveData<FanfictionUIModel>
    private val fanfictionInfoMediator: MediatorLiveData<List<FanfictionInfoUIModel>> by lazy {
        MediatorLiveData<List<FanfictionInfoUIModel>>()
    }

    private var downloadState: ChapterDownloadState = PENDING

    override fun onChapterDownloadState(state: ChapterDownloadState) {
        downloadState = state
    }

    fun getFanfictionInfo(): MediatorLiveData<List<FanfictionInfoUIModel>> = fanfictionInfoMediator

    fun refreshFanfictionInfo(fanfictionId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            apiRepository.loadFanfictionInfo(fanfictionId)
        }
    }

    fun syncChapters(fanfictionId: String) {
        apiRepository.loadAllChapters(fanfictionId, this)
    }

    fun loadInfo(fanfictionId: String) {
        loadChapters(fanfictionId)
        loadFanfictionInfo(fanfictionId)
        fanfictionInfoMediator.apply {
            addSource(fanfictionInfo) {
                fanfictionInfoMediator.value = combineLatestData(chapterList, fanfictionInfo)
            }
            addSource(chapterList) {
                fanfictionInfoMediator.value = combineLatestData(chapterList, fanfictionInfo)
            }
        }
    }

    private fun loadFanfictionInfo(fanfictionId: String) {
        fanfictionInfo = Transformations.map(dbRepository.getFanfictionInfo(fanfictionId)) {
            fanfictionTransformer.toFanfictionUIModel(it)
        }
    }

    private fun loadChapters(fanfictionId: String) {
        chapterList = Transformations.map(dao.getChaptersLivedata(fanfictionId)) {
            fanfictionTransformer.toChapterUIModel(it)
        }
    }

    private fun combineLatestData(
        chapterList: LiveData<List<ChapterUIModel>>,
        fanfictionInfo: LiveData<FanfictionUIModel>
    ): List<FanfictionInfoUIModel> {
        return if (fanfictionInfo.value == null) {
            emptyList()
        } else {
            if (chapterList.value == null) {
                listOf(fanfictionInfo.value!!)
            } else {
                listOf(fanfictionInfo.value!!) + chapterList.value.orEmpty()
            }
        }
    }
}

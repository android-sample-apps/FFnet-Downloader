package fr.ffnet.downloader.search

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.R
import fr.ffnet.downloader.search.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformFailure
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class DownloaderViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val dao: FanfictionDao,
    private val repository: DownloaderRepository
) : ViewModel() {

    private lateinit var currentFanfiction: Fanfiction

    private val fanfiction: MutableLiveData<FanfictionViewModel> by lazy {
        MutableLiveData<FanfictionViewModel>()
    }
    private lateinit var chapterList: LiveData<List<ChapterViewModel>>
    private val chapterProgression: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun loadFanfictionInfos(url: String?) {
        if (!url.isNullOrEmpty()) {
            val urlTransformationResult = urlTransformer.getIdFromUrl(url)
            CoroutineScope(Dispatchers.IO).launch {
                when (urlTransformationResult) {
                    is UrlTransformSuccess -> loadFanfictionInfo(
                        urlTransformationResult.fanfictionId
                    )
                    is UrlTransformFailure -> TODO()
                }
            }
        }
    }

    fun loadChapters() {
        if (currentFanfiction.chapterList.size > 1) {
            chapterList = Transformations.map(dao.getChaptersLivedata(currentFanfiction.id)) { chapterList ->
                chapterProgression.value = resources.getString(
                    R.string.download_info_chapters_value,
                    chapterList.filter { it.isSynced }.size,
                    currentFanfiction.chapterList.size
                )
                chapterList.map {
                    ChapterViewModel(
                        id = it.chapterId,
                        title = it.title,
                        status = resources.getString(
                            when (it.isSynced) {
                                true -> R.string.download_info_chapter_status_synced
                                false -> R.string.download_info_chapter_status_unsynced
                            }
                        )
                    )
                }
            }
            repository.loadAllChapters(currentFanfiction.id, currentFanfiction.chapterList)
        }
    }

    fun getCurrentFanfiction(): LiveData<FanfictionViewModel> = fanfiction

    fun getChapterList(): LiveData<List<ChapterViewModel>> = chapterList

    fun getChapterSyncingProgression(): LiveData<String> = chapterProgression

    private fun loadFanfictionInfo(fanfictionId: String) {
        val fanfictionResult = repository.loadFanfictionInfo(fanfictionId)
        if (fanfictionResult is FanfictionRepositoryResultSuccess) {
            with(fanfictionResult) {
                presentFanfictionInfo(fanfictionInfo)
            }
        } else {

        }
    }

    private fun presentFanfictionInfo(fanfictionInfo: Fanfiction) {
        currentFanfiction = fanfictionInfo
        with(fanfictionInfo) {
            val formatter = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val fanfictionViewModel = FanfictionViewModel(
                id = id,
                title = title,
                words = words.toString(),
                summary = summary,
                updatedDate = formatter.format(updatedDate),
                publishedDate = formatter.format(publishedDate),
                chapterList = chapterList.map {
                    ChapterViewModel(
                        id = it.id,
                        title = it.title,
                        status = resources.getString(
                            when (it.status) {
                                true -> R.string.download_info_chapter_status_synced
                                false -> R.string.download_info_chapter_status_unsynced
                            }
                        )
                    )
                }
            )
            CoroutineScope(Dispatchers.Main).launch {
                chapterProgression.value = resources.getString(
                    R.string.download_info_chapters_value,
                    fanfictionInfo.chapterList.filter { it.status }.size,
                    fanfictionInfo.chapterList.size
                )
                fanfiction.value = fanfictionViewModel
            }
        }
    }

    private fun presentFanfictionInfoError() {

    }
}
